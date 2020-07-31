package com.markozajc.akiwrapper.example;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ApiKey;

@SuppressWarnings("javadoc")
public class AkinatorExample {

	public static final double PROBABILITY_THRESHOLD = 0.85;
	// This will be our probability threshold.

	private static boolean reviewGuess(@Nonnull Guess guess, @Nonnull Scanner sc) {
		System.out.println(guess.getName());
		System.out.println("\t" + (guess.getDescription() == null ? "(no description)" : guess.getDescription()));
		// Displays the guess.

		boolean answered = false;
		boolean isCharacter = false;
		while (!answered) {
			// Asks the user if that is his character.

			System.out.println("Is that your character? (y/n)");
			String line = sc.nextLine();
			switch (line) {
				case "y":
					// If the user has responded positively.
					answered = true;
					isCharacter = true;
					break;

				case "n":
					// If the user has responded negatively.
					answered = true;
					isCharacter = false;
					break;

				default:
					break;
			}
		}

		return isCharacter;
	}

	private static void finish(boolean win) {
		if (win) {
			// If Akinator has won.
			System.out.println("Great!");
			System.out.println("\tGuessed right one more time. I love playing with you!");
		} else {
			// If the user has won.
			System.out.println("Bravo!");
			System.out.println("\tYou have defeated me.");
		}
	}

	@SuppressWarnings("null")
	public static void main(String[] args) throws Exception {
		try (Scanner sc = new Scanner(System.in)) {
			Boolean filterProfanity = getProfanityFilter(sc);
			// Gets user's age. Like the Akinator's website, this will turn on the profanity
			// filter if the age entered is below 16.

			Language language = getLanguage(sc);
			// Gets user's language. Akinator will give the user localized questions and guesses
			// depending on user's language.

			GuessType guessType = getGuessType(sc);
			// Gets the guess type.

			Akiwrapper aw = new AkiwrapperBuilder().setFilterProfanity(filterProfanity)
			    .setLanguage(language)
			    .setGuessType(guessType)
			    .build();
			// Builds the Akiwrapper instance, this is what we'll be using to perform
			// operations such as answering questions, fetching guesses, etc.

			List<Long> declined = new ArrayList<>();
			// A list of rejected guesses, used to prevent them from repeating.

			while (aw.getCurrentQuestion() != null) {
				// Iterates while there are still questions left.

				Question question = aw.getCurrentQuestion();
				if (question == null)
					break;
				// Breaks the loop if question is null; /should/ not occur, but safety is still
				// first.

				System.out.println("Question #" + (question.getStep() + 1));
				System.out.println("\t" + question.getQuestion());
				// Displays the question.

				if (question.getStep() == 0)
					System.out.println(
					    "\nAnswer with Y (yes), N (no), DK (don't know), P (probably) or PN (probably not) or go back in time with B (back).");
				// Displays the tip (only for the first time).

				answerQuestion(sc, aw);

				reviewGuesses(sc, aw, declined);
				// Iterates over any available guesses.
			}

			for (Guess guess : aw.getGuesses()) {
				if (reviewGuess(guess, sc)) {
					// Reviews all final guesses.
					finish(true);
					System.exit(0);
				}
			}

			finish(false);
			// Loses if all guesses are rejected.
		}
	}

	private static void reviewGuesses(@Nonnull Scanner sc, @Nonnull Akiwrapper aw, @Nonnull List<Long> declined) {
		for (Guess guess : aw.getGuessesAboveProbability(PROBABILITY_THRESHOLD)) {
			if (guess.getProbability() > 0.85d && !declined.contains(Long.valueOf(guess.getIdLong()))) {
				// Checks if this guess complies with the conditions.

				if (reviewGuess(guess, sc)) {
					// If the user accepts this guess.
					finish(true);
					System.exit(0);
				}

				declined.add(Long.valueOf(guess.getIdLong()));
				// Registers this guess as rejected.
			}

		}
	}

	private static void answerQuestion(@Nonnull Scanner sc, @Nonnull Akiwrapper aw) {
		boolean answered = false;
		while (!answered) {
			// Iterates while the questions remains unanswered.

			String answer = sc.nextLine().toLowerCase();

			if (answer.equals("y")) {
				aw.answerCurrentQuestion(Answer.YES);

			} else if (answer.equals("n")) {
				aw.answerCurrentQuestion(Answer.NO);

			} else if (answer.equals("dk")) {
				aw.answerCurrentQuestion(Answer.DONT_KNOW);

			} else if (answer.equals("p")) {
				aw.answerCurrentQuestion(Answer.PROBABLY);

			} else if (answer.equals("pn")) {
				aw.answerCurrentQuestion(Answer.PROBABLY_NOT);

			} else if (answer.equals("b")) {
				aw.undoAnswer();

			} else if (answer.equals("resetkey")) {
				ApiKey.accquireApiKey();

			} else if (answer.equals("debug")) {
				System.out.println("Debug information:\n\tCurrent API server: "
				    + aw.getServer().getUrl()
				    + "\n\tCurrent guess count: "
				    + aw.getGuesses().size());
				continue;
				// Displays some debug information.

			} else {
				System.out.println(
				    "Please answer with either [Y]ES, [N]O, [D|ONT |K]NOW, [P]ROBABLY or [P|ROBABLY |N]OT or go back one step with [B]ACK.");
				continue;
			}

			answered = true;
			// Answers the question.
		}
	}

	private static boolean getProfanityFilter(@Nonnull Scanner sc) {
		Boolean result = null;
		System.out.println("What's your age? (18)");
		while (result == null) {
			String age = sc.nextLine();

			if (age.equals("")) {
				result = false;
				continue;
			}

			try {
				result = Integer.parseInt(age) < 16;
				// Tries to format the given number.

			} catch (NumberFormatException e) {
				System.out.println("That's not a real age!");
				// In case the given number is not formattable (too big or not a number).
			}
		}
		return result;
	}

	@Nonnull
	private static Language getLanguage(@Nonnull Scanner sc) {
		Language result = null;
		EnumSet<Language> languages = EnumSet.allOf(Language.class);
		// Fetches all available languages.

		String unsupportedLanguageMessage = "Sorry, that language isn't supported. Rather try with:"
		    + languages.stream().map(Enum::toString).collect(Collectors.joining("\n-", "\n-", ""));
		// Does some Java 8 magic to pre-prepare the error message.

		System.out.println("What's your language? (English)");
		while (result == null) {
			String selectedLanguage = sc.nextLine().toLowerCase().trim();

			if (selectedLanguage.equals("")) {
				result = Language.ENGLISH;
				continue;
			}

			Language matching = languages.stream()
			    .filter(l -> l.toString().toLowerCase().equals(selectedLanguage))
			    .findAny()
			    .orElse(null);

			if (matching == null) {
				System.out.println(unsupportedLanguageMessage);
				continue;
			}

			result = matching;
		}
		return result;
	}

	@Nonnull
	private static GuessType getGuessType(@Nonnull Scanner sc) {
		GuessType result = null;
		EnumSet<GuessType> guessTypes = EnumSet.allOf(GuessType.class);
		// Fetches all available guess types.

		String unsupportedGuessTypeMessage = "Sorry, that guess type isn't supported. Rather try with:"
		    + guessTypes.stream().map(Enum::toString).collect(Collectors.joining("\n-", "\n-", ""));
		// Does some Java 8 magic to pre-prepare the error message.

		System.out.println("What will you be guessing? (character)");
		while (result == null) {
			String selectedGuessType = sc.nextLine().toLowerCase().trim();

			if (selectedGuessType.equals("")) {
				result = GuessType.CHARACTER;
				continue;
			}

			GuessType matching = guessTypes.stream()
			    .filter(l -> l.toString().toLowerCase().equals(selectedGuessType))
			    .findAny()
			    .orElse(null);

			if (matching == null) {
				System.out.println(unsupportedGuessTypeMessage);
				continue;
			}

			result = matching;
		}
		return result;
	}

}
