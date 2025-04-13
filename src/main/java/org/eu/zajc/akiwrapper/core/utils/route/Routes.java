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
package org.eu.zajc.akiwrapper.core.utils.route;

import org.eu.zajc.akiwrapper.Akiwrapper.Answer;
import org.eu.zajc.akiwrapper.core.entities.Guess;
import org.eu.zajc.akiwrapper.core.entities.impl.GuessImpl;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 * A list of API {@link Route}s used by Akiwrapper.
 *
 * @author Marko Zajc
 */
public final class Routes {

	/**
	 * The current zero-indexed question number
	 */
	public static final String PARAMETER_STEP = "step";
	/**
	 * The current progression
	 */
	public static final String PARAMETER_PROGRESSION = "progression";
	/**
	 * The {@link Answer} index
	 */
	public static final String PARAMETER_ANSWER = "answer";
	/**
	 * The last step that a {@link Guess} was proposed on
	 */
	public static final String PARAMETER_STEP_LAST_PROPOSITION = "step_last_proposition";
	/**
	 * The value of {@link Guess#getId()}
	 */
	public static final String PARAMETER_GUESS_ID = "pid";
	/**
	 * The value of {@link Guess#getName()}
	 */
	public static final String PARAMETER_GUESS_NAME = "charac_name";
	/**
	 * The value of {@link Guess#getDescription()}
	 */
	public static final String PARAMETER_GUESS_DESCRIPTION = "charac_desc";
	/**
	 * The value of {@link GuessImpl#getFlagPhoto()}. The purpose of this is unknown
	 */
	public static final String PARAMETER_GUESS_FLAG_PHOTO = "pflag_photo";

	/**
	 * Creates a new game session that all further state is associated with.<br>
	 * <i>This route requires no parameters.</i>
	 */
	public static final Route NEW_SESSION = new RouteBuilder("/game").build();

	/**
	 * Answers the current question and fetches the next one.<br>
	 * <b>This route requires a session</b><br>
	 * Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}</li>
	 * <li>{@link Routes#PARAMETER_PROGRESSION}</li>
	 * <li>{@link Routes#PARAMETER_ANSWER}</li>
	 * <li>{@link Routes#PARAMETER_STEP_LAST_PROPOSITION}</li>
	 * </ul>
	 */
	public static final Route ANSWER = new RouteBuilder("/answer").requiresSession()
		.parameters(PARAMETER_STEP, PARAMETER_PROGRESSION, PARAMETER_ANSWER, PARAMETER_STEP_LAST_PROPOSITION)
		.build();

	/**
	 * Cancels (undoes) an answer and fetches the previous question. <br>
	 * <b>This route requires a session</b><br>
	 * Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}</li>
	 * <li>{@link Routes#PARAMETER_PROGRESSION}</li>
	 * </ul>
	 */
	public static final Route CANCEL_ANSWER =
		new RouteBuilder("/cancel_answer").requiresSession().parameters(PARAMETER_STEP, PARAMETER_PROGRESSION).build();

	/**
	 * <b>IMPORTANT: This route is EXCLUDED from tests!</b> Because automated tests don't
	 * tend to behave like players, calling this during testing might introduce faulty
	 * data into Akinator's algorithm, so please avoid doing that.<br>
	 * Rejects the current {@link Guess}<br>
	 * <b>This route requires a session</b><br>
	 * Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}</li>
	 * <li>{@link Routes#PARAMETER_PROGRESSION}</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 */
	public static final Route EXCLUDE =
		new RouteBuilder("/exclude").requiresSession().parameters(PARAMETER_STEP, PARAMETER_PROGRESSION).build();

	/**
	 * <b>IMPORTANT: This route is EXCLUDED from tests!</b> Because automated tests don't
	 * tend to behave like players, calling this during testing might introduce faulty
	 * data into Akinator's algorithm, so please avoid doing that.<br>
	 * Confirm a guess. While this doesn't affect the current session, because it's
	 * called at the very end, it likely affects Akinator's algorithm and associates the
	 * taken answer route with the confirmed guess, thus improving the game for everyone.
	 * Rejects the current {@link Guess}<br>
	 * <b>This route requires a session</b><br>
	 * Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}</li>
	 * <li>{@link Routes#PARAMETER_GUESS_ID}</li>
	 * <li>{@link Routes#PARAMETER_GUESS_NAME}</li>
	 * <li>{@link Routes#PARAMETER_GUESS_DESCRIPTION}</li>
	 * <li>{@link Routes#PARAMETER_GUESS_FLAG_PHOTO}</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 */
	public static final Route CHOICE = new RouteBuilder("/choice").requiresSession()
		.parameters(PARAMETER_STEP, PARAMETER_GUESS_ID, PARAMETER_GUESS_FLAG_PHOTO, PARAMETER_GUESS_NAME,
					PARAMETER_GUESS_DESCRIPTION)
		.build();

	private Routes() {}

}
