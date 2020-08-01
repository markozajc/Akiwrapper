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

	private static final String LANGUAGE_ID_XPATH = "LANGUAGE/LANG_ID/text()"; // NOSONAR not a URL
	private static final String SUBJECT_ID_XPATH = "SUBJECT/SUBJ_ID/text()"; // NOSONAR not a URL
	private static final String CANDIDATE_URLS_XPATH = "CANDIDATS/*/text()"; // sic
	@Nonnull
	private final String url;
	@Nonnull
	private final Language localization;
	@Nonnull
	private final GuessType guessType;

	/**
	 * Constructs a new instance of {@link ServerImpl}.
	 *
	 * @param url
	 *            server's URL (for example {@code https://srv3.akinator.com:9331/ws}).
	 * @param localization
	 *            server's language.
	 * @param guessType
	 *            server's guess type.
	 */
	public ServerImpl(@Nonnull String url, @Nonnull Language localization, @Nonnull GuessType guessType) {
		this.url = url;
		this.localization = localization;
		this.guessType = guessType;
	}

	/**
	 * Constructs a {@link ServerImpl} from an {@code <INSTANCE>} XML node provided by
	 * the server-listing API endpoint.
	 *
	 * @param instance
	 *            XML node.
	 *
	 * @return a {@link ServerImpl}.
	 */
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

}
