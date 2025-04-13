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

import static java.lang.Thread.sleep;
import static java.net.http.HttpClient.Version.HTTP_2;
import static java.time.Duration.ofSeconds;
import static org.eu.zajc.akiwrapper.core.utils.HttpUtils.*;
import static org.eu.zajc.akiwrapper.core.utils.route.Route.defaultHeaders;
import static org.eu.zajc.akiwrapper.core.utils.route.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.eu.zajc.akiwrapper.core.utils.*;
import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 * An executable HTTP request for a {@link Route}.
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal
public class Request {

	private static final Logger LOG = getLogger(Request.class);

	private static final int MAX_RETRIES = 5;
	private static final long RETRY_SLEEP = ofSeconds(2).toMillis();

	@Nonnull private final URI uri;
	@Nonnull private final HttpClient http;
	@Nonnull private Map<String, Object> parameters;

	Request(@Nonnull URI uri, @Nonnull HttpClient http, @Nonnull Map<String, Object> parameters) {
		this.uri = uri;
		this.http = http;
		this.parameters = parameters;
	}

	@Nonnull
	public Request parameter(@Nonnull String name, @Nonnull Object value) {
		if (this.parameters.containsKey(name))
			this.parameters.put(name, value);
		else
			throw new IllegalArgumentException("Parameter \"" + name + "\" is not defined");

		return this;
	}

	@Nonnull
	public Response<Element> retrieveDocument() {
		var gameRoot = Jsoup.parse(executeRequest().body()).getElementById("game_content");
		if (gameRoot == null)
			throw new MalformedResponseException();

		var status = Status.fromHtml(gameRoot);
		if (status.isErroneous())
			throw new ServerStatusException(status);

		return new Response<>(gameRoot, status);
	}

	@Nonnull
	public Response<JSONObject> retrieveJson() {
		var body = executeRequest().body();

		try {
			var json = new JSONObject(body);
			var status = Status.fromJson(json);
			if (status.isErroneous())
				throw new ServerStatusException(status);

			return new Response<>(json, status);

		} catch (JSONException e) {
			throw new MalformedResponseException(e);
		}
	}

	@Nonnull
	public Response<Void> retrieveEmpty() {
		executeRequest();
		return new Response<>(null, OK);
	}

	@Nonnull
	private HttpResponse<String> executeRequest() {
		if (this.parameters.containsValue(null))
			throw new IllegalStateException("One or more mandatory parameters aren't set");

		try {
			return executeRequest(this.uri, this.parameters, this.http, 0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw Utilities.asUnchecked(e);
		}
	}

	@Nonnull
	private static HttpResponse<String> executeRequest(@Nonnull URI uri, @Nonnull Map<String, Object> parameters,
													   @Nonnull HttpClient http,
													   int attempt) throws IOException, InterruptedException {
		var req = HttpRequest.newBuilder()
			.version(HTTP_2)
			.POST(new FormBody(parameters))
			.uri(uri)
			.headers(defaultHeaders)
			.build();

		if (LOG.isTraceEnabled()) {
			LOG.trace("--> POST {} HTTP/2", req.uri());
			req.headers().map().forEach((key, values) -> values.forEach(v -> LOG.trace("--> {}: {}", key, v)));
			LOG.trace("-->");
			urlEncodeForm(parameters).lines().forEach(l -> LOG.trace("--> {}", l));
		}

		var resp = http.send(req, BodyHandlers.ofString());
		if (LOG.isTraceEnabled()) {
			LOG.trace("<-- HTTP/2 {}", getStatusLine(resp.statusCode()));
			resp.headers().map().forEach((key, values) -> values.forEach(v -> LOG.trace("<-- {}: {}", key, v)));
			LOG.trace("<--");
			resp.body().lines().forEach(l -> LOG.trace("<-- {}", l));
		}

		if (resp.statusCode() >= 500) {
			if (attempt < MAX_RETRIES) {
				if (LOG.isTraceEnabled())
					LOG.trace("Got HTTP {}, retrying after {} ms", getStatusLine(resp.statusCode()), RETRY_SLEEP);
				sleep(RETRY_SLEEP);
				return executeRequest(uri, parameters, http, attempt + 1);

			} else {
				throw new AkinatorException("Got HTTP " + getStatusLine(resp.statusCode()) +
					" and exceeded re-attempts (" +
					MAX_RETRIES +
					")");
			}

		} else if (resp.statusCode() >= 400) {
			throw new AkinatorException("Got HTTP " + getStatusLine(resp.statusCode()));
		}

		return resp;
	}

}
