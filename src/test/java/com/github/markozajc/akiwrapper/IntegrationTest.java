package com.github.markozajc.akiwrapper;

import static com.github.markozajc.akiwrapper.Akiwrapper.Answer.YES;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.slf4j.Logger;

import com.github.markozajc.akiwrapper.Akiwrapper.Answer;
import com.github.markozajc.akiwrapper.core.entities.*;
import com.github.markozajc.akiwrapper.core.entities.Server.*;
import com.github.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;

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
		log.info("Establishing connection");
		Akiwrapper api;
		try {
			api = new AkiwrapperBuilder().setLanguage(language).setGuessType(guessType).build();
		} catch (ServerNotFoundException e) {
			abort("Current combination not supported, server wasn't found.");
			return;
		}

		log.info("Asserting the current state.");
		Question initialQuestion = api.getQuestion();
		int expectedState = 0;
		checkQuestion(initialQuestion, expectedState);
		assertDoesNotThrow(() -> api.getGuesses(), FETCHING_GUESSES_THROWS);
		assertEquals(language, api.getServer().getLanguage(), SERVER_LANGUAGE_NO_MATCH);
		assertEquals(guessType, api.getServer().getGuessType(), SERVER_GUESSTYPE_NO_MATCH);
		log.trace("API server URL: {}", api.getServer().getUrl());

		log.info("Advancing {} steps (one time for each possible answer).", Answer.values().length);
		for (Answer answer : Answer.values()) {
			log.debug("Answering with {} and checking the question.", answer.name());
			Question newQuestion = api.answer(answer);
			assertEquals(newQuestion, api.getQuestion(), QUESTION_CURRENT_NO_MATCH);
			expectedState++;
			checkQuestion(api.getQuestion(), expectedState);
		}

		fetchAndDebugGuesses(log, api);

		log.info("Advancing -{} steps (using undo).", Answer.values().length);
		for (int i = 0; i < Answer.values().length; i++) {
			Question undoneQuestion = api.undoAnswer();
			assertEquals(undoneQuestion, api.getQuestion(), QUESTION_CURRENT_NO_MATCH);
			expectedState--;
			checkQuestion(api.getQuestion(), expectedState);
		}

		log.info("Asserting the current state.");
		assertNull(api.undoAnswer());
		checkQuestion(api.getQuestion(), 0);
		assertDoesNotThrow(() -> api.getGuesses(), FETCHING_GUESSES_THROWS);
		Question currentQuestion = api.getQuestion();
		if (initialQuestion != null && currentQuestion != null)
			assertEquals(initialQuestion.getQuestion(), currentQuestion.getQuestion(), QUESTION_INITIAL_NO_MATCH);
		else
			fail("initialQuestion or currentQuestion were somehow null");
		// using this syntax instead of assertNotNull to please null analysis of @Nullable

		log.info("Exhausting questions.");
		var lastQuestion = api.getQuestion();
		assertNotNull(lastQuestion, "Current question is already null");

		while (api.getQuestion() != null) {
			lastQuestion = api.getQuestion();
			api.answer(YES);
		}

		if (lastQuestion != null)
			log.info("Ran out at step {}.", lastQuestion.getStep());
		else
			fail("Last question was somehow null");

		log.info("Asserting the current state.");
		assertNotNull(api.undoAnswer());
		assertNull(api.answer(YES));

		fetchAndDebugGuesses(log, api);
	}

	private static void fetchAndDebugGuesses(Logger log, Akiwrapper api) {
		log.info("Fetching guesses.", Answer.values().length);
		List<Guess> guesses = api.getGuesses();
		debugGuesses(log, guesses);
	}

	private static void debugGuesses(Logger log, List<Guess> guesses) {
		log.debug("There are {} guesses.", guesses.size());
		for (Guess guess : guesses) {
			log.trace("{} - {}", guess.getProbability(), guess.getName());
		}
	}

	private static void checkQuestion(@Nullable Question question, int expectedState) {
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
