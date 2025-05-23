//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2025 Marko Zajc
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

import static org.eu.zajc.akiwrapper.core.utils.route.Status.QUESTIONS_EXHAUSTED;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.exceptions.MalformedResponseException;
import org.eu.zajc.akiwrapper.core.utils.route.Response;
import org.json.JSONObject;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal
public abstract class AbstractQuery implements Query {

	@Nonnull private final AkiwrapperImpl akiwrapper;
	private final int step;
	private final double progression;

	protected AbstractQuery(@Nonnull AkiwrapperImpl akiwrapper, int step, double progression) {
		this.akiwrapper = akiwrapper;
		this.step = step;
		this.progression = progression;
	}

	protected void ensureCurrent() {
		if (this.akiwrapper.getCurrentQuery() != this)
			throw new IllegalStateException("Can only reply to the current query");
	}

	public Query parseNext(@Nonnull Response<JSONObject> resp) {
		var parsed = resp.getStatus() == QUESTIONS_EXHAUSTED ? null : fromJson(this.akiwrapper, resp.getBody());
		if (parsed instanceof Guess)
			this.akiwrapper.setLastGuessStep(this.step);

		this.akiwrapper.setCurrentResponse(parsed);
		return parsed;
	}

	@Nonnull
	private static Query fromJson(@Nonnull AkiwrapperImpl akiwrapper, JSONObject json) {
		if (json.has("question"))
			return QuestionImpl.fromJson(akiwrapper, json);
		else if (json.has("name_proposition"))
			return GuessImpl.fromJson(akiwrapper, json);
		else
			throw new MalformedResponseException();
	}

	@Override
	public AkiwrapperImpl getAkiwrapper() {
		return this.akiwrapper;
	}

	@Override
	public int getStep() {
		return this.step;
	}

	@Override
	public double getProgression() {
		return this.progression;
	}

}
