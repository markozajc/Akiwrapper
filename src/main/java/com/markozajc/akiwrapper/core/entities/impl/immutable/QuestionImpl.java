package com.markozajc.akiwrapper.core.entities.impl.immutable;

import javax.annotation.*;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.exceptions.MissingQuestionException;
import com.markozajc.akiwrapper.core.utils.JSONUtils;

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
	public QuestionImpl(@Nonnull JSONObject json, @Nonnull Status status) {
		checkMissingQuestion(status);
		this.id = json.getString("questionid");
		this.question = json.getString("question");
		this.step = JSONUtils.getInteger(json, "step").get();
		this.gain = JSONUtils.getDouble(json, "infogain").get();
		this.progression = JSONUtils.getDouble(json, "progression").get();
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
