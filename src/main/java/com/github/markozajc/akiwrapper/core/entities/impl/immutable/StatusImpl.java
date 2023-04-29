package com.github.markozajc.akiwrapper.core.entities.impl.immutable;

import static java.lang.String.format;

import javax.annotation.*;

import org.json.JSONObject;

import com.github.markozajc.akiwrapper.core.entities.Status;

public class StatusImpl implements Status {

	private static final String DIVIDER = " - ";
	private static final String STATUS_FORMAT = "%s" + DIVIDER + "%s";

	public static final StatusImpl STATUS_OK = new StatusImpl(Level.OK, null);

	@Nullable private final String reason;
	@Nonnull private final Level level;

	private StatusImpl(@Nonnull Level level, @Nullable String reason) {
		this.level = level;
		this.reason = reason;
	}

	public StatusImpl(@Nonnull String completion) {
		this(Level.fromString(completion), determineReason(completion));
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
			return format(STATUS_FORMAT, getLevel().toString(), getReason());
	}

}
