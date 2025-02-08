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
package org.eu.zajc.akiwrapper.core.entities;

import java.io.ObjectInputFilter.Status;
import java.net.URL;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.*;
import org.eu.zajc.akiwrapper.Akiwrapper.*;
import org.eu.zajc.akiwrapper.core.exceptions.*;

/**
 * A type of {@link Query} that represents Akinator's question. Questions are
 * answered with {@link #answer(Answer)} (incrementing the {@code step} or undone
 * with {@link #undoAnswer()} (decrementing the {@code step}). Besides the question
 * text, questions also come with an "akitude" picture, a portmanteau of "Akinator"
 * and "attitude", which can be shown to the user.<br>
 * <b>Note:</b> A single {@link Question} object can only be interacted with once.
 * Calling {@link #answer(Answer)} or {@link #undoAnswer()} mutates the session
 * state, so you can only call one of them once.
 *
 * @author Marko Zajc
 */
public interface Question extends Query {

	/**
	 * This is an interaction method for {@link Question}.<br>
	 * Submits an answer for the question and returns the next {@link Query},
	 * incrementing the current step.<br>
	 * If there are no more questions left, this will return {@code null}. <br>
	 * <b>Note:</b> A single {@link Question} object can only be interacted with once.
	 * Calling {@link #answer(Answer)} or {@link #undoAnswer()} mutates the session
	 * state, so you can only call one of them once.
	 *
	 * @param answer
	 *            the {@link Answer} to submit.
	 *
	 * @return the next {@link Query} or {@code null} if there are none left.
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
	 * @see #undoAnswer()
	 *
	 * @apiNote This method is thread safe - interaction methods are locked per
	 *          {@link Akiwrapper} instance.
	 */
	@Nullable
	Query answer(Answer answer);

	/**
	 * This is an interaction method for {@link Question}.<br>
	 * Goes one question backwards, undoing the previous {@link #answer(Answer)} call.
	 * For example, if {@link #getQuestion()} returns a question on step {@code 5},
	 * calling this command will return the question on step {@code 4}. You can call this
	 * as many times as you want, until you reach step {@code 0}. Note that this will
	 * always return a {@link Question}, and never a {@link Guess} or {@code null}.<br>
	 * <b>Note:</b> A single {@link Question} object can only be interacted with once.
	 * Calling {@link #answer(Answer)} or {@link #undoAnswer()} mutates the session
	 * state, so you can only call one of them once.
	 *
	 * @return the previous {@link Question}.
	 *
	 * @throws UndoOutOfBoundsException
	 *             if the session has exhausted all questions (when
	 *             {@link #getQuestion()} returns {@code null}.
	 * @throws IllegalStateException
	 *             if this {@link Question} is not the same as
	 *             {@link Akiwrapper#getCurrentQuery()}, which happens if you attempt to
	 *             interact with it twice.
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}.
	 * @throws AkinatorException
	 *             if something else goes wrong during the API call.
	 *
	 * @see #answer(Answer)
	 *
	 * @apiNote This method is thread safe - interaction methods are locked per
	 *          {@link Akiwrapper} instance.
	 */
	@Nonnull
	Question undoAnswer();

	/**
	 * Returns the question text that should be displayed to the user. This is localized
	 * to the {@link Language} and in line with the {@link Theme} set in the
	 * {@link AkiwrapperBuilder}.<br>
	 *
	 * @return the question text, for example {@code "Is your character real?"}.
	 */
	@Nonnull
	String getText();

	/**
	 * Returns the question text that should be displayed to the user. This is localized
	 * to the {@link Language} and in line with the {@link Theme} set in the
	 * {@link AkiwrapperBuilder}.<br>
	 *
	 * @return the question text, for example {@code "Is your character real?"}.
	 *
	 * @deprecated use {@link #getText()} instead.
	 */
	@Nonnull
	@Deprecated(since = "2.0", forRemoval = true)
	default String getQuestion() {
		return getText();
	}

	/**
	 * URL to the akitude image. "Akitude" is likely a portmanteau of "Akinator" and
	 * "attitude", and they represent Akinator's current confidence - previously this was
	 * calculated using a formula on the step and progression values, now it's returned
	 * by the API. On the website, the akitude is shown on the left of the question
	 * box.<br>
	 *
	 * @return the akitude image URL, for example
	 *         {@code "https://en.akinator.com/assets/img/akitudes_670x1096/defi.png"}.
	 */
	URL getAkitude();

}
