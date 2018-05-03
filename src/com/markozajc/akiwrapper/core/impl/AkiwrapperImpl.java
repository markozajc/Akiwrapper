package com.markozajc.akiwrapper.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

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
import com.markozajc.akiwrapper.core.exceptions.AllServersUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.MissingQuestionException;
import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * An implementation of {@link Akiwrapper}.
 * 
 * @author Marko Zajc
 */
public class AkiwrapperImpl implements Akiwrapper {

	/**
	 * A class used to define the temporary API token.
	 * 
	 * @author Marko Zajc
	 */
	public static class Token {

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

	}

	private final String userAgent;
	private final Server server;
	private final boolean filterProfanity;
	private final Token token;

	private int currentStep;
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
	 * @throws ServerUnavailableException
	 *             if no API server is available
	 * @throws IllegalArgumentException
	 *             is {@code metadata} is null
	 */
	public AkiwrapperImpl(@Nonnull AkiwrapperMetadata metadata)
			throws ServerUnavailableException, IllegalArgumentException {

		if (metadata == null)
			throw new IllegalArgumentException("metadata can't be null");

		{
			Server server = metadata.getServer();
			if (server == null) {
				server = Servers.getFirstAvailableServer();

				if (server == null)
					throw new AllServersUnavailableException();
			}
			if (!server.isUp())
				throw new ServerUnavailableException(server);

			this.server = server;
		}
		// Checks & sets the server

		{
			String userAgent = metadata.getUserAgent();
			if (userAgent == null || userAgent.equals(""))
				userAgent = AkiwrapperMetadata.DEFAULT_USER_AGENT;

			this.userAgent = userAgent;
		}
		// Checks & sets the user-agent

		this.filterProfanity = metadata.doesFilterProfanity();
		// Sets the profanity filter

		JSONObject question;
		{
			String name = metadata.getName();
			if (name == null || name.equals(""))
				name = AkiwrapperBuilder.DEFAULT_NAME;

			try {
				question = Route.NEW_SESSION.getRequest(this.server.getBaseUrl(), this.filterProfanity, name).getJSON();
			} catch (IOException e) {
				/*
				 * Shouldn't happen, the server was requested before
				 */

				throw new RuntimeException(e);
			}
		}
		// Checks & uses the name

		JSONObject identification = question.getJSONObject("parameters").getJSONObject("identification");

		this.token = new Token(Long.parseLong(identification.getString("signature")),
				Integer.parseInt(identification.getString("session")));

		this.currentQuestion = new QuestionImpl(question.getJSONObject("parameters").getJSONObject("step_information"),
				new StatusImpl("OK")
		/*
		 * We can assume that the completion is OK because if it wouldn't be, calling the
		 * Route.NEW_SESSION would have thrown ServerUnavailableException
		 */
		);

		this.currentStep = 0;
	}

	@Override
	public Question answerCurrentQuestion(Answer answer) throws IOException {
		if (this.currentQuestion == null)
			return null;

		JSONObject question = Route.ANSWER
				.getRequest(this.server.getBaseUrl(), this.filterProfanity, "" + this.token.session,
						"" + this.token.signature, "" + this.currentQuestion.getStep(), "" + answer.getId())
				.getJSON();

		try {
			this.currentQuestion = new QuestionImpl(question.getJSONObject("parameters"), new StatusImpl(question));
		} catch (MissingQuestionException e) {
			this.currentQuestion = null;
			return null;
		}

		this.currentStep += 1;
		return this.currentQuestion;
	}

	@Override
	public Question getCurrentQuestion() {
		return this.currentQuestion;
	}

	@Override
	public List<Guess> getGuesses() throws IOException {
		JSONObject list = null;
		try {
			list = Route.LIST.setUserAgent(userAgent)
					.getRequest(this.server.getBaseUrl(), this.filterProfanity, "" + token.session,
							"" + token.signature, "" + this.currentStep)
					.getJSON();
		} catch (StatusException e) {
			if (e.getStatus().getLevel().equals(Level.ERROR)) {
				if (e.getStatus().getReason().toLowerCase().equals("elem list is empty"))
					return Collections.unmodifiableList(new ArrayList<>());
			}

			throw e;
		}

		JSONArray elements = list.getJSONObject("parameters").getJSONArray("elements");
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
		return server;
	}

	/**
	 * @return the currently used user-agent
	 */
	public String getUserAgent() {
		return userAgent;
	}

}
