package com.markozajc.akiwrapper.core.exceptions;

import com.markozajc.akiwrapper.core.entities.ServerGroup;

/**
 * An exception that signals that all servers from a {@link ServerGroup} are
 * unavailable.
 */
public class AllServersUnavailableException extends ServerUnavailableException {

	/**
	 * Creates a new {@link AllServersUnavailableException}.
	 */
	public AllServersUnavailableException(ServerGroup sg) {
		super(sg.getServers());
	}

}
