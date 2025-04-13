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
package org.eu.zajc.akiwrapper.core.utils;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.*;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal
public class LanguageEnumBuilder {

	private static final Pattern LANGUAGE_REGEX = compile(".*changeLocale\\(\\&\\#39\\;([^\\)]+)\\&\\#39\\;\\)");
	private static final Pattern THEME_REGEX = compile(".*chooseTheme\\(\\&\\#39\\;(\\d+)\\&\\#39\\;\\)");

	@Nullable
	private static String getGuessType(int code) {
		switch (code) {
			case 1:
				return null; // CHARACTER, which is always added
			case 2:
				return "OBJECT";
			case 7:
				return "PLACE";
			case 13:
				return "MOVIE_TV_SHOW";
			case 14:
				return "ANIMAL";
			default:
				return "UNKNOWN";
		}
	}

	@Nonnull
	private static String getLanguageName(String code) {
		switch (code) {
			case "en":
				return "English";
			case "ar":
				return "Arabic";
			case "cn":
				return "Chinese";
			case "de":
				return "German";
			case "es":
				return "Spanish";
			case "fr":
				return "French";
			case "il":
				return "Hebrew";
			case "it":
				return "Italian";
			case "jp":
				return "Japanese";
			case "kr":
				return "Korean";
			case "nl":
				return "Dutch";
			case "pl":
				return "Polish";
			case "pt":
				return "Portugese";
			case "ru":
				return "Russian";
			case "tr":
				return "Turkish";
			case "id":
				return "Indonesian";
			default:
				return "Unknown";
		}
	}

	@Nonnull
	@SuppressWarnings("null")
	private static String get(@Nonnull String format, @Nonnull Object... args) {
		try {
			return HttpClient.newHttpClient()
				.send(HttpRequest.newBuilder(new URI(format(format, args))).build(), BodyHandlers.ofString())
				.body();
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}

	@Nonnull
	@SuppressWarnings({ "resource", "null" })
	private static Stream<String> getGuessTypes(String language) {
		return concat(Stream.of("CHARACTER"),
					  new Scanner(get("https://%s.akinator.com/theme_selection", language)).findAll(THEME_REGEX)
						  .map(m -> m.group(1))
						  .mapToInt(Integer::parseInt)
						  .mapToObj(LanguageEnumBuilder::getGuessType)
						  .filter(Objects::nonNull));
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		out.println(new Scanner(get("https://en.akinator.com/")).findAll(LANGUAGE_REGEX)
			.map(m -> m.group(1))
			.map(l -> format("%s(\"%s\", %s)", getLanguageName(l).toUpperCase(), l,
							 getGuessTypes(l).map(s -> "Theme." + s).collect(joining(", "))))
			.collect(joining(",\n", "", ";")));
	}

	private LanguageEnumBuilder() {}

}
