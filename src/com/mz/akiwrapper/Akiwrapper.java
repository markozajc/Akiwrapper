package com.mz.akiwrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jakob.utils.HttpUtils;
import com.mz.akiwrapper.Completion.Level;

public class Akiwrapper {

	public static final String API_URL = "http://api-en1.akinator.com/ws/";
	public static final String NAME = "Akiwrapper";
	private int currentStep;
	private Question currentQuestion;
	private HashMap<Answer, Integer> answerId = new HashMap<>();

	private Token token;

	public enum ServerStatus {
		ONLINE, OFFLINE;
	}

	/**
	 * Returns current server status for the API server. If current server status is
	 * OFFLINE, you are not able to use Akiwrapper for some time. This is completely
	 * dependent on the Akinator team so if the servers are offline, you are not
	 * able to do anything to retrieve them back
	 * 
	 * @return current API server status
	 */
	public static ServerStatus getServerStatus() {
		try {
			new Akiwrapper();
		} catch (IOException e) {
			return ServerStatus.OFFLINE;
		}

		return ServerStatus.ONLINE;
	}

	/**
	 * An enum used to represent an answer to Akinator's question
	 */
	public enum Answer {
		YES, NO, DONT_KNOW, PROBABLY, PROBABLY_NOT;
	}

	/**
	 * Creates a new Akiwrapper and registers a new API session. The first question
	 * can be retrieved with <code>getCurrentQuestion()</code>
	 * 
	 * @throws IOException
	 */
	public Akiwrapper() throws IOException {
		// Creates a new session if this is a new one
		JSONObject question = new JSONObject(HttpUtils
				.sendGet(Akiwrapper.API_URL + "new_session?partner=1&player=" + NAME, "Akiwrapper").getResponseBody());

		Completion compl = new Completion(question.getString("completion"));
		if (!compl.getErrLevel().equals(Level.OK)) {
			throw new IOException("Something went wrong: " + compl.getReason());
		}

		JSONObject identification = question.getJSONObject("parameters").getJSONObject("identification");

		this.token = new Token(Long.parseLong(identification.getString("signature")),
				Integer.parseInt(identification.getString("session")));

		this.currentQuestion = new Question(question.getJSONObject("parameters").getJSONObject("step_information"),
				compl);

		this.currentStep = 0;

		this.answerId.put(Answer.YES, 0);
		this.answerId.put(Answer.NO, 1);
		this.answerId.put(Answer.DONT_KNOW, 2);
		this.answerId.put(Answer.PROBABLY, 3);
		this.answerId.put(Answer.PROBABLY_NOT, 4);
	}

	/**
	 * Answers current question and retrieves the next one. The next question is
	 * passed as return value and can be retrieved later on with
	 * <code>getCurrentQuestion()</code>
	 * 
	 * @return the latest question or null if an answer was found
	 * @throws IOException
	 *             if something goes wrong
	 */
	public Question answerCurrentQuestion(Answer answer) throws IOException {
		JSONObject question = new JSONObject(HttpUtils.sendGet(
				Akiwrapper.API_URL + "answer?session=" + token.getSession() + "&signature=" + token.getSignature()
						+ "&step=" + this.currentQuestion.getStep() + "&answer=" + this.answerId.get(answer),
				"Akiwrapper").getResponseBody());

		this.currentQuestion = new Question(question.getJSONObject("parameters"),
				new Completion(question.getString("completion")));

		this.currentStep += 1;
		return this.currentQuestion;
	}

	/**
	 * Returns current question. You can answer it with
	 * <code>answerCurrentQuestion()</code>
	 * 
	 * @return current question
	 */
	public Question getCurrentQuestion() {
		return this.currentQuestion;
	}

	/**
	 * Returns an array of Akinator's guesses. This is safe to call when
	 * <code>getCurrentQuestion().isEmpty()</code> returns true
	 * 
	 * @return an array of Akinator's guesses, empty if there are no guesses
	 * @throws IOException
	 *             if API call isn't successful
	 */
	public Guess[] getGuesses() throws IOException {
		ArrayList<Guess> guesses = new ArrayList<>();

		String json = HttpUtils
				.sendGet(Akiwrapper.API_URL + "list?session=" + token.getSession() + "&signature="
						+ token.getSignature() + "&mode_question=0&step=" + this.currentStep, "Akiwrapper")
				.getResponseBody();

		JSONObject list = new JSONObject(json);

		Completion compl = new Completion(list.getString("completion"));

		if (compl.getErrLevel().equals(Level.KO) || compl.getErrLevel().equals(Level.WARN)) {
			if (compl.getReason().equalsIgnoreCase("elem list is empty")) {
				return new Guess[] {};

			}
			throw new IOException("Something went wrong: " + compl);

		}

		JSONArray elements = list.getJSONObject("parameters").getJSONArray("elements");
		for (int i = 0; i < elements.length(); i++) {
			guesses.add(new Guess(elements.getJSONObject(i).getJSONObject("element")));
		}

		return guesses.toArray(new Guess[] {});
	}
}

// new_session?partner=1&player=Akiwrapper
// http://api-en1.akinator.com/ws/answer?session=2&signature=1958448761&step=79&answer=0