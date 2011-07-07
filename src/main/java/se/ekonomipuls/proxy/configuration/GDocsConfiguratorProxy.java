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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectResource;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.service.AndroidApiUtil;

import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 15 jun 2011
 */
public class GDocsConfiguratorProxy implements LogTag, ConfiguratorProxy {
	@Inject
	private AndroidApiUtil util;

	// FIXME: Temporary implementation, create a more robust API (not hard coded
	// delegates).
	@Inject
	private FileConfiguratorProxy delegate;

	@Inject
	private Gson gson;

	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String url;

	@InjectResource(R.string.app_name)
	private String appName;

	@SuppressWarnings({ "deprecation", "deprecation" })
	public final Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws JsonIOException, JsonSyntaxException, IOException {
		final String response = util.queryRestUrl(url);
		// final GdocFilterRules rules = gson.fromJson(response,
		// GdocFilterRules.class);
		final HttpTransport transport = AndroidHttp.newCompatibleTransport();
		final GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
		headers.setApplicationName(appName + "/0.1");
		headers.gdataVersion = "3";
		final AtomParser parser = new AtomParser();
		parser.namespaceDictionary = Namespace.DICTIONARY;
		transport.addParser(parser);

		// Log.d(TAG, "Converting to GdocFilterRules: " + rules);
		// if (response != null) {
		// Log.d(TAG, "Got json response: " + response);
		//
		// } else {
		return delegate.getFilterRules();
		// }
		//
		// return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<AddCategoryAction> getCategories() throws JsonIOException,
			JsonSyntaxException, IOException {
		return delegate.getCategories();
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<AddTagAction>> getTags() throws JsonIOException,
			JsonSyntaxException, IOException {
		return delegate.getTags();
	}

	/**
	 * @author Magnus Andersson
	 * @since 2 jul 2011
	 */
	static class GdocType {
		String type;
		String $t;

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "Content [$t=" + $t + "]";
		}

	}

}
