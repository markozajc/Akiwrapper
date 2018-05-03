package com.markozajc.akiwrapper.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;

/**
 * A class defining various API endpoints (routes).
 * 
 * @author Marko Zajc
 */
public class Route {

	/**
	 * Whether to run status checks on {@link Request#getJSON()} by default. Setting this
	 * to false may result in unpredicted exceptions!
	 */
	public static boolean DEFAULT_RUN_CHECKS = true;

	/**
	 * Creates a new session for further gameplay. Parameters:
	 * <ol>
	 * <li>Player's name</li>
	 * </ol>
	 */
	public static final Route NEW_SESSION = new Route(
			"new_session?partner=1&player=%s&constraint=ETAT%%3C%%3E%%27AV%%27",
			"&soft_constraint=ETAT=%27EN%27&question_filter=cat=1", 1);

	/**
	 * Answers a question. Parameters:
	 * <ol>
	 * <li>Session's ID</li>
	 * <li>Session's signature</li>
	 * <li>Current step</li>
	 * <li>Answer's ID</li>
	 * </ol>
	 */
	public static final Route ANSWER = new Route("answer?session=%s&signature=%s&step=%s&answer=%s",
			"&question_filter=cat=1", 4);

	/**
	 * Lists all available guesses. Parameters:
	 * <ol>
	 * <li>Session's ID</li>
	 * <li>Session's signature</li>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route LIST = new Route("list?session=%s&signature=%s&mode_question=0&step=%s", 3);

	private static void testResponse(JSONObject response, Server server) {
		Status compl = new StatusImpl(response);
		if (compl.getLevel().equals(Level.ERROR)) {
			if (compl.getReason().toLowerCase().equals("server down")) {
				throw new ServerUnavailableException(server);
			}

			throw new StatusException(compl);
		}
	}

	private final String path;
	private final String filteredAppendix;
	private String userAgent;

	private final int parametersQuantity;

	private Route(String path, int parameters) {
		this(path, "", parameters);
	}

	private Route(String path, String filteredAppendix, int parameters) {
		this(path, filteredAppendix, parameters, AkiwrapperMetadata.DEFAULT_USER_AGENT);
	}

	private Route(String path, String filteredAppendix, int parameters, String userAgent) {
		this.path = path;
		this.filteredAppendix = filteredAppendix;
		this.parametersQuantity = parameters;
		this.userAgent = userAgent;
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
	 * @return a callable request
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if you have passed too little parameters
	 */
	public Request getRequest(String baseUrl, boolean filterProfanity, String... parameters)
			throws IOException, IllegalArgumentException {
		if (parameters.length < this.parametersQuantity)
			throw new IllegalArgumentException(
					"Insufficient parameters; Expected " + this.parametersQuantity + ", got " + parameters.length);

		String[] encodedParams = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			encodedParams[i] = URLEncoder.encode(parameters[i], "UTF-8");

		return new Request(new URL(baseUrl + String.format(this.path, (Object[]) encodedParams)
				+ (filterProfanity ? this.filteredAppendix : "")), this.userAgent);
	}

	/**
	 * Sets the user-agent that will be used in requests for this route. If no user-agent
	 * is specified, {@link AkiwrapperMetadata#DEFAULT_USER_AGENT} will be used.
	 * 
	 * @param userAgent
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
		return path;
	}

	/**
	 * @return minimal quantity of parameters you would have to pass to
	 *         {@link #getRequest(String, boolean, String...)}
	 */
	public int getParametersQuantity() {
		return parametersQuantity;
	}

	/**
	 * @return user-agent for this route
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

		private URLConnection connection;
		private byte[] bytes = null;

		private Request(URL url, String userAgent) throws IOException {
			this.connection = url.openConnection();
			this.connection.setRequestProperty("User-Agent", userAgent);
		}

		/**
		 * Reads content of the request's URL into an array of bytes.
		 * 
		 * @return content as a byte array
		 * @throws IOException
		 * @see String#String(byte[], String)
		 */
		public byte[] read() throws IOException {
			if (this.bytes == null) {
				try (BufferedInputStream is = new BufferedInputStream(this.connection.getInputStream())) {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

					byte[] chunk = new byte[4096];
					int bytesRead;

					while ((bytesRead = is.read(chunk)) > -1)
						outputStream.write(chunk, 0, bytesRead);

					byte[] result = outputStream.toByteArray();
					this.bytes = result;
					return result;
				}
			}

			return this.bytes.clone();
		}

		/**
		 * Requests the server and returns the route's content as a {@link JSONObject}.
		 * 
		 * @return route's content
		 * @throws IOException
		 * @throws ServerUnavailableException
		 *             in case the server has went down (very unlikely to ever happen)
		 */
		public JSONObject getJSON() throws IOException, ServerUnavailableException {
			return getJSON(DEFAULT_RUN_CHECKS);
		}

		/**
		 * Requests the server and returns the route's content as a {@link JSONObject}.
		 * 
		 * @param runChecks
		 *            whether to run checks for error status codes.
		 * @return route's content
		 * @throws IOException
		 * @throws ServerUnavailableException
		 *             in case the server has went down (very unlikely to ever happen)
		 */
		public JSONObject getJSON(boolean runChecks) throws IOException, ServerUnavailableException {
			JSONObject result = new JSONObject(new String(read(), "UTF-8"));

			if (runChecks)
				testResponse(result, () -> this.connection.getURL()
						.getHost() /* a pretty dirty way to get a Server instance it but still the cleanest one */);

			return result;
		}

	}
}