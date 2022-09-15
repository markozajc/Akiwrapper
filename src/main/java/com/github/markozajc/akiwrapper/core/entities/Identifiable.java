package com.github.markozajc.akiwrapper.core.entities;

import javax.annotation.*;

/**
 * A representation of an object with a numeric identifier. Some objects in the API
 * have an ID appended to them.
 *
 * @author Marko Zajc
 */
public interface Identifiable {

	/**
	 * @return ID of that object. Each object has an unique ID
	 */
	@Nonnull
	String getId();

	/**
	 * @return ID as a long
	 *
	 * @see #getId()
	 */
	@Nonnegative
	default long getIdLong() {
		return Long.parseLong(getId());
	}

}
