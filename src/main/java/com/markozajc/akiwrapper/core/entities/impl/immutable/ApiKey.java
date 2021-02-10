package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.*;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.exceptions.StatusException;

/**
 * A class defining the session key that has to be passed to the
 * {@link Route#NEW_SESSION} endpoint. It is scraped from the website as it is
 * single-use and triggers a KO if reused.
 *
 * @author Marko Zajc
 *
 */
public class ApiKey {

	private static final Pattern API_KEY_PATTERN =
		Pattern.compile("var uid_ext_session = '(.*)'\\;\\n.*var frontaddr = '(.*)'\\;");

	private static final String FORMAT = "frontaddr=%s&uid_ext_session=%s";

	@Nonnull
	private final String sessionUid;
	@Nonnull
	private final String frontAddress;

	ApiKey(@Nonnull String sessionUid, @Nonnull String frontAddress) {
		this.sessionUid = sessionUid;
		this.frontAddress = frontAddress;
	}

	/**
	 * Compiles this {@link ApiKey} into querystring that can be appended to an endpoint.
	 *
	 * @return compiled querystring.
	 */
	@SuppressWarnings("null")
	@Nonnull
	public String compile() {
		try {
			return String.format(FORMAT, URLEncoder.encode(this.frontAddress, "UTF-8"), this.sessionUid);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e); // never throws, UTF-8 is always supported
		}
	}

	/**
	 * Finds and scrapes the API key from Akinator's website and constructs an
	 * {@link ApiKey} instance from it. <br>
	 * <b>Caution!</b>Each {@link ApiKey} is single-use! Reusing it will trigger a
	 * {@link StatusException} from the API server.
	 *
	 * @return an {@link ApiKey}.
	 *
	 * @throws IllegalStateException
	 *             if the API key can't be scraped
	 */
	@SuppressWarnings("null")
	public static ApiKey accquireApiKey() {
		Matcher matcher =
			API_KEY_PATTERN.matcher(Route.UNIREST.get(Route.BASE_AKINATOR_URL + "/game").asString().getBody());
		if (!matcher.find())
			throw new IllegalStateException("Couldn't find the API key! Please consider opening" +
				"a new ticket at https://github.com/markozajc/Akiwrapper/issues.");

		return new ApiKey(matcher.group(1), matcher.group(2));
	}

}
