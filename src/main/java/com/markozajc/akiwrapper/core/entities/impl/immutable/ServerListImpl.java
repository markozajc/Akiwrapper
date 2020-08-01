package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.ServerList;

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

	@Override
	public boolean next() {
		Server server = this.candidateServers.poll();
		if (server == null)
			return false;

		this.currentServer = server;
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
