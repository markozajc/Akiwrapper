package com.github.markozajc.akiwrapper.core.utils;

import static java.lang.Thread.*;

import javax.annotation.Nonnull;

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
	 * A {@link Thread#sleep(long)}-like method that throws {@link InterruptedException}.
	 * The exception is not suppressed or wrapped in a {@link RuntimeException}, but
	 * rather thrown with {@link #asUnchecked(Throwable)}.
	 *
	 * @param millis
	 */
	public static void sleepUnchecked(long millis) {
		try {
			sleep(millis);
		} catch (InterruptedException e) {
			currentThread().interrupt();
			throw asUnchecked(e);
		}
	}

	private Utilities() {}

}
