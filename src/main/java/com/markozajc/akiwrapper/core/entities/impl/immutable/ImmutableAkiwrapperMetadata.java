package com.markozajc.akiwrapper.core.entities.impl.immutable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;

/**
 * An immutable implementation of {@link AkiwrapperMetadata}.
 *
 * @author Marko Zajc
 */
public abstract class ImmutableAkiwrapperMetadata extends AkiwrapperMetadata {

	@Nullable
	protected final Server server;
	protected final boolean filterProfanity;
	@Nonnull
	protected final Language localization;
	@Nonnull
	protected final GuessType guessType;

	/**
	 * Creates a new {@link ImmutableAkiwrapperMetadata} instance.
	 *
	 * @param server
	 *            API server that the requests will be sent to.
	 * @param filterProfanity
	 *            whether to filter out NSFW {@link Question}s and {@link Guess}es.
	 * @param language
	 *            {@link Language} of {@link Question}s.
	 * @param guessType
	 *            {@link GuessType} of {@link Guess}es.
	 */
	public ImmutableAkiwrapperMetadata(@Nullable Server server, boolean filterProfanity, @Nonnull Language language,
									   @Nonnull GuessType guessType) {
		this.server = server;
		this.filterProfanity = filterProfanity;
		this.localization = language;
		this.guessType = guessType;
	}

	@Override
	public Server getServer() {
		return this.server;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	@Override
	public Language getLanguage() {
		return this.localization;
	}

	@Override
	public GuessType getGuessType() {
		return this.guessType;
	}

}
