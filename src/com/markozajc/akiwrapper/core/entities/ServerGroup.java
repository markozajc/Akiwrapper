package com.markozajc.akiwrapper.core.entities;

import java.util.List;

import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.utils.Servers;

public interface ServerGroup {

	Language getLocalization();

	List<Server> getServers();

	/**
	 * Returns the first available API server of the ones in {@link Servers#SERVERS}.
	 * 
	 * @return the first available server or null if no servers are currently available
	 *         (very, very unlikely, almost impossible)
	 */
	@Nullable
	default Server getFirstAvailableServer() {
		return getServers().stream().filter(s -> Servers.isUp(s)).findAny().orElse(null);
	}

}
