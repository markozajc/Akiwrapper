package com.github.markozajc.akiwrapper.core.exceptions;

import com.github.markozajc.akiwrapper.core.entities.Status;
import com.github.markozajc.akiwrapper.core.entities.Status.Level;

/**
 * An exception indicating that the server returned an error code
 * ({@link Level#ERROR}).
 *
 * @author Marko Zajc
 */
public class ServerStatusException extends AkinatorException {

	private final Status status;

	@SuppressWarnings("javadoc") // internal
	public ServerStatusException(Status status) {
		super(status.toString());
		this.status = status;
	}

	/**
	 * @return the erroneous status returned by the server
	 */
	public Status getStatus() {
		return this.status;
	}

}
