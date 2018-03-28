package com.mz.akiwrapper.core.entities.impl;

import com.mz.akiwrapper.core.entities.Server;

public class ServerImpl implements Server {

	private final String baseUrl;

	public ServerImpl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getBaseUrl() {
		return this.baseUrl;
	}

}
