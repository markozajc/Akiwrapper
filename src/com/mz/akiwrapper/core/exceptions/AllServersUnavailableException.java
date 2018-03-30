package com.mz.akiwrapper.core.exceptions;

import com.mz.akiwrapper.core.utils.Servers;

/**
 * An exception that signals that all servers from {@link Servers#SERVERS} are
 * unavailable.
 */
public class AllServersUnavailableException extends ServerUnavailableException {

	/**
	 * Creates a new {@link AllServersUnavailableException}.
	 */
	public AllServersUnavailableException() {
		super(Servers.SERVERS);
	}

}
