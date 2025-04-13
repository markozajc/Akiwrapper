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
package org.eu.zajc.akiwrapper.core.exceptions;

/**
 * The root exception class for exceptions in Akiwrapper.
 *
 * @author Marko Zajc
 */
public class AkinatorException extends RuntimeException {

	@SuppressWarnings("javadoc") // internal
	public AkinatorException() {}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message) {
		super(message);
	}

	@SuppressWarnings("javadoc") // internal
	public AkinatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
