package com.markozajc.akiwrapper.core.utils;

import org.json.JSONObject;

/**
 * A set of utilities for JSON that prevent type errors.
 *
 * @author Marko Zajc
 */
public class JSONUtils {

	private JSONUtils() {}

	/**
	 * @param json
	 * @param key
	 * 
	 * @return value from that key as an integer
	 * 
	 * @throws NumberFormatException
	 *             if the value could in no way be transferred to an integer
	 */
	public static Integer getInteger(JSONObject json, String key) {
		Object object = json.get(key);

		if (object == null)
			return null;

		if (object instanceof Number)
			return Integer.valueOf(((Number) object).intValue());

		if (object instanceof String)
			return Integer.valueOf((String) object);

		throw new NumberFormatException(
		    "Could not format \"" + object + "\" of type " + object.getClass().getName() + " into an Integer.");
	}

	/**
	 * @param json
	 * @param key
	 * 
	 * @return value from that key as a double
	 * 
	 * @throws NumberFormatException
	 *             if the value could in no way be transferred to a double
	 */
	public static Double getDouble(JSONObject json, String key) {
		Object object = json.get(key);

		if (object == null)
			return null;

		if (object instanceof Number)
			return Double.valueOf(((Number) object).doubleValue());

		if (object instanceof String)
			return Double.valueOf((String) object);

		throw new NumberFormatException(
		    "Could not format \"" + object + "\" of type " + object.getClass().getName() + " into a Double.");
	}

	/**
	 * @param json
	 * @param key
	 * 
	 * @return value from that key as a string (calls {@link Object#toString()} on the
	 *         object)
	 */
	public static String getString(JSONObject json, String key) {
		return json.get(key).toString();
	}

}
