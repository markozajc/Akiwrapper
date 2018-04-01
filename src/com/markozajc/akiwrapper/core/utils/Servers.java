package com.markozajc.akiwrapper.core.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerImpl;

/**
 * A class containing various API server utilities.
 * 
 * @author Marko Zajc
 */
public class Servers {

	/**
	 * A list of known Akinator's API servers
	 */
	public static final List<Server> SERVERS = Collections.unmodifiableList(Arrays.asList(new Server[] {
			new ServerImpl("http://api-en1.akinator.com/ws/"),
			new ServerImpl("http://api-en3.akinator.com/ws/"),
			new ServerImpl("http://api-en4.akinator.com/ws/")
	}));

	/**
	 * Checks if an API server is online.
	 * 
	 * @param server
	 *            a server to check
	 * @return true if a new session can be created on the provided server, false if
	 *         not
	 */
	public static boolean isUp(Server server) {
		try {
			JSONObject question = Route.NEW_SESSION.getRequest(server.getBaseUrl(), AkiwrapperBuilder.DEFAULT_NAME)
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
	 * Returns the first available API server of the ones in
	 * {@link Servers#SERVERS}.
	 * 
	 * @return the first available server or null if no servers are currently
	 *         available (very, very unlikely, almost impossible)
	 */
	@Nullable
	public static Server getFirstAvailableServer() {
		return SERVERS.stream().filter(s -> isUp(s)).findAny().orElse(null);
	}

}
