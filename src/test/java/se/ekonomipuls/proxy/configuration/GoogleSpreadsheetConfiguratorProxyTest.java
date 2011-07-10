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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import roboguice.inject.InjectResource;
import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationFileType;
import se.ekonomipuls.service.AndroidApiUtil;
import android.content.Context;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 15 jun 2011
 */
@RunWith(InjectedTestRunner.class)
public class GoogleSpreadsheetConfiguratorProxyTest {

	private static final String GDOC_FILE = "src/test/resources/filter_rules_gdoc.json";
	private static final int FILTER_RULE_SIZE = 29;

	private static final String EXPENSES_TAG_NAME = "Övriga utgifter";
	private static final String INCOME_TAG_NAME = "Övriga inkomster";
	@Mock
	AndroidApiUtil util;

	@Inject
	AndroidApiUtil util2;

	@Inject
	private ConfigurationValidator validator;

	@Inject
	private FileMockUtil fileMock;

	@Inject
	@InjectMocks
	GoogleSpreadsheetConfiguratorProxy proxy;

	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String url;

	@Inject
	Context context;

	@Inject
	@InjectMocks
	private FileConfiguratorProxy config;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadFilterRulesFromGdocs() throws Exception {

		final File f = new File(GDOC_FILE);

		assertTrue(f.isFile());

		final InputStream is = new BufferedInputStream(new FileInputStream(
				GDOC_FILE));

		when(util.queryRestUrlStream(eq(url))).thenReturn(is);

		final Map<String, List<AddFilterRuleAction>> rules = proxy
				.getFilterRules();

		verify(util).queryRestUrlStream(eq(url));

		System.out.println(rules);

		assertNotNull("Filter Rules list should no be null", rules);
		assertTrue("Filter rule list should be exactly " + FILTER_RULE_SIZE
				+ " entries, found " + rules.size() + ".", rules.size() == FILTER_RULE_SIZE);

		final InputStream catStream = fileMock.setupCategoryFileMock();
		final InputStream tagStream = fileMock.setupTagFileMock();

		final String tagString = util2.convertStreamToString(tagStream);
		final String catString = util2.convertStreamToString(catStream);

		when(util.getConfigurationFile(ConfigurationFileType.CATEGORIES))
				.thenReturn(catString);

		when(util.getConfigurationFile(ConfigurationFileType.TAGS))
				.thenReturn(tagString);

		final List<AddCategoryAction> categories = config.getCategories();
		final Map<String, List<AddTagAction>> tags = config.getTags();

		validator
				.validateConfiguration(categories, tags, rules, EXPENSES_TAG_NAME, INCOME_TAG_NAME);
	}

}
