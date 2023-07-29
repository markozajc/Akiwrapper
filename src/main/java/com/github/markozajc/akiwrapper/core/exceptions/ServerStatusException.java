package com.github.markozajc.akiwrapper.core.exceptions;

import com.github.markozajc.akiwrapper.core.entities.Status;

/**
 * An exception indicating that the server returned an error code ("KO").
 *
 * @author Marko Zajc
 */
public class ServerStatusException extends AkinatorException {

	private final Status status;

	public ServerStatusException(Status status) {
		super(status.toString());
		this.status = status;
	}

	/**
	 * @return the problematic status that has been returned
	 */
	public Status getStatus() {
		return this.status;
	}

}
