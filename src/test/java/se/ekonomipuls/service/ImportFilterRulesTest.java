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
package se.ekonomipuls.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.database.AnalyticsFilterRulesDbFacade;
import se.ekonomipuls.proxy.configuration.ConfigurationRemapUtil;
import se.ekonomipuls.proxy.configuration.ConfigurationRemapUtil.SourceType;
import se.ekonomipuls.proxy.configuration.FileMockUtil;
import se.ekonomipuls.proxy.configuration.RemoteConfiguratorProxy;

import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 17 jul 2011
 */
@RunWith(InjectedTestRunner.class)
public class ImportFilterRulesTest {

	@Mock
	private RemoteConfiguratorProxy configProxy;

	@Mock
	AnalyticsFilterRulesDbFacade filterRules;

	@Inject
	private ConfigurationRemapUtil util;

	@Inject
	private FileMockUtil fileMock;

	@Inject
	@InjectMocks
	private ConfigurationService service;

	private List<AddCategoryAction> categories;
	private Map<String, List<AddTagAction>> tags;
	private Map<String, List<AddFilterRuleAction>> rules;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		final InputStream catStream = fileMock.setupCategoryFileMock();
		final InputStream tagStream = fileMock.setupTagFileMock();
		final InputStream rulesStream = fileMock.setupFilterRulesFileMock();

		final JsonReader catReader = new JsonReader(new InputStreamReader(
				catStream, "UTF-8"));
		final JsonReader tagReader = new JsonReader(new InputStreamReader(
				tagStream, "UTF-8"));
		final JsonReader rulesReader = new JsonReader(new InputStreamReader(
				rulesStream, "UTF-8"));

		categories = util.mapCategories(catReader, SourceType.LOCAL_JSON);
		tags = util.mapTags(tagReader, SourceType.LOCAL_JSON);
		rules = util
				.mapFilterRules(rulesReader, SourceType.LOCAL_SPREADSHEET_JSON);

		when(configProxy.getCategories()).thenReturn(categories);

		when(configProxy.getTags()).thenReturn(tags);

		when(configProxy.getFilterRules()).thenReturn(rules);

	}

	@Test
	public void downloadPurgeImportFilterRules() throws Exception {

		when(filterRules.replaceFilterRules(anyListOf(AddFilterRuleAction.class)))
				.thenReturn(3L);

		final boolean result = service
				.importRemoteFilterRulesLocalCategoriesTags();
		assertTrue(result);

		final InOrder inOrder = inOrder(configProxy, filterRules);

		inOrder.verify(configProxy).getFilterRules();
		inOrder.verify(configProxy).getCategories();
		inOrder.verify(configProxy).getTags();
		inOrder.verify(filterRules)
				.replaceFilterRules(anyListOf(AddFilterRuleAction.class));
	}
}
