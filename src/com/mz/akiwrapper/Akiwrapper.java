package com.mz.akiwrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jakob.utils.HttpUtils;
import com.mz.akiwrapper.Completion.Level;

public class Akiwrapper {

	public static final String NAME = "Akiwrapper";
	public static final String USERAGENT = NAME;

	public static final List<String> API_URLS = Arrays.asList("http://api-en1.akinator.com/ws/",
			"http://api-en3.akinator.com/ws/", "http://api-en4.akinator.com/ws/");

	public final String currentApiUrl;
	private final Token token;

	private int currentStep;
	private Question currentQuestion;
	private HashMap<Answer, Integer> answerId = new HashMap<>();

	/**
	 * Check if the API server used by an Akiwrapper instance is still up
	 * 
	 * @param aw
	 *            Akiwrapper instance to check
	 * @return true if that API server is OK, false if otherwise
	 */
	public static boolean isUp(Akiwrapper aw) {
		return isUp(aw.currentApiUrl);
	}

	/**
	 * Checks if an API server is online
	 * 
	 * @param serverUrl
	 *            API server to check
	 * @return true if a new session can be created on <code>serverUrl</code>, false
	 *         if otherwise
	 */
	public static boolean isUp(String serverUrl) {
		try {
			JSONObject question = new JSONObject(
					HttpUtils.sendGet(serverUrl + "new_session?partner=1&player=" + NAME, USERAGENT).getResponseBody());

			if (new Completion(question).getErrLevel().equals(Level.OK)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Returns URL of the first available API server
	 * 
	 * @return API server's URL or null if no servers are available
	 */
	public static String getAvailableServer() {
		for (String serverUrl : API_URLS) {
			if (isUp(serverUrl)) {
				return serverUrl;
			}
		}

		return null;
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
		this.currentApiUrl = getAvailableServer();

		// Creates a new session if this is a new one
		JSONObject question = new JSONObject(HttpUtils
				.sendGet(this.currentApiUrl + "new_session?partner=1&player=" + NAME, "Akiwrapper").getResponseBody());

		Completion compl = new Completion(question);
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
				this.currentApiUrl + "answer?session=" + token.getSession() + "&signature=" + token.getSignature()
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
				.sendGet(this.currentApiUrl + "list?session=" + token.getSession() + "&signature="
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