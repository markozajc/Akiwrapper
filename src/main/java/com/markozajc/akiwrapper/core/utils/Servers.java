package com.markozajc.akiwrapper.core.utils;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A class containing various API server utilities.
 *
 * @author Marko Zajc
 */
@SuppressWarnings("null")
@SuppressFBWarnings("REC_CATCH_EXCEPTION")
public final class Servers {

	private Servers() {}

	/**
	 * Filters the list of {@link Server}s using given parameters.
	 *
	 * @param localization
	 *            language of the server to search for
	 * @param guessType
	 *            guessType of the server to search for
	 *
	 * @return a {@link Server} that suits the given parameters.
	 *
	 * @throws ServerNotFoundException
	 *             if there is no server that matches the query.
	 */
	@Nonnull
	public static Server findServer(@Nonnull Language localization, @Nonnull GuessType guessType) {
		return StandaloneRoutes.getServers()
		    .filter(s -> s.getGuessType() == guessType)
		    .filter(s -> s.getLocalization() == localization)
		    .findAny()
		    .orElseThrow(ServerNotFoundException::new);
	}

}
