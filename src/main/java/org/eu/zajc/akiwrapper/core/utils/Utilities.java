//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2025 Marko Zajc
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.eu.zajc.akiwrapper.core.utils;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.exceptions.MalformedResponseException;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 * Various general utilities for use within Akiwrapper.
 *
 * @author Marko Zajc
 */
public class Utilities {

	/**
	 * Rethrows a checked exception as unchecked using generics trickery (the exception
	 * is not changed or wrapped in a {@link RuntimeException} - it is thrown as-is).
	 *
	 * @param <X>
	 *            the exception type
	 * @param ex
	 *            the exception to throw as unchecked
	 *
	 * @return the exception itself to support {@code throws asUnchecked(e);}. Note that
	 *         it is thrown in this method and nothing is ever returned
	 *
	 * @throws X
	 *             the exception you provide. Always thrown.
	 */
	@SuppressWarnings("unchecked")
	public static <X extends Throwable> RuntimeException asUnchecked(@Nonnull Throwable ex) throws X {
		throw (X) ex;
	}

	/**
	 * Attempts to parse a string using {@link Integer#parseInt(String)}, throwing a
	 * {@link MalformedResponseException} on failure.
	 *
	 * @param s
	 *            the string to parse.
	 *
	 * @return the parsed int.
	 *
	 * @throws MalformedResponseException
	 *             if a {@link NumberFormatException} is thrown.
	 */
	public static int parseInt(String s) throws MalformedResponseException {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new MalformedResponseException(e);
		}
	}

	/**
	 * Attempts to parse a string using {@link Long#parseLong(String)}, throwing a
	 * {@link MalformedResponseException} on failure.
	 *
	 * @param s
	 *            the string to parse.
	 *
	 * @return the parsed long.
	 *
	 * @throws MalformedResponseException
	 *             if a {@link NumberFormatException} is thrown.
	 */
	public static long parseLong(String s) throws MalformedResponseException {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			throw new MalformedResponseException(e);
		}
	}

	/**
	 * Attempts to parse a string using {@link Double#parseDouble(String)}, throwing a
	 * {@link MalformedResponseException} on failure.
	 *
	 * @param s
	 *            the string to parse.
	 *
	 * @return the parsed double.
	 *
	 * @throws MalformedResponseException
	 *             if a {@link NumberFormatException} is thrown.
	 */
	public static double parseDouble(String s) throws MalformedResponseException {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new MalformedResponseException(e);
		}
	}

	private Utilities() {}

}
