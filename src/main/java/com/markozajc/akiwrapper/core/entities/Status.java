package com.markozajc.akiwrapper.core.entities;

/**
 * An interface used to represent API call's completion status.
 *
 * @author Marko Zajc
 */
public interface Status {

	/**
	 * Indicates API call status level
	 */
	public enum Level {

		/**
		 * Everything is OK, you may continue normally.
		 */
		OK("OK"),

		/**
		 * The majority call has completed but something minor might have failed/not
		 * completed.
		 */
		WARNING("WARN"),

		/**
		 * The call has not completed due to an error
		 */
		ERROR("KO"),

		/**
		 * Unknown status (should not ever occur under normal circumstances)
		 */
		UNKNOWN("");

		private String name;

		private Level(String name) {
			this.name = name;
		}

		@Override
		/**
		 * Returns this level's name as provided by the official API.
		 */
		public String toString() {
			return this.name;
		}

	}

	/**
	 * Returns error level
	 *
	 * @return status level
	 */
	Level getLevel();

	/**
	 * Returns error reason
	 *
	 * @return error reason or null if level is OK
	 */
	String getReason();

}
