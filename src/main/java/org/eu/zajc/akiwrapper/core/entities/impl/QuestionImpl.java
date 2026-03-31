//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2026 Marko Zajc
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.eu.zajc.akiwrapper.core.entities.impl;

import static org.eu.zajc.akiwrapper.core.utils.route.Routes.*;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.Akiwrapper.Answer;
import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.eu.zajc.akiwrapper.core.utils.Utilities;
import org.json.*;
import org.jsoup.nodes.Element;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal
public class QuestionImpl extends AbstractQuery implements Question {

	@Nonnull private final String question;

	private QuestionImpl(@Nonnull AkiwrapperImpl akiwrapper, int step, double progression, @Nonnull String question) {
		super(akiwrapper, step, progression);
		this.question = question;
	}

	@Nonnull
	@SuppressWarnings("null")
	public static QuestionImpl fromJson(@Nonnull AkiwrapperImpl akiwrapper, @Nonnull JSONObject json) {
		try {
			return new QuestionImpl(akiwrapper, Utilities.parseInt(json.getString("step")),
									Utilities.parseDouble(json.getString("progression")), json.getString("question"));

		} catch (JSONException e) {
			throw new MalformedResponseException(e);
		}
	}

	@SuppressWarnings("null")
	public static QuestionImpl fromHtml(@Nonnull AkiwrapperImpl akiwrapper, @Nonnull Element gameRoot) {
		var question = Optional.ofNullable(gameRoot.getElementById("question-label"))
			.map(Element::wholeOwnText)
			.orElseThrow(MalformedResponseException::new);

		var step = Utilities.parseInt(Optional.ofNullable(gameRoot.getElementById("step-info"))
			.map(Element::wholeOwnText)
			.orElseThrow(MalformedResponseException::new)) - 1;

		return new QuestionImpl(akiwrapper, step, 0, question);
	}

	@Override
	public Query answer(Answer answer) {
		try {
			this.getAkiwrapper().getInteractionLock().lock();
			this.ensureCurrent();

			var resp = ANSWER.createRequest(this.getAkiwrapper())
				.parameter(PARAMETER_STEP, getStep())
				.parameter(PARAMETER_PROGRESSION, getProgression())
				.parameter(PARAMETER_ANSWER, answer.getId())
				.parameter(PARAMETER_STEP_LAST_PROPOSITION, this.getAkiwrapper().getLastGuessStep())
				.retrieveJson();
			return parseNext(resp);

		} finally {
			this.getAkiwrapper().getInteractionLock().unlock();
		}
	}

	@Override
	public Question undoAnswer() {
		try {
			this.getAkiwrapper().getInteractionLock().lock();
			this.ensureCurrent();

			if (getStep() == 0)
				throw new UndoOutOfBoundsException();

			var resp = CANCEL_ANSWER.createRequest(this.getAkiwrapper())
				.parameter(PARAMETER_STEP, getStep())
				.parameter(PARAMETER_PROGRESSION, getProgression())
				.retrieveJson();

			var next = parseNext(resp);
			if (next instanceof Question)
				return (Question) next;
			else
				throw new MalformedResponseException();

		} finally {
			this.getAkiwrapper().getInteractionLock().unlock();
		}
	}

	@Override
	public String getText() {
		return this.question;
	}

}
