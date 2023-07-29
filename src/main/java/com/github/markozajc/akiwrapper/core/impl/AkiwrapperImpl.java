package com.github.markozajc.akiwrapper.core.impl;

import static com.github.markozajc.akiwrapper.core.entities.Status.Reason.QUESTIONS_EXHAUSTED;
import static com.github.markozajc.akiwrapper.core.utils.route.Routes.*;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import javax.annotation.Nonnull;

import org.json.JSONObject;

import com.github.markozajc.akiwrapper.Akiwrapper;
import com.github.markozajc.akiwrapper.core.entities.*;
import com.github.markozajc.akiwrapper.core.entities.impl.immutable.*;
import com.github.markozajc.akiwrapper.core.exceptions.*;
import com.github.markozajc.akiwrapper.core.utils.ApiKey;

import kong.unirest.UnirestInstance;

@SuppressWarnings("javadoc") // internal impl
public class AkiwrapperImpl implements Akiwrapper {

	public static class Session {

		private static final String FORMAT_QUERYSTRING = "session=%s&signature=%s";

		private final long signature;
		private final int session;

		private Session(long signature, int session) {
			this.signature = signature;
			this.session = session;
		}

		@Nonnull
		public static Session fromJson(@Nonnull JSONObject parameters) {
			var session = parameters.getJSONObject("identification");
			return new Session(parseLong(session.getString("signature")), parseInt(session.getString("session")));
		}

		public long getSignature() {
			return this.signature;
		}

		public int getSession() {
			return this.session;
		}

		public String asQuerystring() {
			return format(FORMAT_QUERYSTRING, this.getSession(), this.getSignature());
		}
	}

	@Nonnull private final ServerImpl server;
	@Nonnull private final UnirestInstance unirest;
	private final boolean filterProfanity;

	private ApiKey apiKey;
	private Session session;
	private Question question;
	private int currentStep;
	private boolean exhausted;

	public AkiwrapperImpl(@Nonnull UnirestInstance unirest, @Nonnull ServerImpl server, boolean filterProfanity) {
		this.filterProfanity = filterProfanity;
		this.server = server;
		this.unirest = unirest;
		this.currentStep = 0;
	}

	@SuppressWarnings("null")
	public void createSession() {
		this.apiKey = ApiKey.accquireApiKey(this.unirest);
		var sessionParameters = NEW_SESSION.createRequest(this).execute().getBody();
		this.session = Session.fromJson(sessionParameters);
		this.question = QuestionImpl.from(sessionParameters.getJSONObject("step_information"));
	}

	@Override
	public Question answer(Answer answer) {
		if (this.exhausted)
			throw new QuestionsExhaustedException();

		var response = ANSWER.createRequest(this)
			.parameter(PARAMETER_STEP, this.currentStep)
			.parameter(PARAMETER_ANSWER, answer.getId())
			.execute();

		if (response.getStatus().getReason() == QUESTIONS_EXHAUSTED) {
			this.currentStep += 1;
			this.question = null;
			this.exhausted = true;
			return null;
		}

		this.question = QuestionImpl.from(response.getBody());
		this.currentStep = this.question.getStep();
		return this.question;
	}

	@Override
	public Question undoAnswer() {
		if (this.exhausted)
			throw new QuestionsExhaustedException(); // the api won't let us

		if (this.currentStep == 0)
			throw new UndoOutOfBoundsException();

		var response = CANCEL_ANSWER.createRequest(this).parameter(PARAMETER_STEP, this.currentStep).execute();
		this.question = QuestionImpl.from(response.getBody());
		this.currentStep = this.question.getStep();
		return this.question;
	}

	@Override
	@SuppressWarnings("null")
	public List<Guess> getGuesses(int count) {
		var request = LIST.createRequest(this).parameter(PARAMETER_STEP, this.currentStep);
		if (count > 0)
			request.parameter(PARAMETER_SIZE, count);
		var response = request.execute();

		return stream(response.getBody().getJSONArray("elements").spliterator(), false).map(JSONObject.class::cast)
			.map(j -> j.getJSONObject("element"))
			.map(GuessImpl::from)
			.sorted()
			.collect(toUnmodifiableList());
	}

	@Override
	public Question getQuestion() {
		return this.question;
	}

	@Override
	public int getStep() {
		return this.currentStep;
	}

	@Override
	public ServerImpl getServer() {
		return this.server;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	public Session getSession() {
		return this.session;
	}

	public ApiKey getApiKey() {
		return this.apiKey;
	}

	@Nonnull
	public UnirestInstance getUnirest() {
		return this.unirest;
	}
}
