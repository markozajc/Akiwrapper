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

import java.util.*;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.entities.impl.AkiwrapperImpl;

@SuppressWarnings("javadoc") // internal util
public final class Route {

	private static final String URL_FORMAT = "https://%s.akinator.com%s";

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
	@SuppressWarnings({ "resource", "null" })
	public Request createRequest(@Nonnull AkiwrapperImpl api) {
		var url = format(URL_FORMAT, api.getLanguage().getLanguageCode(), this.path);

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

		return new Request(url, api.getUnirest(), parameters);
	}

}
