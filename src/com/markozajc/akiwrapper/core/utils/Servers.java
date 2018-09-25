package com.markozajc.akiwrapper.core.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.json.JSONObject;

import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerGroupImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerGroupUnavailableException;

/**
 * A class containing various API server utilities.
 * 
 * @author Marko Zajc
 */
public class Servers {

	/**
	 * A list of known Akinator's API servers
	 */
	public static final Map<Language, ServerGroup> SERVER_GROUPS;

	static {

		Map<Language, ServerGroup> servers = new HashMap<>();

		// Arabic
		servers.put(Language.ARABIC, new ServerGroupImpl(Language.ARABIC, new Server[] {
				new ServerImpl("ns623157.ovh.net:8121", Language.ARABIC),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8155", Language.ARABIC),
		}));

		// Chinese
		servers.put(Language.CHINESE, new ServerGroupImpl(Language.CHINESE, new Server[] {
				new ServerImpl("ns623157.ovh.net:8125", Language.CHINESE),
				new ServerImpl("ns3003941.ip-37-187-149.eu:8148", Language.CHINESE),
		}));

		// Dutch
		servers.put(Language.DUTCH, new ServerGroupImpl(Language.DUTCH, new Server[] {
				new ServerImpl("ns6624370.ip-5-196-85.eu:8133", Language.DUTCH),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8158", Language.DUTCH),
		}));

		// English
		servers.put(Language.ENGLISH, new ServerGroupImpl(Language.ENGLISH, new Server[] {
				new ServerImpl("api-en3.akinator.com", Language.ENGLISH),
				new ServerImpl("api-usa6.akinator.com", Language.ENGLISH),
				new ServerImpl("ns623133.ovh.net:8014", Language.ENGLISH),
				new ServerImpl("62-4-22-192.rev.poneytelecom.eu:8117", Language.ENGLISH),
				new ServerImpl("ns6624370.ip-5-196-85.eu:8118", Language.ENGLISH),
				new ServerImpl("ns3003941.ip-37-187-149.eu:8141", Language.ENGLISH),
				new ServerImpl("ns3003941.ip-37-187-149.eu:8144", Language.ENGLISH),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8157", Language.ENGLISH),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8162", Language.ENGLISH),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8163", Language.ENGLISH),
		}));

		// French
		servers.put(Language.FRENCH, new ServerGroupImpl(Language.FRENCH, new Server[] {
				new ServerImpl("ns623133.ovh.net:8030", Language.FRENCH),
				new ServerImpl("62-4-22-192.rev.poneytelecom.eu:8165", Language.FRENCH),
				new ServerImpl("62-4-22-192.rev.poneytelecom.eu:8167", Language.FRENCH),
				new ServerImpl("ns6624370.ip-5-196-85.eu:8138", Language.FRENCH),
		}));

		// German
		servers.put(Language.GERMAN, new ServerGroupImpl(Language.GERMAN, new Server[] {
				new ServerImpl("api-de3.akinator.com", Language.GERMAN),
				new ServerImpl("ns623133.ovh.net:8005", Language.GERMAN),
				new ServerImpl("ns3003941.ip-37-187-149.eu:8145", Language.GERMAN),
		}));

		// Hebrew
		servers.put(Language.HEBREW, new ServerGroupImpl(Language.HEBREW, new Server[] {
				new ServerImpl("ns623133.ovh.net:8006", Language.HEBREW),
		}));

		// Italian
		servers.put(Language.ITALIAN, new ServerGroupImpl(Language.ITALIAN, new Server[] {
				new ServerImpl("ns6624370.ip-5-196-85.eu:9131", Language.ITALIAN),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8159", Language.ITALIAN),
		}));

		// Japanese
		servers.put(Language.JAPANESE, new ServerGroupImpl(Language.JAPANESE, new Server[] {
				new ServerImpl("ns623133.ovh.net:8012", Language.JAPANESE),
				new ServerImpl("ns6624370.ip-5-196-85.eu:8132", Language.JAPANESE),
				new ServerImpl("ns3003941.ip-37-187-149.eu:8146", Language.JAPANESE),
		}));

		// Korean
		servers.put(Language.KOREAN, new ServerGroupImpl(Language.KOREAN, new Server[] {
				new ServerImpl("62-4-22-192.rev.poneytelecom.eu:8167", Language.KOREAN),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8156", Language.KOREAN),
		}));

		// Polish
		servers.put(Language.POLISH, new ServerGroupImpl(Language.POLISH, new Server[] {
				new ServerImpl("ns3003941.ip-37-187-149.eu:8143", Language.POLISH),
		}));

		// Portuguese
		servers.put(Language.PORTUGUESE, new ServerGroupImpl(Language.PORTUGUESE, new Server[] {
				new ServerImpl("62-4-22-192.rev.poneytelecom.eu:8166", Language.PORTUGUESE),
				new ServerImpl("ns6624370.ip-5-196-85.eu:8111", Language.PORTUGUESE),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8161", Language.PORTUGUESE),
		}));

		// Russian
		servers.put(Language.RUSSIAN, new ServerGroupImpl(Language.RUSSIAN, new Server[] {
				new ServerImpl("ns623157.ovh.net:8124", Language.RUSSIAN),
				new ServerImpl("62-4-22-192.rev.poneytelecom.eu:8169", Language.RUSSIAN),
				new ServerImpl("ns3003941.ip-37-187-149.eu:8142", Language.RUSSIAN),
		}));

		// Spanish
		servers.put(Language.SPANISH, new ServerGroupImpl(Language.SPANISH, new Server[] {
				new ServerImpl("ns623133.ovh.net:8013", Language.SPANISH),
				new ServerImpl("62-210-100-133.rev.poneytelecom.eu:8160", Language.SPANISH),
		}));

		// Turkish
		servers.put(Language.TURKISH, new ServerGroupImpl(Language.TURKISH, new Server[] {
				new ServerImpl("62-4-22-192.rev.poneytelecom.eu:8164", Language.TURKISH),
				new ServerImpl("ns6624370.ip-5-196-85.eu:8134", Language.TURKISH),
		}));

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
					.getRequest(server.getBaseUrl(), AkiwrapperBuilder.DEFAULT_FILTER_PROFANITY,
							AkiwrapperBuilder.DEFAULT_NAME)
					.getJSON();

			if (new StatusImpl(question).getLevel().equals(Level.OK)) {
				return true;
			}
		} catch (Exception e) {
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
	public static Server getFirstAvailableServer(@Nonnull Language localization)
			throws UnsupportedOperationException, ServerGroupUnavailableException {
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
