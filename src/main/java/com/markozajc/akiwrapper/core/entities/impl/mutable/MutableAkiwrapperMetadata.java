package com.markozajc.akiwrapper.core.entities.impl.mutable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ImmutableAkiwrapperMetadata;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * A mutable implementation of {@link AkiwrapperMetadata}.
 *
 * @author Marko Zajc
 */
public abstract class MutableAkiwrapperMetadata extends AkiwrapperMetadata {

	@Nonnull
	protected String name;
	@Nullable
	protected Server server;
	protected boolean filterProfanity;
	@Nonnull
	protected Language localization;
	@Nonnull
	protected GuessType guessType;

	/**
	 * Creates a new {@link ImmutableAkiwrapperMetadata} instance.
	 *
	 * @param server
	 *            API server that the requests will be sent to.
	 * @param name
	 *            user's name, does not have any impact on gameplay.
	 * @param filterProfanity
	 *            whether to filter out NSFW {@link Question}s and {@link Guess}es.
	 * @param language
	 *            {@link Language} of {@link Question}s.
	 * @param guessType
	 *            {@link GuessType} of {@link Guess}es.
	 */
	public MutableAkiwrapperMetadata(@Nonnull String name, @Nullable Server server, boolean filterProfanity,
	                                 @Nonnull Language language, @Nonnull GuessType guessType) {
		this.name = name;
		this.server = server;
		this.filterProfanity = filterProfanity;
		this.localization = language;
		this.guessType = guessType;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Sets user's name.
	 *
	 * @param name
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getName()
	 */
	public MutableAkiwrapperMetadata setName(@Nonnull String name) {
		this.name = name;

		return this;
	}

	@Override
	public Server getServer() {
		return this.server;
	}

	/**
	 * Sets the {@link Server}.
	 *
	 * @param server
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getServer()
	 * @see Servers#findServer(Language, GuessType)
	 */
	@Nonnull
	public MutableAkiwrapperMetadata setServer(@Nullable Server server) {
		this.server = server;

		return this;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	/**
	 * Sets the "filter profanity" mode.
	 *
	 * @param filterProfanity
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #doesFilterProfanity()
	 */
	@Nonnull
	public MutableAkiwrapperMetadata setFilterProfanity(boolean filterProfanity) {
		this.filterProfanity = filterProfanity;

		return this;
	}

	@Override
	public Language getLanguage() {
		return this.localization;
	}

	/**
	 * Sets the {@link Language}.
	 *
	 * @param language
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getLanguage()
	 */
	@Nonnull
	public MutableAkiwrapperMetadata setLanguage(@Nonnull Language language) {
		this.localization = language;

		return this;
	}

	@Override
	public GuessType getGuessType() {
		return this.guessType;
	}

	/**
	 * Sets the {@link GuessType}.
	 *
	 * @param guessType
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getLanguage()
	 */
	@Nonnull
	public MutableAkiwrapperMetadata setGuessType(@Nonnull GuessType guessType) {
		this.guessType = guessType;

		return this;
	}

}
