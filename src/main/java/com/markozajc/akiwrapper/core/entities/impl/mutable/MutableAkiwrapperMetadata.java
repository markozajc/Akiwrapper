package com.markozajc.akiwrapper.core.entities.impl.mutable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * A mutable implementation of {@link AkiwrapperMetadata}.
 *
 * @author Marko Zajc
 */
public abstract class MutableAkiwrapperMetadata extends AkiwrapperMetadata {

	@Nonnull
	protected String name;
	@Nonnull
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
	 * @param localization
	 *            the localization language that will be passed to the API server. This
	 *            affects textual elements such as {@link Question}-s
	 */
	public MutableAkiwrapperMetadata(@Nonnull String name, @Nonnull String userAgent, @Nullable Server server,
	                                 boolean filterProfanity, @Nonnull Language localization) {
		this.name = name;
		this.userAgent = userAgent;
		this.server = server;
		this.filterProfanity = filterProfanity;
		this.localization = localization;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Sets user's name.
	 *
	 * @param name
	 * 
	 * @return current instance, used for chaining
	 * 
	 * @see #getName()
	 */
	public MutableAkiwrapperMetadata setName(@Nonnull String name) {
		this.name = name;

		return this;
	}

	@Override
	public String getUserAgent() {
		return this.userAgent;
	}

	/**
	 * Sets the user-agent.
	 *
	 * @param userAgent
	 * 
	 * @return current instance, used for chaining
	 * 
	 * @see #getUserAgent()
	 */
	public MutableAkiwrapperMetadata setUserAgent(@Nonnull String userAgent) {
		this.userAgent = userAgent;

		return this;
	}

	@Override
	public Server getServer() {
		return this.server;
	}

	/**
	 * Sets the API server.
	 *
	 * @param server
	 * 
	 * @return current instance, used for chaining
	 * 
	 * @see #getServer()
	 * @see Servers#SERVER_GROUPS
	 * @see ServerGroup#getFirstAvailableServer()
	 */
	@Nonnull
	public MutableAkiwrapperMetadata setServer(@Nullable Server server) {
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
	 * 
	 * @return current instance, used for chaining
	 * 
	 * @see #doesFilterProfanity()
	 */
	@Nonnull
	public MutableAkiwrapperMetadata setFilterProfanity(boolean filterProfanity) {
		this.filterProfanity = filterProfanity;

		return this;
	}

	@Override
	public Language getLocalization() {
		return this.localization;
	}

	/**
	 * Sets the localization language.
	 *
	 * @param localization
	 * 
	 * @return current instance, used for chaining
	 * 
	 * @see #getLocalization()
	 */
	@Nonnull
	public MutableAkiwrapperMetadata setLocalization(@Nonnull Language localization) {
		this.localization = localization;

		return this;
	}

}
