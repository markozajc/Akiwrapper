package com.mz.akiwrapper.core;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.mz.akiwrapper.core.entities.CompletionStatus;
import com.mz.akiwrapper.core.entities.CompletionStatus.Level;
import com.mz.akiwrapper.core.entities.impl.CompletionStatusImpl;
import com.mz.akiwrapper.core.exceptions.ServerUnavailableException;

public class Route {

	public static final String DEFAULT_USER_AGENT = AkiwrapperBuilder.DEFAULT_NAME;

	/**
	 * Creates a new session for further gameplay. Parameters:
	 * <ol>
	 * <li>Player's name</li>
	 * </ol>
	 */
	public static final Route NEW_SESSION = new Route("new_session?partner=1&player=%s", 1);

	/**
	 * Answers a question. Parameters:
	 * <ol>
	 * <li>Session's ID</li>
	 * <li>Session's signature</li>
	 * <li>Current step</li>
	 * <li>Answer's ID</li>
	 * </ol>
	 */
	public static final Route ANSWER = new Route("answer?session=%s&signature=%s&step=%s&answer=%s", 4);

	/**
	 * Lists all available guesses. Parameters:
	 * <ol>
	 * <li>Session's ID</li>
	 * <li>Session's signature</li>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route LIST = new Route("list?session=%s&signature=%s&mode_question=0&step=%s", 3);

	private static void testResponse(JSONObject response, String url) {
		CompletionStatus compl = new CompletionStatusImpl(response);
		if (compl.getLevel().equals(Level.ERROR) && compl.getReason().toLowerCase().equals("shutdown"))
			throw new ServerUnavailableException(url);
	}

	private final String path;
	private final int parametersQuantity;

	private final HttpClientBuilder clientBuilder;

	private Route(String path, int parameters) {
		this.path = path;
		this.parametersQuantity = parameters;
		this.clientBuilder = HttpClientBuilder.create().setUserAgent(DEFAULT_USER_AGENT);
	}

	/**
	 * Creates a request for this route that can later be called and converted into
	 * a {@link JSONObject}.
	 * 
	 * @param baseUrl
	 *            base (API's) URL
	 * @param parameters
	 *            parameters to pass to the route (parameters are specified in that
	 *            Route's JavaDoc)
	 * @return a callable request
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if you have passed too little parameters
	 */
	public Request getRequest(String baseUrl, String... parameters) throws IOException, IllegalArgumentException {
		if (parameters.length < this.parametersQuantity)
			throw new IllegalArgumentException(
					"Insufficient parameters; Expected " + this.parametersQuantity + ", got " + parameters.length);

		String[] encodedParams = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			encodedParams[i] = URLEncoder.encode(parameters[i], "UTF-8");

		return new Request(baseUrl + String.format(this.path, (Object[]) encodedParams), this.clientBuilder);
	}

	/**
	 * Sets the user-agent that will be used in requests for this route. If no
	 * user-agent is specified, {@link #DEFAULT_USER_AGENT} will be used.
	 * 
	 * @param userAgent
	 * @return self, useful for chaining
	 */
	public Route setUserAgent(String userAgent) {
		this.clientBuilder.setUserAgent(userAgent);

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
	 *         {@link #getRequest(String, String...)}
	 */
	public int getParametersQuantity() {
		return parametersQuantity;
	}

	/**
	 * @return {@link HttpClientBuilder} for this route, can be adjusted.
	 */
	public HttpClientBuilder getClientBuilder() {
		return clientBuilder;
	}

	/**
	 * A callable request.
	 * 
	 * @author Marko Zajc
	 */
	public class Request {

		private HttpGet request;
		private HttpClientBuilder clientBuilder;

		private Request(String url, HttpClientBuilder client) {
			this.clientBuilder = client;
			this.request = new HttpGet(url);
		}

		/**
		 * Reads the contents of the request's URL.
		 * 
		 * @return content as a byte array
		 * @throws ClientProtocolException
		 * @throws IOException
		 */
		public byte[] read() throws ClientProtocolException, IOException {
			try (CloseableHttpClient client = this.clientBuilder.build()) {
				return EntityUtils.toByteArray(client.execute(this.request).getEntity());
			}
		}

		/**
		 * @return content of the request's URL as a {@link JSONObject}. This will also
		 *         if the server has went down.
		 * @throws ClientProtocolException
		 * @throws IOException
		 * @throws ServerUnavailableException
		 *             in case the server has went down (very unlikely to ever happen)
		 */
		public JSONObject getJSON() throws ClientProtocolException, IOException, ServerUnavailableException {
			JSONObject result = new JSONObject(new String(read(), "UTF-8"));

			testResponse(result, this.request.getURI().getHost());

			return result;
		}

	}

}
