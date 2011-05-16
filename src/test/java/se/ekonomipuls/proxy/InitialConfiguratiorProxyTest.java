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

import static org.junit.Assert.*;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Inject;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.EntityType;

/**
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
@RunWith(InjectedTestRunner.class)
public class InitialConfiguratiorProxyTest {

	private static final String TEST_CATEGORY_CONFIG_FILE = "assets/categories.json";

	@Inject
	@InjectMocks
	private InitialConfiguratorProxy config;

	@Mock
	private EkonomipulsUtil util;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadConfigurationFromFile() throws Exception {

		final InputStream is = new BufferedInputStream(new FileInputStream(
				TEST_CATEGORY_CONFIG_FILE));

		when(util.getCategoriesConfigurationFile())
				.thenReturn(convertStreamToString(is));

		final List<AddCategoryAction> categories = config.getCategories();

		assertNotNull("Category list should not be null", categories);
		assertTrue("Category list should not be exactly", categories.size() == 23);

		int i = 0;
		for (final AddCategoryAction cat : categories) {
			if (cat.getType() == EntityType.EXPENSE) {
				i++;
			}
		}

		assertTrue("Eight Categories should be Expense Categories", i == 8);

	}

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
