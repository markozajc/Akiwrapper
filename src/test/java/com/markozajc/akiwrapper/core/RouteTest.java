package com.markozajc.akiwrapper.core;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {

	@Test
	void testMissingParameters() {
		assertThrows(IllegalArgumentException.class,
					 () -> Route.NEW_SESSION.getRequest("", false /* and no parameters */));
	}

	@Test
	void testTestResponse() {
		JSONObject base = new JSONObject();

		base.put("completion", "KO - SERVER DOWN");
		assertThrows(ServerUnavailableException.class, () -> Route.testResponse(base));

		base.put("completion", "KO - TEST REASON");
		assertThrows(StatusException.class, () -> Route.testResponse(base));

		base.put("completion", "WARN - TEST REASON");
		assertDoesNotThrow(() -> Route.testResponse(base));

		base.put("completion", "OK - TEST REASON");
		assertDoesNotThrow(() -> Route.testResponse(base));

		base.put("completion", "OK");
		assertDoesNotThrow(() -> Route.testResponse(base));
	}

}
