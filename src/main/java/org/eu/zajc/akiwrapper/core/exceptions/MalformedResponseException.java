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

/**
 * Indicates an unexpected or erroneous response from Akinator's API.
 *
 * @author Marko Zajc
 */
public class MalformedResponseException extends AkinatorException {

	@SuppressWarnings("javadoc") // internal
	public MalformedResponseException() {
		this(null);
	}

	@SuppressWarnings("javadoc") // internal
	public MalformedResponseException(Throwable cause) {
		super("Akinator has returned a malformed response. Please try again after a while, or open an issue on" +
			"https://github.com/markozajc/Akiwrapper/issues is the error persists.", cause);
	}

}
