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
package org.eu.zajc.akiwrapper.core.utils;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.utils.route.Route;

import kong.unirest.UnirestInstance;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class ApiKey {

	private static final String EXCEPTION_NO_KEY = "Couldn't find the API key!" +
		"Please consider opening a new ticket at https://github.com/markozajc/Akiwrapper/issues.";

	private static final Pattern API_KEY_PATTERN =
		compile("var uid_ext_session = '(.*)'\\;\\n.*var frontaddr = '(.*)'\\;");

	@Nonnull private final String uidExtSession;
	@Nonnull private final String frontaddr;

	ApiKey(@Nonnull String sessionUid, @Nonnull String frontAddress) {
		this.uidExtSession = sessionUid;
		this.frontaddr = frontAddress;
	}

	@Nonnull
	public String asQuerystringUidExtSession() {
		return "uid_ext_session=" + this.uidExtSession;
	}

	@Nonnull
	public String asQuerystringFrontaddr() {
		return "frontaddr=" + URLEncoder.encode(this.frontaddr, UTF_8);
	}

	@SuppressWarnings("null")
	public static ApiKey accquireApiKey(UnirestInstance unirest) {
		var page = unirest.get(Route.WEBSITE_URL + "/game").asString().getBody();
		var matcher = API_KEY_PATTERN.matcher(page);
		if (matcher.find()) {
			return new ApiKey(matcher.group(1), matcher.group(2));

		} else {
			throw new IllegalStateException(format(EXCEPTION_NO_KEY));
		}
	}

}
