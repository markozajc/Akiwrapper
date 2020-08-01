package com.markozajc.akiwrapper.core.entities.impl.immutable;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerListImplTest {

	@SuppressWarnings("null")
	@Test
	void testEmptyCollection() {
		List<Server> emptyList = Collections.emptyList();
		assertThrows(IllegalArgumentException.class, () -> new ServerListImpl(emptyList));
	}

	@SuppressWarnings("null")
	@Test
	void testServersCollection() {
		List<Server> serversList = asList(new ServerImpl("x", Language.ARABIC, GuessType.ANIMAL),
										  new ServerImpl("x", Language.FRENCH, GuessType.ANIMAL));
		ServerList serverList = new ServerListImpl(serversList);
		assertEquals(serversList.size() - 1, serverList.getRemainingSize());
		assertEquals(serversList, serverList.getServers());
		assertEquals(Language.ARABIC, serverList.getLanguage());
		assertTrue(serverList.next());
		assertEquals(Language.FRENCH, serverList.getLanguage());
		assertFalse(serverList.next());
	}

	@SuppressWarnings("null")
	@Test
	void testNestedServersCollection() {
		List<Server> serversList = asList(new ServerImpl("x", Language.ARABIC, GuessType.ANIMAL),
										  new ServerImpl("x", Language.FRENCH, GuessType.ANIMAL));
		ServerList serverList = new ServerListImpl(Arrays.asList(new ServerListImpl(serversList)));
		assertEquals(serversList.size() - 1, serverList.getRemainingSize());
		assertEquals(serversList, serverList.getServers());
		assertEquals(Language.ARABIC, serverList.getLanguage());
		assertTrue(serverList.next());
		assertEquals(Language.FRENCH, serverList.getLanguage());
		assertFalse(serverList.next());
	}

	@Test
	void testMixedServersCollection() {
		ServerList serverList =
			new ServerListImpl(new ServerImpl("x", Language.ARABIC, GuessType.ANIMAL),
							   new ServerListImpl(new ServerImpl("x", Language.FRENCH, GuessType.ANIMAL)));
		assertEquals(2 /* amount of servers */ - 1, serverList.getRemainingSize());
		assertEquals(Language.ARABIC, serverList.getLanguage());
		assertTrue(serverList.next());
		assertEquals(Language.FRENCH, serverList.getLanguage());
		assertFalse(serverList.next());
	}

}
