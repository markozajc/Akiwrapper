package com.mz.akiwrapper.core.entities;

/**
 * An interface used to represent an identifiable object (if the object has an
 * appended ID set by Akinator's servers).
 * 
 * @author Marko Zajc
 */
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
