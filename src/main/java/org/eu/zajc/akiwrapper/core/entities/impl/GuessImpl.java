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

import javax.annotation.*;

import org.eu.zajc.akiwrapper.Akiwrapper;
import org.eu.zajc.akiwrapper.core.entities.Guess;
import org.eu.zajc.akiwrapper.core.exceptions.MalformedResponseException;
import org.json.*;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class GuessImpl implements Guess {

	@Nonnull private final Akiwrapper akiwrapper;
	@Nonnull private final String id;
	@Nonnull private final String name;
	@Nullable private final String pseudonym;
	@Nonnull private final String description;
	@Nullable private final URL image;
	@Nonnull private final String flagPhoto; // the purpose of this is unknown, but it's required for Routes.CHOICE

	GuessImpl(@Nonnull Akiwrapper akiwrapper, @Nonnull String id, @Nonnull String name, @Nullable String pseudonym,
			  @Nonnull String description, @Nullable URL image, @Nonnull String flagPhoto) {
		this.akiwrapper = akiwrapper;
		this.id = id;
		this.name = name;
		this.pseudonym = pseudonym;
		this.description = description;
		this.image = image;
		this.flagPhoto = flagPhoto;
	}

	@SuppressWarnings("null")
	public static GuessImpl fromJson(@Nonnull Akiwrapper akiwrapper, @Nonnull JSONObject json) {
		try {
			return new GuessImpl(akiwrapper, json.getString("id_proposition"), json.getString("name"),
								 getPseudonym(json), json.getString("description_proposition"),
								 new URI(json.getString("photo")).toURL(), json.getString("flag_photo"));

		} catch (JSONException | URISyntaxException | MalformedURLException e) {
			throw new MalformedResponseException(e);
		}
	}

	@Nullable
	private static String getPseudonym(@Nonnull JSONObject json) {
		var pseudo = json.getString("pseudo");
		return "none".equals(pseudo) ? null : pseudo;
	}

	@Override
	public Akiwrapper getAkiwrapper() {
		return this.akiwrapper;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getPseudonym() {
		return this.pseudonym;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public URL getImage() {
		return this.image;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public String getFlagPhoto() {
		return this.flagPhoto;
	}

}
