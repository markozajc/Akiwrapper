package com.markozajc.akiwrapper;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.slf4j.*;

import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.Server.*;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

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
		Logger log = LoggerFactory.getLogger(String.format("%s-%s", language, guessType));
		log.info("Establishing connection");
		Akiwrapper api;
		try {
			api = new AkiwrapperBuilder().setLanguage(language).setGuessType(guessType).build();
		} catch (ServerNotFoundException e) {
			log.warn("Current combination not supported, server wasn't found.");
			log.trace("", e);
			return;
		}

		log.info("Asserting the current state.");
		Question initialQuestion = api.getCurrentQuestion();
		int expectedState = 0;
		checkQuestion(initialQuestion, expectedState);
		assertEquals(language, api.getServer().getLanguage(), SERVER_LANGUAGE_NO_MATCH);
		assertEquals(guessType, api.getServer().getGuessType(), SERVER_GUESSTYPE_NO_MATCH);
		log.trace("API server URL: {}", api.getServer().getUrl());

		log.info("Advancing {} steps (one time for each possible answer).", Answer.values().length);
		for (Answer answer : Answer.values()) {
			log.debug("Answering with {} and checking the question.", answer.name());
			Question newQuestion = api.answerCurrentQuestion(answer);
			assertEquals(newQuestion, api.getCurrentQuestion(), QUESTION_CURRENT_NO_MATCH);
			expectedState++;
			checkQuestion(api.getCurrentQuestion(), expectedState);
		}

		log.info("Fetching guesses.", Answer.values().length);
		List<Guess> guesses = api.getGuesses();
		debugGuesses(log, guesses);

		log.info("Advancing -{} steps (using undo).", Answer.values().length);
		for (int i = 0; i < Answer.values().length; i++) {
			Question undoneQuestion = api.undoAnswer();
			assertEquals(undoneQuestion, api.getCurrentQuestion(), QUESTION_CURRENT_NO_MATCH);
			expectedState--;
			checkQuestion(api.getCurrentQuestion(), expectedState);
		}

		log.info("Asserting the final state.");
		assertNull(api.undoAnswer());
		checkQuestion(api.getCurrentQuestion(), 0);
		Question currentQuestion = api.getCurrentQuestion();
		if (initialQuestion != null && currentQuestion != null) {
			assertEquals(initialQuestion.getQuestion(), currentQuestion.getQuestion(), QUESTION_INITIAL_NO_MATCH);
		}
		// Neither of those can be null due to checkQuestion checking (and failing on)
		// nullability.

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
