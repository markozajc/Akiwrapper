package com.github.markozajc.akiwrapper.core.exceptions;

import com.github.markozajc.akiwrapper.core.entities.Status;

/**
 * An exception indicating that the server returned an error code ("KO").
 *
 * @author Marko Zajc
 */
public class StatusException extends RuntimeException {

	private final Status status;

	/**
	 * Constructs a new {@link StatusException}.
	 *
	 * @param status
	 *            status to append
	 */
	public StatusException(Status status) {
		super(status.getLevel().toString().toUpperCase() + " - " + status.getReason());
		this.status = status;
	}

	/**
	 * @return the problematic status that has been returned
	 */
	public Status getStatus() {
		return this.status;
	}

}
