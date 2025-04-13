//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2025 Marko Zajc
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

import java.net.http.HttpClient;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.Akiwrapper.*;
import org.eu.zajc.akiwrapper.core.entities.Query;
import org.eu.zajc.akiwrapper.core.entities.impl.AkiwrapperImpl;
import org.eu.zajc.akiwrapper.core.exceptions.LanguageThemeCombinationException;

/**
 * A class used to build an {@link Akiwrapper} object.
 *
 * @author Marko Zajc
 */
public class AkiwrapperBuilder {

	@Nullable private HttpClient httpClient;
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

	private AkiwrapperBuilder(@Nullable HttpClient httpClient, boolean filterProfanity, @Nonnull Language language,
							  @Nonnull Theme theme) {
		this.httpClient = httpClient;
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
	 * Sets the {@link HttpClient} to be used by the built Akiwrapper instance.
	 *
	 * @param httpClient
	 *            the {@link HttpClient} to be used or {$code null} to use
	 *            {@link HttpClient#newHttpClient()}.
	 *
	 * @return current instance, used for chaining.
	 */
	@Nonnull
	public AkiwrapperBuilder setHttpClient(@Nullable HttpClient httpClient) {
		this.httpClient = httpClient;
		return this;
	}

	/**
	 * Returns the {@link HttpClient} to be used by the built Akiwrapper instance.
	 *
	 * @return {@link HttpClient} to be used or {$code null} to use
	 *         {@link HttpClient#newHttpClient()}.
	 */
	@Nullable
	public HttpClient getUnirestInstance() {
		return this.httpClient;
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
	 * @return a new {@link Akiwrapper} instance.
	 *
	 * @throws LanguageThemeCombinationException
	 *             if the {@link Language} and {@link Theme} combination is incompatible.
	 */
	@Nonnull
	@SuppressWarnings("null")
	public Akiwrapper build() throws LanguageThemeCombinationException {
		var httpClient = this.httpClient != null ? this.httpClient : HttpClient.newHttpClient();
		if (!this.language.isThemeSupported(this.theme))
			throw new LanguageThemeCombinationException(this.language, this.theme);

		var api = new AkiwrapperImpl(httpClient, this.language, this.theme, this.filterProfanity);
		api.createSession();
		return api;
	}

}
