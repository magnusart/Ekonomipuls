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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import roboguice.inject.InjectResource;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.proxy.configuration.GoogleFilterRulesWorksheet.Entry;
import se.ekonomipuls.service.AndroidApiUtil;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 15 jun 2011
 */
public class GoogleSpreadsheetConfiguratorProxy implements LogTag,
		ConfiguratorProxy {
	private static final String COLON_SPLIT = ":";

	@Inject
	private AndroidApiUtil util;

	@Inject
	private GoogleWorksheetMapper mapper;

	// FIXME: Temporary implementation, create a more robust API (not hard coded
	// delegates).
	@Inject
	private FileConfiguratorProxy delegate;

	@Inject
	private Gson gson;

	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String url;

	public final Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws IOException {
		final InputStream in = util.queryRestUrlStream(url);
		final JsonReader reader = new JsonReader(new InputStreamReader(in,
				"UTF-8"));

		final GoogleFilterRulesWorksheet worksheet = gson
				.fromJson(reader, GoogleFilterRulesWorksheet.class);

		reader.close();

		final Map<String, List<AddFilterRuleAction>> activeFilterRules = new HashMap<String, List<AddFilterRuleAction>>();

		// Make sure we actually have values
		if (weHaveData(worksheet)) {

			Log.d(TAG, "Parsed worksheet, converting to domain object");

			// Iterate over entries.
			for (final GoogleFilterRulesWorksheet.Entry e : worksheet.feed.entry) {
				Log.d(TAG, "Parsing entry [ " + e.content.value + " ]");

				final HashMap<String, String> row = getFilterRuleValues(e);

				final AddFilterRuleAction action = mapper
						.mapActiveFilterRule(row);

				if (action != null) {
					final String tag = mapper.mapTag(row);

					List<AddFilterRuleAction> actions;
					if (activeFilterRules.containsKey(tag)) {
						actions = activeFilterRules.get(tag);
					} else {
						actions = new ArrayList<AddFilterRuleAction>();
					}

					// Add the action
					actions.add(action);

					activeFilterRules.put(tag, actions);

					Log.d(TAG, "Adding action " + action + " with tag " + tag);
				}

			}
		}

		return activeFilterRules;
	}

	/**
	 * @param e
	 * @return
	 */
	private HashMap<String, String> getFilterRuleValues(final Entry e) {
		final StringTokenizer tokenizer = new StringTokenizer(e.content.value,
				",");
		final HashMap<String, String> columns = new HashMap<String, String>();

		// Split into key value and insert into columns hashmap.
		while (tokenizer.hasMoreTokens()) {
			final String keyValue = tokenizer.nextToken();
			final String[] s = keyValue.split(COLON_SPLIT);
			columns.put(s[0].trim(), s[1].trim());
		}

		return columns;
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
	 * @param worksheet
	 * @return
	 */
	private boolean weHaveData(final GoogleFilterRulesWorksheet worksheet) {
		return (worksheet != null)
				&& (worksheet.feed != null)
				&& ((worksheet.feed.entry != null) && (worksheet.feed.entry
						.size() > 0));
	}
}
