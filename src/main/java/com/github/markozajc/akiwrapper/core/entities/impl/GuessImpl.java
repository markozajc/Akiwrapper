//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
 *
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.github.markozajc.akiwrapper.core.entities.impl;

import static com.github.markozajc.akiwrapper.core.utils.JSONUtils.*;
import static java.lang.Double.compare;

import java.net.*;

import javax.annotation.*;

import org.json.JSONObject;

import com.github.markozajc.akiwrapper.core.entities.Guess;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class GuessImpl implements Guess {

	@Nonnull private final String id;
	@Nonnull private final String name;
	@Nullable private final String description;
	@Nullable private final URL image;
	private final double probability;
	private final boolean explicit;

	GuessImpl(@Nonnull String id, @Nonnull String name, @Nullable String description, @Nullable URL image,
			  @Nonnegative double probability, boolean explicit) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
		this.probability = probability;
		this.explicit = explicit;
	}

	@SuppressWarnings("null")
	public static GuessImpl fromJson(@Nonnull JSONObject json) {
		return new GuessImpl(json.getString("id"), json.getString("name"), getDescription(json), getImage(json),
							 getDouble(json, "proba").orElseThrow(), getInteger(json, "corrupt").orElseThrow() == 1);
	}

	@Nullable
	private static String getDescription(@Nonnull JSONObject json) {
		var desc = json.getString("description");
		return "-".equals(desc) ? null : desc;
	}

	@Nullable
	private static URL getImage(@Nonnull JSONObject json) {
		try {
			return "none.jpg".equals(json.getString("picture_path")) ? null
				: new URL(json.getString("absolute_picture_path"));

		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public double getProbability() {
		return this.probability;
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

	@Override
	public int compareTo(Guess o) {
		return compare(o.getProbability(), this.probability);
	}

	@Override
	public boolean isExplicit() {
		return this.explicit;
	}

}
