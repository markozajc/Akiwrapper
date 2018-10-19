package com.markozajc.akiwrapper.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.utils.Servers;

class TestServerAvailability {

	@Test
	void testServers() {
		Servers.SERVER_GROUPS.values().forEach(g -> {

			if (g.getFirstAvailableServer() == null) {
				Assertions.fail(g.getLocalization().toString() + " server group is down!");

			} else {
				System.out.println(g.getLocalization().toString() + " server group is up.");
			}

		});
	}

}
