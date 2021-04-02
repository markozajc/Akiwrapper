package com.markozajc.akiwrapper.core.entities.impl.immutable;

import javax.annotation.*;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.Status;

public class StatusImpl implements Status {

	private static final long serialVersionUID = 1;

	private static final String DIVIDER = " - ";
	private static final String STATUS_FORMAT = "%s" + DIVIDER + "%s";

	@Nullable
	private final String reason;
	@Nonnull
	private final Level level;

	public StatusImpl(@Nonnull String completion) {
		this.level = determineLevel(completion);
		this.reason = determineReason(completion);
	}

	@SuppressWarnings("null")
	public StatusImpl(@Nonnull JSONObject json) {
		this(json.getString("completion"));
	}

	@Nullable
	private static String determineReason(@Nonnull String completion) {
		int reasonSplitIndex = completion.indexOf(DIVIDER);
		if (reasonSplitIndex != -1)
			return completion.substring(reasonSplitIndex + DIVIDER.length());
		return null;
	}

	@Nonnull
	private static Level determineLevel(@Nonnull String completion) {
		for (Level iteratedLevel : Level.values())
			if (completion.toLowerCase().startsWith(iteratedLevel.toString().toLowerCase()))
				return iteratedLevel;
		return Level.UNKNOWN;
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
		if (getReason() == null)
			return getLevel().toString();
		else
			return String.format(STATUS_FORMAT, getLevel().toString(), getReason());
	}

}
