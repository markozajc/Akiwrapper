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

	public enum Reason {

		OK,
		QUESTIONS_EXHAUSTED,
		LIBRARY_FAILURE,
		SERVER_FAILURE,
		UNKNOWN

	}

	/**
	 * Returns the level of this status. Status level indicates severity of the status.
	 *
	 * @return status level
	 */
	Level getLevel();

	/**
	 * Returns the status message or {@code null} if it was not specified. Note that the
	 * status message is usually pretty cryptic and won't mean much to regular users or
	 * anyone not experienced with the Akinator API. If you need something something more
	 * tangible, use {@link #getReason()}.
	 *
	 * @return status message
	 */
	@Nullable
	String getMessage();

	/**
	 * Returns the status reason, which is picked from a list of predefined values or
	 * {@link Reason#UNKNOWN} if it's meaning/significance are unknown. This generally
	 * helps distinguish between a usage error (a problem with your code), a library
	 * error (a problem with Akiwrapper that should be reported), a server error (a
	 * problem with Akinator's servers that only they can fix), or an unproblematic
	 * status.
	 *
	 * @return status {@link Reason}
	 */
	@Nonnull
	Reason getReason();

}
