package com.markozajc.akiwrapper.core.entities.impl.immutable;

import com.markozajc.akiwrapper.core.entities.Server;

/**
 * An implementation of {@link Server}.
 * 
 * @author Marko Zajc
 */
public class ServerImpl implements Server {

	private final String baseUrl;
	private final Language localization;

	/**
	 * Creates a new instance of {@link ServerImpl}.
	 * 
	 * @param baseUrl
	 *            server's base URL (eg. {@code http://api-en1.akinator.com/ws/}}.
	 */
	public ServerImpl(String baseUrl, Language localization) {
		this.baseUrl = "http://" + baseUrl + "/ws/";
		this.localization = localization;
	}

	@Override
	public String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	public Language getLocalization() {
		return localization;
	}

}
