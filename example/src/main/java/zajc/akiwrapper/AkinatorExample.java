package zajc.akiwrapper;

import static com.github.markozajc.akiwrapper.Akiwrapper.Answer.*;
import static com.github.markozajc.akiwrapper.core.entities.Server.GuessType.CHARACTER;
import static com.github.markozajc.akiwrapper.core.entities.Server.Language.ENGLISH;
import static java.lang.Integer.parseInt;
import static java.lang.System.*;
import static java.util.stream.Collectors.joining;

import java.util.*;

import javax.annotation.Nonnull;

import com.github.markozajc.akiwrapper.*;
import com.github.markozajc.akiwrapper.core.entities.*;
import com.github.markozajc.akiwrapper.core.entities.Server.*;
import com.github.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;

@SuppressWarnings("javadoc")
public class AkinatorExample {

	public static final double PROBABILITY_THRESHOLD = 0.85;
	// This is used to determine which guesses are probable (probability is determined by
	// Akinator) enough to propose to the player.

	@SuppressWarnings("null")
	public static void main(String[] args) throws Exception {
		try (var in = new Scanner(System.in)) {
			boolean filterProfanity = getProfanityFilter(in);
			// Gets player's age. Like the Akinator's website, this will turn on the profanity
			// filter if the age entered is below 16.

			var language = getLanguage(in);
			// Gets player's language. Akinator will give the user localized questions and
			// guesses depending on user's language.

			var guessType = getGuessType(in);
			// Gets the guess type.

			Akiwrapper aw;
			try {
				aw = new AkiwrapperBuilder().setFilterProfanity(filterProfanity)
					.setLanguage(language)
					.setGuessType(guessType)
					.build();
			} catch (ServerNotFoundException e) {
				err.println("Unsupported combination of language and guess type");
				return;
			}
			// Builds the Akiwrapper instance, this is what we'll be using to perform
			// operations such as answering questions, fetching guesses, etc

			var rejected = new ArrayList<Long>();
			// A list of rejected guesses, used to prevent them from repeating

			while (aw.getQuestion() != null) {
				// Runs while there are still questions left

				Question question = aw.getQuestion();
				if (question == null)
					break;
				// Breaks the loop if question is null; /should/ not occur, but safety is still
				// first

				out.printf("Question #%d%n", question.getStep() + 1);
				out.printf("\t%s%n", question.getQuestion());
				// Displays the question.

				if (question.getStep() == 0)
					out.printf("%nAnswer with " +
						"Y (yes), N (no), DK (don't know), P (probably) or PN (probably not) " +
						"or go back in time with B (back).%n");
				// Displays the tip (only for the first time)

				answerQuestion(in, aw);

				reviewGuesses(in, aw, rejected);
				// Iterates over any available guesses.
			}

			for (Guess guess : aw.getGuesses()) {
				if (reviewGuess(guess, in)) {
					// Reviews all final guesses.
					finish(true);
					exit(0);
				}
			}

			finish(false);
			// Loses if all guesses are rejected.
		}
	}

	private static void answerQuestion(@Nonnull Scanner sc, @Nonnull Akiwrapper aw) {
		boolean answered = false;
		while (!answered) {
			// Iterates while the questions remains unanswered.

			var answer = sc.nextLine().toLowerCase();

			if (answer.equals("y")) {
				aw.answer(YES);

			} else if (answer.equals("n")) {
				aw.answer(NO);

			} else if (answer.equals("dk")) {
				aw.answer(DONT_KNOW);

			} else if (answer.equals("p")) {
				aw.answer(PROBABLY);

			} else if (answer.equals("pn")) {
				aw.answer(PROBABLY_NOT);

			} else if (answer.equals("b")) {
				aw.undoAnswer();

			} else if (answer.equals("debug")) {
				out.printf("Debug information:%n\tCurrent API server: %s%n\tCurrent guess count: %d%n",
						   aw.getServer().getUrl(), aw.getGuesses().size());
				continue;
				// Displays some debug information.

			} else {
				out.println("Please answer with either " +
					"[Y]ES, [N]O, [D|ONT |K]NOW, [P]ROBABLY or [P|ROBABLY |N]OT or go back one step with [B]ACK.");
				continue;
			}

			answered = true;
			// Answers the question.
		}
	}

	private static boolean reviewGuess(@Nonnull Guess guess, @Nonnull Scanner sc) {
		out.println(guess.getName());
		out.print("\t");
		if (guess.getDescription() == null)
			out.print("(no description)");
		else
			out.print(guess.getDescription());
		out.println();
		// Displays the guess' information

		boolean answered = false;
		boolean isCharacter = false;
		while (!answered) {
			// Asks the player if the guess is correct

			out.println("Is this your character? (y/n)");
			String line = sc.nextLine();
			switch (line) {
				case "y":
					// If the player has responded positively.
					answered = true;
					isCharacter = true;
					break;

				case "n":
					// If the player has responded negatively.
					answered = true;
					isCharacter = false;
					break;

				default:
					break;
			}
		}

		return isCharacter;
	}

	private static void reviewGuesses(@Nonnull Scanner sc, @Nonnull Akiwrapper aw, @Nonnull List<Long> declined) {
		for (var guess : aw.getGuessesAboveProbability(PROBABILITY_THRESHOLD)) {
			if (!declined.contains(Long.valueOf(guess.getIdLong()))) {
				// Checks if this guess complies with the conditions.

				if (reviewGuess(guess, sc)) {
					// If the player accepts this guess
					finish(true);
					exit(0);
				}

				declined.add(guess.getIdLong());
				// Registers this guess as rejected.
			}
		}
	}

	private static void finish(boolean win) {
		if (win) {
			// If Akinator has won.
			out.println("Great!");
			out.println("\tGuessed right one more time. I love playing with you!");
		} else {
			// If the player has won.
			out.println("Bravo!");
			out.println("\tYou have defeated me.");
		}
	}

	private static boolean getProfanityFilter(@Nonnull Scanner sc) {
		out.println("What's your age? (default: 18)");
		while (true) {
			var age = sc.nextLine();

			if (age.equals(""))
				return false;

			try {
				return parseInt(age) < 16;
			} catch (NumberFormatException e) {
				out.println("That's not a number");
			}
		}
	}

	@Nonnull
	private static Language getLanguage(@Nonnull Scanner sc) {
		var languages = EnumSet.allOf(Language.class);

		out.println("What's your language? (default: English)");
		while (true) {
			String selectedLanguage = sc.nextLine().toLowerCase().trim();

			if (selectedLanguage.equals(""))
				return ENGLISH;

			var language = languages.stream()
				.filter(l -> l.toString().toLowerCase().equals(selectedLanguage))
				.findAny()
				.orElse(null);

			if (language != null) {
				return language;

			} else {
				out.println(languages.stream()
					.map(Enum::toString)
					.collect(joining("\n-", "Sorry, that language isn't supported. Choose between\n-", "")));
			}
		}
	}

	@Nonnull
	private static GuessType getGuessType(@Nonnull Scanner sc) {
		var guessTypes = EnumSet.allOf(GuessType.class);

		out.println("What will you be guessing? (default: character)");
		while (true) {
			String selectedGuessType = sc.nextLine().toLowerCase().trim();

			if (selectedGuessType.equals(""))
				return CHARACTER;

			var guessType = guessTypes.stream()
				.filter(l -> l.toString().toLowerCase().equals(selectedGuessType))
				.findAny()
				.orElse(null);

			if (guessType != null) {
				return guessType;

			} else {
				out.println("" + guessTypes.stream()
					.map(Enum::toString)
					.collect(joining("\n-", "Sorry, that guess type isn't supported. Choose between\n-", "")));
			}
		}
	}

}
