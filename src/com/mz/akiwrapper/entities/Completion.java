package com.mz.akiwrapper.entities;

import org.json.JSONObject;

public class Completion {

	private String reason;
	private Level errLevel;

	/**
	 * Indicates API call error level; OK is success, WARN is warning, KO is failure
	 * and UNKNOWN is unknown error level
	 */
	public enum Level {
		OK, WARN, KO, UNKNOWN;
	}

	/**
	 * A class used to represent completion of an API call
	 * 
	 * @param completion
	 *            completion object in returned JSON
	 */
	public Completion(String completion) {
		if (completion.toLowerCase().startsWith("ok")) {
			this.errLevel = Level.OK;

		} else {
			this.reason = completion.split(" - ", 2)[1];

			if (completion.toLowerCase().startsWith("warn")) {
				this.errLevel = Level.WARN;

			} else if (completion.toLowerCase().startsWith("ko")) {
				this.errLevel = Level.KO;

			} else {
				this.errLevel = Level.UNKNOWN;
			}
		}
	}

	public Completion(JSONObject json) {
		this(json.getString("completion"));
	}
	
	/**
	 * Returns error reason
	 * 
	 * @return error reason or null if errLevel is OK
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Returns error level
	 * 
	 * @return
	 */
	public Level getErrLevel() {
		return errLevel;
	}
}
