package com.github.markozajc.akiwrapper.core.entities.impl;

import static com.github.markozajc.akiwrapper.core.entities.Status.Reason.UNKNOWN;

import javax.annotation.*;

import org.json.JSONObject;

import com.github.markozajc.akiwrapper.core.entities.Status;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class StatusImpl implements Status {

	private static final String DIVIDER = " - ";

	@Nonnull private final Level level;
	@Nullable private final String message;
	@Nonnull private final Reason reason;

	public static StatusImpl fromCompletion(@Nonnull String completion) {
		var level = Level.fromString(completion);
		var message = determineMessage(completion);
		var reason = resolveReason(level, message);
		return new StatusImpl(level, message, reason);
	}

	@SuppressWarnings("null")
	public static StatusImpl fromJson(@Nonnull JSONObject json) {
		return fromCompletion(json.getString("completion"));
	}

	private StatusImpl(@Nonnull Level level, @Nullable String message, @Nonnull Reason reason) {
		this.level = level;
		this.message = message;
		this.reason = reason;
	}

	@Nullable
	private static String determineMessage(@Nonnull String completion) {
		int reasonSplitIndex = completion.indexOf(DIVIDER);
		if (reasonSplitIndex != -1)
			return completion.substring(reasonSplitIndex + DIVIDER.length());
		return null;
	}

	@Override
	public Level getLevel() {
		return this.level;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public Reason getReason() {
		return this.reason;
	}

	@Nonnull
	public static Reason resolveReason(@Nonnull Level level, @Nullable String message) {
		if (level == Level.OK && message == null) {
			return Reason.OK;
		} else if (level == Level.WARNING && message != null && message.equals("NO QUESTION")) {
			return Reason.QUESTIONS_EXHAUSTED;
		} else if (level == Level.ERROR && message != null) {
			if (message.equals("TECHNICAL ERROR"))
				return Reason.SERVER_FAILURE;
			else if (message.equals("MISSING KEY") || message.equals("ELEM LIST IS EMPTY")
				|| message.equals("MISSING PARAMETERS") || message.equals("UNAUTHORIZED"))
				return Reason.LIBRARY_FAILURE;
		}

		return UNKNOWN;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		sb.append(this.level);

		if (this.message != null) {
			sb.append(" - ");
			sb.append(this.message);
		}

		if (this.reason != UNKNOWN) {
			sb.append(" (");
			sb.append(this.reason);
			sb.append(')');
		}

		return sb.toString();
	}

}
