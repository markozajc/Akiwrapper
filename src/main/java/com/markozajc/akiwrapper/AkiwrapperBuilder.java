package com.markozajc.akiwrapper;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.impl.mutable.MutableAkiwrapperMetadata;
import com.markozajc.akiwrapper.core.exceptions.ServerGroupUnavailableException;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl;

/**
 * A class used for building a new Akinator object.
 *
 * @author Marko Zajc
 */
public class AkiwrapperBuilder extends MutableAkiwrapperMetadata {

	/**
	 * Creates a new AkiwrapperBuilder object. The default server used is the first
	 * available server. If a value is not changed, a constant default from
	 * {@link AkiwrapperMetadata} is used.
	 */
	public AkiwrapperBuilder() {
		super(AkiwrapperMetadata.DEFAULT_NAME, AkiwrapperMetadata.DEFAULT_USER_AGENT, null,
		    AkiwrapperMetadata.DEFAULT_FILTER_PROFANITY, AkiwrapperMetadata.DEFAULT_LOCALIZATION);
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
	 *
	 * @throws ServerGroupUnavailableException
	 *             in case no servers of that language are available
	 */
	@Nonnull
	public Akiwrapper build() {
		return new AkiwrapperImpl(this);
	}

}
