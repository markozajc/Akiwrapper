package com.markozajc.akiwrapper.core.utils;

import static kong.unirest.Unirest.spawnInstance;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.AkiwrapperBuilder;

import kong.unirest.UnirestInstance;

public class UnirestUtils {

	private static UnirestInstance SINGLETON_UNIREST;

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
	@SuppressWarnings("null")
	public static synchronized UnirestInstance getInstance() {
		if (SINGLETON_UNIREST == null)
			SINGLETON_UNIREST = createInstance();

		return SINGLETON_UNIREST;
	}

	/**
	 * Shuts down the singleton {@link UnirestInstance}, if it's present, and does
	 * nothing otherwise. Subsequent calls to {@link #getInstance()} will recreate it.
	 */
	public static synchronized void shutdownInstance() {
		if (SINGLETON_UNIREST != null) {
			SINGLETON_UNIREST.shutDown(false);
			SINGLETON_UNIREST = null;
		}
	}

	/**
	 * Creates and configures a new {@link UnirestInstance}. Akinator's API servers are
	 * quite picky about the headers you send to them so if you supply
	 * {@link AkiwrapperBuilder} with your own {@link UnirestInstance} you should either
	 * get it from this or configure it accordingly.
	 *
	 * @return a new properly configured {@link UnirestInstance}
	 */
	@Nonnull
	@SuppressWarnings("null")
	public static UnirestInstance createInstance() {
		var unirest = spawnInstance();
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
