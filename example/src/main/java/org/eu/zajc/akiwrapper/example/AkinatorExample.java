package org.eu.zajc.akiwrapper.example;

import static java.lang.Character.toLowerCase;
import static java.lang.System.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.eu.zajc.akiwrapper.Akiwrapper.Answer.*;
import static org.eu.zajc.akiwrapper.Akiwrapper.Language.ENGLISH;
import static org.eu.zajc.akiwrapper.Akiwrapper.Theme.CHARACTER;

import java.util.*;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.*;
import org.eu.zajc.akiwrapper.Akiwrapper.*;
import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.entities.impl.AkiwrapperImpl;
import org.eu.zajc.akiwrapper.core.exceptions.LanguageThemeCombinationException;

public class AkinatorExample {

	private static final String ANSWER_TIP =
		"Y or yes, N or no, DK or don't know, P or probably, PN or probably not, or go back one step with B or back.";
	private static final Scanner IN = new Scanner(System.in, UTF_8).useDelimiter("\n");

	public static void main(String[] args) {
		boolean filterProfanity = getProfanityFilter();
		// Gets player's age. Like the Akinator's website, this will turn on the profanity
		// filter if the age entered is below 16.

		var language = getLanguage();
		// Gets player's language. Akinator will give the user localized questions and
		// guesses depending on user's language.

		var guessType = getGuessType();
		// Gets the guess type.

		Akiwrapper aw;
		try {
			aw = new AkiwrapperBuilder().setFilterProfanity(filterProfanity)
				.setLanguage(language)
				.setTheme(guessType)
				.build();
		} catch (LanguageThemeCombinationException e) {
			err.println("Unsupported combination of language and guess type");
			return;
		}
		// Builds the Akiwrapper instance, this is what we'll be using to perform
		// operations such as answering questions, fetching guesses, etc

		var query = aw.getCurrentQuery(); // Get the initial query

		while (query != null) {
			// Akinator responds with either a question or a guess after each interaction. We
			// first determine the query type

			if (query instanceof Question q) {
				// The query is a question, display it
				out.printf("Question #%d%n", q.getStep() + 1);
				out.printf("\t%s%n", q.getText());

				// Also display the question tip (on the first question)
				if (q.getStep() == 0)
					out.printf("%nAnswer with %s%n", ANSWER_TIP);

				// .. and then answer it
				query = answer(q);

			} else if (query instanceof Guess g) {
				// The query is a guess, display it
				out.println(g.getName());
				out.printf("\t%s%n%n", g.getDescription());
				// Displays the guess' information

				out.printf("Is this %s you're thinking of? [Y/n] ", aw.getTheme() == CHARACTER ? "who" : "what");
				var input = IN.next().trim();
				// Asks the player if the guess is correct

				if (input.isEmpty() || toLowerCase(input.charAt(0)) == 'y') {
					// Akinator wins if the guess was correct
					out.println("Great!");
					out.println("\tGuessed right one more time. I love playing with you!");
					g.confirm();

				} else {
					// .. otherwise the game continues
					query = g.reject();
				}
			}
		}

		// If Akinator runs out of questions and guesses (when query is null), the player
		// wins
		out.println("Bravo!");
		out.println("\tYou have defeated me.");
	}

	@Nullable
	private static Query answer(Question q) {
		while (true) {
			out.print("> ");
			// Prompts the player for an answer

			var input = IN.next().toLowerCase();

			switch (input) {
				case "y", "yes":
					return q.answer(YES);

				case "n", "no":
					return q.answer(NO);

				case "dk", "don'tknow", "dontknow", "dont know", "don't know":
					return q.answer(DONT_KNOW);

				case "p", "probably":
					return q.answer(PROBABLY);

				case "pn", "probablynot", "probably not":
					return q.answer(PROBABLY_NOT);

				case "b", "back":
					if (q.getStep() == 0)
						out.println("Can't undo on the first question.");
					else
						return q.undoAnswer();
					break;

				case "debug":
					out.println("Debug information:");
					out.printf("\tlastGuessStep: %d%n", ((AkiwrapperImpl) q.getAkiwrapper()).getLastGuessStep());
					out.printf("\tisExhausted: %b%n", ((AkiwrapperImpl) q.getAkiwrapper()).isExhausted());
					break;

				default:
					out.println("Please answer with either " + ANSWER_TIP);
					break;
			}
		}
	}

	private static boolean getProfanityFilter() {
		out.print("Enable profanity filtering? [y/N] ");
		var input = IN.next().trim();
		return !input.isEmpty() && toLowerCase(input.charAt(0)) == 'y';
	}

	@Nonnull
	@SuppressWarnings("null")
	private static Language getLanguage() {
		var languages = EnumSet.allOf(Language.class);

		out.print("What's your language? [English] ");
		while (true) {
			var input = IN.next().trim().toUpperCase();

			if (input.isEmpty())
				return ENGLISH;

			var language = languages.stream().filter(l -> l.toString().equals(input)).findAny();

			if (language.isPresent()) {
				return language.orElseThrow();

			} else {
				out.println(languages.stream()
					.map(Enum::toString)
					.collect(joining("\n-", "Sorry, that language isn't supported. Available options:\n-", "")));
			}
		}
	}

	@Nonnull
	@SuppressWarnings("null")
	private static Theme getGuessType() {
		var guessTypes = EnumSet.allOf(Theme.class);

		out.print("What will you be guessing? [character] ");
		while (true) {
			var input = IN.next().trim().toUpperCase();

			if (input.isEmpty())
				return CHARACTER;

			var guessType = guessTypes.stream().filter(l -> l.toString().equals(input)).findAny();

			if (guessType.isPresent()) {
				return guessType.orElseThrow();

			} else {
				out.println("" + guessTypes.stream()
					.map(Enum::toString)
					.collect(joining("\n-", "Sorry, that guess type isn't supported. Choose between\n-", "")));
			}
		}
	}

}
