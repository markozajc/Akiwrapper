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
import static java.util.stream.Collectors.toList;

import java.io.ObjectInputFilter.Status;
import java.util.*;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.exceptions.*;

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
	 * This can be configured in
	 * {@link AkiwrapperBuilder#setFilterProfanity(boolean)}.<br>
	 * Keep in mind that explicit {@link Guess}es can still be returned by
	 * {@link #getGuesses()} or {@link #suggestGuess()}, see {@link Guess#isExplicit()}
	 * for more details on why that is.
	 *
	 * @return whether the profanity filter is enabled.
	 */
	boolean doesFilterProfanity();

	/**
	 * Sends an answer to the current {@link Question} and fetches the next one,
	 * incrementing the current step.<br>
	 * If there are no more questions left, this will return {@code null}. Any subsequent
	 * calls to this method after the question list has been exhausted will throw a
	 * {@link QuestionsExhaustedException}. A call to this method can be undone with
	 * {@link #undoAnswer()}.
	 *
	 * @param answer
	 *            the {@link Answer} to send.
	 *
	 * @return the next {@link Question} or {@code null} if there are no questions left.
	 *
	 * @throws QuestionsExhaustedException
	 *             if the session has exhausted all questions (when
	 *             {@link #isExhausted()} returns {@code true}).
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}.
	 *
	 * @see #undoAnswer()
	 */
	@Nullable
	Question answer(Answer answer);

	/**
	 * Goes one question backwards, undoing the previous {@link #answer(Answer)} call.
	 * For example, if {@link #getQuestion()} returns a question on step {@code 5},
	 * calling this command will make {@link #getQuestion()} return the question from
	 * step {@code 4}. You can call this as many times as you want, until you reach step
	 * {@code 0}<br>
	 * If this method is called on step {@code 0}, {@link UndoOutOfBoundsException} is
	 * thrown. If this method is called after questions have been exhausted,
	 * {@link QuestionsExhaustedException} is thrown.
	 *
	 * @return the previous {@link Question}.
	 *
	 * @throws UndoOutOfBoundsException
	 *             if the session has exhausted all questions (when
	 *             {@link #getQuestion()} returns {@code null}.
	 * @throws QuestionsExhaustedException
	 *             if the session has exhausted all questions (when
	 *             {@link #isExhausted()} returns {@code true}).
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}.
	 *
	 * @see #answer(Answer)
	 */
	@Nonnull
	Question undoAnswer();

	/**
	 * Returns a probability-sorted (the lower the index, the higher the probability) and
	 * unmodifiable list of <b>all relevant</b> Akinator's guesses, or an empty list if
	 * there are no guesses.
	 *
	 * @return a sorted list of {@link Guess}es.
	 *
	 * @see Akiwrapper#getGuesses(int)
	 *
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}.
	 */
	@Nonnull
	default List<Guess> getGuesses() {
		return getGuesses(0);
	}

	/**
	 * Returns a probability-sorted (the lower the index, the higher the probability) and
	 * unmodifiable list of <b>the first N</b> Akinator's guesses, or an empty list if
	 * there are no guesses.<br>
	 * Note that the API may return less guesses than requested, but not more.
	 *
	 * @param count
	 *            the number of {@link Guess}es to get.
	 *
	 * @return a sorted list of {@link Guess}es.
	 *
	 * @see Akiwrapper#getGuesses()
	 */
	@Nonnull
	List<Guess> getGuesses(int count);

	/**
	 * Returns the current {@link Question} or {@code null} if the question list has been
	 * exhausted.<br>
	 * You can answer it with {@link #answer(Answer)}.
	 *
	 * @return the current question.
	 */
	@Nullable
	Question getQuestion();

	/**
	 * Returns the current step / question number. Keep in mind that this value is
	 * zero-based, so the first question is on step {@code 0}.
	 *
	 * @return the current step
	 */
	int getStep();

	/**
	 * Returns if the session has been exhausted, which occurs after answering 80
	 * {@link Question}s.<br>
	 * Sending or undoing answers can no longer be done after the session is exhausted.
	 *
	 * @return whether the session is exhausted.
	 */
	boolean isExhausted();

	/**
	 * @param probability
	 *            probability threshold
	 *
	 * @return a list of Akinator's guesses with probability above the specified
	 *         probability threshold.
	 *
	 * @see Akiwrapper#getGuesses()
	 *
	 * @deprecated Use {@link #suggestGuess()} instead
	 */
	@Nonnull
	@SuppressWarnings("null")
	@Deprecated(since = "1.6", forRemoval = true)
	default List<Guess> getGuessesAboveProbability(double probability) {
		return getGuesses().stream().filter(g -> g.getProbability() > probability).collect(toList());
	}

	/**
	 * <b>Important:</b> this method mutates the session's state, which means that
	 * subsequent calls to it will not yield the same result.<br>
	 * <br>
	 * Provides a likely {@link Guess} for the current session or {@code null} if none
	 * are available. This method should be called after every call to
	 * {@link #answer(Answer)} to let the player review Akinator's guesses - the internal
	 * logic replicates how Akinator works, only returning a {@link Guess} when
	 * Akinator's confidence is high enough. It will also space suggestions out evenly,
	 * always returning {@code null} for a number of steps after the last suggestion.
	 *
	 * @return a suggested {@link Guess} or {@code null} if none are available
	 */
	@Nullable
	Guess suggestGuess();

	/**
	 * Confirms a {@link Guess}. While this doesn't affect the current session, because
	 * it's called at the very end, it likely affects Akinator's algorithm and associates
	 * the taken answer route with the confirmed guess, thus improving the game for
	 * everyone.
	 *
	 * @param guess
	 *            the {@link Guess} to confirm.
	 *
	 * @apiNote Do not use this method in automated tests, as it introduces faulty data
	 *          into Akinator's database, dulling the ranking algorithm.
	 */
	void confirmGuess(@Nonnull Guess guess);

	/**
	 * <b>Note:</b> this method should only be called immediately after
	 * {@link #suggestGuess()} returns a {@link Guess} - don't send or undo answers
	 * before calling it.<br>
	 * <br>
	 * Rejects the previously suggested {@link Guess} and provides an updated
	 * {@link Question} (or {@code null} if the session is exhausted). The question is on
	 * the same step that the last call to {@link #answer(Answer)} was, but might have
	 * different text.
	 *
	 * @return the replacement {@link Question}.
	 *
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}. Throwing this
	 *             is suppressed if the session is already exhausted.
	 *
	 * @apiNote Do not use this method in automated tests, as it introduces faulty data
	 *          into Akinator's database, dulling the ranking algorithm.
	 */
	@Nullable
	Question rejectLastGuess();

}
