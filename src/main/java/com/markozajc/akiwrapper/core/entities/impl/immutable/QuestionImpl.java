package com.markozajc.akiwrapper.core.entities.impl.immutable;

import static com.markozajc.akiwrapper.core.entities.Status.Level.WARNING;
import static com.markozajc.akiwrapper.core.utils.JSONUtils.*;

import javax.annotation.*;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.exceptions.MissingQuestionException;

public class QuestionImpl implements Question {

	private static final String REASON_OUT_OF_QUESTIONS = "no question";

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

	public QuestionImpl(@Nonnull String id, @Nonnull String question, @Nonnegative int step, @Nonnegative double gain,
						@Nonnegative double progression, @Nonnull Status status) {
		checkMissingQuestion(status);

		this.id = id;
		this.question = question;
		this.step = step;
		this.gain = gain;
		this.progression = progression;
	}

	@SuppressWarnings("null")
	public static QuestionImpl from(@Nonnull JSONObject json, @Nonnull Status status) {
		return new QuestionImpl(json.getString("questionid"), json.getString("question"),
								getInteger(json, "step").orElseThrow(), getDouble(json, "infogain").orElseThrow(),
								getDouble(json, "progression").orElseThrow(), status);
	}

	private static void checkMissingQuestion(@Nonnull Status status) {
		if (status.getLevel() == WARNING && REASON_OUT_OF_QUESTIONS.equalsIgnoreCase(status.getReason()))
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
	public double getInfogain() {
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
