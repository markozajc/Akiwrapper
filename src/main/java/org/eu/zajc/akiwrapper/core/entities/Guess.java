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

import java.io.ObjectInputFilter.Status;
import java.net.URL;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.*;
import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.eu.zajc.akiwrapper.core.utils.Utilities;

/**
 * A representation of Akinator's guess. Guesses are either confirmed with
 * {@link #confirm()} or rejected with {@link #reject()}. Rejecting a guess returns
 * the next question (which will have the same {@code step}), while confirming it
 * ends the session.<br>
 * <b>Note:</b> A single {@link Guess} object can only be interacted with once.
 * Calling {@link #confirm()} or {@link #reject()} mutates the session state, so you
 * can only call one of them once.
 *
 * @author Marko Zajc
 */
public interface Guess extends Query {

	/**
	 * Confirms the {@link Guess}. This ends the session and likely affects Akinator's
	 * algorithm to associate the taken answer route with the confirmed guess, improving
	 * the guessing algorithm.<br>
	 * <b>Note:</b> A single {@link Guess} object can only be interacted with once.
	 * Calling {@link #confirm()} or {@link #reject()} mutates the session state, so you
	 * can only call one of them once.
	 *
	 * @throws IllegalStateException
	 *             if this {@link Question} is not the same as
	 *             {@link Akiwrapper#getCurrentQuery()}, which happens if you attempt to
	 *             interact with it twice.
	 *
	 * @apiNote This method is thread safe - interaction methods are locked per
	 *          {@link Akiwrapper} instance.
	 * @apiNote Since this ends the session and is not really required to succeed, any
	 *          API exceptions thrown are suppressed.
	 * @apiNote Do not use this method in automated tests, as it introduces faulty data
	 *          into Akinator's database, dulling the ranking algorithm.
	 */
	void confirm();

	/**
	 * Rejects the {@link Guess} and provides the next {@link Query}. If the next query
	 * is a question, it will have the same step as the previous one, but different
	 * text.<br>
	 * <b>Note:</b> Rejecting a {@link Guess} does not mean the API won't propose it
	 * again. That happens quite often, in fact.<br>
	 * <b>Note:</b> A single {@link Guess} object can only be interacted with once.
	 * Calling {@link #confirm()} or {@link #reject()} mutates the session state, so you
	 * can only call one of them once.
	 *
	 * @throws IllegalStateException
	 *             if this {@link Question} is not the same as
	 *             {@link Akiwrapper#getCurrentQuery()}, which happens if you attempt to
	 *             interact with it twice.
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}.
	 * @throws AkinatorException
	 *             if something else goes wrong during the API call.
	 *
	 * @return the next {@link Query} or {@code null} if there are none left.
	 *
	 * @apiNote This method is thread safe - interaction methods are locked per
	 *          {@link Akiwrapper} instance.
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
	 * @return the guess' description.
	 */
	@Nonnull
	String getDescription();

	/**
	 * Returns the URL to an image of this subject, which can be a placeholder image
	 * ({@code https://photos.clarinea.fr/BL_1_fr/none.jpg}).
	 *
	 * @return the guess picture URL.
	 */
	@Nullable
	URL getImage();

	/**
	 * Returns this guess's ID. ID's are unique to each guess and can be used to track
	 * rejected guesses, because Akinator won't do that for you.
	 *
	 * @return this guess' ID.
	 *
	 * @see #getIdLong()
	 */
	@Nonnull
	String getId();

	/**
	 * Returns this guess's ID as a {@code long}. ID's are unique to each guess and can
	 * be used to track rejected guesses, because Akinator won't do that for you.
	 *
	 * @return this guess' ID as a {@code long}.
	 *
	 * @see #getId()
	 */
	default long getIdLong() {
		return Utilities.parseLong(getId());
	}

}
