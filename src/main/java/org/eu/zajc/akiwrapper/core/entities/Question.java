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
package org.eu.zajc.akiwrapper.core.entities;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.AkiwrapperBuilder;
import org.eu.zajc.akiwrapper.Akiwrapper.Answer;

/**
 * A representation of Akinator's question that is to be answered with an
 * {@link Answer}. Each {@link Question} object has a localized string question, a
 * step number, gain, and progression.
 *
 * @author Marko Zajc
 */
public interface Question extends Identifiable {

	/**
	 * Current completion percentage (as a double). Higher means that Akinator believes
	 * to be closer to the correct answer.<br>
	 * The value ranges between 0 and 100.
	 *
	 * @return completion percentage.
	 */
	double getProgression();

	/**
	 * Returns the current step (question number). This uses zero-based index, meaning
	 * the first question will be on step {@code 0}.
	 *
	 * @return current step.
	 */
	int getStep();

	/**
	 * Returns the gained accuracy from the last question (as a double). I'm not exactly
	 * sure what this does.
	 *
	 * @return infogain.
	 */
	double getInfogain();

	/**
	 * Returns the question content that should be displayed to the user. This localized
	 * to the language specified in
	 * {@link AkiwrapperBuilder#setLanguage(Server.Language)}.
	 *
	 * @return question.
	 */
	@Nonnull
	String getQuestion();

}
