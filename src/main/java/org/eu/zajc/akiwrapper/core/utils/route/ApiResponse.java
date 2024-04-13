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

@SuppressWarnings("javadoc") // internal util
public class ApiResponse<T> {

	private final T body;
	@Nonnull private final ApiStatus status;

	public ApiResponse(T body, @Nonnull ApiStatus status) {
		this.status = status;
		this.body = body;
	}

	public T getBody() {
		return this.body;
	}

	@Nonnull
	public ApiStatus getStatus() {
		return this.status;
	}

}
