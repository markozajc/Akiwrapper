package com.markozajc.akiwrapper.core.entities;

/**
 * A set of vital data used in API calls and such.
 * 
 * @author Marko Zajc
 */
public interface AkiwrapperMetadata {

	/**
	 * The default name for new Akiwrapper instances.
	 */
	public static final String DEFAULT_NAME = "AkiwrapperUser";

	/**
	 * The default user-agent for new Akiwrapper instances.
	 */
	public static final String DEFAULT_USER_AGENT = "AkiwrapperClient";

	public static final boolean DEFAULT_FILTER_PROFANITY = false;

	/**
	 * @return user's name, does not have any bigger impact on gameplay
	 */
	String getName();

	/**
	 * @return user-agent used in HTTP requests
	 */
	String getUserAgent();

	/**
	 * @return the API server used for all requests. All API servers have equal data and
	 *         endpoints but some might be down so you should never hard-code usage of a
	 *         specific API server
	 */
	Server getServer();

	boolean doesFilterProfanity();

}
