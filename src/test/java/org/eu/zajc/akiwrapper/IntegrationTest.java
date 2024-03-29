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

import static java.lang.String.format;
import static org.eu.zajc.akiwrapper.Akiwrapper.Answer.YES;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.Akiwrapper.Answer;
import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.entities.Server.*;
import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

	private static final String FETCHING_GUESSES_THROWS = "Fetching guesses throws";
	private static final String SERVER_GUESSTYPE_NO_MATCH =
		"The wanted and actual guess type of the server don't match.";
	private static final String SERVER_LANGUAGE_NO_MATCH = "The wanted and actual language of the server don't match.";
	private static final String QUESTION_CURRENT_NO_MATCH =
		"Current question does not match the one just returned by the API.";
	private static final String QUESTION_WRONG_STEP = "Question was on an unexpected step.";
	private static final String QUESTION_EMPTY = "Question mustn't be empty.";
	private static final String QUESTION_NULL = "Question was null";
	private static final String QUESTION_INITIAL_NO_MATCH =
		"Initial question does not match the one after an equal amount of answers and undoes.";

	@ParameterizedTest
	@MethodSource("generateTestAkiwrapper")
	void testAkiwrapper(@Nonnull Language language, @Nonnull GuessType guessType) {
		Logger log = getLogger(format("%s-%s", language, guessType));
		try {
			log.info("Establishing connection");
			Akiwrapper api;
			try {
				api = new AkiwrapperBuilder().setLanguage(language).setGuessType(guessType).build();
			} catch (ServerNotFoundException e) {
				abort("Current combination not supported, server wasn't found.");
				return;
			}

			Question initialQuestion = api.getQuestion();
			testInitialState(log, api, initialQuestion, language, guessType);
			int expectedState = testAnswering(log, api);
			testUndo(log, api, initialQuestion, expectedState);
			testExhaustion(log, api);
		} catch (TestAbortedException e) {
			throw e;

		} catch (Exception e) {
			e.printStackTrace();

			if (e instanceof AkinatorException)
				log.info(((AkinatorException) e).getDebugInformation());

			fail("Got an exception running the test");

		}
	}

	private static void testInitialState(@Nonnull Logger log, @Nonnull Akiwrapper api,
										 @Nullable Question initialQuestion, @Nonnull Language language,
										 @Nonnull GuessType guessType) {
		log.info("Asserting the current state.");
		checkQuestion(0, initialQuestion);
		assertDoesNotThrow(() -> api.getGuesses(), FETCHING_GUESSES_THROWS);
		assertEquals(language, api.getServer().getLanguage(), SERVER_LANGUAGE_NO_MATCH);
		assertEquals(guessType, api.getServer().getGuessType(), SERVER_GUESSTYPE_NO_MATCH);
		log.trace("API server URL: {}", api.getServer().getUrl());
	}

	private static int testAnswering(@Nonnull Logger log, @Nonnull Akiwrapper api) {
		log.info("Advancing {} steps (one time for each possible answer).", Answer.values().length);
		int expectedState = 0;
		for (Answer answer : Answer.values()) {
			log.info("Answering with {} and checking the state (step={}).", answer.name(), api.getStep());
			Question newQuestion = api.answer(answer);
			assertEquals(newQuestion, api.getQuestion(), QUESTION_CURRENT_NO_MATCH);
			expectedState++;
			assertEquals(expectedState, api.getStep());
			checkQuestion(expectedState, api.getQuestion());
			checkGuessCount(log, api);
		}

		log.info("Asserting the current state.");
		fetchAndDebugGuesses(log, api);
		return expectedState;
	}

	private static void testUndo(@Nonnull Logger log, @Nonnull Akiwrapper api, @Nullable Question initialQuestion,
								 int initialExpectedState) {
		log.info("Advancing -{} steps (using undo).", Answer.values().length);
		int expectedState = initialExpectedState;
		for (int i = 0; i < Answer.values().length; i++) {
			log.info("Undoing a step and checking the state (step={}).", api.getStep());
			Question undoneQuestion = api.undoAnswer();
			assertEquals(undoneQuestion, api.getQuestion(), QUESTION_CURRENT_NO_MATCH);
			expectedState--;
			assertEquals(expectedState, api.getStep());
			checkQuestion(expectedState, api.getQuestion());
			checkGuessCount(log, api);
		}

		log.info("Asserting the current state.");
		assertThrows(UndoOutOfBoundsException.class, () -> api.undoAnswer());
		checkQuestion(0, api.getQuestion());
		assertDoesNotThrow(() -> api.getGuesses(), FETCHING_GUESSES_THROWS);
		Question currentQuestion = api.getQuestion();
		if (initialQuestion != null && currentQuestion != null)
			assertEquals(initialQuestion.getQuestion(), currentQuestion.getQuestion(), QUESTION_INITIAL_NO_MATCH);
		else
			fail("initialQuestion or currentQuestion were somehow null");
		// using this syntax instead of assertNotNull to please null analysis of @Nullable
	}

	private static void testExhaustion(@Nonnull Logger log, @Nonnull Akiwrapper api) {
		log.info("Exhausting questions by answering YES to all.");
		var lastQuestion = api.getQuestion();
		assertNotNull(lastQuestion, "Current question is already null");

		int i = api.getStep();
		while (true) {
			checkQuestion(i, api.getQuestion());
			assertEquals(i, api.getStep());

			var question = api.answer(YES);
			if (question == null) {
				log.info("Ran out at step {}.", api.getStep());
				break;

			} else {
				log.info("Exhausting questions (step={})", api.getStep());
				checkQuestion(++i, question);
			}

			if (i > 80)
				fail("Got over step 80, API must have changed. Ensure there are no side effects and find the new limit.");
		}

		log.info("Asserting the current state.");
		assertThrows(QuestionsExhaustedException.class, () -> api.answer(YES));
		assertThrows(QuestionsExhaustedException.class, () -> api.undoAnswer());
		assertDoesNotThrow(() -> api.getGuesses());

		fetchAndDebugGuesses(log, api);
	}

	private static void checkGuessCount(Logger log, Akiwrapper api) {
		for (int i = 1; i < 5; i++) {
			log.info("Fetching {} guesses.", i);
			assertTrue(api.getGuesses(i).size() <= i, "Got more guesses than requested from the API");
		}
	}

	private static void fetchAndDebugGuesses(Logger log, Akiwrapper api) {
		log.info("Fetching all guesses.", Answer.values().length);
		List<Guess> guesses = api.getGuesses();
		debugGuesses(log, guesses);
	}

	private static void debugGuesses(Logger log, List<Guess> guesses) {
		log.info("There are {} guesses.", guesses.size());
		for (Guess guess : guesses) {
			log.info("{} - {}", guess.getProbability(), guess.getName());
		}
	}

	private static void checkQuestion(int expectedState, @Nullable Question question) {
		if (question == null) {
			fail(QUESTION_NULL);
		} else {
			assertFalse(question.getQuestion().isEmpty(), QUESTION_EMPTY);
			assertEquals(expectedState, question.getStep(), QUESTION_WRONG_STEP);
		}
	}

	private static Stream<Arguments> generateTestAkiwrapper() {
		Arguments[] arguments = new Arguments[Language.values().length * GuessType.values().length];
		int i = 0;
		for (Language lang : Language.values())
			for (GuessType guessType : GuessType.values()) {
				arguments[i] = Arguments.of(lang, guessType);
				i++;
			}
		return Stream.of(arguments);
	}

}
