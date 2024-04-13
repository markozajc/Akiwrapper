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
import static org.eu.zajc.akiwrapper.core.utils.route.ApiRoutes.NEW_SESSION;

import java.util.*;

import javax.annotation.*;

import org.eclipse.collections.api.factory.primitive.LongSets;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eu.zajc.akiwrapper.Akiwrapper;
import org.eu.zajc.akiwrapper.core.entities.Response;
import org.eu.zajc.akiwrapper.core.entities.impl.QuestionImpl;
import org.eu.zajc.akiwrapper.core.exceptions.MalformedResponseException;
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

	public static final Logger LOG = LoggerFactory.getLogger(AkiwrapperImpl.class);

	public static final int LAST_STEP = 80;

	@Nonnull private final UnirestInstance unirest;
	@Nonnull private final Language language;
	@Nonnull private final Theme theme;
	private final boolean filterProfanity;

	private Session session;
	private Response currentResponse;
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
		this.currentResponse = QuestionImpl.fromHtml(this, resp);
	}

	@Override
	public Response getCurrentResponse() {
		return this.currentResponse;
	}

	public void setCurrentResponse(@Nullable Response response) {
		this.currentResponse = response;
	}

	public int getLastGuessStep() {
		return this.lastGuessStep;
	}

	public void setLastGuessStep(int lastGuessStep) {
		this.lastGuessStep = lastGuessStep;
	}

	@Override
	public boolean isExhausted() {
		// response is only null after we've exhausted them (that is post step 80)
		return this.currentResponse == null;
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
