package com.markozajc.akiwrapper.core.utils;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONUtilsTest {

	// @formatter:off
	public static final String TEST_GET_INT_JSON =
		"{										"+
		"	\"test1\": \"1\",					"+
		"	\"test2\": 2,						"+
		"	\"test3\": \"not an integer\",		"+
		"	\"test4\": {}						"+
		"}										";
	public static final String TEST_GET_STRING_JSON =
		"{										"+
		"	\"test1\": \"string\",				"+
		"	\"test2\": 's',						"+
		"	\"test3\": 1						"+
		"}										";
	public static final String TEST_GET_DOUBLE_JSON =
		"{										"+
		"	\"test1\": \"1\",					"+
		"	\"test2\": \"0.5\",					"+
		"	\"test3\": 2,						"+
		"	\"test4\": 1.5,						"+
		"	\"test5\": \"not a double\",		"+
		"	\"test6\": {}						"+
		"}										";
	// @formatter:on

	@Test
	void testGetInt() {
		JSONObject json = new JSONObject(TEST_GET_INT_JSON);
		assertEquals(1, JSONUtils.getInteger(json, "test1").orElse(null));
		assertEquals(2, JSONUtils.getInteger(json, "test2").orElse(null));
		assertThrows(NumberFormatException.class, () -> JSONUtils.getInteger(json, "test3"));
		assertThrows(NumberFormatException.class, () -> JSONUtils.getInteger(json, "test4"));
		assertNull(JSONUtils.getInteger(json, "test5").orElse(null));
	}

	@Test
	void testGetString() {
		JSONObject json = new JSONObject(TEST_GET_STRING_JSON);
		assertEquals("string", JSONUtils.getString(json, "test1").orElse(null));
		assertEquals("s", JSONUtils.getString(json, "test2").orElse(null));
		assertEquals("1", JSONUtils.getString(json, "test3").orElse(null));
		assertNull(JSONUtils.getInteger(json, "test4").orElse(null));
	}

	@Test
	void testGetDouble() {
		JSONObject json = new JSONObject(TEST_GET_DOUBLE_JSON);
		assertEquals(1d, JSONUtils.getDouble(json, "test1").orElse(null));
		assertEquals(0.5d, JSONUtils.getDouble(json, "test2").orElse(null));
		assertEquals(2d, JSONUtils.getDouble(json, "test3").orElse(null));
		assertEquals(1.5d, JSONUtils.getDouble(json, "test4").orElse(null));
		assertThrows(NumberFormatException.class, () -> JSONUtils.getInteger(json, "test5"));
		assertThrows(NumberFormatException.class, () -> JSONUtils.getInteger(json, "test6"));
		assertNull(JSONUtils.getInteger(json, "test7").orElse(null));
	}

}
