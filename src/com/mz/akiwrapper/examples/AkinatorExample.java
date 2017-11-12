package com.mz.akiwrapper.examples;

import java.util.Scanner;

import com.mz.akiwrapper.Akiwrapper;
import com.mz.akiwrapper.Akiwrapper.Answer;
import com.mz.akiwrapper.Guess;

public class AkinatorExample {

	public static void main(String[] args) throws Exception {
		Akiwrapper aw = new Akiwrapper();
		// Creates new Akiwrapper object. This will automatically create a new Akinator
		// API session.

		Scanner sc = new Scanner(System.in);

		while (!aw.getCurrentQuestion().isEmpty()) {
			// The "!aw.getCurrentQuestion().isEmpty()" checks if a question is empty. When
			// a question is empty, it means that probability has reached 100% and we can
			// safely retrieve the guesses

			System.out.println(
					"#" + (aw.getCurrentQuestion().getStep() + 1) + ": " + aw.getCurrentQuestion().getQuestion());
			String answer = sc.nextLine();

			if (answer.equals("yes")) {
				aw.answerCurrentQuestion(Answer.YES);

			} else if (answer.equals("no")) {
				aw.answerCurrentQuestion(Answer.NO);

			} else if (answer.equals("dont know")) {
				aw.answerCurrentQuestion(Answer.DONT_KNOW);

			} else if (answer.equals("probably")) {
				aw.answerCurrentQuestion(Answer.PROBABLY);

			} else if (answer.equals("probably not")) {
				aw.answerCurrentQuestion(Answer.PROBABLY_NOT);

			} else {
				System.out.println("Please answer with 'yes', 'no', 'dont know', 'probably' or 'probably not'!");
			}
			// Answers the question
		}

		sc.close();

		System.out.println("Guesses:");
		for (Guess guess : aw.getGuesses()) {
			// Retrieves the guesses. Note that Akiwrapper#getGuesses() will probably return
			// an empty array if Akiwrapper#getCurrentQuestion().isEmpty() returns false

			System.out.println(guess.getName() + " - " + guess.getDescription());
		}
	}

}