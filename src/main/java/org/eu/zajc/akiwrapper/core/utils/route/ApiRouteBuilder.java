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

import static java.util.Collections.emptySet;

import java.util.Set;

import javax.annotation.Nonnull;

@SuppressWarnings("javadoc") // internal util
public class ApiRouteBuilder {

	@Nonnull private final String path;
	private boolean requiresSession = false;
	private Set<String> parameterNames;

	public ApiRouteBuilder(@Nonnull String path) {
		this.path = path;
	}

	@Nonnull
	public ApiRouteBuilder requiresSession() {
		this.requiresSession = true;
		return this;
	}

	@Nonnull
	public ApiRouteBuilder parameters(@Nonnull String... names) {
		this.parameterNames = Set.of(names);
		return this;
	}

	@SuppressWarnings("null")
	public ApiRoute build() {
		return new ApiRoute(this.path, this.requiresSession,
						 this.parameterNames == null ? emptySet() : this.parameterNames);
	}

}
