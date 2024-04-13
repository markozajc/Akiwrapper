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

/**
 * The "core" of interaction with the Akinator's API.<br>
 * Akinator is a 20 questions-type game, which means that the computer
 * algorithmically tries to figure out (using {@link Guess}es) what character,
 * object, or movie the player is thinking about by asking {@link Question}s and
 * getting {@link Answer}s. As mentioned before, this library interfaces with
 * Akinator's online API and does not run the ranking algorithm locally.<br>
 * <br>
 * The library is typically used in the following loop:
 * <ol>
 * <li>The API sends a question, which is retrieved using {@link #getQuestion()} and
 * shown to the player</li>
 * <li>The player gives one of the five {@link Answer}s, which is submitted using
 * {@link #answer(Answer)} and returns the next question</li>
 * <li>Before displaying the next question, {@link #suggestGuess()} is called to
 * fetch the most relevant {@link Guess} (or {@code null} if none is available)</li>
 * </ol>
 * If a {@link Guess} is available, the following should happen:
 * <ol>
 * <li>The guess metadata is shown to the player
 * <ul>
 * <li>If the player confirms the guess, {@link #confirmGuess(Guess)} is called and
 * the game is finished
 * <li>If the player rejects the guess, {@link #rejectLastGuess()} is called and the
 * game continues
 * </ul>
 * </ol>
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

		@Nonnull
		public String getLanguageCode() {
			return this.languageCode;
		}

		@Nonnull
		public Set<Theme> getSupportedThemes() {
			return this.supportedThemes;
		}

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

	@Nullable
	Response getCurrentResponse();

	/**
	 * Returns if the session has been exhausted, which occurs after answering 80
	 * {@link Question}s.<br>
	 * Sending or undoing answers can no longer be done after the session is exhausted.
	 *
	 * @return whether the session is exhausted.
	 */
	boolean isExhausted();

}
