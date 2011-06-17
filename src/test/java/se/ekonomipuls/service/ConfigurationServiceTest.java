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

import static org.mockito.Mockito.*;

import java.io.BufferedInputStream;
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

import com.google.inject.Inject;
import roboguice.inject.InjectResource;
import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.proxy.configuration.GDocsConfiguratorProxy;

/**
 * @author Magnus Andersson
 * @since 15 jun 2011
 */
@RunWith(InjectedTestRunner.class)
public class ConfigurationServiceTest implements LogTag {

	private static final String GDOC_FILE = "src/test/resources/filter_rules_gdoc.json";

	@Inject
	EkonomipulsUtil util;

	@Mock
	GDocsConfiguratorProxy proxy;

	@Inject
	@InjectMocks
	ConfigurationService config;

	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String url;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadFilterRulesFromGdocs() throws Exception {

		final InputStream is = new BufferedInputStream(new FileInputStream(
				GDOC_FILE));

		final String mockedRespose = util.convertStreamToString(is);

		proxy.setUrl(url);

		when(proxy.queryRESTurl(eq(url))).thenReturn(mockedRespose);

		final Map<String, List<AddFilterRuleAction>> rule = config
				.getFilterRules();

		verify(proxy).queryRESTurl(eq(url));
	}
}
