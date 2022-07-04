package com.markozajc.akiwrapper.core.utils;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import java.util.*;

import javax.annotation.Nonnull;

import org.json.JSONObject;

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
	@Nonnull
	@SuppressWarnings("null")
	public static OptionalInt getInteger(@Nonnull JSONObject json, @Nonnull String key) {
		if (!json.has(key))
			return OptionalInt.empty();

		var value = json.get(key);
		if (value instanceof Number) {
			return OptionalInt.of(((Number) value).intValue());

		} else if (value instanceof String) {
			return OptionalInt.of(parseInt((String) value));

		} else {
			throw new NumberFormatException(format("Could not format \"%s\" of type %s into an int", value,
												   value.getClass().getName()));
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
	@Nonnull
	@SuppressWarnings("null")
	public static OptionalDouble getDouble(@Nonnull JSONObject json, @Nonnull String key) {
		if (!json.has(key))
			return OptionalDouble.empty();

		var value = json.get(key);
		if (value instanceof Number) {
			return OptionalDouble.of(((Number) value).doubleValue());

		} else if (value instanceof String) {
			return OptionalDouble.of(parseDouble((String) value));

		} else {
			throw new NumberFormatException(format("Could not format \"%s\" of type %s into a double", value,
												   value.getClass().getName()));
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
	@Nonnull
	@SuppressWarnings("null")
	public static Optional<String> getString(@Nonnull JSONObject json, @Nonnull String key) {
		if (!json.has(key))
			return Optional.empty();
		else
			return Optional.of(json.get(key).toString());
	}

}
