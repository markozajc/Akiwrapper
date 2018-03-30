package com.mz.akiwrapper.core.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import com.mz.akiwrapper.core.entities.Server;

/**
 * An exception representing that the currently used {@link Server} has gone
 * offline.
 */
public class ServerUnavailableException extends RuntimeException {

	private String serverUrl;

	/**
	 * Creates a new {@link ServerUnavailableException} instance for a single
	 * server.
	 * 
	 * @param server
	 */
	public ServerUnavailableException(Server server) {
		super();
		this.serverUrl = server.getBaseUrl();
	}

	/**
	 * Creates a new {@link ServerUnavailableException} instance for multiple
	 * servers.
	 * 
	 * @param servers
	 */
	public ServerUnavailableException(Collection<Server> servers) {
		super();
		this.serverUrl = servers.stream().map(Server::getBaseUrl).collect(Collectors.joining(", "));
	}

	/**
	 * Returns the URL of the API server that went down
	 * 
	 * @return API server's URL
	 */
	public String getServerUrl() {
		return serverUrl;
	}

}
