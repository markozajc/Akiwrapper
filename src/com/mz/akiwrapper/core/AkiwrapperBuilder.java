package com.mz.akiwrapper.core;

import com.mz.akiwrapper.Akiwrapper;
import com.mz.akiwrapper.core.entities.AkiwrapperMetadata;
import com.mz.akiwrapper.core.entities.Server;
import com.mz.akiwrapper.core.entities.impl.mutable.MutableAkiwrapperMetadata;
import com.mz.akiwrapper.core.impl.AkiwrapperImpl;
import com.mz.akiwrapper.core.utils.Servers;

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
		super(null, null, null);
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
	 * @return the API server used for all requests. All API servers have equal data
	 *         and endpoints but some might be down so you should never hard-code
	 *         usage of a specific API server (default: return value from
	 *         {@link Servers#getFirstAvailableServer()}
	 */
	@Override
	public Server getServer() {
		return super.getServer();
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

	/**
	 * @return a new {@link Akiwrapper} instance that will use all set preferences
	 */
	public Akiwrapper build() {
		return new AkiwrapperImpl(this);
	}

}
