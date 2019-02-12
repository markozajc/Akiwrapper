package com.markozajc.akiwrapper.core.exceptions;

/**
 * An exception that signals there is no question left to answer or fetch.
 *
 * @author Marko Zajc
 */
public class MissingQuestionException extends RuntimeException {

	/**
	 * Creates a new {@link MissingQuestionException} instance.
	 */
	public MissingQuestionException() {
		super();
	}

}
