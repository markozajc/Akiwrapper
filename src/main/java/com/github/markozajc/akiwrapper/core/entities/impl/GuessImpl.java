package com.github.markozajc.akiwrapper.core.entities.impl;

import static com.github.markozajc.akiwrapper.core.utils.JSONUtils.*;
import static java.lang.Double.compare;

import java.net.*;

import javax.annotation.*;

import org.json.JSONObject;

import com.github.markozajc.akiwrapper.core.entities.Guess;

@SuppressWarnings("javadoc") // internal impl
public class GuessImpl implements Guess {

	@Nonnull private final String id;
	@Nonnull private final String name;
	@Nullable private final String description;
	@Nullable private final URL image;
	private final double probability;
	private final boolean explicit;

	GuessImpl(@Nonnull String id, @Nonnull String name, @Nullable String description, @Nullable URL image,
			  @Nonnegative double probability, boolean explicit) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
		this.probability = probability;
		this.explicit = explicit;
	}

	@SuppressWarnings("null")
	public static GuessImpl fromJson(@Nonnull JSONObject json) {
		return new GuessImpl(json.getString("id"), json.getString("name"), getDescription(json), getImage(json),
							 getDouble(json, "proba").orElseThrow(), getInteger(json, "corrupt").orElseThrow() == 1);
	}

	@Nullable
	private static String getDescription(@Nonnull JSONObject json) {
		var desc = json.getString("description");
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

	@Override
	public int compareTo(Guess o) {
		return compare(o.getProbability(), this.probability);
	}

	@Override
	public boolean isExplicit() {
		return this.explicit;
	}

}
