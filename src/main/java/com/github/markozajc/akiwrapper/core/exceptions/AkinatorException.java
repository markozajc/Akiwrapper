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
