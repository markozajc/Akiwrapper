package com.markozajc.akiwrapper;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;
import com.markozajc.akiwrapper.core.entities.impl.mutable.MutableAkiwrapperMetadata;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl;

/**
 * A class used for building a new Akinator object.
 * 
 * @author Marko Zajc
 */
public class AkiwrapperBuilder extends MutableAkiwrapperMetadata {

	/**
	 * Creates a new AkiwrapperBuilder object. The default server used is the first
	 * available server.
	 */
	public AkiwrapperBuilder() {
		super(AkiwrapperMetadata.DEFAULT_NAME, AkiwrapperMetadata.DEFAULT_USER_AGENT, null,
				AkiwrapperMetadata.DEFAULT_FILTER_PROFANITY, AkiwrapperMetadata.DEFAULT_LOCALIZATION);
	}

	/**
	 * @return user's name, should not have any bigger impact on gameplay (default:
	 *         {@link AkiwrapperMetadata#DEFAULT_NAME})
	 * @see #setName(String)
	 */
	@Override
	public String getName() {
		return super.getName();
	}

	/**
	 * @return user-agent used in HTTP requests (default:
	 *         {@link AkiwrapperMetadata#DEFAULT_USER_AGENT})
	 * @see #setUserAgent(String)
	 */
	@Override
	public String getUserAgent() {
		return super.getUserAgent();
	}

	/**
	 * @return the API server used for all requests. All API servers have equal data and
	 *         endpoints but some might be down so you should never hard-code usage of a
	 *         specific API server (default: {@code null} (if {@code null} is passed to
	 *         {@link AkiwrapperImpl#AkiwrapperImpl(AkiwrapperMetadata)},
	 *         {@link ServerGroup#getFirstAvailableServer()} will be used))
	 */
	@Override
	public Server getServer() {
		return super.getServer();
	}

	/**
	 * @return whether to tell Akinator's API to filter out NSFW information (default:
	 *         {@link AkiwrapperMetadata#DEFAULT_FILTER_PROFANITY})
	 */
	@Override
	public boolean doesFilterProfanity() {
		return super.doesFilterProfanity();
	}

	@Override
	public Language getLocalization() {
		return super.getLocalization();
	}

	@Override
	public AkiwrapperBuilder setName(String name) {
		super.setName(name);

		return this;
	}

	@Override
	public AkiwrapperBuilder setUserAgent(String userAgent) {
		super.setUserAgent(userAgent);

		return this;
	}

	@Override
	public AkiwrapperBuilder setServer(Server server) {
		super.setServer(server);

		return this;
	}

	@Override
	public AkiwrapperBuilder setFilterProfanity(boolean filterProfanity) {
		super.setFilterProfanity(filterProfanity);

		return this;
	}

	@Override
	public AkiwrapperBuilder setLocalization(Language localization) {
		super.setLocalization(localization);

		return this;
	}

	/**
	 * @return a new {@link Akiwrapper} instance that will use all set preferences
	 */
	public Akiwrapper build() {
		return new AkiwrapperImpl(this);
	}

}
