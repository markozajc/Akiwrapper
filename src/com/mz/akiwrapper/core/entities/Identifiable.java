package com.mz.akiwrapper.core.entities;

public interface Identifiable {

	/**
	 * @return ID of that object. Each object has an unique ID
	 */
	String getId();

	/**
	 * @return ID as a long
	 * 
	 * @see #getId()
	 */
	default long getIdLong() {
		return Long.parseLong(getId());
	}

}
