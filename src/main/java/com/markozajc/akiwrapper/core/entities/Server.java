package com.markozajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;

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

		ARABIC("ar"),
		CHINESE("cn"),
		DUTCH("nl"),
		ENGLISH("en"),
		FRENCH("fr"),
		GERMAN("de"),
		HEBREW("il"),
		INDONESIAN("id"),
		ITALIAN("it"),
		JAPANESE("jp"),
		KOREAN("kr"),
		POLISH("pl"),
		PORTUGUESE("pt"),
		RUSSIAN("ru"),
		SPANISH("es"),
		TURKISH("tr");

		private final String id;

		Language(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}
	}

	/**
	 * Server's guess type (referred to as the "subject" in the API). Decides what kind
	 * of things server's guesses will represent.
	 *
	 * @author Marko Zajc
	 */
	@SuppressWarnings("javadoc")
	public enum GuessType {

		ANIMAL("animals new"),
		CHARACTER("characters"),
		OBJECT("objects");

		private final String label;

		GuessType(String label) {
			this.label = label;
		}

		public String getLabel() {
			return this.label;
		}

	}

	/**
	 * @return The base (API's) URL for this server.
	 *
	 * @deprecated Changed for clarification. Use {@link #getApiUrl()} instead.
	 */
	@Deprecated
	@Nonnull
	default String getBaseUrl() {
		return getApiUrl();
	}

	/**
	 * @return The base (API's) URL for this server.
	 */
	@SuppressWarnings("null")
	@Nonnull
	default String getApiUrl() {
		return String.format(Servers.BASE_URL_FORMAT, getHost());
	}

	/**
	 * @return The bare host for this server (in a {@code hostname:port} format).
	 */
	@Nonnull
	String getHost();

	/**
	 * @return This server's localization language. The server will return localized
	 *         elements (eg. questions) depending on its localization language.
	 */
	@Nonnull
	Language getLocalization();

	/**
	 * @return This server's guess type. The server's guesses will be based on the
	 *         subject type.
	 */
	@Nonnull
	GuessType getSubject();

	/**
	 * Check if the current {@link Server} is still available. This is a shortcut for
	 * {@link Servers#isUp(Server)}.
	 *
	 * @return True if that API server is available, false if not.
	 *
	 * @see Servers#isUp(Server)
	 */
	default boolean isUp() {
		return Servers.isUp(this);
	}

}
