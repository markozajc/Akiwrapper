package com.github.markozajc.akiwrapper.core.utils.route;

import static com.github.markozajc.akiwrapper.core.entities.Status.Level.ERROR;
import static com.github.markozajc.akiwrapper.core.utils.route.Route.*;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.*;

import javax.annotation.*;

import org.json.*;
import org.slf4j.Logger;

import com.github.markozajc.akiwrapper.core.entities.impl.StatusImpl;
import com.github.markozajc.akiwrapper.core.exceptions.*;

import kong.unirest.UnirestInstance;

@SuppressWarnings("javadoc") // internal util
public class Request {

	private static final Logger LOG = getLogger(Request.class);

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

		LOG.trace("--> {}", processedUrl);
		var response = this.unirest.get(processedUrl).asString();
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

	private void checkState() {
		if (this.mandatoryParameters != null && !this.mandatoryParameters.isEmpty()) {
			var unset = this.mandatoryParameters.stream().collect(joining(", "));
			throw new IllegalStateException("Some mandatory parameters aren't set: " + unset);
		}
	}

}
