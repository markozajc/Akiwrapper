package com.markozajc.akiwrapper.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.*;

import javax.annotation.*;

import org.json.*;
import org.slf4j.*;

import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.impl.immutable.*;
import com.markozajc.akiwrapper.core.exceptions.*;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl.Token;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import kong.unirest.*;

public final class Route {

	public static final UnirestInstance UNIREST;

	static {
		UNIREST = Unirest.spawnInstance();
		UNIREST.config()
			.setDefaultHeader("Accept",
							  "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*. q=0.01")
			.setDefaultHeader("Accept-Language", "en-US,en.q=0.9,ar.q=0.8")
			.setDefaultHeader("X-Requested-With", "XMLHttpRequest")
			.setDefaultHeader("Sec-Fetch-Dest", "empty")
			.setDefaultHeader("Sec-Fetch-Mode", "cors")
			.setDefaultHeader("Sec-Fetch-Site", "same-origin")
			.setDefaultHeader("Connection", "keep-alive")
			.setDefaultHeader("User-Agent",
							  "Mozilla/5.0 (Windows NT 10.0. Win64. x64) AppleWebKit/537.36" +
								  "(KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36")
			.setDefaultHeader("Referer", "https://en.akinator.com/game")
			.cookieSpec("ignore");
		// Configures necessary headers
		// https://github.com/markozajc/Akiwrapper/issues/14#issuecomment-612255613
		// Also disable cookies because they aren't necessary.
		// NB: use "standard" if they become necessary. Default value causes log spam and
		// probably doesn't even store cookies right.
	}

	public static final String BASE_AKINATOR_URL = "https://en.akinator.com";
	private static final String SERVER_DOWN_STATUS_MESSAGE = "server down";
	private static final Pattern FILTER_ARGUMENT_PATTERN = Pattern.compile("\\{FILTER\\}");

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
	 * {@link #getRequest(String, boolean, String...)} <br>
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

	public static void testResponse(@Nonnull JSONObject response) {
		Status completion = new StatusImpl(response);
		if (completion.getLevel() == Status.Level.ERROR) {
			if (SERVER_DOWN_STATUS_MESSAGE.equalsIgnoreCase(completion.getReason()))
				throw new ServerUnavailableException(completion);

			throw new StatusException(completion);
		}
	}

	@Nonnull
	public Request getRequest(@Nonnull String baseUrl, boolean filterProfanity, @Nullable Token token,
							  @Nonnull String... parameters) {
		if (parameters.length < this.parametersQuantity)
			throw new IllegalArgumentException("Insufficient parameters; Expected " + this.parametersQuantity +
				", got " +
				parameters.length);

		String[] encodedParams = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			try {
				encodedParams[i] = URLEncoder.encode(parameters[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// Can not occur
				throw new RuntimeException(e);
			}
		}

		String formattedPath = this.path;

		Matcher matcher = FILTER_ARGUMENT_PATTERN.matcher(formattedPath);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; matcher.find(); i++) {
			matcher.appendReplacement(sb, filterProfanity ? this.filterArguments[i] : "");
		}
		matcher.appendTail(sb);
		formattedPath = sb.toString();

		formattedPath = formattedPath.replace("{API_KEY}", ApiKey.accquireApiKey().compile().replace("%", "%%"));

		formattedPath = String.format(formattedPath, (Object[]) encodedParams);

		String jQueryCallback = "jQuery331023608747682107778_" + System.currentTimeMillis();
		formattedPath = formattedPath + "&callback=" + jQueryCallback;

		if (token != null)
			formattedPath = formattedPath + token.compile();

		return new Request(baseUrl + formattedPath, jQueryCallback);
	}

	@Nonnull
	public Request getRequest(@Nonnull String baseUrl, boolean filterProfanity, @Nonnull String... parameters) {
		return this.getRequest(baseUrl, filterProfanity, null, parameters);
	}

	@Nonnull
	public String getPath() {
		return this.path;
	}

	public int getParametersQuantity() {
		return this.parametersQuantity;
	}

	public static class Request {

		private static final Logger LOG = LoggerFactory.getLogger(Route.Request.class);

		@Nonnull
		private final String url;
		@Nonnull
		private final String jQueryCallback;

		Request(@Nonnull String url, @Nonnull String jQueryCallback) {
			this.jQueryCallback = jQueryCallback;
			this.url = url;
		}

		@Nonnull
		public JSONObject getJSON() {
			return getJSON(defaultRunChecks);
		}

		@Nonnull
		public JSONObject getJSON(boolean runChecks) {
			String response = UNIREST.get(this.url).asString().getBody().replace(this.jQueryCallback, "");
			response = response.substring(1, response.length() - 1);
			LOG.trace("--> {}", this.url);
			LOG.trace("<-- {}", response);
			JSONObject result;
			try {
				result = new JSONObject(response);
			} catch (JSONException e) {
				LOG.error("Failed to parse JSON from the API server", e);
				throw new StatusException(new StatusImpl("AW-KO - COULDN'T PARSE JSON"));
			}

			if (runChecks)
				testResponse(result);

			return result;
		}

	}
}
