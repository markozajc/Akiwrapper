package com.markozajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;

/**
 * A class holding configuration for an {@link Akiwrapper} instance. Note that
 * {@link Language}, {@link GuessType}, and {@link Server} configuration are
 * connected - {@link Language} and {@link GuessType} are used to find a suitable
 * {@link Server}, but they will only be used if a {@link Server} is not manually
 * set. It is not recommended to set the {@link Server} manually (unless for
 * debugging purposes or as some kind of workaround where Akiwrapper's server finder
 * fails) as Akiwrapper already does its best to find the most suitable one.
 *
 * @author Marko Zajc
 */
public abstract class AkiwrapperMetadata {

	/**
	 * The default profanity filter preference for new {@link Akiwrapper} instances.
	 */
	public static final boolean DEFAULT_FILTER_PROFANITY = false;

	/**
	 * The default {@link Language} for new {@link Akiwrapper} instances.
	 */
	@Nonnull
	public static final Language DEFAULT_LOCALIZATION = Language.ENGLISH;

	/**
	 * The default {@link GuessType} for new {@link Akiwrapper} instances.
	 */
	@Nonnull
	public static final GuessType DEFAULT_GUESS_TYPE = GuessType.CHARACTER;

	/**
	 * Returns the {@link Server} that requests will be sent to. Might also return a
	 * {@link ServerList} (which extends {@link Server}).
	 *
	 * @return server.
	 */
	@Nullable
	public abstract Server getServer();

	/**
	 * Returns the profanity filter preference. Profanity filtering is done by Akinator
	 * and not by Akiwrapper.
	 *
	 * @return profanity filter preference.
	 */
	public abstract boolean doesFilterProfanity();

	/**
	 * Returns the {@link Language} preference. {@link Language} impacts what language
	 * {@link Question}s and {@link Guess}es are in.<br>
	 * {@link #getGuessType()} and {@link #getLanguage()} decide what {@link Server} will
	 * be used if it's not set manually.
	 *
	 * @return language preference.
	 */
	@Nonnull
	public abstract Language getLanguage();

	/**
	 * Returns the {@link GuessType} preference. {@link GuessType} impacts what kind of
	 * subject {@link Question}s and {@link Guess}es are about.<br>
	 * {@link #getGuessType()} and {@link #getLanguage()} decide what {@link Server} will
	 * be used if it's not set manually.
	 *
	 * @return guess type preference.
	 */
	@Nonnull
	public abstract GuessType getGuessType();
}
