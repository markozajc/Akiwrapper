package com.github.markozajc.akiwrapper.core.entities;

import java.net.URL;

import javax.annotation.*;

import com.github.markozajc.akiwrapper.AkiwrapperBuilder;
import com.github.markozajc.akiwrapper.core.entities.Server.GuessType;

/**
 * A representation of Akinator's guess. A guess may span different types of subject,
 * depending on what was set for the {@link GuessType} in the
 * {@link AkiwrapperBuilder} (default is {@link GuessType#CHARACTER}). A guess
 * consists of four parts - subject name, description (both localized), a URL to the
 * image of the subject, and the probability that the guess is correct. Note that
 * image URL and description are optional, and may be {@code null}. {@link Guess}es
 * implement {@link Comparable} and are by default sorted by probability - the lower
 * the index, the higher the probability.
 *
 * @author Marko Zajc
 */
public interface Guess extends Identifiable, Comparable<Guess> {

	/**
	 * Returns the name of the guessed subject. This is provided in the language that was
	 * specified using the {@link AkiwrapperBuilder}.
	 *
	 * @return guessed characer's name.
	 */
	@Nonnull
	String getName();

	/**
	 * Returns the approximate probability that the answer is the one user has in mind
	 * (as a double).
	 *
	 * @return probability that this is the right answer.
	 */
	double getProbability();

	/**
	 * Returns the description of this subject. As a description is optional and thus not
	 * always present, this may be {@code null}. It is provided in the language that was
	 * specified using the {@link AkiwrapperBuilder}.
	 *
	 * @return description of the guessed subject.
	 */
	@Nullable
	String getDescription();

	/**
	 * Returns the URL to an image of this subject. As an image of the subject is
	 * optional and thus not always present, this may be {@code null}.
	 *
	 * @return URL to picture or null if no picture is attached
	 */
	@Nullable
	URL getImage();

}
