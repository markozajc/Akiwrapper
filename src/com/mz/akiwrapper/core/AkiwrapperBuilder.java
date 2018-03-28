package com.mz.akiwrapper.core;

import com.mz.akiwrapper.Akiwrapper;
import com.mz.akiwrapper.core.entities.Server;
import com.mz.akiwrapper.core.impl.AkiwrapperImpl;

public class AkiwrapperBuilder {

	public static final String DEFAULT_NAME = "Akiwrapper";

	public String name;
	public String userAgent;
	public Server server;

	public AkiwrapperBuilder() {
		this.name = null;
		this.userAgent = null;
		this.server = Server.getFirstAvailableServer();
	}

	public String getName() {
		return name;
	}

	public AkiwrapperBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public AkiwrapperBuilder setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public Server getServer() {
		return server;
	}

	public AkiwrapperBuilder setServer(Server server) {
		this.server = server;
		return this;
	}
	
	public Akiwrapper build() {
		return new AkiwrapperImpl(server, name, userAgent);
	}

}
