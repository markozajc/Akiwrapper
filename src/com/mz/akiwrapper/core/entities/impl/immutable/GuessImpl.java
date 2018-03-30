package com.mz.akiwrapper.core.entities.impl.immutable;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mz.akiwrapper.core.Route;
import com.mz.akiwrapper.core.entities.Guess;
import com.mz.akiwrapper.core.utils.JSONUtils;

/**
 * An implementation of {@link Guess}.
 * 
 * @author Marko Zajc
 */
public class GuessImpl implements Guess {

	private final String id;
	private final String name;
	private final String description;

	private final URL image;

	private final double probability;

	private static URL getImage(JSONObject json) {
		try {
			return json.getString("picture_path").equals("none.jpg") ? null
					: new URL(json.getString("absolute_picture_path"));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private static String getDescription(JSONObject json) {
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
	public GuessImpl(String id, String name, String description, URL image, double probability) {
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
	 *            JSON parameters to use (acquired with {@link Route#LIST} > {@link JSONArray}
	 *            elements > {@link JSONObject} (an index) > {@link JSONObject}
	 *            element)
	 */
	public GuessImpl(JSONObject json) {
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
