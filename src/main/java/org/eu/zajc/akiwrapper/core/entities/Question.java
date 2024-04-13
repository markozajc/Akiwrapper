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
import org.eu.zajc.akiwrapper.Akiwrapper.Answer;
import org.eu.zajc.akiwrapper.core.exceptions.*;

/**
 * A representation of Akinator's question that is to be answered with an
 * {@link Answer}.
 *
 * @author Marko Zajc
 */
public interface Question extends Response {

	/**
	 * Sends an answer to the current {@link Question} and fetches the {@link Response},
	 * incrementing the current step.<br>
	 * If there are no more questions left, this will return {@code null}. Any subsequent
	 * calls to this method after the question list has been exhausted will throw a
	 * {@link QuestionsExhaustedException}. A call to this method can be undone with
	 * {@link #undoAnswer()}.
	 *
	 * @param answer
	 *            the {@link Answer} to send.
	 *
	 * @return the next {@link Question} or {@code null} if there are no questions left.
	 *
	 * @throws QuestionsExhaustedException
	 *             if the session has exhausted all questions (when
	 *             {@link #isExhausted()} returns {@code true}).
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}.
	 *
	 * @see #undoAnswer()
	 */
	@Nullable
	Response answer(Answer answer);

	/**
	 * Goes one question backwards, undoing the previous {@link #answer(Answer)} call.
	 * For example, if {@link #getQuestion()} returns a question on step {@code 5},
	 * calling this command will make {@link #getQuestion()} return the question from
	 * step {@code 4}. You can call this as many times as you want, until you reach step
	 * {@code 0}<br>
	 * If this method is called on step {@code 0}, {@link UndoOutOfBoundsException} is
	 * thrown. If this method is called after questions have been exhausted,
	 * {@link QuestionsExhaustedException} is thrown.
	 *
	 * @return the previous {@link Question}.
	 *
	 * @throws UndoOutOfBoundsException
	 *             if the session has exhausted all questions (when
	 *             {@link #getQuestion()} returns {@code null}.
	 * @throws QuestionsExhaustedException
	 *             if the session has exhausted all questions (when
	 *             {@link #isExhausted()} returns {@code true}).
	 * @throws ServerStatusException
	 *             if the API server returns an erroneous {@link Status}.
	 *
	 * @see #answer(Answer)
	 */
	@Nonnull
	Response undoAnswer();

	/**
	 * Returns the question content that should be displayed to the user. This localized
	 * to the language specified in
	 * {@link AkiwrapperBuilder#setLanguage(Akiwrapper.Language)}.
	 *
	 * @return question.
	 */
	@Nonnull
	String getText();

	/**
	 * Returns the question content that should be displayed to the user. This localized
	 * to the language specified in
	 * {@link AkiwrapperBuilder#setLanguage(Akiwrapper.Language)}.
	 *
	 * @return question.
	 *
	 * @deprecated use {@link #getText()} instead.
	 */
	@Nonnull
	@Deprecated(since = "2.0", forRemoval = true)
	default String getQuestion() {
		return getText();
	}

	URL getAkitude();

}
