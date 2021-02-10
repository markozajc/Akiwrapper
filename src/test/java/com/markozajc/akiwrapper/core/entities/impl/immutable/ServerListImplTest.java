package com.markozajc.akiwrapper.core.entities.impl.immutable;

import static java.util.Arrays.asList;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.Server.*;

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
		List<Server> serversList = asList(new ServerImpl("x", Language.ARABIC, GuessType.ANIMAL),
										  new ServerImpl("x", Language.FRENCH, GuessType.ANIMAL));
		ServerList serverList = new ServerListImpl(serversList);
		assertEquals(serversList.size() - 1, serverList.getRemainingSize());
		assertEquals(serversList, serverList.getServers());
		assertEquals(Language.ARABIC, serverList.getLanguage());
		assertTrue(serverList.hasNext());
		assertTrue(serverList.next());
		assertEquals(Language.FRENCH, serverList.getLanguage());
		assertFalse(serverList.hasNext());
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
		assertTrue(serverList.hasNext());
		assertTrue(serverList.next());
		assertEquals(Language.FRENCH, serverList.getLanguage());
		assertFalse(serverList.hasNext());
		assertFalse(serverList.next());
	}

	@Test
	void testMixedServersCollection() {
		ServerList serverList =
			new ServerListImpl(new ServerImpl("x", Language.ARABIC, GuessType.ANIMAL),
							   new ServerListImpl(new ServerImpl("x", Language.FRENCH, GuessType.ANIMAL)));
		assertEquals(2 /* amount of servers */ - 1, serverList.getRemainingSize());
		assertEquals(Language.ARABIC, serverList.getLanguage());
		assertTrue(serverList.hasNext());
		assertTrue(serverList.next());
		assertEquals(Language.FRENCH, serverList.getLanguage());
		assertFalse(serverList.hasNext());
		assertFalse(serverList.next());
	}

}
