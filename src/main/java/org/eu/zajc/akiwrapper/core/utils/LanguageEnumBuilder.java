package org.eu.zajc.akiwrapper.core.utils;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.*;

import kong.unirest.Unirest;

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
	@SuppressWarnings({ "resource", "null" })
	private static Stream<String> getGuessTypes(String language) {
		return concat(Stream.of("CHARACTER"),
					  new Scanner(Unirest.get(String.format("https://%s.akinator.com/theme_selection", language))
						  .asString()
						  .getBody()).findAll(THEME_REGEX)
							  .map(m -> m.group(1))
							  .mapToInt(Integer::parseInt)
							  .mapToObj(LanguageEnumBuilder::getGuessType)
							  .filter(Objects::nonNull));
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		out.println(new Scanner(Unirest.get("https://en.akinator.com/").asString().getBody()).findAll(LANGUAGE_REGEX)
			.map(m -> m.group(1))
			.map(l -> format("%s(\"%s\", %s)", getLanguageName(l).toUpperCase(), l,
							 getGuessTypes(l).map(s -> "Theme." + s).collect(joining(", "))))
			.collect(joining(",\n", "", ";")));
	}

	private LanguageEnumBuilder() {}

}
