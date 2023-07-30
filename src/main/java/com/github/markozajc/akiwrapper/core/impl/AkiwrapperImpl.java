package com.github.markozajc.akiwrapper.core.impl;

import static com.github.markozajc.akiwrapper.core.entities.Status.Reason.QUESTIONS_EXHAUSTED;
import static com.github.markozajc.akiwrapper.core.utils.route.Routes.*;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import javax.annotation.Nonnull;

import org.eclipse.collections.api.factory.primitive.LongSets;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.json.JSONObject;
import org.slf4j.*;

import com.github.markozajc.akiwrapper.Akiwrapper;
import com.github.markozajc.akiwrapper.core.entities.*;
import com.github.markozajc.akiwrapper.core.entities.impl.immutable.*;
import com.github.markozajc.akiwrapper.core.exceptions.*;
import com.github.markozajc.akiwrapper.core.utils.ApiKey;

import kong.unirest.UnirestInstance;

@SuppressWarnings("javadoc") // internal impl
public class AkiwrapperImpl implements Akiwrapper {

	public static class Session {

		private static final String FORMAT_QUERYSTRING = "session=%s&signature=%s";

		private final long signature;
		private final int session;

		private Session(long signature, int session) {
			this.signature = signature;
			this.session = session;
		}

		@Nonnull
		public static Session fromJson(@Nonnull JSONObject parameters) {
			var session = parameters.getJSONObject("identification");
			return new Session(parseLong(session.getString("signature")), parseInt(session.getString("session")));
		}

		public long getSignature() {
			return this.signature;
		}

		public int getSession() {
			return this.session;
		}

		public String asQuerystring() {
			return format(FORMAT_QUERYSTRING, this.getSession(), this.getSignature());
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(AkiwrapperImpl.class);

	private static final int LAST_STEP = 80;

	@Nonnull private final ServerImpl server;
	@Nonnull private final UnirestInstance unirest;
	private final boolean filterProfanity;

	private ApiKey apiKey;
	private Session session;
	private Question question;
	private int lastGuessStep;
	private MutableLongSet rejectedGuesses = LongSets.mutable.empty();

	public AkiwrapperImpl(@Nonnull UnirestInstance unirest, @Nonnull ServerImpl server, boolean filterProfanity) {
		this.filterProfanity = filterProfanity;
		this.server = server;
		this.unirest = unirest;
	}

	@SuppressWarnings("null")
	public void createSession() {
		this.apiKey = ApiKey.accquireApiKey(this.unirest);
		var sessionParameters = NEW_SESSION.createRequest(this).execute().getBody();
		this.session = Session.fromJson(sessionParameters);
		this.question = QuestionImpl.fromJson(sessionParameters.getJSONObject("step_information"));
	}

	@Override
	public Question answer(Answer answer) {
		if (isExhausted())
			throw new QuestionsExhaustedException();

		var response = ANSWER.createRequest(this)
			.parameter(PARAMETER_STEP, getStep())
			.parameter(PARAMETER_ANSWER, answer.getId())
			.execute();

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

		var response = CANCEL_ANSWER.createRequest(this).parameter(PARAMETER_STEP, getStep()).execute();
		return this.question = QuestionImpl.fromJson(response.getBody());
	}

	@Override
	@SuppressWarnings("null")
	public List<Guess> getGuesses(int count) {
		var request = LIST.createRequest(this).parameter(PARAMETER_STEP, getStep());
		if (count > 0)
			request.parameter(PARAMETER_SIZE, count);
		var response = request.execute();

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
			var response = EXCLUSION.createRequest(this).parameter(PARAMETER_STEP, getStep()).execute();
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
				.execute();
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
	public ServerImpl getServer() {
		return this.server;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	public Session getSession() {
		return this.session;
	}

	public ApiKey getApiKey() {
		return this.apiKey;
	}

	@Nonnull
	public UnirestInstance getUnirest() {
		return this.unirest;
	}

}
