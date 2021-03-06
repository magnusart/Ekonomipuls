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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
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
public class ConfigurationRemapUtil implements LogTag {

	/**
	 * @author Magnus Andersson
	 * @since 17 jul 2011
	 */
	public static enum SourceType {
		LOCAL_JSON, LOCAL_SPREADSHEET_JSON, REMOTE_SPREADSHEET_JSON
	}

	@Inject
	private Gson gson;

	@Inject
	private Provider<GoogleWorksheetMapper> mapperProvider;

	/**
	 * @param reader
	 * @return
	 */
	public Map<String, List<AddTagAction>> mapTags(final JsonReader reader,
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
	public final Map<String, List<AddFilterRuleAction>> mapFilterRules(
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
	public final List<AddCategoryAction> mapCategories(final JsonReader reader,
			final SourceType type) {
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
}
