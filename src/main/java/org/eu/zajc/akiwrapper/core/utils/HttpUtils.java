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
package org.eu.zajc.akiwrapper.core.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

import java.net.URLEncoder;
import java.util.Map;

import javax.annotation.*;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal
public class HttpUtils {

	@Nonnull
	@SuppressWarnings("null")
	public static String urlEncodeForm(Map<? extends Object, ? extends Object> parameters) {
		return parameters.entrySet()
			.stream()
			.map(e -> urlEncode(e.getKey()) + "=" + urlEncode(e.getValue()))
			.collect(joining("&"));
	}

	@Nonnull
	@SuppressWarnings("null")
	private static String urlEncode(@Nullable Object o) {
		return URLEncoder.encode(String.valueOf(o), UTF_8);
	}

	@Nonnull
	public static String getStatusLine(int code) { // NOSONAR
		return code + " " + getStatusLine(code);
	}

	@Nonnull
	public static String getStatusReason(int code) { // NOSONAR
		switch (code) { // NOSONAR
			// informational
			case 100:
				return "Continue";
			case 101:
				return "Switching Protocol";
			case 102:
				return "Processing";
			case 103:
				return "Early Hints";

			// successful
			case 200:
				return "OK";
			case 201:
				return "Created";
			case 202:
				return "Accepted";
			case 203:
				return "Non-Authoritative Information";
			case 204:
				return "No Content";
			case 205:
				return "Reset Content";
			case 206:
				return "Partial Content";
			case 207:
				return "Multi Status";
			case 208:
				return "Already Reported";
			case 226:
				return "IM Used";

			// redirection
			case 300:
				return "Multiple Choice";
			case 301:
				return "Moved Permanently";
			case 302:
				return "Found";
			case 303:
				return "See Other";
			case 304:
				return "Not Modified";
			case 305:
				return "Use Proxy"; // deprecated
			case 307:
				return "Temporary Redirect";
			case 308:
				return "Permanent Redirect";

			// client error
			case 400:
				return "Bad Request";
			case 401:
				return "Unauthorized";
			case 402:
				return "Payment Required";
			case 403:
				return "Forbidden";
			case 404:
				return "Not Found";
			case 405:
				return "Method Not Allowed";
			case 406:
				return "Not Acceptable";
			case 407:
				return "Proxy Authentication Required";
			case 408:
				return "Request Timeout";
			case 409:
				return "Conflict";
			case 410:
				return "Gone";
			case 411:
				return "Length Required";
			case 412:
				return "Precondition Failed";
			case 413:
				return "Payload Too Long";
			case 414:
				return "URI Too Long";
			case 415:
				return "Unsupported Media Type";
			case 416:
				return "Range Not Satisfiable";
			case 417:
				return "Expectation Failed";
			case 418:
				return "I'm a Teapot";
			case 421:
				return "Misdirected Request";
			case 422:
				return "Unprocessable Entity";
			case 423:
				return "Locked";
			case 424:
				return "Failed Dependency";
			case 425:
				return "Too Early";
			case 426:
				return "Upgrade Required";
			case 428:
				return "Precondition Required";
			case 429:
				return "Too Many Requests";
			case 431:
				return "Request Header Fields Too Large";
			case 451:
				return "Unavailable for Legal Reasons";

			// server error
			case 500:
				return "Internal Server Error";
			case 501:
				return "Not Implemented";
			case 502:
				return "Bad Gateway";
			case 503:
				return "Service Unavailable";
			case 504:
				return "Gateway Timeout";
			case 505:
				return "HTTP Version Not Supported";
			case 506:
				return "Variant Also Negotiates";
			case 507:
				return "Insufficient Storage";
			case 508:
				return "Loop Detected";
			case 510:
				return "Not Extended";
			case 511:
				return "Network Authentication Required";

			default:
				return "";
		}
	}

	private HttpUtils() {}

}
