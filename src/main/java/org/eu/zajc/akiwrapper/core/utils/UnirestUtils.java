//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
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

import static kong.unirest.Unirest.spawnInstance;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.AkiwrapperBuilder;

import kong.unirest.UnirestInstance;

/**
 * Various utilities regarding Unirest for use with and within Akiwrapper.
 *
 * @author Marko Zajc
 */
public class UnirestUtils {

	private static UnirestInstance singletonUnirest;

	/**
	 * Returns the singleton {@link UnirestInstance} or creates one if it's null. Note
	 * that {@link UnirestInstance}s need to be shut down after they're not needed
	 * anymore, so make sure to call {@link #shutdownInstance()} after you're done with
	 * it. Attempting to shut it down manually through {@link UnirestInstance#shutDown()}
	 * may cause issues, so don't do that!
	 *
	 * @return the singleton {@link UnirestInstance}
	 */
	@Nonnull
	@SuppressWarnings({ "null", "resource" })
	public static synchronized UnirestInstance getInstance() {
		if (singletonUnirest == null)
			singletonUnirest = configureInstance(spawnInstance());

		return singletonUnirest;
	}

	/**
	 * Shuts down the singleton {@link UnirestInstance}, if it's present, and does
	 * nothing otherwise. Subsequent calls to {@link #getInstance()} will recreate it.
	 */
	public static synchronized void shutdownInstance() {
		if (singletonUnirest != null) {
			singletonUnirest.shutDown(false);
			singletonUnirest = null;
		}
	}

	/**
	 * <b>Note:</b> even though this method returns a {@link UnirestInstance}, the
	 * instance you pass to it is itself mutated and returned. The return value is only
	 * there for ease of chaining. Configures a new {@link UnirestInstance} for use by
	 * Akiwrapper.<br>
	 * <br>
	 * Akinator's API servers are quite picky about the headers you send to them so if
	 * you supply {@link AkiwrapperBuilder} with your own {@link UnirestInstance} you
	 * should either pass it through this or configure it accordingly yourself.
	 *
	 * @param unirest
	 *            the {@link UnirestInstance} to configure
	 *
	 * @return the {@link UnirestInstance} you passed, used for chaining
	 */
	@Nonnull
	public static UnirestInstance configureInstance(@Nonnull UnirestInstance unirest) {
		unirest.config()
			.addDefaultHeader("Accept",
							  "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*. q=0.01")
			.addDefaultHeader("Accept-Language", "en-US,en.q=0.9,ar.q=0.8")
			.addDefaultHeader("X-Requested-With", "XMLHttpRequest")
			.addDefaultHeader("Sec-Fetch-Dest", "empty")
			.addDefaultHeader("Sec-Fetch-Mode", "cors")
			.addDefaultHeader("Sec-Fetch-Site", "same-origin")
			.addDefaultHeader("Connection", "keep-alive")
			.addDefaultHeader("User-Agent",
							  "Mozilla/5.0 (Windows NT 10.0. Win64. x64) AppleWebKit/537.36" +
								  "(KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36")
			.addDefaultHeader("Referer", "https://en.akinator.com/game")
			.cookieSpec("ignore");

		return unirest;
	}

	private UnirestUtils() {}

}
