package com.markozajc.akiwrapper.core.entities.impl.mutable;

import javax.annotation.*;

import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.Server.*;

public abstract class MutableAkiwrapperMetadata extends AkiwrapperMetadata {

	@Nullable
	protected Server server;
	protected boolean filterProfanity;
	@Nonnull
	protected Language language;
	@Nonnull
	protected GuessType guessType;

	protected MutableAkiwrapperMetadata(@Nullable Server server, boolean filterProfanity, @Nonnull Language language,
									 @Nonnull GuessType guessType) {
		this.server = server;
		this.filterProfanity = filterProfanity;
		this.language = language;
		this.guessType = guessType;
	}

	@Override
	public Server getServer() {
		return this.server;
	}

	@Nonnull
	public MutableAkiwrapperMetadata setServer(@Nullable Server server) {
		this.server = server;
		if (server != null) {
			this.language = server.getLanguage();
			this.guessType = server.getGuessType();
		}
		return this;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	@Nonnull
	public MutableAkiwrapperMetadata setFilterProfanity(boolean filterProfanity) {
		this.filterProfanity = filterProfanity;
		return this;
	}

	@Override
	public Language getLanguage() {
		return this.language;
	}

	@Nonnull
	public MutableAkiwrapperMetadata setLanguage(@Nonnull Language language) {
		this.language = language;
		this.server = null;
		return this;
	}

	@Override
	public GuessType getGuessType() {
		return this.guessType;
	}

	@Nonnull
	public MutableAkiwrapperMetadata setGuessType(@Nonnull GuessType guessType) {
		this.guessType = guessType;
		this.server = null;

		return this;
	}

}
