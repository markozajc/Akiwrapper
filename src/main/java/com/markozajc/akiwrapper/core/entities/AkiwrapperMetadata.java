package com.markozajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.Server.Language;

/**
 * A set of vital data used in API calls and such.
 *
 * @author Marko Zajc
 */
public abstract class AkiwrapperMetadata {

	/**
	 * The default name for new {@link Akiwrapper} instances.
	 */
	public static final String DEFAULT_NAME = "website-desktop";

	/**
	 * The default user-agent for new {@link Akiwrapper} instances.
	 */
	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
	    + "Chrome/66.0.3359.181 Safari/537.36";

	/**
	 * The default profanity filter for new {@link Akiwrapper} instances.
	 */
	public static final boolean DEFAULT_FILTER_PROFANITY = false;

	/**
	 * The default localization language for new {@link Akiwrapper} instances.
	 */
	@Nonnull
	public static final Language DEFAULT_LOCALIZATION = Language.ENGLISH;

	/**
	 * @return user's name, does not have any impact on gameplay
	 */
	@Nonnull
	public abstract String getName();

	/**
	 * @return user-agent used in HTTP requests
	 */
	@Nonnull
	public abstract String getUserAgent();

	/**
	 * @return the API server used for all requests. All API servers have equal data and
	 *         endpoints but some might be down so you should never hard-code usage of a
	 *         specific API server
	 */
	@Nullable
	public abstract Server getServer();

	/**
	 * @return whether to tell Akinator's API to filter out NSFW information
	 */
	public abstract boolean doesFilterProfanity();

	/**
	 * @return the language all elements will be in (eg. questions)
	 */
	@Nonnull
	public abstract Language getLocalization();
}
