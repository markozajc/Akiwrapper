package com.mz.akiwrapper.core.entities.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import com.mz.akiwrapper.core.entities.Guess;

public class GuessImpl implements Guess {

	private final String id;
	private final String name;
	private final String description;

	private final URL image;

	private final double probability;

	/**
	 * Object used to represent Akinator's guess
	 * 
	 * @param params
	 *            an element from <code>elements</code> array
	 */
	public GuessImpl(JSONObject params) {
		this.id = params.getString("id");
		this.name = params.getString("name");
		String desc = params.getString("description");
		this.description = desc.equals("-") ? null : desc;

		URL image;
		try {
			image = params.getString("picture_path").equals("none.jpg") ? null
					: new URL(params.getString("absolute_picture_path"));
		} catch (MalformedURLException e) {
			image = null;
		}
		this.image = image;

		this.probability = Double.parseDouble(params.getString("proba"));
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
