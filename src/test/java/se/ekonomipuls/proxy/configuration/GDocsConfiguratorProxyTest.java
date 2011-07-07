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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import roboguice.inject.InjectResource;
import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.service.AndroidApiUtil;
import android.content.Context;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 15 jun 2011
 */
@RunWith(InjectedTestRunner.class)
public class GDocsConfiguratorProxyTest implements LogTag {

	private static final String GDOC_FILE = "src/test/resources/filter_rules_gdoc.json";

	@Mock
	AndroidApiUtil util;

	@Inject
	@InjectMocks
	GDocsConfiguratorProxy proxy;

	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String url;

	@Inject
	Context context;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadFilterRulesFromGdocs() throws Exception {

		final InputStream is = new BufferedInputStream(new FileInputStream(
				GDOC_FILE));

		final String mockedRespose = util.convertStreamToString(is);

		when(util.queryRestUrl(anyString())).thenReturn(mockedRespose);

		final Map<String, List<AddFilterRuleAction>> rule = proxy
				.getFilterRules();

		verify(util).queryRestUrl(eq(url));
	}
}
