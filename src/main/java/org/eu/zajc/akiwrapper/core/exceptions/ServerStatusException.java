//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2025 Marko Zajc
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

import org.eu.zajc.akiwrapper.core.utils.route.Status;

/**
 * An exception indicating that the server returned an erroneous {@link Status}.
 *
 * @author Marko Zajc
 */
public class ServerStatusException extends AkinatorException {

	private final Status status;

	@SuppressWarnings("javadoc") // internal
	public ServerStatusException(Status status) {
		super("Got an erroneous status: " + status.toString());
		this.status = status;
	}

	/**
	 * @return the erroneous {@link Status} returned by the API.
	 */
	public Status getStatus() {
		return this.status;
	}

}
