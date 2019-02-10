package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;

/**
 * An implementation of {@link ServerGroup}.
 *
 * @author Marko Zajc
 */
public class ServerGroupImpl implements ServerGroup {

	private final Language localization;
	private final List<Server> servers;

	/**
	 * Creates a new {@link ServerGroupImpl} instance.
	 *
	 * @param localization
	 *            language of this {@link ServerGroupImpl}
	 * @param servers
	 *            servers of this {@link ServerGroupImpl}
	 * @throws IllegalArgumentException
	 *             in case one or more of the given servers from {@code servers} do not
	 *             have the same localization as {@code localization}.
	 */
	public ServerGroupImpl(Language localization, List<Server> servers) {
		if (servers.stream().anyMatch(s -> !s.getLocalization().equals(localization)))
			throw new IllegalArgumentException(
					"One or more servers do not have the same localization as this ServerGroup ("
							+ localization.toString() + "!");

		this.localization = localization;
		this.servers = Collections.unmodifiableList(servers);
	}

	/**
	 * Creates a new {@link ServerGroupImpl} instance.
	 *
	 * @param localization
	 *            language of this {@link ServerGroupImpl}
	 * @param servers
	 *            servers of this {@link ServerGroupImpl} (as varargs)
	 * @throws IllegalArgumentException
	 *             in case one or more of the given servers from {@code servers} do not
	 *             have the same localization as {@code localization}.
	 */
	public ServerGroupImpl(Language localization, Server... servers) {
		this(localization, Arrays.asList(servers));
	}

	@Override
	public Language getLocalization() {
		return this.localization;
	}

	@Override
	public List<Server> getServers() {
		return this.servers;
	}

}
