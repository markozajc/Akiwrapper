/*
 * Read before using:
 * ==================================================================================
 * ==================================[ DISCLAIMER ]==================================
 * ==================================================================================
 *
 * This might (rarely) be considered a port scanning tool by some ISPs if incorrectly
 * configured or not (even though it only scans a small range of ports). Get familiar
 * with your ISP's policies before using it!
 *
 * I assume no liability whatsoever for any damage, direct or indirect, you getting
 * kicked offline by your ISP or any nuclear wars, apocalypse and so on caused by
 * this software.
 *
 * (also it might break your router so that you'll have to restart it if you set the
 * THREAD_POOL_SIZE value too high, but it's nothing severe)
 *
 * (also my sincerest apologies for every "/.../ a ENGLISH /.../" you see while using
 * this!)
 *
 * With that out of the way, you're free to tweak and use this piece of software to
 * any extent.
 */
package com.markozajc.akiwrapper.listbuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.Route.Request;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;
import com.markozajc.akiwrapper.core.utils.Servers;

/**
 * A class used to build lists of available {@link Server}s along with their
 * {@link Language}s. Currently <u>highly experimental</u>. <b>USE ONLY IF YOU HAVE
 * READ THE DISCLAIMER IN CLASS'S HEADER!!<b>
 *
 * @author Marko Zajc
 */
public class AkinatorServerScanner {

	// ===========================================
	// Configuration
	// ===========================================

	private static final long STATUS_INTERVAL = 3000;
	// Interval at which the status message should be sent (in milliseconds)

	private static final String DUMP_FILENAME_FORMAT = "apidumpd{ts}.json";
	// Format of the server dump file. "{ts}" is replaced with the current timestamp.

	private static final int FAILURE_COMBO_TOLERANCE = 5;
	// How many UnknownHostExceptions can be hit before the host scanner aborts

	private static final boolean DEBUG_OUTPUT = false;
	// Whether to output debug information

	private static final int CONNECTION_TIMEOUT = 150;
	// Connection timeout. Lower values mean faster but less accurate, higher mean the
	// opposite. Decreasing this below 70 might go godspeed, but rest assured that
	// results will be a lot less accurate that way.

	private static final int THREAD_POOL_SIZE = 1;
	// Thread poll size of the server scanner ExecutorService fixed thread pool.
	// Increasing this to something too high might crash your JVM, your router (no joke),
	// or get you kicked offline by your ISP (again, not kidding as it might get a bit
	// spammy and thus considered an abuse by your ISP). Increasing it above 50 might
	// have unwanted side effects.
	// Decrease this to maybe get more accurate results (slower, more accurate).
	// Increase this to allow for better multi-threading (faster, less accurate).

	private static final boolean IGNORE_PING_FAILS = false;
	// Whether to still scan the hosts that returned errors on a ping (EXPERIMENTAL,
	// turning it on will usually just prolong the scan time).

	// ===========================================
	// Messages format
	// ===========================================

	// You may modify these, but adding "%s" tokens might throw a
	// java.util.MissingFormatArgumentException at runtime!

	private static final String HOST_EXISTS_BUT_TIMEOUTS = "[WARN] Host %s exists, but appears to be unavailable. Excluding it from the API scan.\n";
	private static final String HOST_CANT_PING = "[ERORR] Couldn't ping host %s; %s.\n";
	private static final String HOST_LISTING = "[INFO] Listing API hosts.\n";
	private static final String HOST_LISTED = "[INFO] Listed %s API hosts. Took %s milliseconds.\n";
	private static final String HOST_VERIFIED = "[DEBUG] Host %s is most likely an API server.\n";
	private static final String HOST_CANT_CONNECT = "[DEBUG] Can't connect to host %s, most likely due to it blocking connections on port 80.\n";

	private static final String API_LISTING = "[INFO] Initializing the localized API services scan (roughly %s ports).\n";
	private static final String API_PORT_QUERYING = "[DEBUG] Submitting a search for a localized API service @ %s to the ExecutorService.\n";
	private static final String API_SCAN_BEGIN = "[INFO] Beginning the API service scan.\n";
	private static final String API_EXECUTION_EXCEPTION = "[ERROR] Failed to retrieve the API call response; %s.\n";
	private static final String API_INTERRUPTED = "[DEBUG] Got interrupted while fetching the API call response.\n";

	private static final String SERVER_HIT = "[INFO] HIT! %s seems to be a %s Akinator API server.\n";
	private static final String SERVER_TIMEOUT = "[DEBUG] %s timeouts.\n";
	private static final String SERVER_JSON_ERROR = "[ERROR] %s - server is reachable, but returned an invalid JSON response.\n";
	private static final String SERVER_INTERNAL_ERROR = "[ERROR] %s - server is reachable, but reports an internal error; %s.\n";
	private static final String SERVER_DOWN = "[ERROR] %s - server is reachable, its localization can't be tested due to it being down.\n";
	private static final String SERVER_NO_LANGUAGE = "[ERROR] %s - server is reachable, but returns an unknown localization (%s @ index %s).\n";
	private static final String SERVER_CANT_CONNECT = "[ERROR] Connection with %s can't be established; %s.\n";

	private static final String SERIALIZING = "[INFO] Serializing %s found API servers...\n";

	private static final String FILE_CANT_DUMP = "[ERROR] Couldn't dump into %s (%s). %s API servers have been dumped into stout (as JSON)!\n";
	private static final String FILE_COMPLETE = "=======================================================\n"
	    + "[INFO] Done! Dumped %s API servers into %s (as JSON)!\n"
	    + "==========================================================\n";

	private static final String STATUS = "[INFO] Scanning ports.. %s/%s (%s%%)\n";

	//@formatter:off/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////! DO NOT EDIT STUFF BEYOND THIS LINE (unless you know what you're doing) ! /////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////@formatter:on

	private static final String API_FORMAT = "%s:%s";
	// Don't change this, defined by TCP/IP

	private static final String HOSTNAME_FORMAT = "srv%s.akinator.com";
	private static final int API_PORT_MIN = 9000;
	private static final int API_PORT_MAX = 9300;
	// Usually no need to change these

	private static final int ANSWER_INDEX = 2;
	// Currently the index for the "Don't know" answer, DON'T CHANGE UNLESS YOU CHANGE
	// THE STATIC CONSTRUCTOR INITIALIZATION OF "ANSWER_MAPPINGS" AS WELL!!

	private static int totalPorts = 0;
	// Altered at runtime, changing it will have no effect

	private static final Random RANDOM = new Random();
	// Used only for probe name generation

	private static final Map<String, Language> ANSWER_MAPPINGS;
	// Set in the static constructor

	private static final String PROBE_NAME;
	// Set in the static constructor

	private static final AtomicInteger REMAINING_PORTS;
	// Set in the static constructor

	private static final ExecutorService SERVER_SCANNER_ES;
	// Set in the static constructor

	private static final Thread STATUS_THREAD;
	// Set in the static constructor

	private static final Route NEW_SESSION;
	// Set in the static constructor

	static {
		REMAINING_PORTS = new AtomicInteger();
		PROBE_NAME = "AkiwrapperProbe" + RANDOM.nextInt();

		Map<String, Language> answerMappings = new HashMap<>();

		answerMappings.put("\u0627\u0646\u0627 \u0644\u0627 \u0627\u0639\u0644\u0645", Language.ARABIC);
		answerMappings.put("\u4e0d\u77e5\u9053", Language.CHINESE);
		answerMappings.put("Weet ik niet", Language.DUTCH);
		answerMappings.put("Don't know", Language.ENGLISH);
		answerMappings.put("Ne sais pas", Language.FRENCH);
		answerMappings.put("Ich wei\u00df nicht", Language.GERMAN);
		answerMappings.put("\u05d0\u05e0\u05d9 \u05dc\u05d0 \u05d9\u05d5\u05d3\u05e2", Language.HEBREW);
		answerMappings.put("Non lo so", Language.ITALIAN);
		answerMappings.put("\u5206\u304b\u3089\u306a\u3044", Language.JAPANESE);
		answerMappings.put("\ubaa8\ub974\uaca0\uc2b5\ub2c8\ub2e4", Language.KOREAN);
		answerMappings.put("Nie wiem", Language.POLISH);
		answerMappings.put("N\u00e3o sei", Language.PORTUGUESE);
		answerMappings.put("\u042f \u043d\u0435 \u0437\u043d\u0430\u044e", Language.RUSSIAN);
		answerMappings.put("No lo s\u00e9", Language.SPANISH);
		answerMappings.put("Bilmiyorum", Language.TURKISH);
		answerMappings.put("Tidak tahu", Language.MALAY);
		// Escaped non-ASCII characters for better platform encoding independence

		Request.connectionTimeout = CONNECTION_TIMEOUT;
		NEW_SESSION = Route.NEW_SESSION.setUserAgent(PROBE_NAME);

		ANSWER_MAPPINGS = Collections.unmodifiableMap(answerMappings);
		SERVER_SCANNER_ES = Executors.newFixedThreadPool(THREAD_POOL_SIZE, r -> {

			Thread t = new Thread(r, "server-scanner");
			t.setDaemon(true);
			return t;

		});
		STATUS_THREAD = new Thread(() -> {

			while (true) {
				reportStatus(totalPorts - REMAINING_PORTS.get());

				try {
					Thread.sleep(STATUS_INTERVAL);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

		}, "status-thread");
		STATUS_THREAD.setDaemon(true);
	}

	/**
	 * Runs the {@link AkinatorServerScanner}.
	 *
	 * @param args
	 *            does nothing. The configuration parameters are the first few constants
	 *            (you'll know what they do if you'll read the comments below them)
	 */
	public static void main(String[] args) {

		System.out.printf(HOST_LISTING);
		long start = System.currentTimeMillis();
		List<String> hostnames = listHosts();

		System.out.printf(HOST_LISTED, hostnames.size(), System.currentTimeMillis() - start);
		int remaining = hostnames.size() * (API_PORT_MAX - API_PORT_MIN);

		System.out.printf(API_LISTING, remaining);
		totalPorts = remaining;
		REMAINING_PORTS.set(remaining);
		List<Server> servers = scanHosts(hostnames);

		System.out.printf(SERIALIZING, servers.size());
		String serialized = serializeServers(servers);

		String filename = DUMP_FILENAME_FORMAT.replace("{ts}", Long.toString(System.currentTimeMillis()));

		try (PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
			writer.print(serialized);
		} catch (FileNotFoundException e) {
			System.err.printf(FILE_CANT_DUMP, filename, e, servers.size());

		} catch (UnsupportedEncodingException e) {
			// Can't happen, JVM always supports UTF-8
		}

		System.out.printf(FILE_COMPLETE, servers.size(), filename);
	}

	private static void isAvailable(String host, int timeout) throws IOException {
		try (Socket socket = new Socket()) {
			try {
				socket.connect(new InetSocketAddress(host, 80 /* not all servers support HTTPS! */), timeout);
			} catch (ConnectException e) {
				if (DEBUG_OUTPUT) {
					System.out.printf(HOST_CANT_CONNECT, host);
				}
			}
		}
	}

	private static List<String> listHosts() {
		List<String> result = new ArrayList<>();
		int failCombo = 0;
		for (int i = 1; failCombo <= FAILURE_COMBO_TOLERANCE; i++) {
			String hostname = String.format(HOSTNAME_FORMAT, i);
			try {
				isAvailable(hostname, 3000);
				result.add(hostname);
				failCombo = 0;

				if (DEBUG_OUTPUT)
					System.out.printf(HOST_VERIFIED, hostname);

			} catch (SocketTimeoutException e) {
				System.err.printf(HOST_EXISTS_BUT_TIMEOUTS, hostname);

				if (IGNORE_PING_FAILS)
					result.add(hostname);

			} catch (UnknownHostException e) {
				failCombo++;

			} catch (IOException e) {
				failCombo++;

				System.err.printf(HOST_CANT_PING, hostname, e);
			}
		}

		return result;
	}

	private static List<Server> scanHosts(List<String> hostnames) {

		List<Future<Server>> serversCombined = new ArrayList<>();
		for (String hostname : hostnames)
			serversCombined.addAll(scanHost(hostname));

		System.out.printf(API_SCAN_BEGIN);
		STATUS_THREAD.start();

		return serversCombined.stream().map(t -> {
			try {
				return t.get();
			} catch (InterruptedException e) {
				if (DEBUG_OUTPUT)
					System.out.printf(API_INTERRUPTED);

				Thread.currentThread().interrupt();

			} catch (ExecutionException e) {
				System.err.printf(API_EXECUTION_EXCEPTION, e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private static List<Future<Server>> scanHost(String hostname) {
		List<Future<Server>> futures = new ArrayList<>();

		for (int i = 0; i < API_PORT_MAX - API_PORT_MIN; i++) {
			int port = i + API_PORT_MIN;
			if (DEBUG_OUTPUT)
				System.out.printf(API_PORT_QUERYING, port);

			futures.add(SERVER_SCANNER_ES.submit(() -> scanServer(hostname, port)));
		}

		return futures;
	}

	@SuppressWarnings("null")
	private static Server scanServer(String hostname, int port) {
		String base = String.format(API_FORMAT, hostname, port);

		try {
			String answer = NEW_SESSION.getRequest(String.format(Servers.BASE_URL_FORMAT, base), false, PROBE_NAME)
			    .getJSON()
			    .getJSONObject("parameters")
			    .getJSONObject("step_information")
			    .getJSONArray("answers")
			    .getJSONObject(ANSWER_INDEX)
			    .getString("answer");

			Language localization = ANSWER_MAPPINGS.get(answer);
			if (localization == null) {
				System.err.printf(SERVER_NO_LANGUAGE, base, answer, ANSWER_INDEX);

			} else {
				System.out.printf(SERVER_HIT, base, localization);
				return new ServerImpl(base, localization);
			}

		} catch (JSONException e) {
			System.err.printf(SERVER_JSON_ERROR, base);

		} catch (SocketTimeoutException e) {
			if (DEBUG_OUTPUT)
				System.out.printf(SERVER_TIMEOUT, base);

		} catch (ServerUnavailableException e) {
			System.err.printf(SERVER_DOWN, base);

		} catch (StatusException e) {
			System.err.printf(SERVER_INTERNAL_ERROR, base, e.getStatus());

		} catch (IOException e) {
			System.err.printf(SERVER_CANT_CONNECT, base, e);
		}

		REMAINING_PORTS.decrementAndGet();

		return null;
	}

	private static void reportStatus(int remaining) {

		System.out.printf(STATUS, remaining, totalPorts,
		    Long.toString(Math.round((double) remaining / (double) totalPorts * 100)).replace(".0", ""));
	}

	private static String serializeServers(List<Server> servers) {
		JSONObject baseJson = new JSONObject();
		JSONArray serversJson = new JSONArray();

		for (Server server : servers) {
			JSONObject serverJson = new JSONObject();
			serverJson.put("host", server.getHost());
			serverJson.put("localization", server.getLocalization());

			serversJson.put(serverJson);
		}

		baseJson.put("servers", serversJson);
		baseJson.put("created", System.currentTimeMillis());

		return baseJson.toString();
	}

}
