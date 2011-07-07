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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationFileType;
import se.ekonomipuls.service.AndroidApiUtil;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

/**
 * This class loads the default categories for initial configuration of the
 * application.
 * 
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
public class FileConfiguratorProxy implements LogTag, ConfiguratorProxy {

	@Inject
	AndroidApiUtil util;

	@Inject
	Gson gson;

	/** {@inheritDoc} */
	@Override
	public List<AddCategoryAction> getCategories() throws JsonIOException,
			JsonSyntaxException, IOException {

		final String json = util
				.getConfigurationFile(ConfigurationFileType.CATEGORIES);

		final Type listType = new TypeToken<List<AddCategoryAction>>() {
		}.getType();

		final List<AddCategoryAction> categories = gson
				.fromJson(json, listType);

		return categories;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<AddTagAction>> getTags() throws JsonIOException,
			JsonSyntaxException, IOException {

		final Type mapType = new TypeToken<Map<String, List<AddTagAction>>>() {
		}.getType();

		final String json = util
				.getConfigurationFile(ConfigurationFileType.TAGS);

		final Map<String, List<AddTagAction>> tags = gson.fromJson(json,
				mapType);

		return tags;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws JsonIOException, JsonSyntaxException, IOException {
		final Type mapType = new TypeToken<Map<String, List<AddFilterRuleAction>>>() {
		}.getType();

		final String json = util
				.getConfigurationFile(ConfigurationFileType.FILTER_RULES);

		final Map<String, List<AddFilterRuleAction>> rules = gson.fromJson(
				json, mapType);

		return rules;
	}

}
