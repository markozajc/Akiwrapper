package com.markozajc.akiwrapper.core.entities.impl.immutable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.json.JSONObject;

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
	 * Constructs a new {@link QuestionImpl} instance from raw parameters.
	 *
	 * @param id
	 * @param question
	 * @param step
	 * @param gain
	 * @param progression
	 * @param status
	 *
	 * @throws MissingQuestionException
	 *             if there are no more questions left.
	 */
	public QuestionImpl(@Nonnull String id, @Nonnull String question, @Nonnegative int step, @Nonnegative double gain,
	                    @Nonnegative double progression, @Nonnull Status status) {
		checkMissingQuestion(status);

		this.id = id;
		this.question = question;
		this.step = step;
		this.gain = gain;
		this.progression = progression;
	}

	/**
	 * Constructs a new {@link QuestionImpl} instance from a {@link JSONObject}.
	 *
	 * @param json
	 * @param status
	 *
	 * @throws MissingQuestionException
	 *             if there are no more questions left.
	 */
	@SuppressWarnings("null")
	public QuestionImpl(@Nonnull JSONObject json, @Nonnull Status status) {
		checkMissingQuestion(status);
		this.id = json.getString("questionid");
		this.question = json.getString("question");
		this.step = JSONUtils.getInteger(json, "step").get().intValue();
		this.gain = JSONUtils.getDouble(json, "infogain").get().doubleValue();
		this.progression = JSONUtils.getDouble(json, "progression").get().doubleValue();
	}

	private static void checkMissingQuestion(@Nonnull Status status) {
		if (status.getLevel() == Level.WARNING && "no question".equalsIgnoreCase(status.getReason()))
			throw new MissingQuestionException();
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
