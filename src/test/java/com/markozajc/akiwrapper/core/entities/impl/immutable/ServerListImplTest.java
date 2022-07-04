package com.markozajc.akiwrapper.core.entities.impl.immutable;

import static com.markozajc.akiwrapper.core.entities.Server.GuessType.ANIMAL;
import static com.markozajc.akiwrapper.core.entities.Server.Language.*;
import static java.util.Arrays.asList;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.entities.*;

import static org.junit.jupiter.api.Assertions.*;

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
		List<Server> serversList = asList(new ServerImpl("x", ARABIC, ANIMAL), new ServerImpl("x", FRENCH, ANIMAL));
		ServerList serverList = new ServerListImpl(serversList);
		assertEquals(serversList.size() - 1, serverList.getRemainingSize());
		assertEquals(serversList, serverList.getServers());
		assertEquals(ARABIC, serverList.getLanguage());
		assertTrue(serverList.hasNext());
		assertTrue(serverList.next());
		assertEquals(FRENCH, serverList.getLanguage());
		assertFalse(serverList.hasNext());
		assertFalse(serverList.next());
	}

	@SuppressWarnings("null")
	@Test
	void testNestedServersCollection() {
		List<Server> serversList = asList(new ServerImpl("x", ARABIC, ANIMAL), new ServerImpl("x", FRENCH, ANIMAL));
		ServerList serverList = new ServerListImpl(Arrays.asList(new ServerListImpl(serversList)));
		assertEquals(serversList.size() - 1, serverList.getRemainingSize());
		assertEquals(serversList, serverList.getServers());
		assertEquals(ARABIC, serverList.getLanguage());
		assertTrue(serverList.hasNext());
		assertTrue(serverList.next());
		assertEquals(FRENCH, serverList.getLanguage());
		assertFalse(serverList.hasNext());
		assertFalse(serverList.next());
	}

	@Test
	void testMixedServersCollection() {
		ServerList serverList = new ServerListImpl(new ServerImpl("x", ARABIC, ANIMAL),
												   new ServerListImpl(new ServerImpl("x", FRENCH, ANIMAL)));
		assertEquals(2 /* amount of servers */ - 1, serverList.getRemainingSize());
		assertEquals(ARABIC, serverList.getLanguage());
		assertTrue(serverList.hasNext());
		assertTrue(serverList.next());
		assertEquals(FRENCH, serverList.getLanguage());
		assertFalse(serverList.hasNext());
		assertFalse(serverList.next());
	}

}
