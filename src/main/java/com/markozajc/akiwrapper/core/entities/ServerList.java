package com.markozajc.akiwrapper.core.entities;

import java.sql.ResultSet;
import java.util.List;
import java.util.regex.Matcher;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;

/**
 * A representation of multiple {@link Server}s at once. While a single
 * {@link Server} instance is <i>usually</i> enough, it might go down. Akiwrapper
 * will in that case automatically find the next available one in a given
 * {@link ServerList}, until it hits into one that is available. If none are
 * available, it will fail with a {@link ServerUnavailableException}.<br>
 * Note: being available means succeeding to create a new session. Failure can mean
 * that either:
 * <ul>
 * <li>the server is down, which is very unlikely, but is the best case scenario
 * since no code needs to be fixed,
 * <li>or that Akinator has changed something and Akiwrapper doesn't yet support the
 * change, causing failure on a new session.
 * </ul>
 * The {@link ServerList} acts similarly to a {@link Matcher} or a {@link ResultSet}
 * - you can use it the same way as you would a regular {@link Server}, and if it is
 * down or does not work, you can call {@link #next()} to seamlessly switch to the
 * next instance.
 *
 * @author Marko Zajc
 */
public interface ServerList extends Server {

	/**
	 * Iterates to the next {@link Server} in the queue. Returns of all methods will be
	 * replaced with those of the next server. The return value of this method indicates
	 * success - {@code false} means that there are no more servers in the queue (the
	 * server is not changed), {@code true} means that the server has been changed.
	 *
	 * @return success indicator.
	 */
	boolean next();

	/**
	 * Returns the remaining {@link Server} in the queue plus the current {@link Server}.
	 * Note that calling {@link #next()} removes the cycles to the next server and
	 * removes the current one, meaning that it will not be included in this list.
	 *
	 * @return all servers.
	 */
	@Nonnull
	List<Server> getServers();

	/**
	 * Returns the amount of remaining {@link Server}s in the queue.
	 *
	 * @return amount of remaining servers.
	 */
	int getRemainingSize();

}
