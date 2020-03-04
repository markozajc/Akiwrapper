package com.markozajc.akiwrapper.core.entities.impl.immutable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.exceptions.MissingQuestionException;
import com.markozajc.akiwrapper.core.utils.JSONUtils;

/**
 * An implementation of {@link Question}.
 *
 * @author Marko Zajc
 */
public class QuestionImpl implements Question {

	@Nonnull
	private final String id;
	@Nonnull
	private final String question;
	@Nonnegative
	private final int step;
	@Nonnegative
	private final double gain;
	@Nonnegative
	private final double progression;

	/**
	 * Creates a new {@link QuestionImpl} instance from raw parameters.
	 *
	 * @param id
	 * @param question
	 * @param step
	 * @param gain
	 * @param progression
	 * @param status
	 *
	 * @throws MissingQuestionException
	 *             if the message is missing (no more messages left to answer, get the
	 *             final guesses)
	 */
	public QuestionImpl(@Nonnull String id, @Nonnull String question, @Nonnegative int step, @Nonnegative double gain,
	                    @Nonnegative double progression, @Nonnull Status status) {
		if (status.getLevel().equals(Level.WARNING) && status.getReason().equalsIgnoreCase("no question"))
			throw new MissingQuestionException();

		this.id = id;
		this.question = question;
		this.step = step;
		this.gain = gain;
		this.progression = progression;
	}

	/**
	 * Creates a new {@link QuestionImpl} instance.
	 *
	 * @param json
	 *            JSON parameters to use (acquired with {@link Route#ANSWER} or
	 *            {@link Route#NEW_SESSION} &gt; {@link JSONObject} parameters)
	 * @param status
	 *            call completion status
	 *
	 * @throws MissingQuestionException
	 *             if the message is missing (no more messages left to answer, get the
	 *             final guesses)
	 */
	@SuppressWarnings("null")
	public QuestionImpl(@Nonnull JSONObject json, @Nonnull Status status) {
		if (status.getLevel().equals(Level.WARNING) && status.getReason().toLowerCase().equalsIgnoreCase("no question"))
			throw new MissingQuestionException();

		this.id = json.getString("questionid");
		this.question = json.getString("question");
		this.step = JSONUtils.getInteger(json, "step").intValue();
		this.gain = JSONUtils.getDouble(json, "infogain").doubleValue();
		this.progression = JSONUtils.getDouble(json, "progression").doubleValue();
	}

	@Override
	public double getProgression() {
		return this.progression;
	}

	@Override
	public int getStep() {
		return this.step;
	}

	@Override
	public double getGain() {
		return this.gain;
	}

	@Override
	public String getQuestion() {
		return this.question;
	}

	@Override
	public String getId() {
		return this.id;
	}

}
