package com.markozajc.akiwrapper.core.entities;

import java.util.List;

import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * An interface representing a group of API servers. Servers are (usually) grouped by
 * their assigned language.
 * 
 * @author Marko Zajc
 */
public interface ServerGroup {

	/**
	 * @return current language of this server group
	 */
	Language getLocalization();

	/**
	 * @return an unmodifiable list of servers of this {@link ServerGroup}
	 */
	List<Server> getServers();

	/**
	 * @return the first available server of this {@link ServerGroup}. The chances of
	 *         this returning {@code null} (aka all servers of this group are down)
	 *         depend on this {@link ServerGroup}'s size. You should choose calling this
	 *         method over getting a server manually with {@link #getServers()}
	 */
	@Nullable
	default Server getFirstAvailableServer() {
		return getServers().stream().filter(s -> {
			boolean up = Servers.isUp(s);
			return up;
		}).findFirst().orElse(null);
	}

}
