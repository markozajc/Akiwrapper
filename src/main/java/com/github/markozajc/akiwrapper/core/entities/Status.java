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
package com.github.markozajc.akiwrapper.core.entities;

import java.io.Serializable;

import javax.annotation.*;

import com.github.markozajc.akiwrapper.core.exceptions.ServerStatusException;

/**
 * An interface used to represent API call's completion status.
 *
 * @author Marko Zajc
 */
public interface Status extends Serializable {

	/**
	 * Indicates the severity of a response from the API server.
	 */
	public enum Level {

		/**
		 * Everything is OK, you may continue normally.
		 */
		OK("OK"),

		/**
		 * The action has completed, but something minor might have failed/not completed.
		 */
		WARNING("WARN"),

		/**
		 * The action has not completed due to an error.
		 */
		ERROR("KO"),

		/**
		 * Unknown status (should not ever occur under normal circumstances), indicates that
		 * the status level doesn't match any of the known ones.
		 */
		UNKNOWN("");

		private String name;

		Level(String name) {
			this.name = name;
		}

		@Override
		/**
		 * Returns this level's name as provided by the official API.
		 */
		public String toString() {
			return this.name;
		}

		@Nonnull
		@SuppressWarnings("javadoc") // internal impl
		public static Level fromString(@Nonnull String completion) {
			for (Level iteratedLevel : Level.values())
				if (completion.toUpperCase().startsWith(iteratedLevel.toString()))
					return iteratedLevel;

			return UNKNOWN;
		}

	}

	/**
	 * A less cryptic form of the status message, which helps you distinguish between a
	 * usage error (a problem with your code), a library error (a problem with Akiwrapper
	 * that should be reported), a server error (a problem with Akinator's servers that
	 * only they can fix), or an unproblematic status.
	 */
	public enum Reason {

		/**
		 * <b>Note:</b> This {@link Reason} should generally not find its way into a
		 * {@link ServerStatusException}, please open an issue if it ever does.<br>
		 * <br>
		 * The status is non-erroneous.
		 */
		OK,
		/**
		 * <b>Note:</b> This {@link Reason} should generally not find its way into a
		 * {@link ServerStatusException}, please open an issue if it ever does.<br>
		 * <br>
		 * The status is non-erroneous and the questions have been exhausted.
		 */
		QUESTIONS_EXHAUSTED,
		/**
		 * The status is erroneous and likely caused by a bug in the library. Please open an
		 * issue if this occurs.
		 */
		LIBRARY_FAILURE,
		/**
		 * The status is erroneous and likely caused by a problem with Akinator's servers.
		 * Please only open an issue if this occurs consistently for a period of time.
		 */
		SERVER_FAILURE,
		/**
		 * The reason is unknown. Refer to the status message and level for more details.
		 */
		UNKNOWN

	}

	/**
	 * Returns the level of this status. Status level indicates severity of the status.
	 *
	 * @return status level
	 */
	Level getLevel();

	/**
	 * Returns the status message or {@code null} if it was not specified. Note that the
	 * status message is usually pretty cryptic and won't mean much to regular users or
	 * anyone not experienced with the Akinator API. If you need something something more
	 * tangible, use {@link #getReason()}.
	 *
	 * @return status message
	 */
	@Nullable
	String getMessage();

	/**
	 * Returns the status reason, which is picked from a list of predefined values or
	 * {@link Reason#UNKNOWN} if it's meaning/significance are unknown. This generally
	 * helps distinguish between a usage error (a problem with your code), a library
	 * error (a problem with Akiwrapper that should be reported), a server error (a
	 * problem with Akinator's servers that only they can fix), or an unproblematic
	 * status.
	 *
	 * @return status {@link Reason}
	 */
	@Nonnull
	Reason getReason();

}
