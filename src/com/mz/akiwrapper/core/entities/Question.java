package com.mz.akiwrapper.core.entities;

public interface Question extends Identifiable {

	/**
	 * @return current completion percentage (as a double). Higher means closer to
	 *         the answer
	 */
	double getProgression();

	/**
	 * @return current step (question number)
	 */
	int getStep();

	/**
	 * @return gain from the last question
	 */
	double getGain();

	/**
	 * @return the actual question the user should answer to
	 */
	String getQuestion();

}
