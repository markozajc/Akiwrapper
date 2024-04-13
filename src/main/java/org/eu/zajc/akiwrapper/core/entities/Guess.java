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
package org.eu.zajc.akiwrapper.core.entities;

import static java.lang.Long.parseLong;

import java.net.URL;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.Akiwrapper.Theme;
import org.eu.zajc.akiwrapper.AkiwrapperBuilder;

/**
 * A representation of Akinator's guess. A guess may span different types of subject,
 * depending on what was set for the {@link Theme} in the {@link AkiwrapperBuilder}
 * (default is {@link Theme#CHARACTER}).
 *
 * @author Marko Zajc
 */
public interface Guess extends Query {

	/**
	 * Confirms the {@link Guess}. This ends the session and likely affects Akinator's
	 * algorithm to associate the taken answer route with the confirmed guess, improving
	 * the guessing algorithm.
	 *
	 * @apiNote Do not use this method in automated tests, as it introduces faulty data
	 *          into Akinator's database, dulling the ranking algorithm.
	 */
	void confirm();

	/**
	 * Rejects the {@link Guess} and provides the next {@link Query}. If the response
	 * is a question, it will have the same step as the previous one, but different text.
	 *
	 * @return Akinator's {@link Query}.
	 *
	 * @apiNote Do not use this method in automated tests, as it introduces faulty data
	 *          into Akinator's database, dulling the ranking algorithm.
	 */
	@Nullable
	Query reject();

	/**
	 * Returns the name of the guessed subject. This is provided in the language that was
	 * specified using the {@link AkiwrapperBuilder}.
	 *
	 * @return guessed characer's name.
	 */
	@Nonnull
	String getName();

	/**
	 * Returns the pseudonym of the guessed subject. As a pseudonym is optional, this may
	 * be {@code null}. Please note that the pseudonym is sometimes set to a placeholder
	 * value such as "X" or "-" rather than @{code null}. This is provided in the
	 * language that was specified using the {@link AkiwrapperBuilder}.
	 *
	 * @return guessed characer's name.
	 */
	@Nullable
	String getPseudonym();

	/**
	 * Returns the description of this subject. Please note that the description is
	 * sometimes set to a placeholder value such as "X" or "-" rather than @{code null}.
	 * It is provided in the language that was specified using the
	 * {@link AkiwrapperBuilder}.
	 *
	 * @return description of the guessed subject.
	 */
	@Nonnull
	String getDescription();

	/**
	 * Returns the URL to an image of this subject. As an image of the subject is
	 * optional and thus not always present, this may be {@code null}.
	 *
	 * @return URL to picture or null if no picture is attached
	 */
	@Nullable
	URL getImage();

	/**
	 * Returns this guess's ID. ID's are unique to each guess and can be used to track
	 * rejected guesses, because Akinator won't do that for you.
	 *
	 * @return this guess's ID.
	 *
	 * @see #getIdLong()
	 */
	@Nonnull
	String getId();

	/**
	 * Returns this guess's ID as a @{code long}. ID's are unique to each guess and can
	 * be used to track rejected guesses, because Akinator won't do that for you.
	 *
	 * @return this guess's ID as a {@code long}.
	 *
	 * @see #getId()
	 */
	default long getIdLong() {
		return parseLong(getId());
	}

}
