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
package com.github.markozajc.akiwrapper.core.exceptions;

import javax.annotation.*;

import kong.unirest.HttpResponse;

/**
 * The root exception class for exceptions in Akiwrapper.
 *
 * @author Marko Zajc
 */
public class AkinatorException extends RuntimeException {

	private final String requestUrl;
	private final transient HttpResponse<String> response;

	@SuppressWarnings("javadoc") // internal
	public AkinatorException() {
		this.requestUrl = null;
		this.response = null;
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message) {
		super(message);
		this.requestUrl = null;
		this.response = null;
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message, String requestUrl, HttpResponse<String> response) {
		super(message);
		this.requestUrl = requestUrl;
		this.response = response;
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message, Throwable cause) {
		super(message, cause);
		this.requestUrl = null;
		this.response = null;
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message, Throwable cause, String requestUrl, HttpResponse<String> response) {
		super(message, cause);
		this.requestUrl = requestUrl;
		this.response = response;
	}

	@Nullable
	@SuppressWarnings("javadoc") // internal
	public String getRequestUrl() {
		return this.requestUrl;
	}

	@Nullable
	@SuppressWarnings("javadoc") // internal
	public HttpResponse<String> getResponse() {
		return this.response;
	}

	/**
	 * @return the request debug information (when available) or an empty string
	 */
	@Nonnull
	@SuppressWarnings("null")
	public String getDebugInformation() {
		var sb = new StringBuilder();
		if (this.requestUrl != null) {
			sb.append("GET ");
			sb.append(this.requestUrl);
			sb.append("\n\n");
		}

		if (this.response != null) {
			sb.append(this.response.getStatus());
			sb.append(' ');
			sb.append(this.response.getStatusText());
			sb.append('\n');
			sb.append(this.response.getHeaders());
			sb.append("\n\n");
			sb.append(this.response.getBody());
		}

		return sb.toString().strip();
	}

}
