package com.github.markozajc.akiwrapper.core.exceptions;

import com.github.markozajc.akiwrapper.core.entities.Status;
import com.github.markozajc.akiwrapper.core.entities.Status.Level;

import kong.unirest.HttpResponse;

/**
 * An exception indicating that the server returned an error code
 * ({@link Level#ERROR}).
 *
 * @author Marko Zajc
 */
public class ServerStatusException extends AkinatorException {

	private final Status status;

	@SuppressWarnings("javadoc") // internal
	public ServerStatusException(Status status, String requestUrl, HttpResponse<String> response) {
		super(status.toString(), requestUrl, response);
		this.status = status;
	}

	/**
	 * @return the erroneous status returned by the server
	 */
	public Status getStatus() {
		return this.status;
	}

}
