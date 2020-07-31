package com.markozajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

		@Nullable
		public static Language getById(@Nonnull String id) {
			for (Language language : Language.values())
				if (id.equals(language.getId()))
					return language;
			return null;
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

		ANIMAL(14),
		CHARACTER(1),
		OBJECT(2);

		private final int id;

		GuessType(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		@Nullable
		public static GuessType getById(int id) {
			for (GuessType guessType : GuessType.values())
				if (id == guessType.getId())
					return guessType;
			return null;
		}

	}

	/**
	 * @return The base (API's) URL for this server. For example,
	 *         {@code https://srv3.akinator.com:9331/ws}.
	 */
	@Nonnull
	default String getApiUrl() {
		return getHost();
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
	 * @return This server's guess type. The server will be returning guesses based on
	 *         that type.
	 */
	@Nonnull
	GuessType getGuessType();

}
