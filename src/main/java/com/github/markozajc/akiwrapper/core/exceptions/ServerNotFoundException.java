package com.github.markozajc.akiwrapper.core.exceptions;

import javax.annotation.Nonnull;

import com.github.markozajc.akiwrapper.core.entities.Server;
import com.github.markozajc.akiwrapper.core.entities.Server.*;

/**
 * An exception indicating that no {@link Server} could be found for the given
 * combination of {@link Language} and {@link GuessType}.
 *
 * @author Marko Zajc
 */
public class ServerNotFoundException extends AkinatorException {

	@SuppressWarnings("javadoc") // internal
	public ServerNotFoundException(@Nonnull String message) {
		super(message);
	}

}
