package com.github.markozajc.akiwrapper.core.utils.route;

import javax.annotation.Nonnull;

import org.json.JSONObject;

import com.github.markozajc.akiwrapper.core.entities.Status;

@SuppressWarnings("javadoc") // internal util
public class Response {

	@Nonnull private final Status status;
	@Nonnull private final JSONObject body;

	public Response(@Nonnull Status status, @Nonnull JSONObject body) {
		this.status = status;
		this.body = body;
	}

	@Nonnull
	public Status getStatus() {
		return this.status;
	}

	@Nonnull
	public JSONObject getBody() {
		return this.body.getJSONObject("parameters");
	}

}
