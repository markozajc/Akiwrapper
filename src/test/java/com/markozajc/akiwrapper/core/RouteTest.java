package com.markozajc.akiwrapper.core;

import static kong.unirest.Unirest.spawnInstance;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

	@Test
	@SuppressWarnings("null")
	void testMissingParameters() {
		try (var unirest = spawnInstance()) {
			assertThrows(IllegalArgumentException.class,
						 () -> Route.NEW_SESSION.createRequest(unirest, "", false /* and no parameters */));
		}
	}

	@Test
	void testTestResponse() {
		JSONObject base = new JSONObject();

		base.put("completion", "KO - SERVER DOWN");
		assertThrows(ServerUnavailableException.class, () -> Route.Request.ensureSuccessful(base));

		base.put("completion", "KO - TEST REASON");
		assertThrows(StatusException.class, () -> Route.Request.ensureSuccessful(base));

		base.put("completion", "WARN - TEST REASON");
		assertDoesNotThrow(() -> Route.Request.ensureSuccessful(base));

		base.put("completion", "OK - TEST REASON");
		assertDoesNotThrow(() -> Route.Request.ensureSuccessful(base));

		base.put("completion", "OK");
		assertDoesNotThrow(() -> Route.Request.ensureSuccessful(base));
	}

}
