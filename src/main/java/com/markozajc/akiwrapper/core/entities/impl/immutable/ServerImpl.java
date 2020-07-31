package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.jcabi.xml.XML;
import com.markozajc.akiwrapper.core.entities.Server;

/**
 * An implementation of {@link Server}.
 *
 * @author Marko Zajc
 */
public class ServerImpl implements Server {

	private static final String LANGUAGE_ID_XPATH = "//LANGUAGE/LANG_ID/text()";
	private static final String SUBJECT_ID_XPATH = "//SUBJECT/SUBJ_ID/text()";
	private static final String CANDIDATE_URLS_XPATH = "//CANDIDATS/*/text()"; // sic
	@Nonnull
	private final String host;
	@Nonnull
	private final Language localization;
	@Nonnull
	private final GuessType guessType;

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

	/**
	 * Creates a new instance of {@link ServerImpl}.
	 *
	 * @param host
	 *            server's host (for example {@code https://srv3.akinator.com:9331/ws}).
	 * @param localization
	 *            localization language of this server
	 * @param guessType
	 *            guess type of this server
	 */
	public ServerImpl(@Nonnull String host, @Nonnull Language localization, @Nonnull GuessType guessType) {
		this.host = host;
		this.localization = localization;
		this.guessType = guessType;
	}

	@Override
	public Language getLocalization() {
		return this.localization;
	}

	@Override
	public GuessType getSubject() {
		return this.guessType;
	}

	@Override
	public String getHost() {
		return this.host;
	}

}
