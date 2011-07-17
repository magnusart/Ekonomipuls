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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectResource;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationType;
import se.ekonomipuls.service.AndroidApiUtil;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author Magnus Andersson
 * @since 17 jul 2011
 */
@Singleton
public abstract class AbstractConfiguratorProxy implements LogTag,
		ConfiguratorProxy {

	@Inject
	Gson gson;

	@Inject
	private Provider<GoogleWorksheetMapper> mapperProvider;

	/**
	 * @author Magnus Andersson
	 * @since 17 jul 2011
	 */
	static enum SourceType {
		LOCAL_JSON, LOCAL_SPREADSHEET_JSON, REMOTE_SPREADSHEET_JSON
	}

	@Inject
	private AndroidApiUtil util;

	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String filterRulesUrl;

	final Map<ConfigurationType, SourceType> sourceMapping = new HashMap<ConfigurationType, SourceType>();
	private final Map<SourceType, String> remoteUrlMapping = new HashMap<SourceType, String>();

	{
		remoteUrlMapping
				.put(SourceType.REMOTE_SPREADSHEET_JSON, filterRulesUrl);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	@Override
	public List<AddCategoryAction> getCategories() throws IOException {
		final JsonReader reader = getConfiguration(ConfigurationType.CATEGORIES);
		final List<AddCategoryAction> categories = mapCategories(reader, sourceMapping
				.get(ConfigurationType.CATEGORIES));
		reader.close();
		return categories;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<AddTagAction>> getTags() throws IOException {
		final JsonReader reader = getConfiguration(ConfigurationType.TAGS);
		final Map<String, List<AddTagAction>> tags = mapTags(reader, sourceMapping
				.get(ConfigurationType.TAGS));
		reader.close();
		return tags;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	@Override
	public Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws IOException {
		final JsonReader reader = getConfiguration(ConfigurationType.FILTER_RULES);
		final Map<String, List<AddFilterRuleAction>> filterRules = mapFilterRules(reader, sourceMapping
				.get(ConfigurationType.FILTER_RULES));
		reader.close();
		return filterRules;
	}

	/**
	 * @param reader
	 * @return
	 */
	protected Map<String, List<AddTagAction>> mapTags(final JsonReader reader,
			final SourceType type) {
		Map<String, List<AddTagAction>> tags = null;

		switch (type) {
		case LOCAL_JSON:
			final Type mapType = new TypeToken<Map<String, List<AddTagAction>>>() {
			}.getType();
			tags = gson.fromJson(reader, mapType);
			break;
		case LOCAL_SPREADSHEET_JSON:
		case REMOTE_SPREADSHEET_JSON:
			Log.w(TAG, "No Spreadsheet mapper for tags implemented");
			break;
		}

		return tags;
	}

	/**
	 * @param reader
	 * @return
	 */
	protected final Map<String, List<AddFilterRuleAction>> mapFilterRules(
			final JsonReader reader, final SourceType type) {
		Map<String, List<AddFilterRuleAction>> filterRules = null;

		switch (type) {
		case LOCAL_JSON:
			final Type mapType = new TypeToken<Map<String, List<AddFilterRuleAction>>>() {
			}.getType();
			filterRules = gson.fromJson(reader, mapType);
			break;
		case LOCAL_SPREADSHEET_JSON:
		case REMOTE_SPREADSHEET_JSON:
			final GoogleFilterRulesWorksheet worksheet = gson
					.fromJson(reader, GoogleFilterRulesWorksheet.class);
			filterRules = mapperProvider.get().mapActiveFilterRules(worksheet);
			break;
		}

		return filterRules;
	}

	/**
	 * @param reader
	 * @return
	 */
	private final List<AddCategoryAction> mapCategories(
			final JsonReader reader, final SourceType type) {
		List<AddCategoryAction> categories = null;

		switch (type) {
		case LOCAL_JSON:
			final Type listType = new TypeToken<List<AddCategoryAction>>() {
			}.getType();

			categories = gson.fromJson(reader, listType);
			break;
		case LOCAL_SPREADSHEET_JSON:
		case REMOTE_SPREADSHEET_JSON:
			Log.w(TAG, "No Spreadsheet mapper for Categories implemented");
			break;
		}

		return categories;
	}

	private JsonReader getConfiguration(final ConfigurationType type)
			throws IOException {
		final SourceType source = sourceMapping.get(type);

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
}
