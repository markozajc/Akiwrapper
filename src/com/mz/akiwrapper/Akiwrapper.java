package com.mz.akiwrapper;

import java.io.IOException;
import java.util.List;

import com.mz.akiwrapper.core.entities.Guess;
import com.mz.akiwrapper.core.entities.Question;

public interface Akiwrapper {

	/**
	 * An enum used to represent an answer to Akinator's question
	 */
	public enum Answer {
	YES(0),
	NO(1),
	DONT_KNOW(2),
	PROBABLY(3),
	PROBABLY_NOT(4);

		private final int id;

		Answer(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

	}

	/**
	 * Answers current question and retrieves the next one. The next question is
	 * passed as return value and can be retrieved later on with
	 * {@link #getCurrentQuestion()}.
	 * 
	 * @param answer
	 *            the answer
	 * 
	 * @return the latest question or null if an answer was found
	 * @throws IOException
	 *             if something goes wrong
	 */
	public Question answerCurrentQuestion(Answer answer) throws IOException;

	/**
	 * Returns current question. You can answer it with
	 * <code>answerCurrentQuestion()</code>
	 * 
	 * @return current question
	 */
	public Question getCurrentQuestion();

	/**
	 * Returns an array of Akinator's guesses. This is safe to call when
	 * <code>getCurrentQuestion().isEmpty()</code> returns true
	 * 
	 * @return an array of Akinator's guesses, empty if there are no guesses
	 * @throws IOException
	 *             if API call isn't successful
	 */
	public List<Guess> getGuesses() throws IOException;

}