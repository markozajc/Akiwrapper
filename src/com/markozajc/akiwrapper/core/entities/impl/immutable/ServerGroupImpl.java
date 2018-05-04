package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.util.Arrays;
import java.util.List;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;

public class ServerGroupImpl implements ServerGroup {

	private final Language localization;
	private final List<Server> servers;

	public ServerGroupImpl(Language localization, List<Server> servers) throws IllegalArgumentException {
		if (servers.stream().anyMatch(s -> !s.getLocalization().equals(localization)))
			throw new IllegalArgumentException(
					"One or more servers do not have the same localization as this ServerGroup ("
							+ localization.toString() + "!");

		this.localization = localization;
		this.servers = servers;
	}

	public ServerGroupImpl(Language localization, Server... servers) throws IllegalArgumentException {
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
