//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
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

import static org.eu.zajc.akiwrapper.core.utils.JSONUtils.*;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.entities.Question;
import org.json.JSONObject;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class QuestionImpl implements Question {

	@Nonnull private final String id;
	@Nonnull private final String question;
	private final int step;
	private final double gain;
	private final double progression;

	private QuestionImpl(@Nonnull String id, @Nonnull String question, @Nonnegative int step, @Nonnegative double gain,
						 @Nonnegative double progression) {
		this.id = id;
		this.question = question;
		this.step = step;
		this.gain = gain;
		this.progression = progression;
	}

	@SuppressWarnings("null")
	public static QuestionImpl fromJson(@Nonnull JSONObject json) {
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
