package com.markozajc.akiwrapper.core;

import static com.markozajc.akiwrapper.core.entities.Status.Level.ERROR;
import static com.markozajc.akiwrapper.core.entities.impl.immutable.ApiKey.accquireApiKey;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;

import java.net.URLEncoder;
import java.util.regex.*;

import javax.annotation.*;

import org.json.*;
import org.slf4j.*;

import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.*;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl.Session;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import kong.unirest.UnirestInstance;

public final class Route {

	public static final String BASE_AKINATOR_URL = "https://en.akinator.com";
	private static final String SERVER_DOWN_STATUS_MESSAGE = "server down";
	private static final Pattern FILTER_ARGUMENT_PATTERN = compile("\\{FILTER\\}");

	/**
	 * Whether to run status checks on {@link Request#getJSON()} by default. Setting this
	 * to false may result in unpredicted exceptions! <b>You usually don't need to alter
	 * this value</b>
	 */
	@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
	public static boolean defaultRunChecks = true; // NOSONAR

	/**
	 * Creates a new session for further gameplay.<br>
	 * <b>Caution!</b> Because this endpoint uses a static hostname, you <u>must</u> pass
	 * an empty string to {@code baseUrl} of
	 * {@link #createRequest(UnirestInstance, String, boolean, Object...)} <br>
	 * Parameters:
	 * <ol>
	 * <li>Current time in milliseconds</li>
	 * <li>API server's URL</li>
	 * </ol>
	 */
	public static final Route NEW_SESSION =
		new Route(1,
				  BASE_AKINATOR_URL +
					  "/new_session?partner=1&player=website-desktop&constraint=ETAT%%3C%%3E%%27AV%%27&{API_KEY}" +
					  "&soft_constraint={FILTER}&question_filter={FILTER}&_=%s&urlApiWs=%s",
				  "ETAT=%%27EN%%27", "cat=1");

	/**
	 * Answers a question. Parameters:
	 * <ol>
	 * <li>Current step</li>
	 * <li>Answer's ID</li>
	 * </ol>
	 */
	public static final Route ANSWER = new Route(2, "/answer?step=%s&answer=%s", "&question_filter=cat=1");

	/**
	 * Cancels (undoes) an answer. Parameters:
	 * <ol>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route CANCEL_ANSWER =
		new Route(1, "/cancel_answer?step=%s&answer=-1", "&question_filter=cat=1");

	/**
	 * Lists all available guesses. Parameters:
	 * <ol>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route LIST = new Route(1, "/list?mode_question=0&step=%s");

	@Nonnull
	private final String path;
	@Nonnull
	private final String[] filterArguments;

	private final int parametersQuantity;

	private Route(int parameters, @Nonnull String path) {
		this(parameters, path, new String[0]);
	}

	private Route(int parameters, @Nonnull String path, @Nonnull String... filterArguments) {
		this.path = path;
		this.filterArguments = filterArguments.clone();
		this.parametersQuantity = parameters;
	}

	/**
	 * @deprecated Use
	 *             {@link #createRequest(UnirestInstance,String,boolean,Session,String...)}
	 *             instead
	 */
	@Nonnull
	@Deprecated(since = "1.5.2", forRemoval = true)
	public Request getRequest(@Nonnull UnirestInstance unirest, @Nonnull String baseUrl, boolean filterProfanity,
							  @Nullable Session token, @Nonnull String... parameters) {
		return createRequest(unirest, baseUrl, filterProfanity, token, (Object[]) parameters);
	}

	@Nonnull
	public Request createRequest(@Nonnull UnirestInstance unirest, @Nonnull String baseUrl, boolean filterProfanity,
								 @Nullable Session token, @Nonnull Object... parameters) {
		if (parameters.length < this.parametersQuantity)
			throw new IllegalArgumentException("Insufficient parameters; Expected " + this.parametersQuantity +
				", got " +
				parameters.length);

		String[] encodedParams = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			encodedParams[i] = URLEncoder.encode(parameters[i].toString(), UTF_8);
		}

		String formattedPath = this.path;

		Matcher matcher = FILTER_ARGUMENT_PATTERN.matcher(formattedPath);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; matcher.find(); i++) {
			matcher.appendReplacement(sb, filterProfanity ? this.filterArguments[i] : "");
		}
		matcher.appendTail(sb);
		formattedPath = sb.toString();
		formattedPath = formattedPath.replace("{API_KEY}", accquireApiKey(unirest).querystring().replace("%", "%%"));
		formattedPath = format(formattedPath, (Object[]) encodedParams);

		String jQueryCallback = "jQuery331023608747682107778_" + currentTimeMillis();
		formattedPath = formattedPath + "&callback=" + jQueryCallback;

		if (token != null)
			formattedPath = formattedPath + token.querystring();

		return new Request(unirest, baseUrl + formattedPath, jQueryCallback.length());
	}

	/**
	 * @deprecated Use {@link #createRequest(UnirestInstance,String,boolean,Object...)}
	 *             instead
	 */
	@Nonnull
	@Deprecated(since = "1.5.2", forRemoval = true)
	public Request getRequest(@Nonnull UnirestInstance unirest, @Nonnull String baseUrl, boolean filterProfanity,
							  @Nonnull String... parameters) {
		return createRequest(unirest, baseUrl, filterProfanity, (Object[]) parameters);
	}

	@Nonnull
	public Request createRequest(@Nonnull UnirestInstance unirest, @Nonnull String baseUrl, boolean filterProfanity,
								 @Nonnull Object... parameters) {
		return this.createRequest(unirest, baseUrl, filterProfanity, null, parameters);
	}

	@Nonnull
	public String getPath() {
		return this.path;
	}

	/**
	 * @deprecated Use {@link #getParameterCount()} instead
	 */
	@Deprecated(since = "1.5.2", forRemoval = true)
	public int getParametersQuantity() {
		return getParameterCount();
	}

	public int getParameterCount() {
		return this.parametersQuantity;
	}

	public static class Request {

		private static final Logger LOG = LoggerFactory.getLogger(Route.Request.class);

		@Nonnull
		private final UnirestInstance unirest;
		@Nonnull
		private final String url;
		private final int jQueryCallbackLength;

		Request(@Nonnull UnirestInstance unirest, @Nonnull String url, int jQueryCallbackLength) {
			this.unirest = unirest;
			this.jQueryCallbackLength = jQueryCallbackLength;
			this.url = url;
		}

		@Nonnull
		public JSONObject getJSON() {
			return getJSON(defaultRunChecks);
		}

		@Nonnull
		public JSONObject getJSON(boolean runChecks) {
			var response = this.unirest.get(this.url).asString().getBody();
			response = response.substring(this.jQueryCallbackLength + 1, response.length() - 1);

			LOG.trace("--> {}", this.url);
			LOG.trace("<-- {}", response);

			try {
				var result = new JSONObject(response);

				if (runChecks)
					ensureSuccessful(result);

				return result;

			} catch (JSONException e) {
				LOG.error("Failed to parse JSON from the API server", e);
				throw new StatusException(new StatusImpl("AW-KO - COULDN'T PARSE JSON"));
			}
		}

		static void ensureSuccessful(@Nonnull JSONObject response) {
			Status completion = new StatusImpl(response);
			if (completion.getLevel() == ERROR) {
				if (SERVER_DOWN_STATUS_MESSAGE.equalsIgnoreCase(completion.getReason()))
					throw new ServerUnavailableException(completion);

				throw new StatusException(completion);
			}
		}
	}

}
