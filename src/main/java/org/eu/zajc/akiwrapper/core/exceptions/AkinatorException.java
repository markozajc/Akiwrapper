//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.eu.zajc.akiwrapper.core.exceptions;

import javax.annotation.Nonnull;

/**
 * The root exception class for exceptions in Akiwrapper.
 *
 * @author Marko Zajc
 */
public class AkinatorException extends RuntimeException {

	@SuppressWarnings("javadoc") // internal
	public AkinatorException() {}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message) {
		super(message);
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @return the request debug information (when available) or an empty string
	 *
	 * @deprecated will always return an empty string. There is no direct replacement for
	 *             this, but enabling trace logs will show contents of HTTP requests and
	 *             responses.
	 */
	@Nonnull
	@Deprecated(since = "2.0", forRemoval = true)
	public String getDebugInformation() {
		return "";
	}

}
