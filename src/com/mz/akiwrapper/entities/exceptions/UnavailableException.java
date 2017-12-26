package com.mz.akiwrapper.entities.exceptions;

/**
 * An exception representing that the current API server went down
 */
public class UnavailableException extends RuntimeException {

	private String serverUrl;

	public UnavailableException(String serverUrl) {
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
