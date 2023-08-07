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
package com.github.markozajc.akiwrapper.core.exceptions;

import com.github.markozajc.akiwrapper.Akiwrapper;

/**
 * An exception indicating that {@link Akiwrapper#undoAnswer()} has been called on
 * the first question - when {@link Akiwrapper#getStep()} is {@code 0}.
 *
 * @author Marko Zajc
 */
public class UndoOutOfBoundsException extends AkinatorException {

	@SuppressWarnings("javadoc") // internal
	public UndoOutOfBoundsException() {}

}
