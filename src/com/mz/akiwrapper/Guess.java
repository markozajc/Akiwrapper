package com.mz.akiwrapper;

import org.json.JSONObject;

public class Guess {

	private JSONObject params;

	/**
	 * Object used to represent Akinator's guess
	 * 
	 * @param params
	 *            an element from <code>elements</code> array
	 */
	public Guess(JSONObject params) {
		this.params = params;
	}

	/**
	 * Returns this characer's name
	 * 
	 * @return this characer's name
	 */
	public String getName() {
		return this.params.getString("name");
	}

	/**
	 * Returns probability that this is the answer. 1 is the most sure, 0 is the
	 * least sure
	 * 
	 * @return
	 */
	public double getProbability() {
		return Double.parseDouble(this.params.getString("proba"));
	}

	/**
	 * Returns description of this character
	 * 
	 * @return description of this character
	 */
	public String getDescription() {
		return this.params.getString("description");
	}

	/**
	 * Returns URL to attached picture of this character
	 * 
	 * @return URL to picture of null if no picture is attached
	 */
	public String getImageUrl() {
		return this.params.getString("picture_path").equals("none.jpg") ? null
				: this.params.getString("absolute_picture_path");
	}
}
