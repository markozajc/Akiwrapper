package com.markozajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * A representation of an API server. All requests (except for
 * {@link Route#NEW_SESSION} are passed to an such server. Each server has a
 * predefined {@link Language} and {@link GuessType}.
 *
 * @author Marko Zajc
 */
public interface Server {

	/**
	 * A language specific to a {@link Server}.
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
	 * of things server's guesses will represent.<br>
	 * <b>Caution!</b> Not all {@link Language}s support all {@link GuessType}s. The
	 * standard ones seem to be {@link #ANIMAL}, {@link #CHARACTER}, and {@link #OBJECT},
	 * but you might still face {@link ServerNotFoundException}s using them or other
	 * ones.
	 *
	 * @author Marko Zajc
	 */
	@SuppressWarnings("javadoc")
	public enum GuessType {

		ANIMAL(14),
		MOVIE_TV_SHOW(13),
		PLACE(7),
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
	 * Server's host name. As the people behind Akinator tend to mix up their servers and
	 * the API in general, this should only fetch values from the server-listing endpoint
	 * (which is done in {@link Servers#getServers()}. The host is a valid URL, complete
	 * with the path to the endpoint.<br>
	 * Example: {@code https://srv3.akinator.com:9331/ws}
	 *
	 * @return server's host.
	 */
	@Nonnull
	String getUrl();

	/**
	 * Returns this {@link Server}'s {@link Language}. The server will return localized
	 * {@link Question}s and {@link Guess}es depending on its {@link Language}.
	 *
	 * @return server's language.
	 */
	@Nonnull
	Language getLanguage();

	/**
	 * Returns this server's {@link GuessType}. The server will be returning guesses
	 * based on that type (also referred to as the subject).
	 *
	 * @return server's guess type.
	 */
	@Nonnull
	GuessType getGuessType();

}
