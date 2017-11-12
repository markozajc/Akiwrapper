package com.jakob.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.jakob.utils.entities.HttpEasyResponse;

public class HttpUtils {

	/**
	 * Sends a blank HTTP GET request
	 * 
	 * @param site
	 *            site to request
	 * @throws IOException
	 *             if website couldn't be reached
	 */
	public static void sendBlankGet(String site) throws IOException {
		URL url = new URL(site);
		InputStream is = url.openStream();
		is.close();
	}

	/**
	 * Requests data from a website and returns it into a HttpEasyResponse format
	 * 
	 * @param site
	 *            website to send request to
	 * @param headers
	 *            headers to use
	 * @return website's response
	 * @throws ClientProtocolException
	 *             if no protocol (http://, https://) was provided
	 * @throws IOException
	 *             if website couldn't be accessed
	 */
	public static HttpEasyResponse sendGet(String site, HashMap<String, String> headers)
			throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(site);

		for (String header : headers.keySet()) {
			get.addHeader(header, headers.get(header));
		}

		HttpResponse response = client.execute(get);

		return new HttpEasyResponse(response.getEntity(), response);
	}

	/**
	 * Requests data from a website and returns it into a HttpEasyResponse format
	 * 
	 * @param site
	 *            website to send request to
	 * @param userAgent
	 *            UserAgent to use
	 * @return website's response
	 * @throws ClientProtocolException
	 *             if no protocol (http://, https://) was provided
	 * @throws IOException
	 *             if website couldn't be accessed
	 */
	public static HttpEasyResponse sendGet(String site, String userAgent)
			throws ClientProtocolException, IOException {
		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Agent", userAgent);
		
		return sendGet(site, headers);
	}
	
	/**
	 * Sends a new HTTP POST request
	 * 
	 * @param site
	 *            website to send request to
	 * @param headers
	 *            headers to use
	 * @param jsonParameters
	 *            JSON encoded parameters to use
	 * @return website's response
	 * @throws IOException
	 *             if website couldn't be accessed
	 */
	public static HttpEasyResponse sendPost(String site, HashMap<String, String> headers, String jsonParameters)
			throws IOException {
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost post = new HttpPost(site);

		for (String header : headers.keySet()) {
			post.addHeader(header, headers.get(header));
		}

		post.setEntity(new StringEntity(jsonParameters));

		HttpResponse response = client.execute(post);
		
		return new HttpEasyResponse(response.getEntity(), response);
	}
}
