package com.mz.akiwrapper.core.entities;

import com.mz.akiwrapper.core.utils.Servers;

/**
 * An interface representing an API server.
 * 
 * @author Marko Zajc
 */
public interface Server {

	/**
	 * @return the base (API's) URL for this server
	 */
	String getBaseUrl();

	/**
	 * Check if the current {@link Server} is still available. This is a shortcut
	 * for {@link Servers#isUp(Server)}
	 * 
	 * @return true if that API server is available, false if not
	 * @see Servers#isUp(Server)
	 */
	default boolean isUp() {
		return Servers.isUp(this);
	}

}
