package com.github.markozajc.akiwrapper.core.entities;

import java.io.Serializable;

import javax.annotation.*;

/**
 * An interface used to represent API call's completion status.
 *
 * @author Marko Zajc
 */
public interface Status extends Serializable {

	/**
	 * Indicates the severity of a response from the API server.
	 */
	public enum Level {

		/**
		 * Everything is OK, you may continue normally.
		 */
		OK("OK"),

		/**
		 * The action has completed, but something minor might have failed/not completed.
		 */
		WARNING("WARN"),

		/**
		 * The action has not completed due to an error.
		 */
		ERROR("KO"),

		/**
		 * The action might have completed completed, but an error has occurred on
		 * Akiwrapper's side. This status is never actually returned by the API, but is made
		 * up by Akiwrapper internally to indicate some errors caused by invalid or
		 * unexpected API responses.
		 */
		AKIWRAPPER_ERROR("AW-KO"),

		/**
		 * Unknown status (should not ever occur under normal circumstances), indicates that
		 * the status level doesn't match any of the known ones.
		 */
		UNKNOWN("");

		private String name;

		Level(String name) {
			this.name = name;
		}

		@Override
		/**
		 * Returns this level's name as provided by the official API.
		 */
		public String toString() {
			return this.name;
		}

		@Nonnull
		@SuppressWarnings("javadoc") // internal impl
		public static Level fromString(@Nonnull String completion) {
			for (Level iteratedLevel : Level.values())
				if (completion.toUpperCase().startsWith(iteratedLevel.toString()))
					return iteratedLevel;

			return UNKNOWN;
		}

	}

	/**
	 * Returns the level of this status. Status level indicates severity of the status.
	 *
	 * @return status level
	 */
	Level getLevel();

	/**
	 * Returns the status reason or {@code null} if it was not specified. Note that the
	 * status reason is usually pretty cryptic and won't mean much to regular users or
	 * anyone not experienced with the Akinator API.
	 *
	 * @return status reason
	 */
	@Nullable
	String getReason();

}
