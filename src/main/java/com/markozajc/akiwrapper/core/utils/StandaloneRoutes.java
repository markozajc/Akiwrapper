package com.markozajc.akiwrapper.core.utils;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.xml.XMLDocument;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerImpl;

import kong.unirest.Unirest;

public class StandaloneRoutes {

	private static final Logger LOG = LoggerFactory.getLogger(StandaloneRoutes.class);

	private static final String FOOTPRINT = "cd8e6509f3420878e18d75b9831b317f";
	private static final String LIST_URL = "https://global3.akinator.com/ws/instances_v2.php?media_id=14&footprint="
	    + FOOTPRINT
	    + "&mode=https";

	@SuppressWarnings("null")
	public static Stream<Server> getServers() {
		return new XMLDocument(fetchListXml()).nodes("//RESULT/PARAMETERS")
		    .stream()
		    .flatMap(xml -> ServerImpl.fromXml(xml).stream());
	}

	private static String fetchListXml() {
		return Unirest.get(LIST_URL).asString().getBody();
	}

}
