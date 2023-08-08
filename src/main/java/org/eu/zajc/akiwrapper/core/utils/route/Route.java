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

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.eu.zajc.akiwrapper.core.utils.route.Route.Endpoint.GAME_SERVER;

import java.net.URLEncoder;
import java.util.*;
import java.util.function.Supplier;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.impl.AkiwrapperImpl;

@SuppressWarnings("javadoc") // internal util
public final class Route {

	public static final String WEBSITE_URL = "https://en.akinator.com";

	@Nonnull private final String path;
	@Nonnull private final Endpoint endpoint;
	private final boolean requiresSession;
	private final boolean requiresFrontaddr;
	private final boolean requiresUidExtSession;
	private final boolean requiresUrlApiWs;
	private final Map<String, Supplier<String>> automaticParameters;
	@Nullable private final String profanityDisabledQuerystring;
	@Nullable private final String profanityEnabledQuerystring;
	@Nullable private Set<String> mandatoryParameters;
	@Nullable private Map<String, String> parameters;
	private boolean pathHasQuerystring;

	Route(@Nonnull String path, @Nonnull Endpoint endpoint, boolean requiresSession, boolean requiresFrontaddr,
		  boolean requiresUidExtSession, boolean requiresUrlApiWs,
		  @Nullable Map<String, Supplier<String>> automaticParameters, @Nullable String profanityDisabledQuerystring,
		  @Nullable String profanityEnabledQuerystring, @Nullable Set<String> mandatoryParameters,
		  @Nullable Map<String, String> parameters, boolean pathHasQuerystring) {
		this.path = path;
		this.endpoint = endpoint;
		this.requiresSession = requiresSession;
		this.requiresFrontaddr = requiresFrontaddr;
		this.requiresUidExtSession = requiresUidExtSession;
		this.requiresUrlApiWs = requiresUrlApiWs;
		this.automaticParameters = automaticParameters;
		this.profanityDisabledQuerystring = profanityDisabledQuerystring;
		this.profanityEnabledQuerystring = profanityEnabledQuerystring;
		this.mandatoryParameters = mandatoryParameters;
		this.parameters = parameters;
		this.pathHasQuerystring = pathHasQuerystring;
	}

	@Nonnull
	@SuppressWarnings({ "resource", "null" })
	public Request createRequest(@Nonnull AkiwrapperImpl api) {
		boolean hasQuerystring = this.pathHasQuerystring;

		var url = new StringBuilder();
		if (this.endpoint == GAME_SERVER) {
			url.append(api.getServer().getUrl());
		} else {
			url.append(WEBSITE_URL);
		}

		url.append(this.path);

		// generate and append automatic parameters
		if (this.automaticParameters != null && !this.automaticParameters.isEmpty()) {
			url.append(formatQuerystring(formatParameters(this.automaticParameters), hasQuerystring));
			hasQuerystring = true;
		}

		// append urlApiWs
		if (this.requiresUrlApiWs) {
			url.append(formatQuerystring(api.getServer().asUrlApiWs(), hasQuerystring));
			hasQuerystring = true;
		}

		// append session
		if (this.requiresSession) {
			if (api.getSession() == null)
				throw new IllegalStateException("Session is required but not set in the Akiwrapper object");

			url.append(formatQuerystring(api.getSession().asQuerystring(), hasQuerystring));
			hasQuerystring = true;
		}

		// append frontaddr (of api key)
		if (this.requiresFrontaddr) {
			url.append(formatQuerystring(api.getApiKey().asQuerystringFrontaddr(), hasQuerystring));
			hasQuerystring = true;
		}

		// append uid_ext_session (of api key)
		if (this.requiresUidExtSession) {
			url.append(formatQuerystring(api.getApiKey().asQuerystringUidExtSession(), hasQuerystring));
			hasQuerystring = true;
		}

		// append profanity parameters
		if (api.doesFilterProfanity()) {
			if (this.profanityDisabledQuerystring != null) {
				url.append(formatQuerystring(this.profanityDisabledQuerystring, hasQuerystring));
				hasQuerystring = true;
			}

		} else if (this.profanityEnabledQuerystring != null) {
			url.append(formatQuerystring(this.profanityEnabledQuerystring, hasQuerystring));
			hasQuerystring = true;
		}

		return new Request(url.toString(), api.getUnirest(), this.mandatoryParameters, this.parameters, hasQuerystring);
	}

	@Nonnull
	static String formatQuerystring(@Nonnull String querystring, boolean pathHasQuerystring) {
		return (pathHasQuerystring ? '&' : '?') + querystring;
	}

	@Nonnull
	@SuppressWarnings("null")
	static String formatParameters(@Nonnull Map<String, ?> parameters) {
		return parameters.entrySet().stream().filter(e -> e.getValue() != null).map(e -> {
			Object value = e.getValue();
			if (value instanceof Supplier)
				value = ((Supplier<?>) value).get();

			return format("%s=%s", e.getKey(), URLEncoder.encode(value.toString(), UTF_8));
		}).collect(joining("&"));
	}

	enum Endpoint {
		WEBSITE, // en.akinator.com
		GAME_SERVER // srvX.akinator.com
	}

}
