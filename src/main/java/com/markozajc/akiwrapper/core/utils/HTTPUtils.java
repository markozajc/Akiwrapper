package com.markozajc.akiwrapper.core.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;

/**
 * A utility class for making HTTP requests.
 *
 * @author Marko Zajc
 */
public class HTTPUtils {

	private HTTPUtils() {}

	/**
	 * Reads {@link URLConnection} into a byte array.
	 *
	 * @param conn
	 *            connection to read from
	 *
	 * @return content as a byte array
	 * 
	 * @throws IOException
	 * 
	 * @see String#String(byte[], String)
	 */
	public static byte[] read(URLConnection conn) throws IOException {
		try (BufferedInputStream is = new BufferedInputStream(conn.getInputStream())) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			byte[] chunk = new byte[4096];
			int bytesRead;

			while ((bytesRead = is.read(chunk)) > -1)
				outputStream.write(chunk, 0, bytesRead);

			return outputStream.toByteArray();
		}
	}

}
