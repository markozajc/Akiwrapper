package com.github.markozajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;

import com.github.markozajc.akiwrapper.Akiwrapper.Answer;
import com.github.markozajc.akiwrapper.AkiwrapperBuilder;

/**
 * A representation of Akinator's question that is to be answered with an
 * {@link Answer}. Each {@link Question} object has a localized string question, a
 * step number, gain, and progression.
 *
 * @author Marko Zajc
 */
public interface Question extends Identifiable {

	/**
	 * Current completion percentage (as a double). Higher means that Akinator believes
	 * to be closer to the correct answer.<br>
	 * The value ranges between 0 and 100.
	 *
	 * @return completion percentage.
	 */
	double getProgression();

	/**
	 * Returns the current step (question number). This uses zero-based index, meaning
	 * the first question will be on step {@code 0}.
	 *
	 * @return current step.
	 */
	int getStep();

	/**
	 * Returns the gained accuracy from the last question (as a double). I'm not exactly
	 * sure what this does.
	 *
	 * @return infogain.
	 */
	double getInfogain();

	/**
	 * Returns the question content that should be displayed to the user. This localized
	 * to the language specified in
	 * {@link AkiwrapperBuilder#setLanguage(Server.Language)}.
	 *
	 * @return question.
	 */
	@Nonnull
	String getQuestion();

}
