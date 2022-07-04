package com.markozajc.akiwrapper.core.entities.impl.immutable;

import static com.markozajc.akiwrapper.core.Route.BASE_AKINATOR_URL;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import kong.unirest.UnirestInstance;

public class ApiKey {

	private static final String EXCEPTION_NO_KEY = "Couldn't find the API key!" +
		"Please consider opening a new ticket at https://github.com/markozajc/Akiwrapper/issues." +
		"Base64 encoded page: %s";

	private static final Pattern API_KEY_PATTERN =
		compile("var uid_ext_session = '(.*)'\\;\\n.*var frontaddr = '(.*)'\\;");

	private static final String FORMAT = "frontaddr=%s&uid_ext_session=%s";

	@Nonnull
	private final String sessionUid;
	@Nonnull
	private final String frontAddress;

	ApiKey(@Nonnull String sessionUid, @Nonnull String frontAddress) {
		this.sessionUid = sessionUid;
		this.frontAddress = frontAddress;
	}

	@Nonnull
	@SuppressWarnings("null")
	public String querystring() {
		return format(FORMAT, URLEncoder.encode(this.frontAddress, UTF_8), this.sessionUid);
	}

	@SuppressWarnings("null")
	public static ApiKey accquireApiKey(UnirestInstance unirest) {
		var page = unirest.get(BASE_AKINATOR_URL + "/game").asString().getBody();
		var matcher = API_KEY_PATTERN.matcher(page);
		if (matcher.find()) {
			return new ApiKey(matcher.group(1), matcher.group(2));

		} else {
			throw new IllegalStateException(format(EXCEPTION_NO_KEY,
												   Base64.getEncoder().encodeToString(page.getBytes(UTF_8))));
		}
	}

}
