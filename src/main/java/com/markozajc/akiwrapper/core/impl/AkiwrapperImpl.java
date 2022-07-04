package com.markozajc.akiwrapper.core.impl;

import static com.markozajc.akiwrapper.core.Route.*;
import static com.markozajc.akiwrapper.core.entities.Status.Level.ERROR;
import static com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl.STATUS_OK;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import javax.annotation.*;

import org.json.JSONObject;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.impl.immutable.*;
import com.markozajc.akiwrapper.core.exceptions.*;

import kong.unirest.UnirestInstance;

public class AkiwrapperImpl implements Akiwrapper {

	private static final String NO_MORE_QUESTIONS_STATUS = "elem list is empty";
	private static final String PARAMETERS_KEY = "parameters";

	public static class Session {

		private static final String FORMAT_QUERYSTRING = "&session=%s&signature=%s";

		private final long signature;
		private final int session;

		public Session(long signature, int session) {
			this.signature = signature;
			this.session = session;
		}

		public long getSignature() {
			return this.signature;
		}

		public int getSession() {
			return this.session;
		}

		public String querystring() {
			return format(FORMAT_QUERYSTRING, this.getSession(), this.getSignature());
		}
	}

	@Nonnull
	private final Server server;
	@Nonnull
	private final UnirestInstance unirest;
	private final boolean filterProfanity;
	@Nonnull
	private final Session session;
	@Nonnegative
	private int currentStep;
	@Nullable
	private Question question;
	private List<Guess> guessCache;

	@SuppressWarnings("null")
	public AkiwrapperImpl(@Nonnull UnirestInstance unirest, @Nonnull Server server, boolean filterProfanity) {
		var questionJson =
			NEW_SESSION.createRequest(unirest, "", filterProfanity, currentTimeMillis(), server.getUrl()).getJSON();

		var parameters = questionJson.getJSONObject(PARAMETERS_KEY);

		this.session = getSession(parameters);
		this.question = QuestionImpl.from(parameters.getJSONObject("step_information"), STATUS_OK);
		this.filterProfanity = filterProfanity;
		this.server = server;
		this.unirest = unirest;
		this.currentStep = 0;
	}

	@Nonnull
	private static Session getSession(@Nonnull JSONObject parameters) {
		var session = parameters.getJSONObject("identification");
		return new Session(parseLong(session.getString("signature")), parseInt(session.getString("session")));
	}

	@SuppressWarnings("null")
	@Override
	public Question answer(Answer answer) {
		this.guessCache = null;
		var oldQuestion = this.question;
		if (oldQuestion != null) {
			var newQuestionJson = ANSWER
				.createRequest(this.unirest, this.server.getUrl(), this.filterProfanity, this.session,
							   oldQuestion.getStep(), answer.getId())
				.getJSON();

			try {
				this.question =
					QuestionImpl.from(newQuestionJson.getJSONObject(PARAMETERS_KEY), new StatusImpl(newQuestionJson));

			} catch (MissingQuestionException e) { // NOSONAR It does not need to be logged
				this.question = null;
				return null;
			}

			this.currentStep += 1;
			return this.question;
		}

		return null;
	}

	@Override
	@SuppressWarnings("null")
	public Question undoAnswer() {
		this.guessCache = null;

		Question current = getQuestion();
		if (current == null)
			return null;

		if (current.getStep() < 1)
			return null;

		var questionJson = CANCEL_ANSWER
			.createRequest(this.unirest, this.server.getUrl(), this.filterProfanity, this.session, current.getStep())
			.getJSON();

		this.question = QuestionImpl.from(questionJson.getJSONObject(PARAMETERS_KEY), new StatusImpl(questionJson));

		this.currentStep -= 1;

		return this.question;
	}

	@Override
	public Question getQuestion() {
		return this.question;
	}

	@SuppressWarnings("null")
	@Override
	public List<Guess> getGuesses() {
		try {
			if (this.guessCache == null)
				this.guessCache = stream(LIST
					.createRequest(this.unirest, this.server.getUrl(), this.filterProfanity, this.session,
								   this.currentStep)
					.getJSON()
					.getJSONObject(PARAMETERS_KEY)
					.getJSONArray("elements")
					.spliterator(), false).map(JSONObject.class::cast)
						.map(j -> j.getJSONObject("element"))
						.map(GuessImpl::from)
						.sorted()
						.collect(toUnmodifiableList());

			return this.guessCache;

		} catch (StatusException e) {
			if (e.getStatus().getLevel() == ERROR
				&& NO_MORE_QUESTIONS_STATUS.equalsIgnoreCase(e.getStatus().getReason())) {
				return emptyList();
			}

			throw e;
		}
	}

	@Override
	public Server getServer() {
		return this.server;
	}

}
