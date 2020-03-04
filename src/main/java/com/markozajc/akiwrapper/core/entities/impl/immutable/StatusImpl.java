package com.markozajc.akiwrapper.core.entities.impl.immutable;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.Status;

/**
 * An implementation of {@link Status}.
 *
 * @author Marko Zajc
 */
public class StatusImpl implements Status {

	private static final String STATUS_FORMAT = "%s - %s";

	private final String reason;
	private final Level level;

	/**
	 * Creates a new {@link StatusImpl} instance from raw parameters.
	 *
	 * @param completion
	 */
	public StatusImpl(String completion) {
		if (completion.toLowerCase().startsWith("ok")) {
			this.level = Level.OK;
			this.reason = null;

		} else {
			this.reason = completion.split(" - ", 2)[1];

			if (completion.toLowerCase().startsWith("warn")) {
				this.level = Level.WARNING;

			} else if (completion.toLowerCase().startsWith("ko")) {
				this.level = Level.ERROR;

			} else {
				this.level = Level.UNKNOWN;
			}
		}
	}

	/**
	 * Creates a new {@link StatusImpl} instance.
	 *
	 * @param json
	 *            completion level (acquired with (Any {@link Route}) &gt;
	 *            {@link JSONObject} completion)
	 */
	public StatusImpl(JSONObject json) {
		this(json.getString("completion"));
	}

	@Override
	public String getReason() {
		return this.reason;
	}

	@Override
	public Level getLevel() {
		return this.level;
	}

	@Override
	public String toString() {
		return String.format(STATUS_FORMAT, this.level.toString(), this.reason);
	}

}
