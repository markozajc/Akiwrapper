package com.markozajc.akiwrapper.core.entities.impl.mutable;

import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * A mutable implementation of {@link AkiwrapperMetadata}.
 * 
 * @author Marko Zajc
 */
public abstract class MutableAkiwrapperMetadata implements AkiwrapperMetadata {

	protected String name;
	protected String userAgent;
	protected Server server;
	protected boolean filterProfanity;

	/**
	 * Creates a new {@link MutableAkiwrapperMetadata} instance.
	 * 
	 * @param server
	 *            the API server to use (will be checked with {@link Server#isUp()}
	 *            first).
	 * @param name
	 *            player's name (won't have any huge impact but is still passed to the
	 *            Akinator API for convenience.
	 * @param userAgent
	 *            the user-agent to use
	 */
	public MutableAkiwrapperMetadata(String name, String userAgent, Server server, boolean filterProfanity) {
		this.name = name;
		this.userAgent = userAgent;
		this.server = server;
		this.filterProfanity = filterProfanity;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets user's name.
	 * 
	 * @param name
	 * @return current instance, used for chaining
	 */
	public MutableAkiwrapperMetadata setName(String name) {
		this.name = name;

		return this;
	}

	@Override
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * Sets the user-agent.
	 * 
	 * @param userAgent
	 * @return current instance, used for chaining
	 * @see #getUserAgent()
	 */
	public MutableAkiwrapperMetadata setUserAgent(@Nullable String userAgent) {
		this.userAgent = userAgent;

		return this;
	}

	/**
	 * @return the API server used for all requests. All API servers have equal data and
	 *         endpoints but some might be down so you should never hard-code usage of a
	 *         specific API server
	 */
	@Override
	public Server getServer() {
		return server;
	}

	/**
	 * Sets the API server.
	 * 
	 * @param server
	 * @return current instance, used for chaining
	 * @see Servers#SERVERS
	 * @see Servers#getFirstAvailableServer()
	 */
	public MutableAkiwrapperMetadata setServer(Server server) {
		this.server = server;

		return this;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	public MutableAkiwrapperMetadata setFilterProfanity(boolean filterProfanity) {
		this.filterProfanity = filterProfanity;

		return this;
	}

}
