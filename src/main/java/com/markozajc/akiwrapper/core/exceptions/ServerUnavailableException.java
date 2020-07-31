package com.markozajc.akiwrapper.core.exceptions;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Status;

/**
 * An exception indicating that the currently used {@link Server} has gone offline.
 *
 * @author Marko Zajc
 */
public class ServerUnavailableException extends StatusException {

	/**
	 * Constructs a new {@link ServerUnavailableException} from a {@link Status}.
	 *
	 * @param status
	 *            erroneous status.
	 */
	public ServerUnavailableException(@Nonnull Status status) {
		super(status);
	}

}
