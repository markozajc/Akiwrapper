package com.markozajc.akiwrapper;

import javax.annotation.Nonnull;

import org.slf4j.*;

import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.Server.*;
import com.markozajc.akiwrapper.core.entities.impl.mutable.MutableAkiwrapperMetadata;
import com.markozajc.akiwrapper.core.exceptions.*;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * A class used to build an {@link Akiwrapper} object. It allows you to set various
 * values before building it in a method chaining fashion.
 *
 * @author Marko Zajc
 */
public class AkiwrapperBuilder extends MutableAkiwrapperMetadata {

	private static final Logger LOG = LoggerFactory.getLogger(AkiwrapperBuilder.class);

	/**
	 * Creates a new AkiwrapperBuilder object. The default server used is the first
	 * available server. If a value is not changed, a constant default from
	 * {@link AkiwrapperMetadata} is used.
	 */
	public AkiwrapperBuilder() {
		super(null, AkiwrapperMetadata.DEFAULT_FILTER_PROFANITY, AkiwrapperMetadata.DEFAULT_LOCALIZATION,
			  AkiwrapperMetadata.DEFAULT_GUESS_TYPE);
	}

	/**
	 * Sets the {@link Server} or (recommended) a {@link ServerList}. It is not
	 * recommended to set the {@link Server} manually (unless for debugging purposes or
	 * as some kind of workaround where Akiwrapper's server finder fails) as Akiwrapper
	 * already does its best to find the most suitable one. <br>
	 * <b>Caution!</b> Setting the server to a non-null value overwrites the
	 * {@link Language} and the {@link GuessType} with the given {@link Server}'s values.
	 *
	 * @param server
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getServer()
	 * @see Servers#findServers(Language, GuessType)
	 */
	@Override
	public AkiwrapperBuilder setServer(Server server) {
		super.setServer(server);

		return this;
	}

	/**
	 * Sets the "filter profanity" mode.
	 *
	 * @param filterProfanity
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #doesFilterProfanity()
	 */
	@Override
	public AkiwrapperBuilder setFilterProfanity(boolean filterProfanity) {
		super.setFilterProfanity(filterProfanity);

		return this;
	}

	/**
	 * Sets the {@link Language}.<br>
	 * <b>Caution!</b> Setting the {@link Language} will set the {@link Server} to
	 * {@code null} (meaning it will be automatically selected).
	 *
	 * @param language
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getLanguage()
	 */
	@Override
	public AkiwrapperBuilder setLanguage(Language language) {
		super.setLanguage(language);

		return this;
	}

	/**
	 * Sets the {@link GuessType}.<br>
	 * <b>Caution!</b> Setting the {@link Language} will set the {@link Server} to
	 * {@code null} (meaning it will be automatically selected).
	 *
	 * @param guessType
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getLanguage()
	 */
	@Override
	public AkiwrapperBuilder setGuessType(GuessType guessType) {
		super.setGuessType(guessType);

		return this;
	}

	/**
	 * @return a new {@link Akiwrapper} instance that will use all set preferences
	 *
	 * @throws ServerNotFoundException
	 *             if no server with that {@link Language} and {@link GuessType} is
	 *             available.
	 */
	@Nonnull
	public Akiwrapper build() throws ServerNotFoundException {
		Server server = findServer();
		if (server instanceof ServerList) {
			ServerList serverList = (ServerList) server;
			int count = serverList.getRemainingSize() + 1;
			do {
				LOG.debug("Using server {} out of {} from the list.", count - serverList.getRemainingSize(), count);
				try {
					return new AkiwrapperImpl(server, this.filterProfanity);
				} catch (ServerUnavailableException e) { // NOSONAR v
					LOG.debug("Server seems to be down.");
					// We can safely ignore this, let's just iterate to the next instance.
				} catch (RuntimeException e) {
					LOG.warn("Failed to construct an instance, trying the next available server", e);
				}
			} while (serverList.next());
			throw new ServerUnavailableException("KO - NO SERVER AVAILABLE");
		} else {
			LOG.debug("Given Server is not a ServerList, only attempting to build once.");
			return new AkiwrapperImpl(server, this.filterProfanity);
		}
	}

	@Nonnull
	private Server findServer() throws ServerNotFoundException {
		Server server = this.getServer();
		if (server == null)
			server = Servers.findServers(this.getLanguage(), this.getGuessType());
		return server;
	}

}
