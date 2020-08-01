package com.markozajc.akiwrapper.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.GuessImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.QuestionImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.MissingQuestionException;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * An implementation of {@link Akiwrapper}.
 *
 * @author Marko Zajc
 */
public class AkiwrapperImpl implements Akiwrapper {

	private static final String NO_MORE_QUESTIONS_STATUS = "elem list is empty";
	private static final String PARAMETERS_KEY = "parameters";

	/**
	 * A class used to define the session token.
	 *
	 * @author Marko Zajc
	 */
	public static class Token {

		private static final String AUTH_QUERYSTRING = "&session=%s&signature=%s";

		private final long signature;
		private final int session;

		/**
		 * Creates a new {@link Token}.
		 *
		 * @param signature
		 * @param session
		 */
		public Token(long signature, int session) {
			this.signature = signature;
			this.session = session;
		}

		private long getSignature() {
			return this.signature;
		}

		private int getSession() {
			return this.session;
		}

		/**
		 * @return the compiled token
		 */
		public String compile() {
			return String.format(AUTH_QUERYSTRING, "" + this.getSession(), "" + this.getSignature());
		}

	}

	@Nonnull
	private final Server server;
	private final boolean filterProfanity;
	@Nonnull
	private final Token token;
	@Nonnegative
	private int currentStep;
	@Nullable
	private Question currentQuestion;

	/**
	 * Constructs a new {@link Akiwrapper} instance and creates a new API session. The
	 * first question can be retrieved with {@link #getCurrentQuestion()}.
	 *
	 * @param metadata
	 *            {@link AkiwrapperMetadata} to use. All {@code null} values will be
	 *            replaced with the default values that are specified in
	 *            {@link AkiwrapperMetadata} as constants.
	 *
	 * @throws ServerNotFoundException
	 *             if no {@link Server} is available for the given
	 *             {@link AkiwrapperMetadata}.
	 */
	@SuppressWarnings("null")
	public AkiwrapperImpl(@Nonnull AkiwrapperMetadata metadata) throws ServerNotFoundException { // NOSONAR That's a
																								 // false-positive
		this.server = getServer(metadata);
		this.filterProfanity = metadata.doesFilterProfanity();
		this.currentStep = 0;

		JSONObject question;
		question = Route.NEW_SESSION
			.getRequest("", this.filterProfanity, Long.toString(System.currentTimeMillis()), this.server.getUrl())
			.getJSON();

		JSONObject parameters = question.getJSONObject(PARAMETERS_KEY);
		this.token = getToken(parameters);
		this.currentQuestion = new QuestionImpl(parameters.getJSONObject("step_information"), new StatusImpl("OK"));
	}

	@Nonnull
	private static Token getToken(@Nonnull JSONObject parameters) {
		JSONObject identification = parameters.getJSONObject("identification");
		return new Token(Long.parseLong(identification.getString("signature")),
						 Integer.parseInt(identification.getString("session")));
	}

	@Nonnull
	private static Server getServer(@Nonnull AkiwrapperMetadata metadata) throws ServerNotFoundException {
		Server server = metadata.getServer();
		if (server == null)
			server = Servers.findServer(metadata.getLanguage(), metadata.getGuessType());
		return server;
	}

	@SuppressWarnings("null")
	@Override
	public Question answerCurrentQuestion(Answer answer) {
		Question currentQuestion2 = this.currentQuestion;
		if (currentQuestion2 != null) {
			JSONObject question = Route.ANSWER
				.getRequest(this.server.getUrl(), this.filterProfanity, this.token, "" + currentQuestion2.getStep(),
							"" + answer.getId())
				.getJSON();
			try {
				this.currentQuestion =
					new QuestionImpl(question.getJSONObject(PARAMETERS_KEY), new StatusImpl(question));
			} catch (MissingQuestionException e) { // NOSONAR It does not need to be logged
				this.currentQuestion = null;
				return null;
			}

			this.currentStep += 1;
			return this.currentQuestion;
		}

		return null;
	}

	@SuppressWarnings("null")
	@Override
	public Question undoAnswer() {
		Question current = getCurrentQuestion();
		if (current == null)
			return null;

		if (current.getStep() < 1)
			return null;

		JSONObject question = Route.CANCEL_ANSWER
			.getRequest(this.server.getUrl(), this.filterProfanity, this.token, Integer.toString(current.getStep()))
			.getJSON();

		this.currentQuestion = new QuestionImpl(question.getJSONObject(PARAMETERS_KEY), new StatusImpl(question));

		this.currentStep -= 1;
		return this.currentQuestion;
	}

	@Override
	public Question getCurrentQuestion() {
		return this.currentQuestion;
	}

	@SuppressWarnings("null")
	@Override
	public List<Guess> getGuesses() {
		JSONObject list = null;
		try {
			list = Route.LIST.getRequest(this.server.getUrl(), this.filterProfanity, this.token, "" + this.currentStep)
				.getJSON();
		} catch (StatusException e) {
			if (e.getStatus().getLevel() == Level.ERROR
				&& NO_MORE_QUESTIONS_STATUS.equalsIgnoreCase(e.getStatus().getReason())) {
				return Collections.unmodifiableList(new ArrayList<>());
			}

			throw e;
		}

		JSONArray elements = list.getJSONObject(PARAMETERS_KEY).getJSONArray("elements");
		List<Guess> guesses = new ArrayList<>();
		for (int i = 0; i < elements.length(); i++)
			guesses.add(new GuessImpl(elements.getJSONObject(i).getJSONObject("element")));
		// Currently the only way to (cleanly) extract JSONObjects from a JSONArray
		// without having to box and unbox it a million times is to use this old (and
		// ugly) but gold, condition-based for loop :P

		return Collections.unmodifiableList(guesses);
	}

	@Override
	public Server getServer() {
		return this.server;
	}

}
