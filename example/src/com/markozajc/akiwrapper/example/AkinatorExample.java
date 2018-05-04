package com.markozajc.akiwrapper.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.utils.Servers;

@SuppressWarnings("javadoc")
public class AkinatorExample {

	public static final double PROBABILITY_THRESHOLD = 0.85;
	// This will be our probability threshold.

	private static boolean reviewGuess(Guess guess, Scanner sc) {
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

	public static void main(String[] args) throws Exception {
		try (Scanner sc = new Scanner(System.in)) {

			System.out.println("What's your name? (desktopPlayer)");
			String name = sc.nextLine().trim();
			if (name.equals(""))
				name = "desktopPlayer";
			// In case the user has just pressed <ENTER>, we'll use the default setting.

			// Gets user's name (this won't be important in the future but is still done,
			// for some reason).

			Boolean filterProfanity = null;
			{
				System.out.println("What's your age? (18)");
				while (filterProfanity == null) {
					String age = sc.nextLine();

					if (age.equals("")) {
						filterProfanity = false;
						continue;
					}

					try {
						filterProfanity = Integer.parseInt(age) < 16;
						// Tries to format the given number.

					} catch (NumberFormatException e) {
						System.out.println("That's not a real age!");
						// In case the given number is not formattable (too big or not a number).
					}
				}
			}
			// Gets user's age. Like the Akinator's website, this will turn on the profanity
			// filter if the age entered is below 16.

			Language localization = null;
			{
				List<Language> languages = new ArrayList<>(Servers.SERVER_GROUPS.keySet());
				// Fetches all available languages.

				String unsupportedLanguageMessage = "Sorry, that language isn't supported. Rather try with:"
						+ languages.stream().map(Enum::toString).collect(Collectors.joining("\n-", "\n-", ""));
				// Does some Java 8 magic to pre-prepare an error message.

				System.out.println("What's your language? (English)");
				while (localization == null) {
					String language = sc.nextLine().toLowerCase().trim();

					if (language.equals("")) {
						localization = Language.ENGLISH;
						continue;
					}

					Language matching = languages.stream()
							.filter(l -> l.toString().toLowerCase().equals(language))
							.findAny()
							.orElse(null);

					if (matching == null) {
						System.out.println(unsupportedLanguageMessage);
						continue;
					}

					localization = matching;
				}
			}
			// Gets user's language. Akinator will give the user localized questions and guesses
			// depending on user's language.

			Akiwrapper aw = new AkiwrapperBuilder().setName(name)
					.setFilterProfanity(filterProfanity)
					.setLocalization(localization)
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

					} else if (answer.equals("debug")) {
						System.out.println("Debug information:\n\tCurrent API server: " + aw.getServer().getBaseUrl()
								+ "\n\tCurrent guess count: " + aw.getGuesses().size()
								+ "(freshly fetched)\n\tCurrent API server availability: "
								+ (aw.getServer().isUp() ? "ONLINE" : "OFFILNE"));
						continue;
						// Displays some debug information.

					} else {
						System.out.println(
								"Please answer with either YES, NO, DONT KNOW, PROBABLY or PROBABLY NOT or go back one step with BACK.");
						continue;
					}

					answered = true;
					// Answers the question.
				}

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

}