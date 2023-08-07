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
package com.github.markozajc.akiwrapper.core.utils.route;

import static com.github.markozajc.akiwrapper.core.utils.route.Route.formatParameters;
import static com.github.markozajc.akiwrapper.core.utils.route.Route.Endpoint.GAME_SERVER;
import static java.lang.System.currentTimeMillis;

import java.util.*;
import java.util.function.Supplier;

import javax.annotation.*;

import com.github.markozajc.akiwrapper.core.utils.route.Route.Endpoint;

@SuppressWarnings("javadoc") // internal util
public class RouteBuilder {

	@Nonnull private final String path;
	@Nonnull private Endpoint endpoint = GAME_SERVER;
	private boolean requiresSession = false;
	private boolean requiresFrontaddr = false;
	private boolean requiresUidExtSession = false;
	private boolean requiresUrlApiWs = false;
	private Map<String, String> constantParameters;
	private Map<String, Supplier<String>> automaticParameters;
	private Map<String, String> profanityDisabledParameters;
	private Map<String, String> profanityEnabledParameters;
	private Set<String> mandatoryParameters;
	private Map<String, String> parameters; // using Map instead of Set to avoid conversion later

	public RouteBuilder(@Nonnull String path) {
		this.path = path;
	}

	@Nonnull
	public RouteBuilder endpoint(@Nonnull Endpoint endpoint) {
		this.endpoint = endpoint;
		return this;
	}

	@Nonnull
	public RouteBuilder requiresSession() {
		this.requiresSession = true;
		return this;
	}

	@Nonnull
	public RouteBuilder requiresFrontaddr() {
		this.requiresFrontaddr = true;
		return this;
	}

	@Nonnull
	public RouteBuilder requiresUidExtSession() {
		this.requiresUidExtSession = true;
		return this;
	}

	@Nonnull
	public RouteBuilder requiresUrlApiWs() {
		this.requiresUrlApiWs = true;
		return this;
	}

	@Nonnull
	public RouteBuilder constantParameter(@Nonnull String key, @Nonnull String value) {
		if (this.constantParameters == null)
			this.constantParameters = new HashMap<>();
		this.constantParameters.put(key, value);
		return this;
	}

	@Nonnull
	public RouteBuilder automaticParameter(@Nonnull String key, @Nonnull Supplier<String> valueSupplier) {
		if (this.automaticParameters == null)
			this.automaticParameters = new HashMap<>();
		this.automaticParameters.put(key, valueSupplier);
		return this;
	}

	@Nonnull
	public RouteBuilder profanityDisabledParameter(@Nonnull String key, @Nonnull String value) {
		if (this.profanityDisabledParameters == null)
			this.profanityDisabledParameters = new HashMap<>();
		this.profanityDisabledParameters.put(key, value);
		return this;
	}

	@Nonnull
	public RouteBuilder profanityEnabledParameter(@Nonnull String key, @Nonnull String value) {
		if (this.profanityEnabledParameters == null)
			this.profanityEnabledParameters = new HashMap<>();
		this.profanityEnabledParameters.put(key, value);
		return this;
	}

	@Nonnull
	public RouteBuilder mandatoryParameter(@Nonnull String key) {
		putParameter(key, null);

		if (this.mandatoryParameters == null)
			this.mandatoryParameters = new HashSet<>();
		this.mandatoryParameters.add(key);

		return this;
	}

	@Nonnull
	public RouteBuilder optionalParameter(@Nonnull String key) {
		putParameter(key, null);
		return this;
	}

	@Nonnull
	public RouteBuilder defaultParameter(@Nonnull String key, @Nonnull String value) {
		putParameter(key, value);
		return this;
	}

	private void putParameter(@Nonnull String key, @Nullable String value) {
		if (this.parameters == null)
			this.parameters = new HashMap<>();

		this.parameters.put(key, value);
	}

	@SuppressWarnings("null")
	public Route build() {
		// these two are required by all routes (the callback one might only be required for
		// NEW_SESSION, but it changes response parsing logic so I'm opting to just put it
		// into all requests for future proofing if not anything else)
		automaticParameter("_", () -> Long.toString(currentTimeMillis()));
		constantParameter("callback", "jQuery");

		String processedPath = this.path;
		if (this.constantParameters != null)
			processedPath += "?" + formatParameters(this.constantParameters);

		String profanityDisabledQuerystring =
			this.profanityDisabledParameters == null ? null : formatParameters(this.profanityDisabledParameters);

		String profanityEnabledQuerystring =
			this.profanityDisabledParameters == null ? null : formatParameters(this.profanityEnabledParameters);

		return new Route(processedPath, this.endpoint, this.requiresSession, this.requiresFrontaddr,
						 this.requiresUidExtSession, this.requiresUrlApiWs, this.automaticParameters,
						 profanityDisabledQuerystring, profanityEnabledQuerystring, this.mandatoryParameters,
						 this.parameters, this.constantParameters != null);
	}

}
