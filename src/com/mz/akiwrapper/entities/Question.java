package com.mz.akiwrapper;

import org.json.JSONObject;

import com.mz.akiwrapper.Completion.Level;

public class Question {

	private int step;
	private String question;
	private double progression;
	private boolean isEmpty;

	/**
	 * Creates a new Question object, used to represent Akinator's question
	 * 
	 * @param token
	 *            current session token
	 * @param step
	 *            current step
	 */
	public Question(JSONObject parameters, Completion compl) {
		if (compl.getErrLevel().equals(Level.WARN) && compl.getReason().equalsIgnoreCase("no question")) {
			this.isEmpty = true;

		} else {
			this.step = Integer.parseInt(parameters.getString("step"));
			this.question = parameters.getString("question");
			this.progression = Double.parseDouble(parameters.getString("progression"));

		}
	}

	/**
	 * Returns current completion percentage. Higher means closer to the answer.
	 * 
	 * @return current completion percentage
	 */
	public double getProgression() {
		return progression;
	}

	/**
	 * Returns current step (question number)
	 * 
	 * @return current step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * Returns the question
	 * 
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * Returns true if this question is empty. If this method returns true, it's
	 * safe to call <code>getGuesses()</code> from Akiwrapper object
	 * 
	 * @return true if this question is empty (everything is null), false if this
	 *         question is not empty
	 */
	public boolean isEmpty() {
		return this.isEmpty;
	}

	@Override
	/**
	 * Returns this question formatted as<br>
	 * <code>#step: question text</code>
	 */
	public String toString() {
		return "#" + this.step + ": " + this.question;
	}
}
