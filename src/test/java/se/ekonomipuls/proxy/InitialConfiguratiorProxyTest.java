/**
 * Copyright 2011 Magnus Andersson, Michael Svensson
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
package se.ekonomipuls.proxy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationFileType;
import se.ekonomipuls.model.EntityType;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
@RunWith(InjectedTestRunner.class)
public class InitialConfiguratiorProxyTest {

	private static final String TEST_CATEGORY_CONFIG_FILE = "assets/categories.json";

	private static final String TEST_TAGS_CONFIG_FILE = "assets/tags.json";

	@Inject
	@InjectMocks
	private InitialConfiguratorProxy config;

	@Mock
	private EkonomipulsUtil util;

	private InputStream is;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadCategoriesFromFile() throws Exception {

		final List<AddCategoryAction> categories = setupCategoryFileMock();

		assertNotNull("Category list should not be null", categories);
		assertTrue("Category list should be exactly 22 entries", categories.size() == 22);

		int i = 0;
		for (final AddCategoryAction cat : categories) {
			if (cat.getType() == EntityType.EXPENSE) {
				i++;
			}
		}

		assertTrue("Eight Categories should be Expense Categories", i == 8);

	}

	@Test
	public void loadTagConfigurationFromFile() throws Exception {
		final Map<String, List<AddTagAction>> tags = setupTagFileMock();
		final List<AddCategoryAction> categories = setupCategoryFileMock();

		assertNotNull("Tags map should not be null", tags);

		// Make sure all categories are represented in the tags file
		for (final AddCategoryAction cat : categories) {
			assertTrue("Could not find the the key " + cat.getName()
					+ " in the tag map.", tags.containsKey(cat.getName()));
		}
	}

	/**
	 * @return
	 * 
	 */
	private Map<String, List<AddTagAction>> setupTagFileMock() throws Exception {
		final InputStream is = new BufferedInputStream(new FileInputStream(
				TEST_TAGS_CONFIG_FILE));

		when(util.getConfigurationFile(ConfigurationFileType.TAGS))
				.thenReturn(convertStreamToString(is));

		return config.getTags();
	}

	/**
	 * @return
	 * @throws IOException
	 * 
	 */
	private List<AddCategoryAction> setupCategoryFileMock() throws Exception {
		final InputStream is = new BufferedInputStream(new FileInputStream(
				TEST_CATEGORY_CONFIG_FILE));

		when(util.getConfigurationFile(ConfigurationFileType.CATEGORIES))
				.thenReturn(convertStreamToString(is));

		return config.getCategories();
	}

	// FIXME only use one version of this code, duplicated here from
	// EkonomipulsUtil
	private String convertStreamToString(final InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			final Writer writer = new StringWriter();

			final char[] buffer = new char[1024];
			try {
				final Reader reader = new BufferedReader(new InputStreamReader(
						is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}
