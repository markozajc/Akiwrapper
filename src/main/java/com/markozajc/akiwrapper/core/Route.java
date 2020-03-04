package com.markozajc.akiwrapper.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl.Token;
import com.markozajc.akiwrapper.core.utils.HTTPUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A class defining various API endpoints (routes).
 *
 * @author Marko Zajc
 */
public class Route {

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
		Matcher matcher = API_KEY_PATTERN.matcher(
		    new String(HTTPUtils.read(new URL(BASE_AKINATOR_URL + "/game").openConnection()), StandardCharsets.UTF_8));
		if (!matcher.find())
			throw new IOException(
			    "Couldn't scrap the API key! Please consider opening a new ticket at https://github.com/markozajc/Akiwrapper/issues.");

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
	 * Creates a new session for further gameplay. Parameters:
	 * <ol>
	 * <li>Player's name</li>
	 * </ol>
	 */
	public static final Route NEW_SESSION = new Route(1,
	    "new_session?partner=1&player=%s&constraint=ETAT%%3C%%3E%%27AV%%27&{API_KEY}&soft_constraint={FILTER}&question_filter={FILTER}",
	    "ETAT=%%27EN%%27", "cat=1");

	/**
	 * Answers a question. Parameters:
	 * <ol>
	 * <li>Current step</li>
	 * <li>Answer's ID</li>
	 * </ol>
	 */
	public static final Route ANSWER = new Route(2, "answer?step=%s&answer=%s", "&question_filter=cat=1");

	/**
	 * Cancels (undoes) an answer. Parameters:
	 * <ol>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route CANCEL_ANSWER = new Route(1, "cancel_answer?step=%s&answer=-1", "&question_filter=cat=1");

	/**
	 * Lists all available guesses. Parameters:
	 * <ol>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route LIST = new Route(1, "list?mode_question=0&step=%s");

	/**
	 * Tests whether a response is a successful or a failed one.
	 *
	 * @param response
	 *            the response to test
	 * @param server
	 *            the {@link Server} to include in a {@link ServerUnavailableException},
	 *            if it occurs
	 * 
	 * @throws ServerUnavailableException
	 *             throws if the status is equal to {@link Level#ERROR} and the error
	 *             message hints that the server is down
	 * @throws StatusException
	 *             thrown if the status is equal to {@link Level#ERROR}
	 */
	public static void testResponse(JSONObject response, Server server) {
		Status compl = new StatusImpl(response);
		if (compl.getLevel().equals(Level.ERROR)) {
			if (compl.getReason().equalsIgnoreCase("server down")) {
				throw new ServerUnavailableException(server);
			}

			throw new StatusException(compl);
		}
	}

	private final String path;
	private final String[] filterArguments;
	private String userAgent = AkiwrapperMetadata.DEFAULT_USER_AGENT;

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
	 * @return a callable request
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

		return new Request(new URL(baseUrl + formattedPath), this.userAgent, jQueryCallback);
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
	 * Sets the user-agent that will be used in requests for this route. If no user-agent
	 * is specified, {@link AkiwrapperMetadata#DEFAULT_USER_AGENT} will be used.
	 *
	 * @param userAgent
	 * 
	 * @return self, useful for chaining
	 */
	public Route setUserAgent(String userAgent) {
		this.userAgent = userAgent;

		return this;
	}

	/**
	 * @return route's path (unformatted)
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return minimal quantity of parameters you would have to pass to
	 *         {@link #getRequest(String, boolean, String...)}
	 */
	public int getParametersQuantity() {
		return this.parametersQuantity;
	}

	/**
	 * @return user-agent for this route
	 * 
	 * @see #setUserAgent(String)
	 */
	public String getClientBuilder() {
		return this.userAgent;
	}

	/**
	 * A callable request.
	 *
	 * @author Marko Zajc
	 */
	public static class Request {

		/**
		 * The connection timeout in milliseconds. Set this to something lower if you're
		 * going to send a lot of request to not-confirmed servers. Set this to {@code -1} to
		 * use {@link URLConnection}'s default timeout setting. <b>You usually don't need to
		 * alter this value</b>
		 */
		@SuppressFBWarnings({
		    "MS_CANNOT_BE_FINAL", "MS_SHOULD_BE_FINAL"
		})
		public static int connectionTimeout = 2500; // NOSONAR

		URLConnection connection;
		private byte[] bytes = null;
		private final String jQueryCallback;

		Request(URL url, String userAgent, String jQueryCallback) throws IOException {
			this.jQueryCallback = jQueryCallback;
			this.connection = url.openConnection();
			if (connectionTimeout != -1)
				this.connection.setConnectTimeout(connectionTimeout);

			this.connection.setRequestProperty("User-Agent", userAgent);
		}

		/**
		 * Reads content of the request's URL into an array of bytes.
		 *
		 * @return content as a byte array
		 * 
		 * @throws IOException
		 * 
		 * @see String#String(byte[], String)
		 */
		public byte[] read() throws IOException {
			if (this.bytes == null) {
				byte[] newBytes = HTTPUtils.read(this.connection);
				this.bytes = newBytes;
			}

			return this.bytes.clone();
		}

		/**
		 * Requests the server and returns the route's content as a {@link JSONObject}.
		 *
		 * @return route's content
		 * 
		 * @throws IOException
		 * @throws ServerUnavailableException
		 *             in case the server has went down (very unlikely to ever happen)
		 */
		public JSONObject getJSON() throws IOException {
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
		 * @throws IOException
		 * @throws ServerUnavailableException
		 *             thrown if the server has gone down
		 * @throws StatusException
		 *             thrown if the server returns an error response
		 *
		 */
		public JSONObject getJSON(boolean runChecks) throws IOException {
			String response = new String(read(), StandardCharsets.UTF_8).replace(this.jQueryCallback, "");
			response = response.substring(1, response.length() - 1);
			JSONObject result = new JSONObject(response);

			if (runChecks)
				testResponse(result, new Server() {

					@Override
					public Language getLocalization() {
						throw new UnsupportedOperationException(); // testResponse() does not need to know the language
					}

					@Override
					public String getHost() {
						return Request.this.connection.getURL().getHost()
						    + ":"
						    + Request.this.connection.getURL().getPort();
					}

				});

			return result;
		}

	}
}
