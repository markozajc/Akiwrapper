//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
 *
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.eu.zajc.akiwrapper;

import static java.lang.String.format;
import static org.eu.zajc.akiwrapper.core.entities.Server.GuessType.CHARACTER;
import static org.eu.zajc.akiwrapper.core.entities.Server.Language.ENGLISH;
import static org.eu.zajc.akiwrapper.core.utils.Servers.findServers;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.entities.Server.*;
import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.eu.zajc.akiwrapper.core.impl.AkiwrapperImpl;
import org.eu.zajc.akiwrapper.core.utils.UnirestUtils;
import org.slf4j.*;

import kong.unirest.UnirestInstance;

/**
 * A class used to build an {@link Akiwrapper} object.
 *
 * @author Marko Zajc
 */
public class AkiwrapperBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(AkiwrapperBuilder.class);

	@Nullable private UnirestInstance unirest;
	private boolean filterProfanity;
	@Nonnull private Language language;
	@Nonnull private GuessType guessType;

	/**
	 * The default profanity filter preference for new {@link Akiwrapper} instances.
	 */
	public static final boolean DEFAULT_FILTER_PROFANITY = false;

	/**
	 * The default {@link Language} for new {@link Akiwrapper} instances.
	 */
	@Nonnull public static final Language DEFAULT_LANGUAGE = ENGLISH;

	/**
	 * The default {@link Language} for new {@link Akiwrapper} instances.
	 *
	 * @deprecated Use {@link #DEFAULT_LANGUAGE} instead
	 */
	@Deprecated(since = "1.6", forRemoval = true)
	@Nonnull public static final Language DEFAULT_LOCALIZATION = DEFAULT_LANGUAGE;

	/**
	 * The default {@link GuessType} for new {@link Akiwrapper} instances.
	 */
	@Nonnull public static final GuessType DEFAULT_GUESS_TYPE = CHARACTER;

	private AkiwrapperBuilder(@Nullable UnirestInstance unirest, boolean filterProfanity, @Nonnull Language language,
							  @Nonnull GuessType guessType) {
		this.unirest = unirest;
		this.filterProfanity = filterProfanity;
		this.language = language;
		this.guessType = guessType;
	}

	/**
	 * Creates a new {@link AkiwrapperBuilder} with the following defaults:
	 * <ul>
	 * <li>profanity filtering is set to {@code false}
	 * ({@link #DEFAULT_FILTER_PROFANITY}),
	 * <li>language is set to {@link Language#ENGLISH} ({@link #DEFAULT_LANGUAGE}),
	 * <li>guess type is set to {@link GuessType#CHARACTER}
	 * ({@link #DEFAULT_GUESS_TYPE}),
	 * </ul>
	 */
	public AkiwrapperBuilder() {
		this(null, DEFAULT_FILTER_PROFANITY, DEFAULT_LANGUAGE, DEFAULT_GUESS_TYPE);
	}

	/**
	 * Sets the {@link UnirestInstance} to be used by the built Akiwrapper instance. Note
	 * that Akinator's services are quite picky about the HTTP client configuration, so
	 * you will very likely need to put your instance through
	 * {@link UnirestUtils#configureInstance(UnirestInstance)} before using it with
	 * Akiwrapper. You will also need to shut it down yourself, or, if you decide to set
	 * or leave this on {$code null}, call {@link UnirestUtils#shutdownInstance()} to
	 * shut down Akiwrapper's default singleton instance, otherwise its threads will stay
	 * alive.
	 *
	 * @param unirest
	 *            the {@link UnirestInstance} to be used by Akiwrapper or {$code null} to
	 *            use {@link UnirestUtils#getInstance()}
	 *
	 * @return current instance, used for chaining
	 *
	 * @see UnirestUtils#getInstance()
	 */
	@Nonnull
	public AkiwrapperBuilder setUnirestInstance(@Nullable UnirestInstance unirest) {
		this.unirest = unirest;
		return this;
	}

	/**
	 * Returns the {@link UnirestInstance} to be used by the built Akiwrapper instance.
	 * If this is {$code null}, {@link UnirestUtils#getInstance()} will be used (which
	 * means you will need to shut it down through
	 * {@link UnirestUtils#shutdownInstance()}, otherwise its threads will stay alive).
	 *
	 * @return {@link UnirestInstance} to be used or {$code null} for
	 *         {@link UnirestUtils#getInstance()}
	 */
	@Nullable
	public UnirestInstance getUnirestInstance() {
		return this.unirest;
	}

	/**
	 * Sets the "filter profanity" mode. Keep in mind that explicit {@link Guess}es can
	 * still be returned by {@link Akiwrapper#getGuesses()} or
	 * {@link Akiwrapper#suggestGuess()}, see {@link Guess#isExplicit()} for more details
	 * on why that is.<br>
	 * This is set to {@code false} by default.
	 *
	 * @param filterProfanity
	 *
	 * @return current instance, used for chaining.
	 *
	 * @see #doesFilterProfanity()
	 */
	@Nonnull
	public AkiwrapperBuilder setFilterProfanity(boolean filterProfanity) {
		this.filterProfanity = filterProfanity;
		return this;
	}

	/**
	 * Returns the profanity filter preference. Profanity filtering is done by Akinator
	 * and not by Akiwrapper. Keep in mind that explicit {@link Guess}es can still be
	 * returned by {@link Akiwrapper#getGuesses()} or {@link Akiwrapper#suggestGuess()},
	 * see {@link Guess#isExplicit()} for more details on why that is.<br>
	 * This is set to {@code false} by default.
	 *
	 * @return profanity filter preference.
	 */
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	/**
	 * <b>Note:</b> not all {@link Language}s support all {@link GuessType}s. The
	 * standard ones seem to be {@link GuessType#ANIMAL}, {@link GuessType#CHARACTER},
	 * and {@link GuessType#OBJECT}, but you might still face
	 * {@link ServerNotFoundException}s using them or other ones.<br>
	 * <br>
	 * Sets the {@link Language}. The server will return localized {@link Question}s and
	 * {@link Guess}es depending on this preference.<br>
	 * This is set to {@link Language#ENGLISH} by default.
	 *
	 * @param language
	 *
	 * @return current instance, used for chaining.
	 *
	 * @see #getLanguage()
	 */
	@Nonnull
	public AkiwrapperBuilder setLanguage(@Nonnull Language language) {
		this.language = language;
		return this;
	}

	/**
	 * Returns the {@link Language}. The server will return localized {@link Question}s
	 * and {@link Guess}es depending on this preference.<br>
	 * This is set to {@link Language#ENGLISH} by default.
	 *
	 * @return language preference.
	 */
	@Nonnull
	public Language getLanguage() {
		return this.language;
	}

	/**
	 * <b>Note:</b> not all {@link Language}s support all {@link GuessType}s. The
	 * standard ones seem to be {@link GuessType#ANIMAL}, {@link GuessType#CHARACTER},
	 * and {@link GuessType#OBJECT}, but you might still face
	 * {@link ServerNotFoundException}s using them or other ones.<br>
	 * <br>
	 * Sets the {@link GuessType}. This decides what kind of things the {@link Server}'s
	 * {@link Guess}es will represent. While the name might imply that this affects only
	 * guess content, it also affects {@link Question}s.<br>
	 * This is set to {@link GuessType#CHARACTER} by default.
	 *
	 * @param guessType
	 *
	 * @return current instance, used for chaining.
	 *
	 * @see #getLanguage()
	 */
	@Nonnull
	public AkiwrapperBuilder setGuessType(@Nonnull GuessType guessType) {
		this.guessType = guessType;
		return this;
	}

	/**
	 * Returns the {@link GuessType} preference. {@link GuessType} impacts what kind of
	 * subject {@link Question}s and {@link Guess}es are about.<br>
	 * {@link #getGuessType()} and {@link #getLanguage()} decide what {@link Server} will
	 * be used if it's not set manually.<br>
	 * This is set to {@link GuessType#CHARACTER} by default.
	 *
	 * @return guess type preference.
	 */
	@Nonnull
	public GuessType getGuessType() {
		return this.guessType;
	}

	/**
	 * Creates a new {@link Akiwrapper} instance from your preferences. If no
	 * {@link UnirestInstance} was set (with
	 * {@link #setUnirestInstance(UnirestInstance)}), a singleton instance will be
	 * acquired from {@link UnirestUtils#getInstance()}. This instance must be shut down
	 * after you're done using Akiwrapper with {@link UnirestUtils#shutdownInstance()},
	 * otherwise its threads will stay alive.
	 *
	 * @return a new {@link Akiwrapper} instance.
	 *
	 * @throws ServerNotFoundException
	 *             if no server with that {@link Language} and {@link GuessType} is
	 *             available.
	 */
	@Nonnull
	@SuppressWarnings({ "resource", "null" })
	public Akiwrapper build() throws ServerNotFoundException {
		var unirest = this.unirest != null ? this.unirest : UnirestUtils.getInstance();

		var servers = findServers(unirest, this.getLanguage(), this.getGuessType());
		if (servers.isEmpty())
			throw new ServerNotFoundException(format("No servers exist for %s - %s", this.language, this.guessType));

		for (var server : servers) {
			try {
				var api = new AkiwrapperImpl(unirest, server, this.filterProfanity);
				api.createSession();
				return api;

			} catch (ServerStatusException e) {
				LOG.debug("Failed to construct an instance, trying the next available server", e);
			}
		}

		throw new ServerNotFoundException(format("Servers exist for %s - %s, but none of them is usable", this.language,
												 this.guessType));
	}

}
