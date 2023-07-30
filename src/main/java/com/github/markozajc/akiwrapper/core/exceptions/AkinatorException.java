package com.github.markozajc.akiwrapper.core.exceptions;

/**
 * The root exception class for exceptions in Akiwrapper.
 *
 * @author Marko Zajc
 */
public class AkinatorException extends RuntimeException {

	@SuppressWarnings("javadoc") // internal
	public AkinatorException() {
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message) {
		super(message);
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
