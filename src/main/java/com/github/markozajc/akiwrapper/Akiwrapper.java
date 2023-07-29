package com.github.markozajc.akiwrapper;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.annotation.*;

import com.github.markozajc.akiwrapper.core.entities.*;

/**
 * The "core" of interaction with the Akinator's API.
 *
 * @author Marko Zajc
 */
public interface Akiwrapper {

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
	 * @return the API server this instance of Akiwrapper uses.
	 */
	@Nonnull
	Server getServer();

	boolean doesFilterProfanity();

	/**
	 * Answers current question and retrieves the next one. The next question is passed
	 * as return value and can be retrieved later on with {@link #getQuestion()}. If
	 * there are no more questions left, this will return {@code null}.
	 *
	 * @param answer
	 *            the answer
	 *
	 * @return the latest question or null if an answer was found
	 */
	@Nullable
	Question answer(Answer answer);

	/**
	 * Goes one question backwards.<br>
	 * For example, if {@link #getQuestion()} returns a question on step {@code 5},
	 * calling this command will make {@link #getQuestion()} return the question from
	 * step {@code 4}. You can call this as many times as you want.<br>
	 * <strong> Beware that calling this when {@link #getQuestion()} returns a question
	 * on step {@code 0}, calling this will return {@code null} and nothing will actually
	 * be changed!<br>
	 * This will also return {@code null} if {@link #getQuestion()} returns {@code null}
	 * as well.</strong>
	 *
	 * @return the past message
	 */
	@Nullable
	Question undoAnswer();

	/**
	 * Returns a probability-sorted (the lower the index, the higher the probability) and
	 * unmodifiable list of Akinator's guesses, or an empty list if there are no guesses.
	 * Note that this method caches the result, which means subsequent calls will not
	 * make API requests.
	 *
	 * @return a sorted list of guesses
	 *
	 * @see Akiwrapper#getGuessesAboveProbability(double)
	 */
	@Nonnull
	default List<Guess> getGuesses() {
		return getGuesses(0);
	}

	@Nonnull
	List<Guess> getGuesses(int count);

	/**
	 * Returns the current question. You can answer it with {@link #answer(Answer)}. If
	 * there are no more questions left, this will return {@code null}.
	 *
	 * @return current question
	 */
	@Nullable
	Question getQuestion();

	int getStep();

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

	void confirmGuess(@Nonnull Guess guess);

	@Nullable
	Question rejectLastGuess();

}
