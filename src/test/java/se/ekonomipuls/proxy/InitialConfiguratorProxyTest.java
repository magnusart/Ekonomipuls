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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationFileType;
import se.ekonomipuls.model.EntityType;
import se.ekonomipuls.proxy.configuration.ConfigurationValidator;
import se.ekonomipuls.proxy.configuration.InitialConfiguratorProxy;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
@RunWith(InjectedTestRunner.class)
public class InitialConfiguratorProxyTest {

	private static final String ASSETS = "assets/";

	private static final int FILTER_RULE_SIZE = 8;

	private static final int CATEGORY_SIZE = 22;

	private static final String EXPENSES_TAG_NAME = "Övriga utgifter";

	private static final String INCOME_TAG_NAME = "Övriga inkomster";

	@Inject
	@InjectMocks
	private InitialConfiguratorProxy config;

	@Inject
	private ConfigurationValidator validator;

	@Mock
	private EkonomipulsUtil util;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadCategoriesFromFile() throws Exception {

		final List<AddCategoryAction> categories = setupCategoryFileMock();

		assertNotNull("Category list should not be null", categories);
		assertTrue("Category list should be exactly " + CATEGORY_SIZE
				+ " entries, found " + categories.size() + ".", categories.size() == CATEGORY_SIZE);

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
		final List<AddCategoryAction> categories = setupCategoryFileMock();
		final Map<String, List<AddTagAction>> tags = setupTagFileMock();

		assertNotNull("Tags map should not be null", tags);

		// Make sure all categories are represented in the tags file
		for (final AddCategoryAction cat : categories) {
			assertTrue("Could not find the the key " + cat.getName()
					+ " in the tag map.", tags.containsKey(cat.getName()));
		}
	}

	@Test
	public void loadFilterRuleFromFile() throws Exception {
		final Map<String, List<AddFilterRuleAction>> rules = setupFilterRulesFileMock();

		assertNotNull("Filter Rules list should no be null", rules);
		assertTrue("Filter rule list should be exactly " + FILTER_RULE_SIZE
				+ " entries, found " + rules.size() + ".", rules.size() == FILTER_RULE_SIZE);

		assertTrue(rules.containsKey("Lön"));
		assertTrue(rules.containsKey("Luncher"));
	}

	@Test
	public void validateConfigurationTest() throws Exception {
		final List<AddCategoryAction> categories = setupCategoryFileMock();
		final Map<String, List<AddTagAction>> tags = setupTagFileMock();
		final Map<String, List<AddFilterRuleAction>> rules = setupFilterRulesFileMock();

		validator
				.validateConfiguration(categories, tags, rules, EXPENSES_TAG_NAME, INCOME_TAG_NAME);
	}

	/**
	 * @return
	 */
	private Map<String, List<AddFilterRuleAction>> setupFilterRulesFileMock()
			throws Exception {
		final InputStream is = new BufferedInputStream(new FileInputStream(
				ASSETS + ConfigurationFileType.FILTER_RULES.getFileName()));

		when(util.getConfigurationFile(ConfigurationFileType.FILTER_RULES))
				.thenReturn(util.convertStreamToString(is));

		return config.getFilterRules();
	}

	/**
	 * @return
	 * 
	 */
	private Map<String, List<AddTagAction>> setupTagFileMock() throws Exception {
		final InputStream is = new BufferedInputStream(new FileInputStream(
				ASSETS + ConfigurationFileType.TAGS.getFileName()));

		when(util.getConfigurationFile(ConfigurationFileType.TAGS))
				.thenReturn(util.convertStreamToString(is));

		return config.getTags();
	}

	/**
	 * @return
	 * @throws IOException
	 * 
	 */
	private List<AddCategoryAction> setupCategoryFileMock() throws Exception {
		final InputStream is = new BufferedInputStream(new FileInputStream(
				ASSETS + ConfigurationFileType.CATEGORIES.getFileName()));

		when(util.getConfigurationFile(ConfigurationFileType.CATEGORIES))
				.thenReturn(util.convertStreamToString(is));

		return config.getCategories();
	}

}
