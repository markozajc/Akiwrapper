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
package org.eu.zajc.akiwrapper.core.entities;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.utils.Servers;

import kong.unirest.UnirestInstance;

/**
 * A representation of an API server. Each server has a predefined {@link Language}
 * and {@link GuessType}.
 *
 * @author Marko Zajc
 */
public interface Server {

	/**
	 * A language specific to a {@link Server}. The server will return localized
	 * {@link Question}s and {@link Guess}es depending on its language.
	 *
	 * @author Marko Zajc
	 */
	@SuppressWarnings("javadoc")
	public enum Language {

		ARABIC("ar"),
		CHINESE("cn"),
		DUTCH("nl"),
		ENGLISH("en"),
		FRENCH("fr"),
		GERMAN("de"),
		HEBREW("il"),
		INDONESIAN("id"),
		ITALIAN("it"),
		JAPANESE("jp"),
		KOREAN("kr"),
		POLISH("pl"),
		PORTUGUESE("pt"),
		RUSSIAN("ru"),
		SPANISH("es"),
		TURKISH("tr");

		private final String id;

		Language(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}

		@Nullable
		public static Language getById(@Nonnull String id) {
			for (Language language : Language.values())
				if (id.equals(language.getId()))
					return language;
			return null;
		}
	}

	/**
	 * Represents the server's guess type (also referred to as the subject or theme).
	 * This decides what kind of things the {@link Server}'s {@link Guess}es will
	 * represent. While the name might imply that this affects only guess content, it
	 * also affects {@link Question}s.<br>
	 *
	 * @author Marko Zajc
	 */
	@SuppressWarnings("javadoc")
	public enum GuessType {

		ANIMAL(14),
		MOVIE_TV_SHOW(13),
		PLACE(7),
		CHARACTER(1),
		OBJECT(2);

		private final int id;

		GuessType(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		@Nullable
		public static GuessType getById(int id) {
			for (GuessType guessType : GuessType.values())
				if (id == guessType.getId())
					return guessType;
			return null;
		}

	}

	/**
	 * Server's base URL. As the people behind Akinator tend to mix up their servers and
	 * the API in general, this should only fetch values from the server-listing endpoint
	 * (which is done in {@link Servers#getServers(UnirestInstance)}. The host is a valid
	 * URL, complete with the path to the endpoint.<br>
	 * Example: {@code https://srv3.akinator.com:9331/ws}
	 *
	 * @return server's host.
	 */
	@Nonnull
	String getUrl();

	/**
	 * Returns this {@link Server}'s {@link Language}. The server will return localized
	 * {@link Question}s and {@link Guess}es depending on its language.
	 *
	 * @return server's language.
	 */
	@Nonnull
	Language getLanguage();

	/**
	 * Returns this server's {@link GuessType}. The server will be returning guesses
	 * based on that type (also referred to as the subject).
	 *
	 * @return server's guess type.
	 */
	@Nonnull
	GuessType getGuessType();

}
