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

import java.net.*;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.Akiwrapper;
import org.eu.zajc.akiwrapper.core.entities.Question;
import org.eu.zajc.akiwrapper.core.exceptions.MalformedResponseException;
import org.eu.zajc.akiwrapper.core.utils.Utilities;
import org.json.*;
import org.jsoup.nodes.Element;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class QuestionImpl implements Question {

	@Nonnull private final Akiwrapper akiwrapper;
	@Nonnull private final String question;
	@Nonnull private final URL akitude;
	private final int step;
	private final double progression;

	private QuestionImpl(@Nonnull Akiwrapper akiwrapper, @Nonnull String question, int step, double progression,
						 @Nonnull URL akitude) {
		this.akiwrapper = akiwrapper;
		this.question = question;
		this.step = step;
		this.progression = progression;
		this.akitude = akitude;
	}

	@SuppressWarnings("null")
	public static QuestionImpl fromJson(@Nonnull Akiwrapper akiwrapper, @Nonnull JSONObject json) {
		try {
			return new QuestionImpl(akiwrapper, json.getString("question"), Utilities.parseInt(json.getString("step")),
									Utilities.parseDouble(json.getString("progression")),
									new URI("https://en.akinator.com/assets/img/akitudes_670x1096/" +
										json.getString("akitude")).toURL());

		} catch (JSONException | MalformedURLException | URISyntaxException e) {
			throw new MalformedResponseException(e);
		}
	}

	@SuppressWarnings("null")
	public static QuestionImpl fromHtml(@Nonnull Akiwrapper akiwrapper, @Nonnull Element gameRoot) {
		URL akitude;
		try {
			akitude = new URI("https://en.akinator.com" + Optional.ofNullable(gameRoot.getElementById("akitude"))
				.map(e -> e.attr("src"))
				.orElseThrow(MalformedResponseException::new)).toURL();

			var question = Optional.ofNullable(gameRoot.getElementById("question-label"))
				.map(Element::text)
				.orElseThrow(MalformedResponseException::new);

			var step = Utilities.parseInt(Optional.ofNullable(gameRoot.getElementById("step-info"))
				.map(Element::text)
				.orElseThrow(MalformedResponseException::new)) - 1;

			return new QuestionImpl(akiwrapper, question, step, 0, akitude);

		} catch (MalformedURLException | URISyntaxException e) {
			throw new MalformedResponseException(e);
		}
	}

	@Override
	public Akiwrapper getAkiwrapper() {
		return this.akiwrapper;
	}

	@Override
	public String getText() {
		return this.question;
	}

	@Override
	public int getStep() {
		return this.step;
	}

	@Override
	public double getProgression() {
		return this.progression;
	}

	@Override
	public URL getAkitude() {
		return this.akitude;
	}

}
