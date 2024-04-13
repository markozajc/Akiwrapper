//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
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
package org.eu.zajc.akiwrapper.core.impl;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.StreamSupport.stream;
import static org.eu.zajc.akiwrapper.core.utils.route.Routes.*;
import static org.eu.zajc.akiwrapper.core.utils.route.Status.Reason.QUESTIONS_EXHAUSTED;

import java.util.*;

import javax.annotation.Nonnull;

import org.eclipse.collections.api.factory.primitive.LongSets;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eu.zajc.akiwrapper.Akiwrapper;
import org.eu.zajc.akiwrapper.core.entities.*;
import org.eu.zajc.akiwrapper.core.entities.impl.*;
import org.eu.zajc.akiwrapper.core.exceptions.*;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.slf4j.*;

import kong.unirest.UnirestInstance;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class AkiwrapperImpl implements Akiwrapper {

	public static class Session {

		private final String session;
		private final String signature;

		private Session(String signature, String session) {
			this.signature = signature;
			this.session = session;
		}

		@Nonnull
		@SuppressWarnings("null")
		public static Session fromHtml(@Nonnull Element gameRoot) {
			return Optional.ofNullable(gameRoot.getElementById("askSoundlike")).map(o -> {
				var session = ofNullable(o.getElementById("session")).map(e -> e.attr("value")).orElse(null);
				var signature = ofNullable(o.getElementById("signature")).map(e -> e.attr("value")).orElse(null);
				if (session == null || signature == null)
					return null;

				return new Session(session, signature);
			}).orElseThrow(MalformedResponseException::new);
		}

		public void apply(@Nonnull Map<String, Object> parameters) {
			parameters.put("session", this.session);
			parameters.put("signature", this.signature);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(AkiwrapperImpl.class);

	private static final int LAST_STEP = 80;

	@Nonnull private final UnirestInstance unirest;
	@Nonnull private final Language language;
	@Nonnull private final Theme theme;
	private final boolean filterProfanity;

	private Session session;
	private Response lastResponse;
	private int lastGuessStep;
	private MutableLongSet rejectedGuesses = LongSets.mutable.empty();

	public AkiwrapperImpl(@Nonnull UnirestInstance unirest, @Nonnull Language language, @Nonnull Theme theme,
						  boolean filterProfanity) {
		this.unirest = unirest;
		this.language = language;
		this.theme = theme;
		this.filterProfanity = filterProfanity;
	}

	@SuppressWarnings("null")
	public void createSession() {
		var resp = NEW_SESSION.createRequest(this).retrieveDocument().getBody();
		this.session = Session.fromHtml(resp);
		this.lastResponse = QuestionImpl.fromHtml(this, resp);
	}

	@Override
	public Question answer(Answer answer) {
		if (isExhausted())
			throw new QuestionsExhaustedException();

		var response = ANSWER.createRequest(this)
			.parameter(PARAMETER_STEP, getStep())
			.parameter(PARAMETER_ANSWER, answer.getId())
			.retrieveJson();

		if (response.getStatus().getReason() == QUESTIONS_EXHAUSTED) {
			return this.question = null;
		}

		return this.question = QuestionImpl.fromJson(response.getBody());
	}

	@Override
	@SuppressWarnings("null")
	public Question undoAnswer() {
		if (isExhausted())
			throw new QuestionsExhaustedException(); // the api won't let us

		if (getStep() == 0)
			throw new UndoOutOfBoundsException();

		var response = CANCEL_ANSWER.createRequest(this).parameter(PARAMETER_STEP, getStep()).retrieveJson();
		return this.question = QuestionImpl.fromJson(response.getBody());
	}

	@Override
	@SuppressWarnings("null")
	public List<Guess> getGuesses(int count) {
		var request = LIST.createRequest(this).parameter(PARAMETER_STEP, getStep());
		if (count > 0)
			request.parameter(PARAMETER_SIZE, count);
		var response = request.retrieveJson();

		return stream(response.getBody().getJSONArray("elements").spliterator(), false).map(JSONObject.class::cast)
			.map(j -> j.getJSONObject("element"))
			.map(GuessImpl::fromJson)
			.sorted()
			.collect(toUnmodifiableList());
	}

	@Override
	public Guess suggestGuess() {
		boolean shouldSuggest = isExhausted() || getStep() - this.lastGuessStep >= 5 // NOSONAR I'm just copying
			&& (this.question != null && this.question.getProgression() > 97 || getStep() - this.lastGuessStep == 25)
			&& getStep() != 75;
		// I have no clue what that last part is, but that's what akinator does

		if (!shouldSuggest)
			return null;

		for (var guess : getGuesses(2)) {
			if (!this.rejectedGuesses.contains(guess.getIdLong())) {
				this.rejectedGuesses.add(guess.getIdLong());
				this.lastGuessStep = getStep();
				return guess;
			}
		}
		return null;
	}

	@Override
	public Question rejectLastGuess() {
		try {
			var response = EXCLUSION.createRequest(this).parameter(PARAMETER_STEP, getStep()).retrieveJson();
			return this.question = QuestionImpl.fromJson(response.getBody());

		} catch (AkinatorException e) {
			if (isExhausted()) {
				// we don't care about out session anymore anyways, throwing would be silly
				LOG.warn("Caught an exception when rejecting a guess", e);
				return null;

			} else {
				throw e;
			}
		}

	}

	@Override
	public void confirmGuess(Guess guess) {
		try {
			CHOICE.createRequest(this)
				.parameter(PARAMETER_STEP, getStep())
				.parameter(PARAMETER_ELEMENT, guess.getId())
				.retrieveJson();
		} catch (AkinatorException e) {
			// we don't care about out session anymore anyways, throwing would be silly
			LOG.warn("Caught an exception when confirming a guess", e);
		}
	}

	@Override
	public Question getQuestion() {
		return this.question;
	}

	@Override
	public int getStep() {
		var question = this.getQuestion();
		return question == null ? LAST_STEP : question.getStep();
	}

	@Override
	public boolean isExhausted() {
		// question is only null after we've exhausted them (that is post step 80)
		return this.question == null;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	public Session getSession() {
		return this.session;
	}

	@Nonnull
	public UnirestInstance getUnirest() {
		return this.unirest;
	}

}
