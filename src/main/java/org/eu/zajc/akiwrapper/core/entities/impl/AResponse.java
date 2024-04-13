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

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.exceptions.MalformedResponseException;
import org.eu.zajc.akiwrapper.core.impl.AkiwrapperImpl;
import org.eu.zajc.akiwrapper.core.utils.route.ApiResponse;
import org.json.JSONObject;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public abstract class AResponse implements Response {

	@Nonnull private final AkiwrapperImpl akiwrapper;
	private final int step;
	private final double progression;

	protected AResponse(@Nonnull AkiwrapperImpl akiwrapper, int step, double progression) {
		this.akiwrapper = akiwrapper;
		this.step = step;
		this.progression = progression;
	}

	protected void ensureCurrent() {
		if (this.akiwrapper.getCurrentResponse() != this)
			throw new IllegalStateException("Can only reply to the current response");
	}

	public Response parseNext(@Nonnull ApiResponse<JSONObject> resp) {
		// TODO skip rejected guesses
		// TODO check if status exhausted and return null

		var parsed = fromJson(this.akiwrapper, resp.getBody());
		if (parsed instanceof Guess)
			this.akiwrapper.setLastGuessStep(this.step);

		this.akiwrapper.setCurrentResponse(parsed);
		return parsed;
	}

	@Nonnull
	private Response fromJson(@Nonnull AkiwrapperImpl akiwrapper, JSONObject json) {
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
