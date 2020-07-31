package com.markozajc.akiwrapper.core.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;

/**
 * An exception indicating that the currently used {@link Server} has gone offline.
 *
 * @author Marko Zajc
 */
public class ServerUnavailableException extends StatusException {

	private final String serverUrl;

	/**
	 * Constructs a new {@link ServerUnavailableException} instance for a single
	 * {@link Server}.
	 *
	 * @param server
	 *            Unavailable {@link Server}
	 */
	public ServerUnavailableException(@Nonnull Server server) {
		super(new StatusImpl("KO - SERVER DOWN"));
		this.serverUrl = server.getApiUrl();
	}

	/**
	 * Constructs a new {@link ServerUnavailableException} instance for multiple
	 * {@link Server}s.
	 *
	 * @param servers
	 *            {@link Collection} of unavailable {@link Server}s
	 */
	public ServerUnavailableException(Collection<Server> servers) {
		super(new StatusImpl("KO - SERVER DOWN"));
		this.serverUrl = servers.stream().map(Server::getApiUrl).collect(Collectors.joining(", "));
	}

	/**
	 * Returns the URL of the {@link Server} that went down. Will return a string of
	 * multiple URLs delimited by commas in case the {@link Collection} of servers was
	 * passed to the constructor.
	 *
	 * @return URL(s) of {@link Server}(s)
	 */
	public String getServerUrl() {
		return this.serverUrl;
	}

}
