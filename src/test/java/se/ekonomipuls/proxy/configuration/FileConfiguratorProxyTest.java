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
package se.ekonomipuls.proxy.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationType;
import se.ekonomipuls.model.EntityType;
import se.ekonomipuls.service.AndroidApiUtil;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
@RunWith(InjectedTestRunner.class)
public class FileConfiguratorProxyTest {
	private static final int FILTER_RULE_SIZE = 30;
	private static final int CATEGORY_SIZE = 22;

	private static final String EXPENSES_TAG_NAME = "Övriga utgifter";
	private static final String INCOME_TAG_NAME = "Övriga inkomster";

	@Inject
	private ConfigurationValidator validator;

	@Mock
	private AndroidApiUtil util;

	@Inject
	private FileMockUtil fileMock;

	@Inject
	@InjectMocks
	private FileConfiguratorProxy config;
	private List<AddCategoryAction> categories;
	private Map<String, List<AddTagAction>> tags;
	private Map<String, List<AddFilterRuleAction>> rules;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		final InputStream catStream = fileMock.setupCategoryFileMock();
		final InputStream tagStream = fileMock.setupTagFileMock();
		final InputStream rulesStream = fileMock.setupFilterRulesFileMock();

		when(util.getConfigurationFile(ConfigurationType.CATEGORIES))
				.thenReturn(catStream);

		when(util.getConfigurationFile(ConfigurationType.TAGS))
				.thenReturn(tagStream);

		when(util.getConfigurationFile(ConfigurationType.FILTER_RULES))
				.thenReturn(rulesStream);

		categories = config.getCategories();
		tags = config.getTags();
		rules = config.getFilterRules();
	}

	@Test
	public void loadCategoriesFromFile() throws Exception {
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
		assertNotNull("Tags map should not be null", tags);

		// Make sure all categories are represented in the tags file
		for (final AddCategoryAction cat : categories) {
			assertTrue("Could not find the the key " + cat.getName()
					+ " in the tag map.", tags.containsKey(cat.getName()));
		}
	}

	@Test
	public void loadFilterRuleFromFile() throws Exception {
		assertNotNull("Filter Rules list should no be null", rules);
		assertTrue("Filter rule list should be exactly " + FILTER_RULE_SIZE
				+ " entries, found " + rules.size() + ".", rules.size() == FILTER_RULE_SIZE);

		assertTrue(rules.containsKey("Lön"));
		assertTrue(rules.containsKey("Luncher"));
	}

	@Test
	public void validateConfigurationTest() throws Exception {
		validator
				.validateConfiguration(categories, tags, rules, EXPENSES_TAG_NAME, INCOME_TAG_NAME);
	}

}
