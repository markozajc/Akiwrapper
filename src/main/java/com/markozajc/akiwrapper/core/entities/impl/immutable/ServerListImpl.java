package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.*;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.*;

public class ServerListImpl implements ServerList {

	@Nonnull
	private Server currentServer;
	@Nonnull
	private final Queue<Server> candidateServers;

	@SuppressWarnings("null")
	public ServerListImpl(@Nonnull Server first, @Nonnull Server... candidates) {
		this(first, Arrays.asList(candidates));
	}

	public ServerListImpl(@Nonnull Server first, @Nonnull Collection<Server> candidates) {
		this.candidateServers = unwrapServersIntoQueue(candidates);
		this.currentServer = first;
	}

	@SuppressWarnings("null")
	public ServerListImpl(@Nonnull Collection<Server> servers) {
		if (servers.isEmpty())
			throw new IllegalArgumentException("The collection of servers may not be empty");

		ConcurrentLinkedQueue<Server> queue = unwrapServersIntoQueue(servers);
		this.candidateServers = queue;
		this.currentServer = this.candidateServers.remove();
	}

	@SuppressWarnings("null")
	@Nonnull
	private static ConcurrentLinkedQueue<Server> unwrapServersIntoQueue(@Nonnull Collection<Server> servers) {
		return servers.stream().flatMap(s -> {
			if (s instanceof ServerList)
				return ((ServerList) s).getServers().stream();
			else
				return Stream.of(s);
		}).collect(Collectors.toCollection(ConcurrentLinkedQueue<Server>::new));
	}

	@Override
	public String getUrl() {
		return this.currentServer.getUrl();
	}

	@Override
	public Language getLanguage() {
		return this.currentServer.getLanguage();
	}

	@Override
	public GuessType getGuessType() {
		return this.currentServer.getGuessType();
	}

	@SuppressWarnings("null")
	@Override
	public boolean next() {
		if (!hasNext())
			return false;

		this.currentServer = this.candidateServers.remove();
		return true;
	}

	@Override
	public List<Server> getServers() {
		List<Server> result = new ArrayList<>(getRemainingSize() + 1);
		result.add(this.currentServer);
		result.addAll(this.candidateServers);
		return result;
	}

	@Override
	public int getRemainingSize() {
		return this.candidateServers.size();
	}

}
