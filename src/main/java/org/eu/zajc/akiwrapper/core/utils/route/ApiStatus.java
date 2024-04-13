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
package org.eu.zajc.akiwrapper.core.utils.route;

import javax.annotation.Nonnull;

import org.json.JSONObject;
import org.jsoup.nodes.Element;

/**
 * An interface used to represent API call's completion status.
 *
 * @author Marko Zajc
 */
public enum ApiStatus {

	/**
	 * Everything is OK, you may continue normally.
	 */
	OK("OK", false),

	/**
	 * The status is non-erroneous and the questions have been exhausted.
	 */
	QUESTIONS_EXHAUSTED("SOUNDLIKE", false),

	/**
	 * The action has not completed due to an error.
	 */
	ERROR("KO", true),

	/**
	 * Unknown status (should not ever occur under normal circumstances), indicates that
	 * the status level doesn't match any of the known ones.
	 */
	UNKNOWN("", true);

	private final String name;
	private final boolean erroneous;

	ApiStatus(String name, boolean erroneous) {
		this.name = name;
		this.erroneous = erroneous;
	}

	/**
	 * @return whether or not an exception should be thrown when this status is received
	 */
	public boolean isErroneous() {
		return this.erroneous;
	}

	/**
	 * @return this level's name as provided by the API
	 */
	@Override
	public String toString() {
		return this.name;
	}

	@Nonnull
	@SuppressWarnings({ "javadoc", "null" }) // internal impl
	public static ApiStatus fromJson(@Nonnull JSONObject json) {
		return json.has("completion") ? fromString(json.getString("completion")) : OK;
	}

	@Nonnull
	@SuppressWarnings({ "javadoc", "null" }) // internal impl
	public static ApiStatus fromHtml(@Nonnull Element gameRoot) {
		// determines error status based on the akitude. this isn't the most stable, but it's
		// more reliable than checking localized strings or document structure
		return gameRoot.getElementsByClass("akinator-body")
			.stream()
			.findAny()
			.map(e -> e.child(0))
			.map(e -> e.attr("alt"))
			.map(e -> e.equals("akitude-surprise") ? ERROR : OK)
			.orElse(OK);
	}

	@Nonnull
	@SuppressWarnings("javadoc") // internal impl
	public static ApiStatus fromString(@Nonnull String completion) {
		for (ApiStatus iteratedLevel : ApiStatus.values())
			if (completion.toUpperCase().startsWith(iteratedLevel.toString()))
				return iteratedLevel;

		return UNKNOWN;
	}

}
