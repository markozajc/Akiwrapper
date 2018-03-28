package com.mz.akiwrapper.core.exceptions;

/**
 * An exception representing that the current API server went down
 */
public class ServerUnavailableException extends RuntimeException {

	private String serverUrl;

	public ServerUnavailableException(String serverUrl) {
		super();
		this.serverUrl = serverUrl;
	}

	/**
	 * Returns the URL of the API server that went down
	 * 
	 * @return API server's URL
	 */
	public String getServerUrl() {
		return serverUrl;
	}

}
