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
package com.github.markozajc.akiwrapper.core.entities.impl.immutable;

import static com.github.markozajc.akiwrapper.core.entities.Status.Reason.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.github.markozajc.akiwrapper.core.entities.Status.Level;
import com.github.markozajc.akiwrapper.core.entities.impl.StatusImpl;

import static org.junit.jupiter.api.Assertions.*;

class StatusImplTest {

	@Test
	void testReason() {
		assertEquals(OK, StatusImpl.fromCompletion("OK").getReason());
		assertEquals(QUESTIONS_EXHAUSTED, StatusImpl.fromCompletion("WARN - NO QUESTION").getReason());
		assertEquals(SERVER_FAILURE, StatusImpl.fromCompletion("KO - TECHNICAL ERROR").getReason());
		assertEquals(LIBRARY_FAILURE, StatusImpl.fromCompletion("KO - UNAUTHORIZED").getReason());
		assertEquals(LIBRARY_FAILURE, StatusImpl.fromCompletion("KO - ELEM LIST IS EMPTY").getReason());
		assertEquals(LIBRARY_FAILURE, StatusImpl.fromCompletion("KO - MISSING KEY").getReason());
		assertEquals(LIBRARY_FAILURE, StatusImpl.fromCompletion("KO - MISSING PARAMETERS").getReason());
		assertEquals(UNKNOWN, StatusImpl.fromCompletion("OK - MESSAGE").getReason());
		assertEquals(UNKNOWN, StatusImpl.fromCompletion("WARN - MESSAGE").getReason());
		assertEquals(UNKNOWN, StatusImpl.fromCompletion("KO - MESSAGE").getReason());
	}

	@ParameterizedTest
	@EnumSource(value = Level.class, mode = EXCLUDE)
	void testStringConstructorNoMessage(@Nonnull Level level) {
		@SuppressWarnings("null")
		var status = StatusImpl.fromCompletion(level.toString());
		assertEquals(level, status.getLevel());
		assertNull(status.getMessage());
	}

	@ParameterizedTest
	@MethodSource("generateTestStringConstructorWithMessage")
	void testStringConstructorWithMessage(@Nonnull Level level, @Nonnull String message) {
		String completion = level.toString() + " - " + message;
		var status = StatusImpl.fromCompletion(completion);
		assertEquals(level, status.getLevel());
		assertEquals(message, status.getMessage());
	}

	private static Stream<Arguments> generateTestStringConstructorWithMessage() {
		String[] messages = { "", "message", "message with spaces", "UPPERCASE", "UPPERCASE WITH SPACES" };
		Arguments[] arguments = new Arguments[Level.values().length * messages.length];
		int i = 0;
		for (Level level : Level.values())
			for (String message : messages) {
				arguments[i] = Arguments.of(level, message);
				i++;
			}
		return Stream.of(arguments);
	}

}
