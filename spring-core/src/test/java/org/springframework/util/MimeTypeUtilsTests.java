/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.util;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MimeTypeUtils}.
 */
public class MimeTypeUtilsTests {

	/**
	 * Multipart boundaries must not be predictable, otherwise an attacker able to
	 * observe one boundary can forge subsequent ones and pollute multipart content.
	 */
	@Test
	public void generateMultipartBoundaryUsesSecureRandom() throws Exception {
		Field field = MimeTypeUtils.class.getDeclaredField("RND");
		field.setAccessible(true);
		Random rnd = (Random) field.get(null);
		assertTrue("Multipart boundaries must be generated with a cryptographically " +
				"strong random number generator, but was: " + rnd.getClass().getName(),
				rnd instanceof SecureRandom);
	}

	@Test
	public void generateMultipartBoundary() throws Exception {
		Set<String> boundaries = new HashSet<String>();
		for (int i = 0; i < 100; i++) {
			byte[] boundary = MimeTypeUtils.generateMultipartBoundary();
			assertTrue("Invalid boundary length: " + boundary.length,
					boundary.length >= 30 && boundary.length <= 40);
			assertTrue("Duplicate boundary generated", boundaries.add(new String(boundary, "US-ASCII")));
		}
	}

	@Test
	public void generateMultipartBoundaryString() {
		String boundary = MimeTypeUtils.generateMultipartBoundaryString();
		assertNotNull(boundary);
		assertTrue(boundary.length() >= 30);
		assertNotEquals(boundary, MimeTypeUtils.generateMultipartBoundaryString());
	}

}
