package com.mz.akiwrapper.core.utils;

import org.json.JSONObject;

public class JSONUtils {

	public static Integer getInteger(JSONObject json, String key) throws NumberFormatException {
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

	public static String getString(JSONObject json, String key) {
		return json.get(key).toString();
	}

}
