package com.markozajc.akiwrapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server;

/**
 * The "core" of interaction with the Akinator's API. Contains all methods required
 * to fully utilize all (known) Akinator's API's endpoints.
 *
 * @author Marko Zajc
 */
public interface Akiwrapper {

	/**
	 * An enum used to represent an answer to Akinator's question
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
	 * Answers current question and retrieves the next one. The next question is passed
	 * as return value and can be retrieved later on with {@link #getCurrentQuestion()}.
	 * If there are no more questions left, this will return {@code null}.
	 *
	 * @param answer
	 *            the answer
	 *
	 * @return the latest question or null if an answer was found
	 * 
	 * @throws IOException
	 *             if something goes wrong
	 */
	@Nullable
	Question answerCurrentQuestion(Answer answer) throws IOException;

	/**
	 * Goes one step backwards (just like {@link #answerCurrentQuestion(Answer)}, except
	 * it goes back instead of forward).<br>
	 * For example, if {@link #getCurrentQuestion()} returns a question on step
	 * {@code 5}, calling this command will make {@link #getCurrentQuestion()} return the
	 * question from step {@code 4}. You can call this as many times as you want.<br>
	 * <strong> Beware that calling this when {@link #getCurrentQuestion()} returns a
	 * question on step {@code 0}, calling this will return {@code null} and nothing will
	 * actually be changed!<br>
	 * This will also return {@code null} if {@link #getCurrentQuestion()} returns
	 * {@code null},</strong>
	 *
	 * @return the past message
	 * 
	 * @throws IOException
	 */
	@Nullable
	Question undoAnswer() throws IOException;

	/**
	 * Returns current question. You can answer it with
	 * {@link #answerCurrentQuestion(Answer)}. If there are no more questions left, this
	 * will return {@code null}.
	 *
	 * @return current question
	 */
	@Nullable
	Question getCurrentQuestion();

	/**
	 * @return an unmodifiable list of Akinator's guesses, empty if there are no guesses
	 * 
	 * @throws IOException
	 *             if API call isn't successful
	 * 
	 * @see Akiwrapper#getGuessesAboveProbability(double)
	 */
	@Nonnull
	List<Guess> getGuesses() throws IOException;

	/**
	 * @return the API server this instance of Akiwrapper uses
	 */
	@Nonnull
	Server getServer();

	/**
	 * @param probability
	 *            probability threshold
	 * 
	 * @return a list of Akinator's guesses with probability above the specified
	 *         probability threshold.
	 * 
	 * @throws IOException
	 * 
	 * @see Akiwrapper#getGuesses()
	 */
	@SuppressWarnings("null")
	@Nonnull
	default List<Guess> getGuessesAboveProbability(double probability) throws IOException {
		return getGuesses().stream().filter(g -> g.getProbability() > probability).collect(Collectors.toList());
	}
}
