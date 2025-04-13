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

import static java.lang.String.format;
import static java.util.Map.entry;

import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.entities.impl.AkiwrapperImpl;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 * A representation of an Akinator API route, used to create {@link Request}s.
 *
 * @author Marko Zajc
 *
 * @see RouteBuilder
 * @see Routes
 */
@SuppressWarnings("javadoc") // internal
public final class Route {

	private static final String URL_FORMAT = "https://%s.akinator.com%s";

	/**
	 * Default HTTP headers passed to Akinator. Assigning this in your code removes
	 * warranty, and should only be done as a workaround when things break.
	 */
	public static String[] defaultHeaders; // NOSONAR
	static {
		var headers = Stream.<Entry<String, String>>builder();
		headers.add(entry("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:137.0) Gecko/20100101 Firefox/137.0"));
		headers.add(entry("Accept", "*/*"));
		headers.add(entry("Accept-Language", "en-US,en;q=0.5"));
		headers.add(entry("Referer", "https://en.akinator.com/game"));
		headers.add(entry("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		headers.add(entry("X-Requested-With", "XMLHttpRequest"));
		headers.add(entry("Origin", "https://en.akinator.com"));
		headers.add(entry("DNT", "1"));
		headers.add(entry("Sec-GPC", "1"));
		headers.add(entry("Sec-Fetch-Dest", "empty"));
		headers.add(entry("Sec-Fetch-Mode", "cors"));
		headers.add(entry("Sec-Fetch-Site", "same-origin"));
		headers.add(entry("Pragma", "no-cache"));
		headers.add(entry("Cache-Control", "no-cache"));
		headers.add(entry("TE", "trailers"));
		defaultHeaders = headers.build().flatMap(e -> Stream.of(e.getKey(), e.getValue())).toArray(String[]::new); // NOSONAR
	}

	private static final String PARAM_PROFANITY_FILTER = "cm";
	private static final String PARAM_THEME = "sid";

	@Nonnull private final String path;
	private final boolean requiresSession;
	@Nonnull private List<String> parameterNames;

	Route(@Nonnull String path, boolean requiresSession, @Nonnull List<String> parameters) {
		this.path = path;
		this.requiresSession = requiresSession;
		this.parameterNames = parameters;
	}

	@Nonnull
	public Request createRequest(@Nonnull AkiwrapperImpl api) {
		URI uri;
		try {
			uri = new URI(format(URL_FORMAT, api.getLanguage().getLanguageCode(), this.path));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		var parameters = new HashMap<String, Object>();
		this.parameterNames.forEach(p -> parameters.put(p, null)); // can't use Collectors.toMap due to null values

		// append common parameters
		parameters.put(PARAM_PROFANITY_FILTER, api.doesFilterProfanity());
		parameters.put(PARAM_THEME, api.getTheme().getId());

		// append session
		if (this.requiresSession) {
			if (api.getSession() == null)
				throw new IllegalStateException("Session is required but not set in the Akiwrapper object");

			api.getSession().apply(parameters);
		}

		return new Request(uri, api.getHttpClient(), parameters);
	}

}
