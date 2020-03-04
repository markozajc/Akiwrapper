package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.utils.JSONUtils;

/**
 * An implementation of {@link Guess}.
 *
 * @author Marko Zajc
 */
public class GuessImpl implements Guess {

	@Nonnull
	private final String id;
	@Nonnull
	private final String name;
	@Nullable
	private final String description;
	@Nullable
	private final URL image;
	@Nonnegative
	private final double probability;

	@Nullable
	private static URL getImage(@Nonnull JSONObject json) {
		try {
			return json.getString("picture_path").equals("none.jpg") ? null
			    : new URL(json.getString("absolute_picture_path"));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Nullable
	private static String getDescription(@Nonnull JSONObject json) {
		String desc = json.getString("description");
		return desc.equals("-") ? null : desc;
	}

	/**
	 * Creates a new {@link GuessImpl} instance from raw parameters.
	 *
	 * @param id
	 * @param name
	 * @param description
	 * @param image
	 * @param probability
	 */
	public GuessImpl(@Nonnull String id, @Nonnull String name, @Nullable String description, @Nullable URL image,
	                 @Nonnegative double probability) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
		this.probability = probability;
	}

	/**
	 * Creates a new {@link GuessImpl} instance.
	 *
	 * @param json
	 *            JSON parameters to use (acquired with {@link Route#LIST} &gt;
	 *            {@link JSONArray} elements &gt; {@link JSONObject} (an index) &gt;
	 *            {@link JSONObject} element)
	 */
	@SuppressWarnings("null")
	public GuessImpl(@Nonnull JSONObject json) {
		this(json.getString("id"), json.getString("name"), getDescription(json), getImage(json),
		    JSONUtils.getDouble(json, "proba").doubleValue());
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public double getProbability() {
		return this.probability;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public URL getImage() {
		return this.image;
	}

	@Override
	public String getId() {
		return this.id;
	}

}
