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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectResource;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.proxy.configuration.ConfigurationRemapUtil.SourceType;
import se.ekonomipuls.service.AndroidApiUtil;
import se.ekonomipuls.service.AndroidApiUtil.ConfigurationType;

import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Magnus Andersson
 * @since 17 jul 2011
 */
@Singleton
public abstract class AbstractConfiguratorProxy implements LogTag,
		ConfiguratorProxy {

	@Inject
	protected ConfigurationRemapUtil remapUtil;

	@Inject
	private AndroidApiUtil util;

	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String filterRulesUrl;

	final Map<ConfigurationType, SourceType> sourceMapping = new HashMap<ConfigurationType, SourceType>();
	private final Map<ConfigurationType, String> remoteUrlMapping = new HashMap<ConfigurationType, String>();

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	public List<AddCategoryAction> getCategories() throws IOException {
		final JsonReader reader = getConfiguration(ConfigurationType.CATEGORIES);
		final List<AddCategoryAction> categories = remapUtil.mapCategories(
				reader, sourceMapping.get(ConfigurationType.CATEGORIES));
		reader.close();
		return categories;
	}

	/** {@inheritDoc} */
	public Map<String, List<AddTagAction>> getTags() throws IOException {
		final JsonReader reader = getConfiguration(ConfigurationType.TAGS);
		final Map<String, List<AddTagAction>> tags = remapUtil.mapTags(reader,
				sourceMapping.get(ConfigurationType.TAGS));
		reader.close();
		return tags;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	public Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws IOException {
		final JsonReader reader = getConfiguration(ConfigurationType.FILTER_RULES);
		final Map<String, List<AddFilterRuleAction>> filterRules = remapUtil
				.mapFilterRules(reader,
						sourceMapping.get(ConfigurationType.FILTER_RULES));
		reader.close();
		return filterRules;
	}

	private JsonReader getConfiguration(final ConfigurationType type)
			throws IOException {
		final SourceType source = sourceMapping.get(type);

		if (remoteUrlMapping.size() == 0) {
			initRemoteUrlMapping();
		}

		InputStream is = null;

		switch (source) {
		case LOCAL_JSON:
			is = util.getConfigurationFile(type);
			break;
		case LOCAL_SPREADSHEET_JSON:
			is = util.getConfigurationFile(type);
			break;
		case REMOTE_SPREADSHEET_JSON:
			is = util.queryRestUrlStream(remoteUrlMapping.get(type));
			break;
		}

		if (is == null) {
			return null;
		}

		return new JsonReader(new InputStreamReader(is, "UTF-8"));
	}

	/**
	 * Do this after constructor finished to be able to access injected
	 * resources.
	 */
	private void initRemoteUrlMapping() {
		remoteUrlMapping.put(ConfigurationType.FILTER_RULES, filterRulesUrl);
	}
}
