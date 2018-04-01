package com.markozajc.akiwrapper.core.exceptions;

import com.markozajc.akiwrapper.core.entities.Status;

/**
 * An exception signaling that the server returned an error code ("KO").
 * 
 * @author Marko Zajc
 */
public class StatusException extends RuntimeException {

	private Status status;

	/**
	 * Creates a new {@link StatusException}.
	 * 
	 * @param status
	 *            status to append
	 */
	public StatusException(Status status) {
		this.status = status;
	}

	/**
	 * @return the problematic status that has been returned
	 */
	public Status getStatus() {
		return status;
	}

}
