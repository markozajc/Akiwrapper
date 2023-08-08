//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
 *
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.eu.zajc.akiwrapper.core.entities.impl;

import static org.eu.zajc.akiwrapper.core.entities.Status.Reason.UNKNOWN;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.entities.Status;
import org.json.JSONObject;

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
