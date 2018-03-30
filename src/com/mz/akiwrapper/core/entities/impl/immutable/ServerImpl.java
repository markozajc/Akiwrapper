package com.mz.akiwrapper.core.entities.impl.immutable;

import com.mz.akiwrapper.core.entities.Server;

/**
 * An implementation of {@link Server}.
 * 
 * @author Marko Zajc
 */
public class ServerImpl implements Server {

	private final String baseUrl;

	/**
	 * Creates a new instance of {@link ServerImpl}.
	 * 
	 * @param baseUrl
	 *            server's base URL (eg. {@code http://api-en1.akinator.com/ws/}}.
	 */
	public ServerImpl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getBaseUrl() {
		return this.baseUrl;
	}

}
