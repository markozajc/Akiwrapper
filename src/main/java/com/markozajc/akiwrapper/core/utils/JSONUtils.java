package com.markozajc.akiwrapper.core.utils;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.json.*;

/**
 * A set of utilities for JSON that prevent type errors.
 *
 * @author Marko Zajc
 */
public final class JSONUtils {

	private JSONUtils() {}

	/**
	 * Gets an {@link Integer} value from a {@link JSONObject}.
	 *
	 * @param json
	 * @param key
	 *
	 * @return {@link Optional} integer value
	 */
	@SuppressWarnings("null")
	@Nonnull
	public static Optional<Integer> getInteger(@Nonnull JSONObject json, @Nonnull String key) {
		try {
			Object object = json.get(key);
			Integer value;
			if (object instanceof Number)
				value = ((Number) object).intValue();
			else if (object instanceof String)
				value = Integer.valueOf((String) object);
			else
				throw new NumberFormatException("Could not format \"" + object +
					"\" of type " +
					object.getClass().getName() +
					" into a Double.");

			return Optional.of(value);

		} catch (JSONException e) { // NOSONAR It just means that the key wasn't found.
			return Optional.empty();
		}
	}

	/**
	 * Gets a {@link Double} value from a {@link JSONObject}.
	 *
	 * @param json
	 * @param key
	 *
	 * @return {@link Optional} double value
	 */
	@SuppressWarnings("null")
	@Nonnull
	public static Optional<Double> getDouble(@Nonnull JSONObject json, @Nonnull String key) {
		try {
			Object object = json.get(key);
			Double value;
			if (object instanceof Number)
				value = ((Number) object).doubleValue();
			else if (object instanceof String)
				value = Double.valueOf((String) object);
			else
				throw new NumberFormatException("Could not format \"" + object +
					"\" of type " +
					object.getClass().getName() +
					" into a Double.");

			return Optional.of(value);

		} catch (JSONException e) { // NOSONAR It just means that the key wasn't found.
			return Optional.empty();
		}
	}

	/**
	 * Gets a {@link String} value from a {@link JSONObject}.
	 *
	 * @param json
	 * @param key
	 *
	 * @return {@link Optional} string value
	 */
	@SuppressWarnings("null")
	@Nonnull
	public static Optional<String> getString(@Nonnull JSONObject json, @Nonnull String key) {
		try {
			return Optional.of(json.get(key).toString());
		} catch (JSONException e) { // NOSONAR It just means that the key wasn't found.
			return Optional.empty();
		}
	}

}
