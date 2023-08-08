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
package org.eu.zajc.akiwrapper.core.utils.route;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.entities.Status;
import org.json.JSONObject;

@SuppressWarnings("javadoc") // internal util
public class Response {

	@Nonnull private final Status status;
	@Nonnull private final JSONObject body;

	public Response(@Nonnull Status status, @Nonnull JSONObject body) {
		this.status = status;
		this.body = body;
	}

	@Nonnull
	public Status getStatus() {
		return this.status;
	}

	@Nonnull
	@SuppressWarnings("null")
	public JSONObject getBody() {
		return this.body.getJSONObject("parameters");
	}

}
