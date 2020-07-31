package com.markozajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;

/**
 * A set of vital data used in API calls and such.
 *
 * @author Marko Zajc
 */
public abstract class AkiwrapperMetadata {

	/**
	 * Default name for new {@link Akiwrapper} instances.
	 */
	public static final String DEFAULT_NAME = "website-desktop";

	/**
	 * Default profanity filter preference for new {@link Akiwrapper} instances.
	 */
	public static final boolean DEFAULT_FILTER_PROFANITY = false;

	/**
	 * Default localization {@link Language} for new {@link Akiwrapper} instances.
	 */
	@Nonnull
	public static final Language DEFAULT_LOCALIZATION = Language.ENGLISH;

	/**
	 * Default {@link GuessType} for new {@link Akiwrapper} instances.
	 */
	@Nonnull
	public static final GuessType DEFAULT_GUESS_TYPE = GuessType.CHARACTER;

	/**
	 * @return user's name, does not have any impact on gameplay.
	 */
	@Nonnull
	public abstract String getName();

	/**
	 * @return API server that the requests will be sent to.
	 */
	@Nullable
	public abstract Server getServer();

	/**
	 * @return whether to filter out NSFW {@link Question}s and {@link Guess}es.
	 */
	public abstract boolean doesFilterProfanity();

	/**
	 * @return {@link Language} of {@link Question}s.
	 */
	@Nonnull
	public abstract Language getLanguage();

	/**
	 * @return {@link GuessType} of {@link Guess}es.
	 */
	@Nonnull
	public abstract GuessType getGuessType();
}
