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
