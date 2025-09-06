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
package org.eu.zajc.akiwrapper.core.utils.route;

import static java.util.Collections.*;

import java.util.*;

import javax.annotation.Nonnull;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 * A builder for {@link Route}.
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal
public class RouteBuilder {

	@Nonnull private final String path;
	private boolean requiresSession = false;
	@SuppressWarnings("null") @Nonnull private Map<String, Object> staticParameters = emptyMap();
	@SuppressWarnings("null") @Nonnull private Set<String> variableParameterNames = emptySet();

	public RouteBuilder(@Nonnull String path) {
		this.path = path;
	}

	@Nonnull
	public RouteBuilder requiresSession() {
		this.requiresSession = true;
		return this;
	}

	@Nonnull
	public RouteBuilder staticParameter(@Nonnull Map<String, Object> staticParameters) {
		this.staticParameters = staticParameters;
		return this;
	}

	@Nonnull
	@SuppressWarnings("null")
	public RouteBuilder variableParameters(@Nonnull String... names) {
		this.variableParameterNames = Set.of(names);
		return this;
	}

	@Nonnull
	@SuppressWarnings("null")
	public Route build() {
		return new Route(this.path, this.requiresSession, this.staticParameters,
						 List.copyOf(this.variableParameterNames));
	}

}
