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
package org.eu.zajc.akiwrapper;

import static java.util.Collections.unmodifiableSet;

import java.util.*;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.exceptions.LanguageThemeCombinationException;

/**
 * The "core" of interaction with the Akinator's API.<br>
 * Akinator is a 20 questions-type game, which means that the computer
 * algorithmically tries to figure out (using {@link Guess}es) what character,
 * object, or movie the player is thinking about by asking {@link Question}s and
 * getting {@link Answer}s. This library interfaces with Akinator's online API and
 * does not run the ranking algorithm locally.<br>
 * <br>
 * The library is typically used in the following loop:
 * <ol>
 * <li>An {@link Akiwrapper} object is constructed using
 * {@link AkiwrapperBuilder}</li>
 * <li>A {@link Query} from {@link #getCurrentQuery()} is displayed to the user.</li>
 * <li>The program decides based on the type of the {@link Query}:</li>
 * <ol>
 * <li>If it's a {@link Guess}, it's shown to the user and responded to with either
 * {@link Guess#confirm()} or {@link Guess#reject()}. Confirming a guess is the lose
 * condition and ends the game.</li>
 * <li>If it's a {@link Question}, it's shown to the user and responded to with
 * either {@link Question#answer(Answer)} or {@link Question#undoAnswer()}.</li>
 * <li>If it's {@code null}, Akinator has no more queries. This is the win
 * condition.</li>
 * </ol>
 * </ol>
 * Queries can either be retrieved from return values of interaction methods
 * ({@link Question#answer(Answer)}, {@link Question#undoAnswer()}, etc.) or from
 * {@link #getCurrentQuery()}. Keep in mind that you can't respond to the same query
 * more than once.<br>
 * An example of this library in action can be viewed in the {@code examples/}
 * directory of the repository.
 *
 * @author Marko Zajc
 */
public interface Akiwrapper {

	/**
	 * The language determines how {@link Question}s and {@link Guess}es are localized.
	 *
	 * @author Marko Zajc
	 */
	@SuppressWarnings("javadoc")
	public enum Language {

		ENGLISH("en", Theme.CHARACTER, Theme.OBJECT, Theme.ANIMAL),
		ARABIC("ar", Theme.CHARACTER),
		CHINESE("cn", Theme.CHARACTER),
		GERMAN("de", Theme.CHARACTER, Theme.ANIMAL),
		SPANISH("es", Theme.CHARACTER, Theme.ANIMAL),
		FRENCH("fr", Theme.CHARACTER, Theme.OBJECT, Theme.ANIMAL),
		HEBREW("il", Theme.CHARACTER),
		ITALIAN("it", Theme.CHARACTER, Theme.ANIMAL),
		JAPANESE("jp", Theme.CHARACTER, Theme.ANIMAL),
		KOREAN("kr", Theme.CHARACTER),
		DUTCH("nl", Theme.CHARACTER),
		POLISH("pl", Theme.CHARACTER),
		PORTUGESE("pt", Theme.CHARACTER),
		RUSSIAN("ru", Theme.CHARACTER),
		TURKISH("tr", Theme.CHARACTER),
		INDONESIAN("id", Theme.CHARACTER);

		@Nonnull private final String languageCode;
		@Nonnull private final Set<Theme> supportedThemes;

		@SuppressWarnings("null")
		Language(@Nonnull String languageCode, @Nonnull Theme supportedTheme, @Nonnull Theme... otherSupportedThemes) {
			this.languageCode = languageCode;
			this.supportedThemes = unmodifiableSet(EnumSet.of(supportedTheme, otherSupportedThemes));
		}

		/**
		 * @return the two-character language code used in akinator.com subdomains.
		 */
		@Nonnull
		public String getLanguageCode() {
			return this.languageCode;
		}

		/**
		 * Returns an unmodifiable {@link Set} of {@link Theme}s supported by this
		 * {@link Language}. Attempting to construct an {@link Akiwrapper} instance with an
		 * incompatible {@link Language}-{@link Theme} combination will throw a
		 * {@link LanguageThemeCombinationException}.
		 *
		 * @return supported {@link Theme}s.
		 */
		@Nonnull
		public Set<Theme> getSupportedThemes() {
			return this.supportedThemes;
		}

		/**
		 * Checks if this {@link Language} supports a given {@link Theme}. Attempting to
		 * construct an {@link Akiwrapper} instance with an incompatible
		 * {@link Language}-{@link Theme} combination will throw a
		 * {@link LanguageThemeCombinationException}.
		 *
		 * @param theme
		 *            the {@link Theme} to check support for.
		 *
		 * @return whether the theme is supported.
		 */
		public boolean isThemeSupported(@Nonnull Theme theme) {
			return this.supportedThemes.contains(theme);
		}

	}

	/**
	 * Represents the theme (sometimes called subject) of the game. This determines what
	 * kind of {@link Question}s and {@link Guess}es will be provided. Please note that
	 * while all {@link Language}s support {@link Theme#CHARACTER}, but other themes
	 * might not be supported. Call {@link Language#getSupportedThemes()} for a list of
	 * supported themes.
	 *
	 * @author Marko Zajc
	 */
	@SuppressWarnings("javadoc")
	public enum Theme {

		CHARACTER(1),
		OBJECT(2),
		ANIMAL(14);

		private final int id;

		Theme(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

	}

	/**
	 * An enum used to represent an answer to Akinator's question.
	 */
	public enum Answer {

		/**
		 * Answers with "yes" (positive)
		 */
		YES(0),

		/**
		 * Answers with "no" (negative)
		 */
		NO(1),

		/**
		 * Skips this question (neutral answer)
		 */
		DONT_KNOW(2),

		/**
		 * Answers with "probably" (almost positive)
		 */
		PROBABLY(3),

		/**
		 * Answers with "probably not" (almost negative)
		 */
		PROBABLY_NOT(4);

		private final int id;

		Answer(int id) {
			this.id = id;
		}

		/**
		 * @return this answer's ID which is passed to the API
		 */
		public int getId() {
			return this.id;
		}

	}

	/**
	 * Returns the {@link Language} used. Akinator returns localized {@link Question}s
	 * and {@link Guess}es depending by this. Please note that while all
	 * {@link Language}s support {@link Theme#CHARACTER}, but other themes might not be
	 * supported. Call {@link Language#getSupportedThemes()} for a list of supported
	 * themes.
	 *
	 * @return the language.
	 */
	@Nonnull
	Language getLanguage();

	/**
	 * Returns the {@link Theme} used. Akinator returns different kinds of
	 * {@link Question}s and {@link Guess}es depending by this.
	 *
	 * @return server's guess type.
	 */
	@Nonnull
	Theme getTheme();

	/**
	 * Returns whether or not Akinator has been instructed to filter out explicit
	 * content.<br>
	 * This can be configured in {@link AkiwrapperBuilder#setFilterProfanity(boolean)}.
	 *
	 * @return whether the profanity filter is enabled.
	 */
	boolean doesFilterProfanity();

	/**
	 * Returns the current {@link Query}. This will contain the initial {@link Query}
	 * after the {@link Akiwrapper} instance is constructed, and will get updated as
	 * Queries are responded to - the {@link Query} returned by interaction methods
	 * ({@link Question#answer(Answer)}, {@link Question#undoAnswer()}, etc.) will be the
	 * same as the {@link Query} returned by this method.<br>
	 * After the game ends, either by reaching question 80 or by confirming a
	 * {@link Guess}, this will return {@code null}
	 *
	 * @return the current {@link Query} or {@code null} if the game has ended.
	 */
	@Nullable
	Query getCurrentQuery();

	/**
	 * Returns if the game has ended, which occurs after answering 80 {@link Question}s
	 * or calling {@link Guess#confirm()}.<br>
	 * Sending or undoing answers can no longer be done after the game has ended.
	 *
	 * @return whether the game has ended.
	 *
	 * @deprecated Check if {@link #getCurrentQuery()} is {@code null} instead
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	boolean isExhausted();

}
