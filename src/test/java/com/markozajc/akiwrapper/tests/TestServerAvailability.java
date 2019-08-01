package com.markozajc.akiwrapper.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;
import com.markozajc.akiwrapper.core.utils.Servers;

class TestServerAvailability {

	/**
	 * Whether to fail if a {@link Server} is unavailable.
	 */
	private static final boolean FAIL_IF_SERVER_DOWN = false;
	/**
	 * Whether to fail if a {@link ServerGroup} (a {@link Language}) is completely unavailable.
	 */
	private static final boolean FAIL_IF_GROUP_DOWN = true;

	private static final String GROUP_DOWN = "[FATAL] %s is down!\n";
	private static final String GROUP_AVAILABLE = "[INFO] %s is up with %s available servers.\n";
	private static final String SERVER_DOWN = "\t[WARNING] %s-%s is down!\n";
	private static final String TESTING_SERVER = "\t[DEBUG] Testing %s: %s/%s\n";
	private static final String TESTING_GROUP = "[INFO] Testing %s\n";

	@Test
	void testServers() {
		Servers.SERVER_GROUPS.values().forEach(g -> {

			System.out.printf(TESTING_GROUP, g.getLocalization().toString());

			int available = 0;
			int size = g.getServers().size();
			for (int i = 0; i < size; i++) {
				System.out.printf(TESTING_SERVER, g.getLocalization().toString(), i + 1, size);

				if (g.getServers().get(i).isUp()) {
					available++;

				} else {
					System.err.printf(SERVER_DOWN, g.getLocalization(), i + 1);

					if (FAIL_IF_SERVER_DOWN)
						Assertions.fail();
				}

			}

			if (available > 0) {
				System.out.printf(GROUP_AVAILABLE, g.getLocalization().toString(), available);

			} else {
				System.err.printf(GROUP_DOWN, g.getLocalization().toString());

				if (FAIL_IF_GROUP_DOWN)
					Assertions.fail();
			}

		});
	}

}
