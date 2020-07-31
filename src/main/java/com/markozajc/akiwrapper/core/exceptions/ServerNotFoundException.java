package com.markozajc.akiwrapper.core.exceptions;

import com.markozajc.akiwrapper.core.entities.Server;

/**
 * An exception indicating that no {@link Server} could be found for the given query
 *
 * @author Marko Zajc
 */
public class ServerNotFoundException extends Exception {

	/**
	 * Constructs a new {@link ServerNotFoundException}.
	 */
	public ServerNotFoundException() {
		super();
	}

}
