package com.markozajc.akiwrapper.core.entities.impl.immutable;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;

/**
 * An immutable implementation of {@link AkiwrapperMetadata}.
 * 
 * @author Marko Zajc
 */
public abstract class ImmutableAkiwrapperMetadata implements AkiwrapperMetadata {

	protected final String name;
	protected final String userAgent;
	protected final Server server;
	protected final boolean filterProfanity;

	@Nonnull
	protected final Language localization;

	/**
	 * Creates a new {@link ImmutableAkiwrapperMetadata} instance.
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
	public ImmutableAkiwrapperMetadata(String name, String userAgent, Server server, boolean filterProfanity,
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

	@Override
	public String getUserAgent() {
		return userAgent;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public boolean doesFilterProfanity() {
		return filterProfanity;
	}

	@Override
	public Language getLocalization() {
		return localization;
	}

}
