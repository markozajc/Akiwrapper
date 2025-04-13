//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2025 Marko Zajc
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.eu.zajc.akiwrapper.core.utils;

import static org.eu.zajc.akiwrapper.core.utils.HttpUtils.urlEncodeForm;

import java.net.http.HttpRequest.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Flow.Subscriber;

import javax.annotation.Nonnull;

public class FormBody implements BodyPublisher {

	private final BodyPublisher backend;

	public FormBody(@Nonnull Map<? extends Object, ? extends Object> parameters) {
		this.backend = BodyPublishers.ofString(urlEncodeForm(parameters));
	}

	@Override
	public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
		this.backend.subscribe(subscriber);
	}

	@Override
	public long contentLength() {
		return this.backend.contentLength();
	}

}
