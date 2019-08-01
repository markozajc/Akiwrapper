package com.markozajc.akiwrapper.core.entities.impl.immutable;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.Server;

/**
 * An implementation of {@link Server}.
 *
 * @author Marko Zajc
 */
public class ServerImpl implements Server {

	@Nonnull
	private final String host;
	@Nonnull
	private final Language localization;

	/**
	 * Creates a new instance of {@link ServerImpl}.
	 *
	 * @param host
	 *            server's host (for example {@code srv1.akinator.com.9100}}.
	 * @param localization
	 *            the localization language of this server
	 */
	public ServerImpl(@Nonnull String host, @Nonnull Language localization) {
		this.host = host;
		this.localization = localization;
	}

	@Override
	public Language getLocalization() {
		return this.localization;
	}

	@Override
	public String getHost() {
		return this.host;
	}

}
