package com.markozajc.akiwrapper.core.entities;

import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * An interface representing an API server.
 *
 * @author Marko Zajc
 */
public interface Server {

	/**
	 * A localization language specific to a {@link Server} (or a {@link ServerGroup}).
	 *
	 * @author Marko Zajc
	 */
	@SuppressWarnings("javadoc")
	public enum Language {
		ARABIC,
		CHINESE,
		DUTCH,
		ENGLISH,
		FRENCH,
		GERMAN,
		HEBREW,
		ITALIAN,
		JAPANESE,
		KOREAN,
		POLISH,
		PORTUGUESE,
		RUSSIAN,
		SPANISH,
		TURKISH;
	}

	/**
	 * @return the base (API's) URL for this server
	 * @deprecated Changed for clarification. Use {@link #getApiUrl()} instead.
	 */
	@Deprecated
	default String getBaseUrl() {
		return getApiUrl();
	}

	/**
	 * @return the base (API's) URL for this server
	 */
	default String getApiUrl() {
		return String.format(Servers.BASE_URL_FORMAT, getHost());
	}

	/**
	 * @return the bare host for this server (in a {@code hostname:port} format)
	 */
	String getHost();

	/**
	 * @return this server's localization language. The server will return localized
	 *         elements (eg. questions) depending on its localization language
	 */
	Language getLocalization();

	/**
	 * Check if the current {@link Server} is still available. This is a shortcut for
	 * {@link Servers#isUp(Server)}
	 *
	 * @return true if that API server is available, false if not
	 * @see Servers#isUp(Server)
	 */
	default boolean isUp() {
		return Servers.isUp(this);
	}

}
