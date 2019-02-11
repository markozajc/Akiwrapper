package com.markozajc.akiwrapper.core.entities.impl.immutable;

import com.markozajc.akiwrapper.core.entities.Server;

/**
 * An implementation of {@link Server}.
 *
 * @author Marko Zajc
 */
public class ServerImpl implements Server {

	private final String host;
	private final Language localization;

	/**
	 * Creates a new instance of {@link ServerImpl}.
	 *
	 * @param host
	 *            server's host (for example {@code srv1.akinator.com.9100}}.
	 * @param localization
	 *            the localization language of this server
	 */
	public ServerImpl(String host, Language localization) {
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
