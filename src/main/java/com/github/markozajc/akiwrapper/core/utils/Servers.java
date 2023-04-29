package com.github.markozajc.akiwrapper.core.utils;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.github.markozajc.akiwrapper.core.entities.*;
import com.github.markozajc.akiwrapper.core.entities.Server.*;
import com.github.markozajc.akiwrapper.core.entities.impl.immutable.*;
import com.github.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.jcabi.xml.XMLDocument;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import kong.unirest.UnirestInstance;

/**
 * A class containing various API server utilities.
 *
 * @author Marko Zajc
 */
@SuppressFBWarnings("REC_CATCH_EXCEPTION")
public final class Servers {

	private static final String LIST_URL =
		"https://global3.akinator.com/ws/instances_v2.php?media_id=14&mode=https&footprint=cd8e6509f3420878e18d75b9831b317f";

	private Servers() {}

	/**
	 * Finds correct {@link Server}s using given parameters and compiles a
	 * {@link ServerList} out of them.
	 *
	 * @param unirest
	 *            the {@link UnirestInstance} to use for the request
	 * @param localization
	 *            language of the server to search for
	 * @param guessType
	 *            guessType of the server to search for
	 *
	 * @return a {@link ServerList} with {@link Server}s that suit the given parameters.
	 *
	 * @throws ServerNotFoundException
	 *             if there is no server that matches the query.
	 */
	@Nonnull
	public static ServerList findServers(@Nonnull UnirestInstance unirest, @Nonnull Language localization,
										 @Nonnull GuessType guessType) throws ServerNotFoundException {
		List<Server> servers = getServers(unirest).filter(s -> s.getGuessType() == guessType)
			.filter(s -> s.getLanguage() == localization)
			.collect(toList());
		if (servers.isEmpty())
			throw new ServerNotFoundException();
		return new ServerListImpl(servers);
	}

	/**
	 * Fetches and builds a {@link Stream} of {@link Server}s from the server-listing API
	 * endpoint. All servers in this list should be up and running.
	 *
	 * @param unirest
	 *            the {@link UnirestInstance} to use for the request
	 *
	 * @return a {@link Stream} of all {@link Server}s.
	 */
	@SuppressWarnings("null")
	public static Stream<Server> getServers(@Nonnull UnirestInstance unirest) {
		return new XMLDocument(fetchListXml(unirest)).nodes("//RESULT/PARAMETERS/*")
			.stream()
			.flatMap(xml -> ServerImpl.fromXml(xml).stream());
	}

	private static String fetchListXml(@Nonnull UnirestInstance unirest) {
		return unirest.get(LIST_URL).asString().getBody();
	}

}
