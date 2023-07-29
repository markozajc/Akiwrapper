package com.github.markozajc.akiwrapper.core.entities.impl.immutable;

import static com.github.markozajc.akiwrapper.core.utils.JSONUtils.*;

import javax.annotation.*;

import org.json.JSONObject;

import com.github.markozajc.akiwrapper.core.entities.Question;

@SuppressWarnings("javadoc") // internal impl
public class QuestionImpl implements Question {

	@Nonnull private final String id;
	@Nonnull private final String question;
	@Nonnegative private final int step;
	@Nonnegative private final double gain;
	@Nonnegative private final double progression;

	private QuestionImpl(@Nonnull String id, @Nonnull String question, @Nonnegative int step, @Nonnegative double gain,
						@Nonnegative double progression) {
		this.id = id;
		this.question = question;
		this.step = step;
		this.gain = gain;
		this.progression = progression;
	}

	@SuppressWarnings("null")
	public static QuestionImpl from(@Nonnull JSONObject json) {
		return new QuestionImpl(json.getString("questionid"), json.getString("question"),
								getInteger(json, "step").orElseThrow(), getDouble(json, "infogain").orElseThrow(),
								getDouble(json, "progression").orElseThrow());
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
