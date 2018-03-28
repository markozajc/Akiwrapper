package com.mz.akiwrapper.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.mz.akiwrapper.Akiwrapper;
import com.mz.akiwrapper.core.AkiwrapperBuilder;
import com.mz.akiwrapper.core.Route;
import com.mz.akiwrapper.core.entities.CompletionStatus;
import com.mz.akiwrapper.core.entities.CompletionStatus.Level;
import com.mz.akiwrapper.core.entities.Guess;
import com.mz.akiwrapper.core.entities.Question;
import com.mz.akiwrapper.core.entities.Server;
import com.mz.akiwrapper.core.entities.impl.CompletionStatusImpl;
import com.mz.akiwrapper.core.entities.impl.GuessImpl;
import com.mz.akiwrapper.core.entities.impl.QuestionImpl;
import com.mz.akiwrapper.core.exceptions.ServerUnavailableException;

public class AkiwrapperImpl implements Akiwrapper {

	public class Token {

		public final long signature;
		public final int session;

		public Token(long signature, int session) {
			this.signature = signature;
			this.session = session;
		}

	}

	private final String userAgent;
	private final Server server;
	private final Token token;

	private int currentStep;
	private Question currentQuestion;

	/**
	 * Creates a new Akiwrapper and registers a new API session. The first question
	 * can be retrieved with {@link #getCurrentQuestion()}.
	 * 
	 * @param server
	 *            the API server to use (will be checked with {@link Server#isUp()}
	 *            first).
	 * 
	 * @param name
	 *            player's name (won't have any huge impact but is still passed to
	 *            the Akinator API for convenience.
	 * @param userAgent
	 *            the user-agent to use
	 * 
	 * @throws ServerUnavailableException
	 *             if no API server is available
	 * @throws IllegalArgumentException
	 *             is server is null
	 */
	public AkiwrapperImpl(Server server, String name, String userAgent)
			throws ServerUnavailableException, IllegalArgumentException {
		if (server == null)
			throw new IllegalArgumentException("Server can't be null");

		if (!server.isUp())
			throw new ServerUnavailableException(server.getBaseUrl());

		this.server = server;
		// Checks & sets the server

		if (name == null || name.equals(""))
			name = AkiwrapperBuilder.DEFAULT_NAME;

		if (userAgent == null || userAgent.equals(""))
			userAgent = Route.DEFAULT_USER_AGENT;

		JSONObject question;
		try {
			question = Route.NEW_SESSION.getRequest(this.server.getBaseUrl(), name).getJSON();
		} catch (IOException e) {
			/*
			 * Shouldn't happen, the server was requested before
			 */

			throw new RuntimeException(e);
		}

		JSONObject identification = question.getJSONObject("parameters").getJSONObject("identification");

		this.token = new Token(Long.parseLong(identification.getString("signature")),
				Integer.parseInt(identification.getString("session")));

		this.currentQuestion = new QuestionImpl(question.getJSONObject("parameters").getJSONObject("step_information"),
				new CompletionStatusImpl("OK")
		/*
		 * We can assume that the completion is OK because if it wouldn't be, calling
		 * the Route.NEW_SESSION would have thrown ServerUnavailableException
		 */
		);

		this.currentStep = 0;
		this.userAgent = userAgent;
	}

	@Override
	public Question answerCurrentQuestion(Answer answer) throws IOException {
		JSONObject question = Route.ANSWER
				.getRequest(this.server.getBaseUrl(), "" + this.token.session, "" + this.token.signature,
						"" + this.currentQuestion.getStep(), "" + answer.getId())
				.getJSON();

		this.currentQuestion = new QuestionImpl(question.getJSONObject("parameters"),
				new CompletionStatusImpl(question));

		this.currentStep += 1;
		return this.currentQuestion;
	}

	@Override
	public Question getCurrentQuestion() {
		return this.currentQuestion;
	}

	@Override
	public List<Guess> getGuesses() throws IOException {
		JSONObject list = Route.LIST.setUserAgent(userAgent)
				.getRequest(this.server.getBaseUrl(), "" + token.session, "" + token.signature, "" + this.currentStep)
				.getJSON();

		CompletionStatus compl = new CompletionStatusImpl(list);

		if (compl.getLevel().equals(Level.ERROR) || compl.getLevel().equals(Level.WARNING)) {
			if (compl.getReason().equalsIgnoreCase("elem list is empty"))
				return new ArrayList<>();

			throw new IOException("Something went wrong: " + compl);

		}

		return list.getJSONObject("parameters").getJSONArray("elements").toList().stream().map(e -> {
			JSONObject json = (JSONObject) e;

			return new GuessImpl(json.getJSONObject("element"));
		}).collect(Collectors.toList());
	}

	/**
	 * @return the currently used user-agent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @return the currently used API server
	 */
	public Server getServer() {
		return server;
	}

}
