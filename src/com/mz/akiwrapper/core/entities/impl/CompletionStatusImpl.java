package com.mz.akiwrapper.core.entities.impl;

import org.json.JSONObject;

import com.mz.akiwrapper.core.entities.CompletionStatus;

public class CompletionStatusImpl implements CompletionStatus {

	private final String reason;
	private final Level level;

	public CompletionStatusImpl(String completion) {
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

	public CompletionStatusImpl(JSONObject json) {
		this(json.getString("completion"));
	}

	@Override
	public String getReason() {
		return reason;
	}

	@Override
	public Level getLevel() {
		return level;
	}
}
