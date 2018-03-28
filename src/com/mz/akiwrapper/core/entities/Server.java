package com.mz.akiwrapper.core.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.mz.akiwrapper.core.AkiwrapperBuilder;
import com.mz.akiwrapper.core.Route;
import com.mz.akiwrapper.core.entities.CompletionStatus.Level;
import com.mz.akiwrapper.core.entities.impl.CompletionStatusImpl;
import com.mz.akiwrapper.core.entities.impl.ServerImpl;

public interface Server {

	/**
	 * A list of known Akinator's API servers
	 */
	public static final List<Server> SERVERS = Collections.unmodifiableList(Arrays.asList(new Server[] {
			new ServerImpl("http://api-en1.akinator.com/ws/"),
			new ServerImpl("http://api-en3.akinator.com/ws/"),
			new ServerImpl("http://api-en4.akinator.com/ws/")
	}));

	/**
	 * @return the base (API's) URL for this server
	 */
	String getBaseUrl();

	/**
	 * Check if the current {@link Server} is still available. This is a shortcut
	 * for {@link #isUp(Server)}
	 * 
	 * @return true if that API server is available, false if not
	 * @see #isUp(Server)
	 */
	default boolean isUp() {
		return isUp(this);
	}

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

			if (new CompletionStatusImpl(question).getLevel().equals(Level.OK)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Returns the first available API server of the ones in {@link #SERVERS}.
	 * 
	 * @return the first available server or null if no servers are currently
	 *         available (very, very unlikely, almost impossible)
	 */
	public static Server getFirstAvailableServer() {
		return SERVERS.stream().filter(s -> isUp(s)).findAny().orElse(null);
	}

}
