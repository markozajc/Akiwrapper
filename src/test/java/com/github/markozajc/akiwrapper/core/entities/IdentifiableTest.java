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
	void testGetIdLongLong() {
		String maxLongString = Long.toString(Long.MAX_VALUE);
		if (maxLongString == null) {
			fail(); // Sorry suppress warnings broke and would let me ignore null warnings
			return; // Also because eclipse doesn't realize that fail throws
		}

		Identifiable identifiable = () -> maxLongString;
		assertEquals(Long.MAX_VALUE, identifiable.getIdLong());
	}

	@Test
	void testGetIdLongUnparsable() {
		Identifiable identifiable = () -> "abcd";
		assertThrows(NumberFormatException.class, () -> identifiable.getIdLong());
	}

}
