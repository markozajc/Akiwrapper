package com.markozajc.akiwrapper.core.entities.impl.immutable;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.CompletionStatus;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.CompletionStatus.Level;
import com.markozajc.akiwrapper.core.exceptions.MissingQuestionException;
import com.markozajc.akiwrapper.core.utils.JSONUtils;

/**
 * An implementation of {@link Question}.
 * 
 * @author Marko Zajc
 */
public class QuestionImpl implements Question {

	private final String id;
	private final String question;

	private final int step;

	private final double gain;
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
	 * @throws MissingQuestionException
	 *             if the message is missing (no more messages left to answer, get
	 *             the final guesses)
	 */
	public QuestionImpl(String id, String question, int step, double gain, double progression, CompletionStatus status)
			throws MissingQuestionException {
		if (status.getLevel().equals(Level.WARNING) && status.getReason().toLowerCase().equals("no question"))
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
	 *            {@link Route#NEW_SESSION} > {@link JSONObject} parameters)
	 * @param status
	 *            call completion status
	 * @throws MissingQuestionException
	 *             if the message is missing (no more messages left to answer, get
	 *             the final guesses)
	 */
	public QuestionImpl(JSONObject json, CompletionStatus status) throws MissingQuestionException {
		this(json.getString("questionid"), json.getString("question"),
				JSONUtils.getInteger(json, "step").intValue(),
				JSONUtils.getDouble(json, "infogain").doubleValue(),
				JSONUtils.getDouble(json, "progression").doubleValue(), status);
	}

	@Override
	public double getProgression() {
		return progression;
	}

	@Override
	public int getStep() {
		return step;
	}

	@Override
	public double getGain() {
		return this.gain; // TODO check if this is real
	}

	@Override
	public String getQuestion() {
		return question;
	}

	@Override
	public String getId() {
		return this.id;
	}

}
