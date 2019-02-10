package com.markozajc.akiwrapper.core.entities;

import com.markozajc.akiwrapper.Akiwrapper.Answer;

/**
 * A class used to represent Akinator's question that can be answered with an
 * {@link Answer}.
 *
 * @author Marko Zajc
 */
public interface Question extends Identifiable {

	/**
	 * @return current completion percentage (as a double). Higher means closer to the
	 *         answer
	 */
	double getProgression();

	/**
	 * @return current step (question number). This uses zero-based index, meaning the
	 *         first question will be on step {@code 0}
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
