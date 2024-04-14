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
package org.eu.zajc.akiwrapper;

import static org.eu.zajc.akiwrapper.Akiwrapper.Language.ENGLISH;
import static org.eu.zajc.akiwrapper.Akiwrapper.Theme.CHARACTER;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.Akiwrapper.*;
import org.eu.zajc.akiwrapper.core.entities.Query;
import org.eu.zajc.akiwrapper.core.entities.impl.AkiwrapperImpl;
import org.eu.zajc.akiwrapper.core.exceptions.LanguageThemeCombinationException;
import org.eu.zajc.akiwrapper.core.utils.UnirestUtils;

import kong.unirest.UnirestInstance;

/**
 * A class used to build an {@link Akiwrapper} object.
 *
 * @author Marko Zajc
 */
public class AkiwrapperBuilder {

	@Nullable private UnirestInstance unirest;
	private boolean filterProfanity;
	@Nonnull private Language language;
	@Nonnull private Theme theme;

	/**
	 * The default profanity filter preference for new {@link Akiwrapper} instances.
	 */
	public static final boolean DEFAULT_FILTER_PROFANITY = false;

	/**
	 * The default {@link Language} for new {@link Akiwrapper} instances.
	 */
	@Nonnull public static final Language DEFAULT_LANGUAGE = ENGLISH;

	/**
	 * The default {@link Theme} for new {@link Akiwrapper} instances.
	 */
	@Nonnull public static final Theme DEFAULT_THEME = CHARACTER;

	/**
	 * The default {@link Theme} for new {@link Akiwrapper} instances.
	 *
	 * @deprecated Use {@link #DEFAULT_THEME} instead
	 */
	@Deprecated(since = "2.0", forRemoval = true) @Nonnull public static final Theme DEFAULT_GUESS_TYPE = CHARACTER;

	private AkiwrapperBuilder(@Nullable UnirestInstance unirest, boolean filterProfanity, @Nonnull Language language,
							  @Nonnull Theme theme) {
		this.unirest = unirest;
		this.filterProfanity = filterProfanity;
		this.language = language;
		this.theme = theme;
	}

	/**
	 * Creates a new {@link AkiwrapperBuilder} with the following defaults:
	 * <ul>
	 * <li>profanity filtering is set to {@code false}
	 * ({@link #DEFAULT_FILTER_PROFANITY}),
	 * <li>language is set to {@link Language#ENGLISH} ({@link #DEFAULT_LANGUAGE}),
	 * <li>theme is set to {@link Theme#CHARACTER} ({@link #DEFAULT_GUESS_TYPE}),
	 * </ul>
	 */
	public AkiwrapperBuilder() {
		this(null, DEFAULT_FILTER_PROFANITY, DEFAULT_LANGUAGE, DEFAULT_THEME);
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
	 *            use {@link UnirestUtils#getInstance()}.
	 *
	 * @return current instance, used for chaining.
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
	 *         {@link UnirestUtils#getInstance()}.
	 */
	@Nullable
	public UnirestInstance getUnirestInstance() {
		return this.unirest;
	}

	/**
	 * Sets the profanity filter preference. Profanity filtering is done by Akinator and
	 * not by Akiwrapper. Keep in mind that Akinator's filters aren't perfect, so
	 * explicit {@link Query} objects can still be returned.<br>
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
	 * and not by Akiwrapper. Keep in mind that Akinator's filters aren't perfect, so
	 * explicit {@link Query} objects can still be returned.<br>
	 * This is set to {@code false} by default.
	 *
	 * @return profanity filter preference.
	 */
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	/**
	 * <b>Note:</b> while all {@link Language}s support {@link Theme#CHARACTER}, but
	 * other themes might not be supported. Call {@link Language#getSupportedThemes()}
	 * for a list of supported themes. <br>
	 * Sets the {@link Language}. Akinator will return localized {@link Query} objects
	 * depending on this preference.<br>
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
	 * <b>Note:</b> while all {@link Language}s support {@link Theme#CHARACTER}, but
	 * other themes might not be supported. Call {@link Language#getSupportedThemes()}
	 * for a list of supported themes. <br>
	 * Sets the {@link Language}. Akinator will return localized {@link Query} objects
	 * depending on this preference.<br>
	 * This is set to {@link Language#ENGLISH} by default.
	 *
	 * @return language preference.
	 *
	 * @see #setLanguage(Language)
	 */
	@Nonnull
	public Language getLanguage() {
		return this.language;
	}

	/**
	 * <b>Note:</b> while all {@link Language}s support {@link Theme#CHARACTER}, but
	 * other themes might not be supported. Call {@link Language#getSupportedThemes()}
	 * for a list of supported themes. <br>
	 * Sets the {@link Theme}. This decides the theme of Akinator's {@link Query}
	 * objects.<br>
	 * This is set to {@link Theme#CHARACTER} by default.
	 *
	 * @param theme
	 *
	 * @return current instance, used for chaining.
	 *
	 * @see #getTheme()
	 */
	@Nonnull
	public AkiwrapperBuilder setTheme(@Nonnull Theme theme) {
		this.theme = theme;
		return this;
	}

	/**
	 * <b>Note:</b> while all {@link Language}s support {@link Theme#CHARACTER}, but
	 * other themes might not be supported. Call {@link Language#getSupportedThemes()}
	 * for a list of supported themes. <br>
	 * Sets the {@link Theme}. This decides the theme of Akinator's {@link Query}
	 * objects.<br>
	 * This is set to {@link Theme#CHARACTER} by default.
	 *
	 * @param theme
	 *
	 * @return current instance, used for chaining.
	 *
	 * @see #getGuessType()
	 *
	 * @deprecated Use {@link #setTheme(Theme)} instead
	 */
	@Nonnull
	@Deprecated(since = "2.0", forRemoval = true)
	public AkiwrapperBuilder setGuessType(@Nonnull Theme theme) {
		this.theme = theme;
		return this;
	}

	/**
	 * <b>Note:</b> while all {@link Language}s support {@link Theme#CHARACTER}, but
	 * other themes might not be supported. Call {@link Language#getSupportedThemes()}
	 * for a list of supported themes. <br>
	 * Sets the {@link Theme}. This decides the theme of Akinator's {@link Query}
	 * objects.<br>
	 * This is set to {@link Theme#CHARACTER} by default.
	 *
	 * @return theme preference.
	 *
	 * @see #setTheme(Theme)
	 */
	@Nonnull
	public Theme getTheme() {
		return this.theme;
	}

	/**
	 * <b>Note:</b> while all {@link Language}s support {@link Theme#CHARACTER}, but
	 * other themes might not be supported. Call {@link Language#getSupportedThemes()}
	 * for a list of supported themes. <br>
	 * Sets the {@link Theme}. This decides the theme of Akinator's {@link Query}
	 * objects.<br>
	 * This is set to {@link Theme#CHARACTER} by default.
	 *
	 * @return theme preference.
	 *
	 * @see #setGuessType(Theme)
	 *
	 * @deprecated Use {@link #getTheme()} instead.
	 */
	@Nonnull
	@Deprecated(since = "2.0", forRemoval = true)
	public Theme getGuessType() {
		return this.theme;
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
	 * @throws LanguageThemeCombinationException
	 *             if the {@link Language} and {@link Theme} combination is incompatible.
	 */
	@Nonnull
	@SuppressWarnings("resource")
	public Akiwrapper build() throws LanguageThemeCombinationException {
		var unirest = this.unirest != null ? this.unirest : UnirestUtils.getInstance();
		if (!this.language.isThemeSupported(this.theme))
			throw new LanguageThemeCombinationException(this.language, this.theme);

		var api = new AkiwrapperImpl(unirest, this.language, this.theme, this.filterProfanity);
		api.createSession();
		return api;
	}

}
