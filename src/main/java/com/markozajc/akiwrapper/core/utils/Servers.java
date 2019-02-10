package com.markozajc.akiwrapper.core.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
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

/**
 * A class containing various API server utilities.
 *
 * @author Marko Zajc
 */
public class Servers {

	private Servers() {}

	/**
	 * A list of all known Akinator's API servers.
	 */
	public static final Map<Language, ServerGroup> SERVER_GROUPS;

	static {

		Map<Language, ServerGroup> servers = new EnumMap<>(Language.class);

		// @formatter:off
		// Disables Eclipse's formatter to preserve arrays' list structure

		// Arabic
		servers.put(Language.ARABIC, new ServerGroupImpl(Language.ARABIC,
				new ServerImpl("srv2.akinator.com:9155", Language.ARABIC),
				new ServerImpl("srv5.akinator.com:9121", Language.ARABIC)
		));

		// Chinese
		servers.put(Language.CHINESE, new ServerGroupImpl(Language.CHINESE,
				new ServerImpl("srv5.akinator.com:9125", Language.CHINESE),
				new ServerImpl("srv7.akinator.com:9148", Language.CHINESE),
				new ServerImpl("srv9.akinator.com:9135", Language.CHINESE),
				new ServerImpl("srv11.akinator.com:9150", Language.CHINESE)
		));

		// Dutch
		servers.put(Language.DUTCH, new ServerGroupImpl(Language.DUTCH,
				new ServerImpl("srv2.akinator.com:9158", Language.DUTCH),
				new ServerImpl("srv9.akinator.com:9133", Language.DUTCH)
		));

		// English
		servers.put(Language.ENGLISH, new ServerGroupImpl(Language.ENGLISH,
				new ServerImpl("srv2.akinator.com:9157", Language.ENGLISH),
				new ServerImpl("srv2.akinator.com:9162", Language.ENGLISH),
				new ServerImpl("srv2.akinator.com:9163", Language.ENGLISH),
				new ServerImpl("srv3.akinator.com:9117", Language.ENGLISH),
				new ServerImpl("srv4.akinator.com:9014", Language.ENGLISH),
				new ServerImpl("srv5.akinator.com:9122", Language.ENGLISH),
				new ServerImpl("srv6.akinator.com:9126", Language.ENGLISH),
				new ServerImpl("srv7.akinator.com:9141", Language.ENGLISH),
				new ServerImpl("srv7.akinator.com:9144", Language.ENGLISH),
				new ServerImpl("srv10.akinator.com:9129", Language.ENGLISH),
				new ServerImpl("srv11.akinator.com:9152", Language.ENGLISH)
		));

		// French
		servers.put(Language.FRENCH, new ServerGroupImpl(Language.FRENCH,
				new ServerImpl("srv3.akinator.com:9165", Language.FRENCH),
				new ServerImpl("srv3.akinator.com:9167", Language.FRENCH),
				new ServerImpl("srv4.akinator.com:9030", Language.FRENCH),
				new ServerImpl("srv4.akinator.com:9178", Language.FRENCH),
				new ServerImpl("srv9.akinator.com:9138", Language.FRENCH),
				new ServerImpl("srv10.akinator.com:9176", Language.FRENCH)
		));

		// German
		servers.put(Language.GERMAN, new ServerGroupImpl(Language.GERMAN,
				new ServerImpl("srv7.akinator.com:9145", Language.GERMAN),
				new ServerImpl("srv11.akinator.com:9171", Language.GERMAN)
		));

		// Hebrew
		servers.put(Language.HEBREW, new ServerGroupImpl(Language.HEBREW,
				new ServerImpl("srv4.akinator.com:9170", Language.HEBREW),
				new ServerImpl("srv10.akinator.com:9119", Language.HEBREW)
		));

		// Italian
		servers.put(Language.ITALIAN, new ServerGroupImpl(Language.ITALIAN,
				new ServerImpl("srv2.akinator.com:9159", Language.ITALIAN),
				new ServerImpl("srv9.akinator.com:9131", Language.ITALIAN)
		));

		// Japanese
		servers.put(Language.JAPANESE, new ServerGroupImpl(Language.JAPANESE,
				new ServerImpl("srv4.akinator.com:9154", Language.JAPANESE),
				new ServerImpl("srv7.akinator.com:9146", Language.JAPANESE),
				new ServerImpl("srv9.akinator.com:9132", Language.JAPANESE),
				new ServerImpl("srv10.akinator.com:9120", Language.JAPANESE),
				new ServerImpl("srv11.akinator.com:9153", Language.JAPANESE),
				new ServerImpl("srv11.akinator.com:9172", Language.JAPANESE)
		));

		// Korean
		servers.put(Language.KOREAN, new ServerGroupImpl(Language.KOREAN,
				new ServerImpl("srv2.akinator.com:9156", Language.KOREAN),
				new ServerImpl("srv3.akinator.com:9168", Language.KOREAN)
		));

		// Polish
		servers.put(Language.POLISH, new ServerGroupImpl(Language.POLISH,
				new ServerImpl("srv5.akinator.com:9123", Language.POLISH),
				new ServerImpl("srv7.akinator.com:9143", Language.POLISH)
		));

		// Portuguese
		servers.put(Language.PORTUGUESE, new ServerGroupImpl(Language.PORTUGUESE,
				new ServerImpl("srv2.akinator.com:9161", Language.PORTUGUESE),
				new ServerImpl("srv3.akinator.com:9166", Language.PORTUGUESE),
				new ServerImpl("srv11.akinator.com:9174", Language.PORTUGUESE)
		));

		// Russian
		servers.put(Language.RUSSIAN, new ServerGroupImpl(Language.RUSSIAN,
				new ServerImpl("srv3.akinator.com:9169", Language.RUSSIAN),
				new ServerImpl("srv5.akinator.com:9124", Language.RUSSIAN),
				new ServerImpl("srv7.akinator.com:9142", Language.RUSSIAN)
		));

		// Spanish
		servers.put(Language.SPANISH, new ServerGroupImpl(Language.SPANISH,
				new ServerImpl("srv2.akinator.com:9160", Language.SPANISH),
				new ServerImpl("srv6.akinator.com:9127", Language.SPANISH),
				new ServerImpl("srv11.akinator.com:9151", Language.SPANISH)
		));

		// Turkish
		servers.put(Language.TURKISH, new ServerGroupImpl(Language.TURKISH,
				new ServerImpl("srv3.akinator.com:9164", Language.TURKISH),
				new ServerImpl("srv9.akinator.com:9134", Language.TURKISH)
		));

		// @formatter:on
		// Re-enables Eclipse's formatter

		SERVER_GROUPS = Collections.unmodifiableMap(servers);
	}

	/**
	 * Checks if an API server is online.
	 *
	 * @param server
	 *            a server to check
	 * @return true if a new session can be created on the provided server, false if not
	 */
	public static boolean isUp(Server server) {
		try {
			JSONObject question = Route.NEW_SESSION
					.getRequest(server.getBaseUrl(), AkiwrapperMetadata.DEFAULT_FILTER_PROFANITY,
						AkiwrapperMetadata.DEFAULT_NAME)
					.getJSON();
			// Checks if a server can be connected to by creating a new session on it

			if (new StatusImpl(question).getLevel().equals(Level.OK))
				return true;

		} catch (StatusException e) {
			if (e.getStatus().getReason().equals("KEY NOT FOUND")) {
				// Checks if the exception was thrown because of an obsolete API key

				try {
					Route.scrapApiKey();
					return isUp(server);
					// Attempts to "rescrap" the API key and run the method again

				} catch (IOException ioe) {
					// In case API key can not be scraped. If this ever occurs, it's Akiwrapper's fault
					// (or you haven't updated to the newest version)
					Logger.getLogger("Akiwrapper").severe("Couldn't scrape the API key; " + ioe.toString());
					return false;

				} catch (StackOverflowError soe) {
					// In case something goes terribly wrong and the API key does not get scraped, but
					// neither is an exception thrown on the Route.scrapApiKey call
					return false;
				}

			}

			return false;

		} catch (IllegalArgumentException | IOException e) {
			// If the server is unreachable
			return false;
		}

		return false;
	}

	/**
	 * Searches for an available server for the given localization language. If there is
	 * no available server, this will throw a {@link ServerGroupUnavailableException}.
	 *
	 * @param localization
	 *            language of the server to search for
	 * @return the first server available for that language
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
			throw new UnsupportedOperationException(
					"Language " + localization.toString() + " is not supported by the Akinator's API.");

		Server result = sg.getFirstAvailableServer();
		if (result == null)
			throw new ServerGroupUnavailableException(sg);

		return result;
	}

}
