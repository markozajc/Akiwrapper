//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2025 Marko Zajc
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
package org.eu.zajc.akiwrapper.core.entities.impl;

import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static org.eu.zajc.akiwrapper.core.utils.route.Routes.NEW_SESSION;

import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.regex.Pattern;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.Akiwrapper;
import org.eu.zajc.akiwrapper.core.entities.Query;
import org.eu.zajc.akiwrapper.core.exceptions.MalformedResponseException;
import org.jsoup.nodes.Element;
import org.slf4j.*;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal
public class AkiwrapperImpl implements Akiwrapper {

	public static class Session {

		private static final Pattern IDENTIFIER_PATTERN =
			compile("localStorage\\.setItem\\('identifiant', '([^']*)'\\);");

		@Nonnull private final String session;
		@Nonnull private final String signature;
		@Nullable private final String identifier;

		private Session(@Nonnull String session, @Nonnull String signature, @Nullable String identifier) {
			this.session = session;
			this.signature = signature;
			this.identifier = identifier;
		}

		@Nonnull
		@SuppressWarnings("null")
		public static Session fromHtml(@Nonnull Element gameRoot) {
			return Optional.ofNullable(gameRoot.getElementById("askSoundlike")).map(o -> {
				var session = ofNullable(o.getElementById("session")).map(e -> e.attr("value")).orElse(null);
				var signature = ofNullable(o.getElementById("signature")).map(e -> e.attr("value")).orElse(null);
				if (session == null || signature == null)
					return null;

				String identifier = null;
				var identifierMatcher = IDENTIFIER_PATTERN.matcher(gameRoot.getElementsByTag("script").html());
				if (identifierMatcher.find())
					identifier = identifierMatcher.group(1);
				else
					LOG.trace("Couldn't find the session identifier");

				return new Session(session, signature, identifier);
			}).orElseThrow(MalformedResponseException::new);
		}

		public void apply(@Nonnull Map<String, Object> parameters) {
			parameters.put("session", this.session);
			parameters.put("signature", this.signature);
		}

		@Nullable
		public String getIdentifier() {
			return this.identifier;
		}

	}

	public static final Logger LOG = LoggerFactory.getLogger(AkiwrapperImpl.class);

	public static final int LAST_STEP = 80;

	@Nonnull private final HttpClient httpClient;
	@Nonnull private final Language language;
	@Nonnull private final Theme theme;
	private final boolean filterProfanity;

	private Session session;
	private Query currentQuery;
	private volatile int lastGuessStep;
	@Nonnull private Lock interactionLock = new ReentrantLock();

	public AkiwrapperImpl(@Nonnull HttpClient httpClient, @Nonnull Language language, @Nonnull Theme theme,
						  boolean filterProfanity) {
		this.httpClient = httpClient;
		this.language = language;
		this.theme = theme;
		this.filterProfanity = filterProfanity;
	}

	@SuppressWarnings("null")
	public void createSession() {
		var resp = NEW_SESSION.createRequest(this).retrieveDocument().getBody();
		this.session = Session.fromHtml(resp);
		this.currentQuery = QuestionImpl.fromHtml(this, resp);
	}

	@Override
	public Query getCurrentQuery() {
		return this.currentQuery;
	}

	@Override
	public Language getLanguage() {
		return this.language;
	}

	@Override
	public Theme getTheme() {
		return this.theme;
	}

	@Override
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	@Nonnull
	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	public Session getSession() {
		return this.session;
	}

	public void setCurrentResponse(@Nullable Query response) {
		this.currentQuery = response;
	}

	public int getLastGuessStep() {
		return this.lastGuessStep;
	}

	public void setLastGuessStep(int lastGuessStep) {
		this.lastGuessStep = lastGuessStep;
	}

	@Nonnull
	public Lock getInteractionLock() {
		return this.interactionLock;
	}
}
