package com.jakob.utils.entities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class HttpEasyResponse {
	
	private String responseString;
	private HttpResponse response;
	private int responseCode;
	private String responseBody;
	
	public HttpEasyResponse(HttpEntity entity, HttpResponse response) throws IOException {
		this.responseBody = new String(EntityUtils.toByteArray(entity), StandardCharsets.ISO_8859_1);
		
		this.response = response;
		this.responseCode = response.getStatusLine().getStatusCode();
	}

	@Deprecated
	public String getResponseString() {
		return responseString;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public int getResponseCode() {
		return responseCode;
	}
	
	public String getResponseBody() {
		return responseBody;
	}

	@Override
	public String toString() {
		return responseCode + ": " + response.getStatusLine().getReasonPhrase();
	}
	
}
