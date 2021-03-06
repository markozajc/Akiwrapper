package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.net.*;

import javax.annotation.*;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.utils.JSONUtils;

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

	public GuessImpl(@Nonnull String id, @Nonnull String name, @Nullable String description, @Nullable URL image,
					 @Nonnegative double probability) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
		this.probability = probability;
	}

	@SuppressWarnings("null")
	public GuessImpl(@Nonnull JSONObject json) {
		this(json.getString("id"), json.getString("name"), getDescription(json), getImage(json),
			 JSONUtils.getDouble(json, "proba").get());
	}

	@Nullable
	private static String getDescription(@Nonnull JSONObject json) {
		String desc = json.getString("description");
		return "-".equals(desc) ? null : desc;
	}

	@Nullable
	private static URL getImage(@Nonnull JSONObject json) {
		try {
			return "none.jpg".equals(json.getString("picture_path")) ? null
				: new URL(json.getString("absolute_picture_path"));
		} catch (MalformedURLException e) {
			return null;
		}
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
