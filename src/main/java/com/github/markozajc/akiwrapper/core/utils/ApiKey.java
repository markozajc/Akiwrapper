package com.github.markozajc.akiwrapper.core.utils;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.github.markozajc.akiwrapper.core.utils.route.Route;

import kong.unirest.UnirestInstance;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 *
 * @author Marko Zajc
 */
@SuppressWarnings("javadoc") // internal impl
public class ApiKey {

	private static final String EXCEPTION_NO_KEY = "Couldn't find the API key!" +
		"Please consider opening a new ticket at https://github.com/markozajc/Akiwrapper/issues.";

	private static final Pattern API_KEY_PATTERN =
		compile("var uid_ext_session = '(.*)'\\;\\n.*var frontaddr = '(.*)'\\;");

	@Nonnull private final String uidExtSession;
	@Nonnull private final String frontaddr;

	ApiKey(@Nonnull String sessionUid, @Nonnull String frontAddress) {
		this.uidExtSession = sessionUid;
		this.frontaddr = frontAddress;
	}

	@Nonnull
	public String asQuerystringUidExtSession() {
		return "uid_ext_session=" + this.uidExtSession;
	}

	@Nonnull
	public String asQuerystringFrontaddr() {
		return "frontaddr=" + URLEncoder.encode(this.frontaddr, UTF_8);
	}

	@SuppressWarnings("null")
	public static ApiKey accquireApiKey(UnirestInstance unirest) {
		var page = unirest.get(Route.WEBSITE_URL + "/game").asString().getBody();
		var matcher = API_KEY_PATTERN.matcher(page);
		if (matcher.find()) {
			return new ApiKey(matcher.group(1), matcher.group(2));

		} else {
			throw new IllegalStateException(format(EXCEPTION_NO_KEY));
		}
	}

}
