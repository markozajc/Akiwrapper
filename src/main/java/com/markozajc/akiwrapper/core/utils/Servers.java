package com.markozajc.akiwrapper.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerGroupImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerGroupUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A class containing various API server utilities.
 *
 * @author Marko Zajc
 */
@SuppressWarnings("null")
@SuppressFBWarnings("REC_CATCH_EXCEPTION")
public class Servers {

	private static final int MAX_SCRAP_ATTEMPTS = 3;
	/**
	 * The format for Akinator's base URL format. Use with
	 * {@link String#format(String, Object...)} and provide the hostname and the port as
	 * the parameter (for example {@code srv1.akinator.com.9100}}.
	 */
	public static final String BASE_URL_FORMAT = "https://%s/ws/";

	private Servers() {}

	/**
	 * A list of all known Akinator's API servers.
	 */
	public static final Map<Language, ServerGroup> SERVER_GROUPS;

	static {

		Map<Language, ServerGroup> serverGroups = new EnumMap<>(Language.class);

		try {
			Map<Language, List<Server>> servers = new EnumMap<>(Language.class);

			try (InputStream is = Servers.class.getResourceAsStream("/servers.json")) {
				try (Scanner s = new Scanner(is, "UTF-8")) {
					s.useDelimiter("\\A");

					JSONObject serversBaseJson = new JSONObject(s.hasNext() ? s.next() : "{}");

					if (!serversBaseJson.has("servers"))
						throw new IOException();

					serversBaseJson.getJSONArray("servers").forEach(o -> {
						JSONObject serverJson = (JSONObject) o;
						Language localization = Language.valueOf(serverJson.getString("localization"));

						if (!servers.containsKey(localization))
							servers.put(localization, new ArrayList<>());

						servers.get(localization).add(new ServerImpl(serverJson.getString("host"), localization));
					});

				}
			}

			servers.forEach((language, server) -> serverGroups.put(language, new ServerGroupImpl(language, server)));

		} catch (Exception e) {
			System.err.println("[ERROR] Akiwrapper - Couldn't load the server list; " + e); // NOSONAR
		}

		SERVER_GROUPS = Collections.unmodifiableMap(serverGroups);
	}

	/**
	 * Checks if an API server is online.
	 *
	 * @param server
	 *            a server to check
	 * 
	 * @return true if a new session can be created on the provided server, false if not
	 */
	public static boolean isUp(Server server) {
		return isUp(server, 0);
	}

	/**
	 * Checks if an API server is online.
	 *
	 * @param server
	 *            a server to check
	 * 
	 * @return true if a new session can be created on the provided server, false if not
	 */
	private static boolean isUp(Server server, int attempt) {
		try {
			JSONObject question = Route.NEW_SESSION
			    .getRequest(server.getApiUrl(), true, AkiwrapperMetadata.DEFAULT_NAME)
			    .getJSON(true);
			// Checks if a server can be connected to by creating a new session on it

			if (new StatusImpl(question).getLevel().equals(Level.OK))
				return true;

		} catch (StatusException e) {
			if (e.getStatus().getReason().startsWith("KEY NOT FOUND")) {
				// Checks if the exception was thrown because of an obsolete API key

				if (attempt > MAX_SCRAP_ATTEMPTS)
					return false;
				// In case something goes terribly wrong and the API key does not get scraped, but
				// neither is an exception thrown on the Route.scrapApiKey call - or the KNF error
				// has been returned for a reason not connected with the API key

				try {
					Route.accquireApiKey();
					return isUp(server, attempt + 1);
					// Attempts to "rescrap" the API key and run the method again

				} catch (IOException ioe) {
					// In case API key can not be scraped. If this ever occurs, it's Akiwrapper's fault
					// (or you haven't updated to the newest version)
					Logger.getLogger("Akiwrapper").severe("Couldn't scrape the API key; " + ioe.toString());
					return false;
				}

			}

		} catch (IllegalArgumentException | IOException e) {
			// If the server is unreachable
		}

		return false;
	}

	/**
	 * Searches for an available server for the given localization language. If there is
	 * no available server, this will throw a {@link ServerGroupUnavailableException}.
	 *
	 * @param localization
	 *            language of the server to search for
	 * 
	 * @return the first server available for that language
	 * 
	 * @throws UnsupportedOperationException
	 *             if language {@code localization} is not supported by the Akinator's
	 *             API
	 * @throws ServerGroupUnavailableException
	 *             if there are no available servers for language {@code localization}
	 */
	@Nonnull
	public static Server getFirstAvailableServer(@Nonnull Language localization) {
		ServerGroup sg = SERVER_GROUPS.get(localization);
		if (sg == null)
			throw new IllegalArgumentException(
			    "Language " + localization.toString() + " is not supported by the Akinator's API.");

		Server result = sg.getFirstAvailableServer();
		if (result == null)
			throw new ServerGroupUnavailableException(sg);

		return result;
	}

}
