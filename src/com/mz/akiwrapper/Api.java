package com.mz.akiwrapper;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import com.jakob.utils.HttpUtils;
import com.mz.akiwrapper.entities.Completion;
import com.mz.akiwrapper.entities.Completion.Level;
import com.mz.akiwrapper.entities.Token;
import com.mz.akiwrapper.entities.exceptions.UnavailableException;

public class Api {

	private static void testResponse(JSONObject response, String url) {
		Completion compl = new Completion(response);
		if (compl.getErrLevel().equals(Level.KO) && compl.getReason().toLowerCase().equals("shutdown")) {
			throw new UnavailableException(url);
		}
	}

	protected static JSONObject newSession(String url, String name) throws IOException {
		JSONObject resp = null;
		try {
			resp = new JSONObject(
					HttpUtils.sendGet(url + "new_session?partner=1&player=" + name, "Akiwrapper").getResponseBody());
		} catch (ClientProtocolException e) {
			throw new IllegalArgumentException(url + " is not a valid URL");
		}

		testResponse(resp, url);

		return resp;
	}

	protected static JSONObject answer(String url, Token token, int currentStep, int answerId) throws IOException {
		JSONObject resp = null;
		try {
			resp = new JSONObject(HttpUtils.sendGet(url + "answer?session=" + token.getSession() + "&signature="
					+ token.getSignature() + "&step=" + currentStep + "&answer=" + answerId, "Akiwrapper")
					.getResponseBody());
		} catch (ClientProtocolException e) {
			throw new IllegalArgumentException(url + " is not a valid URL");
		}

		testResponse(resp, url);

		return resp;
	}

	protected static JSONObject list(String url, Token token, int currentStep) throws IOException {
		JSONObject resp = null;
		try {
			resp = new JSONObject(HttpUtils.sendGet(url + "list?session=" + token.getSession() + "&signature="
					+ token.getSignature() + "&mode_question=0&step=" + currentStep, "Akiwrapper").getResponseBody());
		} catch (ClientProtocolException e) {
			throw new IllegalArgumentException(url + " is not a valid URL");
		}

		testResponse(resp, url);

		return resp;
	}

}
