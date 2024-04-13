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

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.Akiwrapper;

public interface Response {

	@Nonnull
	Akiwrapper getAkiwrapper();

	/**
	 * Returns the current step (question number). This uses zero-based index, meaning
	 * the first question will be on step {@code 0}. TODO details for guess
	 *
	 * @return current step.
	 */
	int getStep();

	/**
	 * Current completion percentage (as a double). Higher means that Akinator believes
	 * to be closer to the correct answer.<br>
	 * The value ranges between 0 and 100.
	 *
	 * @return completion percentage.
	 */
	double getProgression();

}
