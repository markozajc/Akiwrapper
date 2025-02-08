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

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.Akiwrapper;

/**
 * A common interface for Akinator's queries, which will be either {@link Question}s
 * or {@link Guess}es. <br>
 * <b>Note:</b> A single {@link Query} object can only be interacted with once.
 * Calling interaction methods mutates the session state, so you can only call one of
 * them once.
 *
 * @author Marko Zajc
 */
public interface Query {

	/**
	 * @return the {@link Akiwrapper} instance.
	 */
	@Nonnull
	Akiwrapper getAkiwrapper();

	/**
	 * Returns the current step (question number). This uses zero-based index, meaning
	 * the first question will be on step {@code 0}. Answering a {@link Question}
	 * increments the step, and undoing a {@link Question} decrements it. {@link Guess}es
	 * will have the same {@code step} as the previous {@link Question}, and rejecting
	 * them will not increment the step.
	 *
	 * @return current step.
	 */
	int getStep();

	/**
	 * Current completion percentage (as a double). A higher value means that Akinator
	 * believes it is closer to the correct answer. {@link Guess}es will have the same
	 * {@code prorgession} as the previous {@link Question}.<br>
	 * The value ranges between 0 and 100.
	 *
	 * @return completion percentage.
	 */
	double getProgression();

}
