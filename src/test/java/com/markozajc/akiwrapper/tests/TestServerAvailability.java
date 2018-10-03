package com.markozajc.akiwrapper.tests;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.utils.Servers;

class TestServerAvailability {

	@Test
	void testServers() {

		Servers.SERVER_GROUPS.entrySet().forEach(e -> {

			e.getValue().getServers().forEach(s -> {

				try {
					Route.NEW_SESSION.getRequest(s.getBaseUrl(), true, "AkiwrapperTest").getJSON();
				} catch (ServerUnavailableException | IllegalArgumentException | IOException e1) {
					Assertions.fail(s.getBaseUrl() + " is down!");
				}

				System.out.println(s.getLocalization().toString() + " - " + s.getBaseUrl() + " is working correctly.");
			});

		});
	}

}
