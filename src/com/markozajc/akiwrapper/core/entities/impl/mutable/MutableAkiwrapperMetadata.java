package com.markozajc.akiwrapper.core.entities.impl.mutable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;
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

	@Nonnull
	protected Language localization;

	/**
	 * Creates a new {@link MutableAkiwrapperMetadata} instance.
	 * 
	 * @param server
	 *            the API server to use
	 * @param name
	 *            player's name (won't have any huge impact but is still passed to the
	 *            Akinator API for convenience)
	 * @param userAgent
	 *            the user-agent to use
	 * @param filterProfanity
	 *            whether to filter out all profanity elements
	 */
	public MutableAkiwrapperMetadata(String name, String userAgent, Server server, boolean filterProfanity,
			@Nonnull Language localization) {
		this.name = name;
		this.userAgent = userAgent;
		this.server = server;
		this.filterProfanity = filterProfanity;
		this.localization = localization;
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
	 * @see #getName()
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

	@Override
	public Server getServer() {
		return server;
	}

	/**
	 * Sets the API server.
	 * 
	 * @param server
	 * @return current instance, used for chaining
	 * @see #getServer()
	 * @see Servers#SERVER_GROUPS
	 * @see ServerGroup#getFirstAvailableServer()
	 */
	public MutableAkiwrapperMetadata setServer(Server server) {
		this.server = server;

		return this;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	/**
	 * Sets the "filter profanity" mode.
	 * 
	 * @param filterProfanity
	 * @return current instance, used for chaining
	 * @see #doesFilterProfanity()
	 */
	public MutableAkiwrapperMetadata setFilterProfanity(boolean filterProfanity) {
		this.filterProfanity = filterProfanity;

		return this;
	}

	public MutableAkiwrapperMetadata setLocalization(@Nonnull Language localization) {
		this.localization = localization;

		return this;
	}

	@Override
	public Language getLocalization() {
		return localization;
	}

}
