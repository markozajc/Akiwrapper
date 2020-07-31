package com.markozajc.akiwrapper.core.utils;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.jcabi.xml.XMLDocument;
import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerImpl;
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

	private static final String FOOTPRINT = "cd8e6509f3420878e18d75b9831b317f";
	private static final String LIST_URL = "https://global3.akinator.com/ws/instances_v2.php?media_id=14&mode=https&footprint="
	    + FOOTPRINT;

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
		return getServers().filter(s -> s.getGuessType() == guessType)
		    .filter(s -> s.getLanguage() == localization)
		    .findAny()
		    .orElseThrow(ServerNotFoundException::new);
	}

	/**
	 * Fetches and builds a {@link Stream} of {@link Server}s from the server-listing API
	 * endpoint. All servers in this list should be up and running.
	 *
	 * @return a {@link Stream} of all {@link Server}s.
	 */
	public static Stream<Server> getServers() {
		return new XMLDocument(fetchListXml()).nodes("//RESULT/PARAMETERS/*")
		    .stream()
		    .flatMap(xml -> ServerImpl.fromXml(xml).stream());
	}

	private static String fetchListXml() {
		return Route.UNIREST.get(LIST_URL).asString().getBody();
	}

}
