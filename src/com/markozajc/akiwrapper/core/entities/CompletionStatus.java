package com.markozajc.akiwrapper.core.entities;

/**
 * An interface used to represent API call's completion status.
 * 
 * @author Marko Zajc
 */
public interface CompletionStatus {

	/**
	 * Indicates API call status level
	 */
	public enum Level {
	/**
	 * Everything is OK, you may continue normally.
	 */
	OK,

	/**
	 * The majority call has completed but something minor might have failed/not
	 * completed.
	 */
	WARNING,

	/**
	 * The call has not completed due to an error
	 */
	ERROR,

	/**
	 * Unknown status (should not ever occur under normal circumstances)
	 */
	UNKNOWN;
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
