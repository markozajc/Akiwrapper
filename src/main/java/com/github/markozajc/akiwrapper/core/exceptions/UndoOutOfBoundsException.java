package com.github.markozajc.akiwrapper.core.exceptions;

import com.github.markozajc.akiwrapper.Akiwrapper;

/**
 * An exception indicating that {@link Akiwrapper#undoAnswer()} has been called on
 * the first question - when {@link Akiwrapper#getStep()} is {@code 0}.
 *
 * @author Marko Zajc
 */
public class UndoOutOfBoundsException extends AkinatorException {

	@SuppressWarnings("javadoc") // internal
	public UndoOutOfBoundsException() {}

}
