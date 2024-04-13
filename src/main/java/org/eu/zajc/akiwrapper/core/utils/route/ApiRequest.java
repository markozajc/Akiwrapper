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
import static java.time.Duration.ofSeconds;
import static kong.unirest.ContentType.APPLICATION_FORM_URLENCODED;
import static org.eu.zajc.akiwrapper.core.utils.Utilities.sleepUnchecked;
import static org.eu.zajc.akiwrapper.core.utils.route.ApiStatus.OK;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

import kong.unirest.*;

@SuppressWarnings("javadoc") // internal util
public class ApiRequest {

	private static final Logger LOG = getLogger(ApiRequest.class);

	private static final int MAX_RETRIES = 5;
	private static final long RETRY_SLEEP = ofSeconds(2).toMillis();

	@Nonnull private final String url;
	@Nonnull private final UnirestInstance unirest;
	@Nonnull private Map<String, Object> parameters;

	ApiRequest(@Nonnull String url, @Nonnull UnirestInstance unirest, @Nonnull Map<String, Object> parameters) {
		this.url = url;
		this.unirest = unirest;
		this.parameters = parameters;
	}

	@Nonnull
	public ApiRequest parameter(@Nonnull String name, @Nonnull Object value) {
		if (this.parameters.containsKey(name))
			this.parameters.put(name, value);
		else
			throw new IllegalArgumentException("Parameter \"" + name + "\" is not defined");

		return this;
	}

	@Nonnull
	public ApiResponse<Element> retrieveDocument() {
		var resp = executeRequest();
		var body = resp.getBody();

		var doc = Jsoup.parse(body);
		var gameRoot = doc.getElementById("game_content");
		if (gameRoot == null)
			throw new MalformedResponseException();

		var status = ApiStatus.fromHtml(gameRoot);
		if (status.isErroneous())
			throw new ServerStatusException(status);

		return new ApiResponse<>(gameRoot, status);
	}

	@Nonnull
	public ApiResponse<JSONObject> retrieveJson() {
		var resp = executeRequest();
		var body = resp.getBody();

		try {
			var json = new JSONObject(body);
			var status = ApiStatus.fromJson(json);
			if (status.isErroneous())
				throw new ServerStatusException(status);

			return new ApiResponse<>(json, status);

		} catch (JSONException e) {
			throw new MalformedResponseException(e);
		}
	}

	@Nonnull
	public ApiResponse<Void> retrieveEmpty() {
		executeRequest();
		return new ApiResponse<>(null, OK);
	}

	@Nonnull
	private HttpResponse<String> executeRequest() {
		if (this.parameters.containsValue(null))
			throw new IllegalStateException("One or more mandatory parameters aren't set");

		return executeRequest(this.url, this.parameters, this.unirest, 0);
	}

	@Nonnull
	private static HttpResponse<String> executeRequest(@Nonnull String url, @Nonnull Map<String, Object> parameters,
													   @Nonnull UnirestInstance unirest, int attempt) {
		var req = unirest.post(url).contentType(APPLICATION_FORM_URLENCODED.getMimeType()).fields(parameters);
		if (LOG.isTraceEnabled())
			req.toSummary().asString().lines().forEach(l -> LOG.trace("--> {}", l));

		var resp = req.asString();
		if (LOG.isTraceEnabled()) {
			LOG.trace("<-- {} {}", resp.getStatus(), resp.getStatusText());
			resp.getHeaders().toString().lines().forEach(l -> LOG.trace("<-- {}", l));
			LOG.trace("<-- ========================");
			resp.getBody().lines().forEach(l -> LOG.trace("<-- {}", l));
		}

		if (resp.getStatus() >= 500) {
			if (attempt < MAX_RETRIES) {
				LOG.trace("Got HTTP {} {}, retrying after {} ms", resp.getStatus(), resp.getStatusText(), RETRY_SLEEP);
				sleepUnchecked(RETRY_SLEEP);
				return executeRequest(url, parameters, unirest, attempt + 1);

			} else {
				throw new AkinatorException(format("Got HTTP %d %s and exceeded re-attempts (%d)", resp.getStatus(),
												   resp.getStatusText(), MAX_RETRIES));
			}

		} else if (!resp.isSuccess()) {
			throw new AkinatorException("Got HTTP %d %s".formatted(resp.getStatus(), resp.getStatusText()));
		}

		return resp;
	}

}
