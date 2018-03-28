package com.mz.akiwrapper.core.entities;

public interface CompletionStatus {

	/**
	 * Indicates API call status level
	 */
	public enum Level {
	OK, WARNING, ERROR, UNKNOWN;
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
