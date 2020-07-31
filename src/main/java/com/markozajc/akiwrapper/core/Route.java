package com.markozajc.akiwrapper.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl.Token;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

/**
 * A class defining various API endpoints (routes).
 *
 * @author Marko Zajc
 */
public class Route {

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
		        "Mozilla/5.0 (Windows NT 10.0. Win64. x64) AppleWebKit/537.36"
		            + "(KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36")
		    .setDefaultHeader("Referer", "https://en.akinator.com/game");
		// Configures necessary headers
		// https://github.com/markozajc/Akiwrapper/issues/14#issuecomment-612255613
	}

	private static final String SERVER_DOWN_STATUS_MESSAGE = "server down";

	private static final Pattern FILTER_ARGUMENT_PATTERN = Pattern.compile("\\{FILTER\\}");

	private static class ApiKey {

		private static final String FORMAT = "frontaddr=%s&uid_ext_session=%s";

		@Nonnull
		private final String sessionUid;
		@Nonnull
		private final String frontAddress;

		ApiKey(@Nonnull String sessionUid, @Nonnull String frontAddress) {
			this.sessionUid = sessionUid;
			this.frontAddress = frontAddress;
		}

		@SuppressWarnings("null")
		@Nonnull
		String compile() {
			try {
				return String.format(FORMAT, URLEncoder.encode(this.frontAddress, "UTF-8"), this.sessionUid);
			} catch (UnsupportedEncodingException e) {
				return ""; // never throws
			}
		}

	}

	private static final String BASE_AKINATOR_URL = "https://en.akinator.com";
	// The base Akinator URL, used for scraping various elements (and not for the API
	// calls)

	private static final Pattern API_KEY_PATTERN = Pattern
	    .compile("var uid_ext_session = '(.*)'\\;\\n.*var frontaddr = '(.*)'\\;");

	/**
	 * Scraps the API key from Akinator's website and stores it for later use.
	 *
	 * @return the API key
	 *
	 * @throws IOException
	 *             in case the API key can't be scraped
	 */
	@SuppressWarnings("null")
	public static ApiKey accquireApiKey() throws IOException {
		Matcher matcher = API_KEY_PATTERN.matcher(UNIREST.get(BASE_AKINATOR_URL + "/game").asString().getBody());
		if (!matcher.find())
			throw new IOException(
			    "Couldn't find the API key! Please consider opening a new ticket at https://github.com/markozajc/Akiwrapper/issues.");

		return new ApiKey(matcher.group(1), matcher.group(2));
	}

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
	public static final Route NEW_SESSION = new Route(1,
	    "https://en.akinator.com/new_session?partner=1&player=website-desktop&constraint=ETAT%%3C%%3E%%27AV%%27&{API_KEY}"
	        + "&soft_constraint={FILTER}&question_filter={FILTER}&_=%s&urlApiWs=%s",
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
	public static final Route CANCEL_ANSWER = new Route(1, "/cancel_answer?step=%s&answer=-1",
	    "&question_filter=cat=1");

	/**
	 * Lists all available guesses. Parameters:
	 * <ol>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route LIST = new Route(1, "/list?mode_question=0&step=%s");

	/**
	 * Tests whether a response is a successful or a failed one.
	 *
	 * @param response
	 *            the response to test
	 *
	 * @throws ServerUnavailableException
	 *             throws if the status is equal to {@link Level#ERROR} and the error
	 *             message hints that the server is down
	 * @throws StatusException
	 *             thrown if the status is equal to {@link Level#ERROR}
	 */
	public static void testResponse(JSONObject response) {
		Status completion = new StatusImpl(response);
		if (completion.getLevel() == Level.ERROR) {
			if (SERVER_DOWN_STATUS_MESSAGE.equalsIgnoreCase(completion.getReason()))
				throw new ServerUnavailableException(completion);

			throw new StatusException(completion);
		}
	}

	private final String path;
	private final String[] filterArguments;

	private final int parametersQuantity;

	private Route(int parameters, String path) {
		this(parameters, path, new String[0]);
	}

	private Route(int parameters, String path, String... filterArguments) {
		this.path = path;
		this.filterArguments = filterArguments.clone();
		this.parametersQuantity = parameters;
	}

	/**
	 * Creates a request for this route that can later be called and converted into a
	 * {@link JSONObject}.
	 *
	 * @param baseUrl
	 *            base (API's) URL
	 * @param filterProfanity
	 *            whether to filter profanity. Akinator's website will automatically
	 *            enable that if you choose an age below 16
	 * @param token
	 *            the token used for session authentication
	 * @param parameters
	 *            parameters to pass to the route (parameters are specified in that
	 *            Route's JavaDoc)
	 *
	 * @return a {@link Request}
	 *
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if you have passed too little parameters
	 */
	public Request getRequest(String baseUrl, boolean filterProfanity, @Nullable Token token,
	                          String... parameters) throws IOException {
		if (parameters.length < this.parametersQuantity)
			throw new IllegalArgumentException(
			    "Insufficient parameters; Expected " + this.parametersQuantity + ", got " + parameters.length);

		String[] encodedParams = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			encodedParams[i] = URLEncoder.encode(parameters[i], "UTF-8");

		String formattedPath = this.path;

		Matcher matcher = FILTER_ARGUMENT_PATTERN.matcher(formattedPath);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; matcher.find(); i++) {
			matcher.appendReplacement(sb, filterProfanity ? this.filterArguments[i] : "");
		}
		matcher.appendTail(sb);
		formattedPath = sb.toString();

		formattedPath = formattedPath.replace("{API_KEY}", accquireApiKey().compile().replace("%", "%%"));

		formattedPath = String.format(formattedPath, (Object[]) encodedParams);

		String jQueryCallback = "jQuery331023608747682107778_" + System.currentTimeMillis();
		formattedPath = formattedPath + "&callback=" + jQueryCallback;

		if (token != null)
			formattedPath = formattedPath + token.compile();

		return new Request(baseUrl + formattedPath, jQueryCallback);
	}

	/**
	 * Creates a request for this route that can later be called and converted into a
	 * {@link JSONObject}.
	 *
	 * @param baseUrl
	 *            base (API's) URL
	 * @param filterProfanity
	 *            whether to filter profanity. Akinator's website will automatically
	 *            enable that if you choose an age below 16
	 * @param parameters
	 *            parameters to pass to the route (parameters are specified in that
	 *            Route's JavaDoc)
	 *
	 * @return a callable request
	 *
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if you have passed too little parameters
	 */
	public Request getRequest(String baseUrl, boolean filterProfanity, String... parameters) throws IOException {
		return this.getRequest(baseUrl, filterProfanity, null, parameters);
	}

	/**
	 * @return route's path (unformatted)
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return the minimal quantity of parameters you would have to pass to
	 *         {@link #getRequest(String, boolean, String...)}
	 */
	public int getParametersQuantity() {
		return this.parametersQuantity;
	}

	/**
	 * An executable request.
	 *
	 * @author Marko Zajc
	 */
	public static class Request {

		private final String url;
		private final String jQueryCallback;

		Request(String url, String jQueryCallback) {
			this.jQueryCallback = jQueryCallback;
			this.url = url;
		}

		/**
		 * Requests the server and returns the route's content as a {@link JSONObject}.
		 *
		 * @return route's content
		 *
		 * @throws ServerUnavailableException
		 *             if the server has gone down
		 */
		public JSONObject getJSON() {
			return getJSON(defaultRunChecks);
		}

		/**
		 * Requests the server and returns the route's content as a {@link JSONObject}.
		 *
		 * @param runChecks
		 *            whether to run checks for error status codes.
		 *
		 * @return route's content
		 *
		 * @throws ServerUnavailableException
		 *             if the server has gone down
		 * @throws StatusException
		 *             if the server returns an error response
		 *
		 */
		public JSONObject getJSON(boolean runChecks) {
			String response = UNIREST.get(this.url).asString().getBody().replace(this.jQueryCallback, "");
			response = response.substring(1, response.length() - 1);
			System.out.println(this.url); // TODO remove
			System.out.println(response); // TODO remove
			JSONObject result = new JSONObject(response);

			if (runChecks)
				testResponse(result);

			return result;
		}

	}
}
