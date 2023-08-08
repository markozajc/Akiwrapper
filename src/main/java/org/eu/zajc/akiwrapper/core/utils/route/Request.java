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
package org.eu.zajc.akiwrapper.core.utils.route;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.joining;
import static org.eu.zajc.akiwrapper.core.entities.Status.Level.ERROR;
import static org.eu.zajc.akiwrapper.core.utils.Utilities.sleepUnchecked;
import static org.eu.zajc.akiwrapper.core.utils.route.Route.*;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.*;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.entities.impl.StatusImpl;
import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.json.*;
import org.slf4j.Logger;

import kong.unirest.*;

@SuppressWarnings("javadoc") // internal util
public class Request {

	private static final Logger LOG = getLogger(Request.class);

	private static final int MAX_RETRIES = 5;
	private static final long RETRY_SLEEP = ofSeconds(2).toMillis();

	@Nonnull private final String url;
	@Nonnull private final UnirestInstance unirest;
	private Set<String> mandatoryParameters;
	private Map<String, String> parameters;
	private final boolean urlHasQuerystring;

	Request(@Nonnull String url, @Nonnull UnirestInstance unirest, @Nullable Set<String> mandatoryParameters,
			@Nullable Map<String, String> parameters, boolean pathHasQuerystring) {
		this.url = url;
		this.unirest = unirest;
		this.urlHasQuerystring = pathHasQuerystring;

		if (mandatoryParameters != null)
			this.mandatoryParameters = new HashSet<>(mandatoryParameters);
		else
			this.mandatoryParameters = null;

		if (parameters != null)
			this.parameters = new HashMap<>(parameters);
		else
			this.parameters = null;
	}

	@Nonnull
	@SuppressWarnings("null")
	public Request parameter(@Nonnull String name, int value) {
		parameter(name, Integer.toString(value));
		return this;
	}

	@Nonnull
	public Request parameter(@Nonnull String name, @Nonnull String value) {
		if (this.parameters != null && this.parameters.containsKey(name)) {
			this.parameters.put(name, value);

			if (this.mandatoryParameters != null)
				this.mandatoryParameters.remove(name);

		} else {
			throw new IllegalArgumentException("Parameter \"" + name + "\" is not defined");
		}
		return this;
	}

	@Nonnull
	@SuppressWarnings("null")
	public Response execute() {
		checkState();

		String processedUrl = this.url;
		boolean hasQuerystring = this.urlHasQuerystring;

		if (this.parameters != null && !this.parameters.isEmpty())
			processedUrl += formatQuerystring(formatParameters(this.parameters), hasQuerystring);

		var response = executeRequest(processedUrl, this.unirest, 0);
		var json = response.getBody();

		LOG.trace("<-- {}", json);
		json = json.substring(7 /* "jQuery(" */, json.length() - 1 /* ")" */); // cut the callback

		try {
			var body = new JSONObject(json);
			var status = StatusImpl.fromJson(body);
			if (status.getLevel() == ERROR)
				throw new ServerStatusException(status, processedUrl, response);

			return new Response(status, body);

		} catch (JSONException e) {
			throw new AkinatorException("Couldn't parse a server response", e, processedUrl, response);
		}
	}

	@Nonnull
	private static HttpResponse<String> executeRequest(@Nonnull String processedUrl, @Nonnull UnirestInstance unirest,
													   int attempt) {
		LOG.trace("--> {}", processedUrl);
		var response = unirest.get(processedUrl).asString();

		if (response.getStatus() >= 500) {
			if (attempt < MAX_RETRIES) {
				LOG.trace("Got HTTP {} {}, retrying after {} ms", response.getStatus(), response.getStatusText(),
						  RETRY_SLEEP);
				sleepUnchecked(RETRY_SLEEP);
				return executeRequest(processedUrl, unirest, attempt + 1);

			} else {
				var message = format("Got HTTP %d %s and exceeded re-attempts (%d)", response.getStatus(),
									 response.getStatusText(), MAX_RETRIES);
				throw new AkinatorException(message);
			}
		}

		return response;
	}

	private void checkState() {
		if (this.mandatoryParameters != null && !this.mandatoryParameters.isEmpty()) {
			var unset = this.mandatoryParameters.stream().collect(joining(", "));
			throw new IllegalStateException("Some mandatory parameters aren't set: " + unset);
		}
	}

}
