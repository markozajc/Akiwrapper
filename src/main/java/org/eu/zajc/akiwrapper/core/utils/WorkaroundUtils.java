//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
 *
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.eu.zajc.akiwrapper.core.utils;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.security.*;
import java.security.cert.*;

import javax.annotation.Nonnull;
import javax.net.ssl.*;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import kong.unirest.Config;

/**
 * <b>Note:</b> This is an internal class and its internals are subject to change
 * without prior deprecation. Use with caution.<br>
 * <br>
 * A utility class with workarounds for problems with Akinator's infrastructure.
 *
 * @author Marko Zajc
 */
public class WorkaroundUtils {

	/**
	 * <b>Note:</b> even though this method returns a {@link Config}, the instance you
	 * pass to it is itself mutated and returned. The return value is only there for ease
	 * of chaining.<br>
	 * <br>
	 * Applies a workaround for the {@code PKIX path building failed} exception to a
	 * Unirest {@link Config}.
	 *
	 * @param config
	 *            the {@link Config} to apply the workaround to
	 *
	 * @return the {@link Config} for chaining
	 *
	 * @implNote it seems that Akinator sysadmins have misconfigured the en.akinator.com
	 *           server such that it fails to send the intermediate SSL certificate.
	 *           Because we only trust the root certificate, the local certificate chain
	 *           resolution fails and an exception is thrown. This workaround manually
	 *           trusts the intermediate certificate such that the resolution succeeds
	 *           without having to completely disable certificate validation. You can get
	 *           the modified {@link SSLContext} to apply yourself with
	 *           {@link #getIncompleteChainWorkaroundSSLContext()} or if you're using
	 *           your own {@link SSLContext} and you want to add the
	 *           {@link TrustManager}, you can get it from
	 *           {@link #getIncompleteChainWorkaroundCustomTrustManager()}.
	 */
	@Nonnull
	@SuppressWarnings("null")
	public static Config workaroundIncompleteChain(@Nonnull Config config) {
		return config.sslContext(getIncompleteChainWorkaroundSSLContext());
	}

	/**
	 * @return the {@link SSLContext} used to workaround the
	 *         {@code PKIX path building failed} exception.
	 *
	 * @see #workaroundIncompleteChain(Config)
	 */
	@Nonnull
	public static SSLContext getIncompleteChainWorkaroundSSLContext() {
		try {
			var defaultTrust = getDefaultTrustManager();
			var customTrust = getIncompleteChainWorkaroundCustomTrustManager();
			var combinedTrust = getCombinedTrust(defaultTrust, customTrust);

			var sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { combinedTrust }, null);
			return sslContext;
		} catch (GeneralSecurityException | IOException e) {
			throw new RuntimeException("Could not create a workaround SSLContext", e);
		}
	}

	@Nonnull
	private static X509TrustManager getCombinedTrust(@Nonnull X509TrustManager defaultTrust,
													 @Nonnull X509TrustManager customTrust) {
		return new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// merging isn't necessary
				return defaultTrust.getAcceptedIssuers();
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				try {
					defaultTrust.checkServerTrusted(chain, authType);
				} catch (CertificateException e) {
					customTrust.checkServerTrusted(chain, authType);
				}
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// merging isn't necessary
				defaultTrust.checkClientTrusted(chain, authType);
			}
		};
	}

	/**
	 * @return the {@link X509TrustManager} used to workaround the
	 *         {@code PKIX path building failed} exception.
	 *
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 *
	 * @see #workaroundIncompleteChain(Config)
	 */
	@Nonnull
	@SuppressWarnings("null")
	@SuppressFBWarnings("HARD_CODE_PASSWORD")
	public static X509TrustManager getIncompleteChainWorkaroundCustomTrustManager() throws KeyStoreException,
																					IOException,
																					NoSuchAlgorithmException,
																					CertificateException {
		KeyStore store;
		try (var is = WorkaroundUtils.class.getResourceAsStream("/intermediate.jks")) {
			store = KeyStore.getInstance(KeyStore.getDefaultType());
			store.load(is, "thingamabob".toCharArray()); // NOSONAR not a secure credential
		}

		if (store.size() != 1)
			throw new IOException("The intermediate keystore does not contain exactly one entry (did loading fail?)");

		var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(store);

		return stream(tmf.getTrustManagers()).filter(X509TrustManager.class::isInstance)
			.map(X509TrustManager.class::cast)
			.findAny()
			.orElseThrow();
	}

	@Nonnull
	@SuppressWarnings("null")
	private static X509TrustManager getDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
		var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init((KeyStore) null);

		return stream(tmf.getTrustManagers()).filter(X509TrustManager.class::isInstance)
			.map(X509TrustManager.class::cast)
			.findAny()
			.orElseThrow();
	}

	private WorkaroundUtils() {}

}
