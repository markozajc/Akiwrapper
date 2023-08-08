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
package org.eu.zajc.akiwrapper.core.entities.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.entities.Server;

import com.jcabi.xml.XML;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class ServerImpl implements Server {

	private static final String LANGUAGE_ID_XPATH = "LANGUAGE/LANG_ID/text()"; // NOSONAR not a URL
	private static final String SUBJECT_ID_XPATH = "SUBJECT/SUBJ_ID/text()"; // NOSONAR not a URL
	private static final String CANDIDATE_URLS_XPATH = "CANDIDATS/*/text()"; // sic
	@Nonnull private final String url;
	@Nonnull private final Language localization;
	@Nonnull private final GuessType guessType;

	public ServerImpl(@Nonnull String url, @Nonnull Language localization, @Nonnull GuessType guessType) {
		this.url = url;
		this.localization = localization;
		this.guessType = guessType;
	}

	@SuppressWarnings("null")
	@Nonnull
	public static List<ServerImpl> fromXml(@Nonnull XML instance) {
		String languageId = instance.xpath(LANGUAGE_ID_XPATH).get(0);
		Language language = Language.getById(languageId);
		if (language == null)
			throw new IllegalStateException("'" + languageId + "' is not a recognized language.");

		int guessTypeId = Integer.parseInt(instance.xpath(SUBJECT_ID_XPATH).get(0));
		GuessType guessType = GuessType.getById(guessTypeId);
		if (guessType == null)
			throw new IllegalStateException("'" + guessTypeId + "' is not a recognized guess type ID.");

		return instance.xpath(CANDIDATE_URLS_XPATH)
			.stream()
			.map(host -> new ServerImpl(host, language, guessType))
			.collect(Collectors.toList());
	}

	@Override
	public Language getLanguage() {
		return this.localization;
	}

	@Override
	public GuessType getGuessType() {
		return this.guessType;
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	@Nonnull
	public String asUrlApiWs() {
		return "urlApiWs=" + URLEncoder.encode(this.url, UTF_8);
	}

}
