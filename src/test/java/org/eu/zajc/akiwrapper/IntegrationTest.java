//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2025 Marko Zajc
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
import static org.junit.jupiter.api.Assumptions.abort;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.stream.Stream;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.Akiwrapper.*;
import org.eu.zajc.akiwrapper.core.entities.Question;
import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.eu.zajc.akiwrapper.core.utils.UnirestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

import kong.unirest.core.*;

class IntegrationTest {

	private static final String THEME_MISMATCH = "The requested and actual theme don't match";
	private static final String LANGUAGE_MISMATCH = "The requested and actual language don't match";
	private static final String QUESTION_CURRENT_NO_MATCH =
		"Current question does not match the one returned by the API";
	private static final String QUESTION_WRONG_STEP = "Question is on an unexpected step";
	private static final String QUESTION_EMPTY = "Question text is empty";
	private static final String QUESTION_NULL = "Question is null";
	private static final String QUESTION_INITIAL_NO_MATCH =
		"Initial question text does not match the one after an equal amount of answers and undoes";

	private static final UnirestInstance UNIREST = UnirestUtils.configureInstance(Unirest.spawnInstance());
	static {
		UNIREST.config().connectTimeout(300000).requestTimeout(300000);
	}

	@ParameterizedTest
	@MethodSource("generateTestAkiwrapper")
	void testAkiwrapper(@Nonnull Language language, @Nonnull Theme theme) {

		Logger log = getLogger(format("%s-%s", language, theme));
		try {
			log.info("Establishing connection");
			Akiwrapper api;
			try {
				api = new AkiwrapperBuilder().setUnirestInstance(UNIREST).setLanguage(language).setTheme(theme).build();
			} catch (LanguageThemeCombinationException e) {
				abort("Language-theme combination not supported.");
				return;
			}

			var initialQuestion = testInitialState(log, api, language, theme);
			int expectedState = testAnswering(log, api);
			testUndo(log, api, initialQuestion.getText(), expectedState);

		} catch (TestAbortedException e) {
			throw e;

		} catch (Exception e) {
			e.printStackTrace();

			fail("Got an exception running the test");
		}
	}

	private static Question testInitialState(@Nonnull Logger log, @Nonnull Akiwrapper api, @Nonnull Language language,
											 @Nonnull Theme theme) {
		log.info("Asserting the current state.");

		assertEquals(language, api.getLanguage(), LANGUAGE_MISMATCH);
		assertEquals(theme, api.getTheme(), THEME_MISMATCH);

		var query = api.getCurrentQuery();
		if (query instanceof Question) {
			checkQuestion(0, (Question) query);
			return (Question) query;

		} else {
			fail("Initial query is not a question");
			return null;
		}
	}

	private static int testAnswering(@Nonnull Logger log, @Nonnull Akiwrapper api) {
		log.info("Advancing {} steps (one time for each possible answer).", Answer.values().length);
		int expectedState = 0;
		var question = (Question) api.getCurrentQuery();
		for (Answer answer : Answer.values()) {
			if (question == null) {
				fail(QUESTION_NULL);
				return -1; // unreachable
			}

			log.info("Answering with {} and checking the state (step={}, progression={}).", answer.name(),
					 question.getStep(), question.getProgression());

			var query = question.answer(answer);
			if (query instanceof Question)
				question = (Question) query;
			else
				fail("New query is not a question, is " + question);

			assertSame(question, api.getCurrentQuery(), QUESTION_CURRENT_NO_MATCH);
			expectedState++;
			assertEquals(expectedState, question.getStep());
			checkQuestion(expectedState, question);
		}

		log.info("Asserting the current state.");
		return expectedState;
	}

	private static void testUndo(@Nonnull Logger log, @Nonnull Akiwrapper api, @Nonnull String initialQuestionText,
								 int initialExpectedState) {
		log.info("Advancing -{} steps (using undo).", Answer.values().length);
		int expectedState = initialExpectedState;

		var question = (Question) api.getCurrentQuery();

		for (int i = 0; i < Answer.values().length; i++) {
			if (question == null) {
				fail(QUESTION_NULL);
				return; // unreachable
			}

			log.info("Undoing a step and checking the state (step={}, progression={}).", question.getStep(),
					 question.getProgression());

			question = question.undoAnswer();
			expectedState--;
			assertSame(question, api.getCurrentQuery(), QUESTION_CURRENT_NO_MATCH);
			checkQuestion(expectedState, question);
		}

		log.info("Asserting the current state.");
		if (question == null) {
			fail(QUESTION_NULL);
			return; // unreachable
		}

		var finalQuestion = question;
		assertThrows(UndoOutOfBoundsException.class, finalQuestion::undoAnswer);
		checkQuestion(0, question);

		assertEquals(initialQuestionText, question.getText(), QUESTION_INITIAL_NO_MATCH);
	}

	private static void checkQuestion(int expectedState, @Nullable Question question) {
		if (question == null) {
			fail(QUESTION_NULL);

		} else {
			assertFalse(question.getText().isEmpty(), QUESTION_EMPTY);
			assertEquals(expectedState, question.getStep(), QUESTION_WRONG_STEP);
		}
	}

	private static Stream<Arguments> generateTestAkiwrapper() {
		// we're running tests on a cartesian product of Language and Theme values
		Arguments[] arguments = new Arguments[Language.values().length * Theme.values().length];
		int i = 0;
		for (Language lang : Language.values())
			for (Theme theme : Theme.values()) {
				arguments[i] = Arguments.of(lang, theme);
				i++;
			}
		return Stream.of(arguments);
	}

}
