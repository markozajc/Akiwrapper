package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.*;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.Route;

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

	@SuppressWarnings("null")
	@Nonnull
	public String compile() {
		try {
			return String.format(FORMAT, URLEncoder.encode(this.frontAddress, "UTF-8"), this.sessionUid);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e); // never throws, UTF-8 is always supported
		}
	}

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
