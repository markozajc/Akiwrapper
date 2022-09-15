package com.github.markozajc.akiwrapper.core.entities;

import javax.annotation.*;

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
	 * Current completion percentage (as a double). Higher means that Akinator is closer
	 * to the correct answer (or the game is close to the end?). Not sure if that's the
	 * case, but I believe this can go down as well.
	 *
	 * @return completion percentage.
	 */
	@Nonnegative
	double getProgression();

	/**
	 * Returns the current step (question number). This uses zero-based index, meaning
	 * the first question will be on step {@code 0}.
	 *
	 * @return current step.
	 */
	@Nonnegative
	int getStep();

	/**
	 * Returns the gained accuracy from the last question (as a double). I'm not exactly
	 * sure what this does, but I'm pretty sure that it's meant to describe how well
	 * Akinator can pinpoint the answer after a question was with the answered question.
	 *
	 * @return accuracy gain.
	 *
	 * @deprecated Use {@link #getInfogain()} instead
	 */
	@Nonnegative
	@Deprecated(since = "1.5.2", forRemoval = true)
	default double getGain() {
		return getInfogain();
	}

	/**
	 * Returns the gained accuracy from the last question (as a double). I'm not exactly
	 * sure what this does, but I'm pretty sure that it's meant to describe how well
	 * Akinator can pinpoint the answer after a question was with the answered question.
	 *
	 * @return accuracy gain.
	 */
	@Nonnegative
	double getInfogain();

	/**
	 * Returns the actual question that the user must answer. This is provided in the
	 * language that was specified using the {@link AkiwrapperBuilder}.
	 *
	 * @return question.
	 */
	@Nonnull
	String getQuestion();

}
