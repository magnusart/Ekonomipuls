/**
 * Copyright 2011 Magnus Andersson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.ekonomipuls.proxy.configuration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.*;

import com.google.inject.Singleton;

import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationFileType;

/**
 * @author Magnus Andersson
 * @since 8 jul 2011
 */
@Singleton
public class FileMockUtil {

	protected static final String ASSETS = "assets/";

	/**
	 * @return
	 * @return
	 */
	InputStream setupFilterRulesFileMock() throws Exception {
		final String filePath = ASSETS
				+ ConfigurationFileType.FILTER_RULES.getFileName();

		assertFileExists(filePath);

		return new BufferedInputStream(new FileInputStream(filePath));
	}

	/**
	 * @return
	 * @return
	 * 
	 */
	InputStream setupTagFileMock() throws Exception {

		final String filePath = ASSETS
				+ ConfigurationFileType.TAGS.getFileName();

		assertFileExists(filePath);

		return new BufferedInputStream(new FileInputStream(filePath));
	}

	/**
	 * @return
	 * @return
	 * @throws IOException
	 * 
	 */
	InputStream setupCategoryFileMock() throws Exception {
		final String filePath = ASSETS
				+ ConfigurationFileType.CATEGORIES.getFileName();

		assertFileExists(filePath);

		return new BufferedInputStream(new FileInputStream(filePath));

	}

	/**
	 * @param filePath
	 */
	private void assertFileExists(final String filePath) {
		final File f = new File(filePath);
		assertTrue(f.isFile());
	}

}
