package com.markozajc.akiwrapper.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
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
import com.markozajc.akiwrapper.core.exceptions.ServerGroupUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * An implementation of {@link Akiwrapper}.
 *
 * @author Marko Zajc
 */
public class AkiwrapperImpl implements Akiwrapper {

	private static final String PARAMETERS_KEY = "parameters";

	/**
	 * A class used to define the temporary API token.
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
	private final String userAgent;
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
	 * Creates a new Akiwrapper and registers a new API session. The first question can
	 * be retrieved with {@link #getCurrentQuestion()}.
	 *
	 * @param metadata
	 *            metadata to use. All {@code null} values will be replaced with the
	 *            default values (you can see defaults at {@link AkiwrapperBuilder}'s
	 *            getters)
	 *
	 * @throws ServerGroupUnavailableException
	 *             if no API server is available
	 * @throws IllegalArgumentException
	 *             is {@code metadata} is null
	 */
	@SuppressWarnings("null")
	public AkiwrapperImpl(@Nonnull AkiwrapperMetadata metadata) {
		Server serverCopy = metadata.getServer();
		if (serverCopy == null)
			serverCopy = Servers.getFirstAvailableServer(metadata.getLocalization());

		this.server = serverCopy;
		// Checks & sets the server

		this.userAgent = metadata.getUserAgent();
		// Checks & sets the user-agent

		this.filterProfanity = metadata.doesFilterProfanity();
		// Sets the profanity filter

		JSONObject question;
		String name = metadata.getName();

		try {
			question = Route.NEW_SESSION.getRequest(this.server.getApiUrl(), this.filterProfanity, name).getJSON();
		} catch (IOException e) {
			// Shouldn't happen, the server was requested before
			throw new IllegalStateException(e);
		}
		// Checks & uses the name

		JSONObject identification = question.getJSONObject(PARAMETERS_KEY).getJSONObject("identification");

		this.token = new Token(Long.parseLong(identification.getString("signature")),
		    Integer.parseInt(identification.getString("session")));

		this.currentQuestion = new QuestionImpl(
		    question.getJSONObject(PARAMETERS_KEY).getJSONObject("step_information"), new StatusImpl("OK")
		/*
		 * We can assume that the completion is OK because if it wouldn't be, calling the
		 * Route.NEW_SESSION would have thrown ServerUnavailableException
		 */
		);

		this.currentStep = 0;
	}

	@SuppressWarnings("null")
	@Override
	public Question answerCurrentQuestion(Answer answer) throws IOException {
		Question currentQuestion2 = this.currentQuestion;
		if (currentQuestion2 != null) {
			JSONObject question = Route.ANSWER
			    .getRequest(this.server.getApiUrl(), this.filterProfanity, this.token, "" + currentQuestion2.getStep(),
			        "" + answer.getId())
			    .getJSON();
			try {
				this.currentQuestion = new QuestionImpl(question.getJSONObject(PARAMETERS_KEY),
				    new StatusImpl(question));
			} catch (MissingQuestionException e) {
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
	public Question undoAnswer() throws IOException {
		Question current = getCurrentQuestion();
		if (current == null)
			return null;

		if (current.getStep() < 1)
			return null;

		JSONObject question = Route.CANCEL_ANSWER
		    .getRequest(this.server.getApiUrl(), this.filterProfanity, this.token, Integer.toString(current.getStep()))
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
	public List<Guess> getGuesses() throws IOException {
		JSONObject list = null;
		try {
			list = Route.LIST.setUserAgent(this.userAgent)
			    .getRequest(this.server.getApiUrl(), this.filterProfanity, this.token, "" + this.currentStep)
			    .getJSON();
		} catch (StatusException e) {
			if (e.getStatus().getLevel().equals(Level.ERROR)
			    && e.getStatus().getReason().equalsIgnoreCase("elem list is empty")) {
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

	/**
	 * @return the currently used user-agent
	 */
	public String getUserAgent() {
		return this.userAgent;
	}

}
