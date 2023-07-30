package com.github.markozajc.akiwrapper.core.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentifiableTest {

	@Test
	void testGetIdLong() {
		Identifiable identifiable = () -> "1234";
		assertEquals(1234, identifiable.getIdLong());
	}

	@Test
	@SuppressWarnings("null")
	void testGetIdLongLong() {
		String maxLongString = Long.toString(Long.MAX_VALUE);
		Identifiable identifiable = () -> maxLongString;
		assertEquals(Long.MAX_VALUE, identifiable.getIdLong());
	}

	@Test
	void testGetIdLongUnparsable() {
		Identifiable identifiable = () -> "abcd";
		assertThrows(NumberFormatException.class, () -> identifiable.getIdLong());
	}

}
