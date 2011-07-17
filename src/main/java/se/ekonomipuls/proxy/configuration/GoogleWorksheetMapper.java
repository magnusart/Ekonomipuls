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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.proxy.configuration.GoogleFilterRulesWorksheet.Entry;
import android.util.Log;

import com.google.inject.Singleton;

/**
 * @author Magnus Andersson
 * @since 8 jul 2011
 */
@Singleton
final class GoogleWorksheetMapper implements LogTag {

	private static final String AKTIV_KEY = "aktiv";
	private static final String AKTIV_JA = "Ja";

	private static final String NAME_KEY = "filternamn";
	private static final String DESCRIPTION＿KEY = "beskrivning";
	private static final String PATTERN＿KEY = "textmatchningsmönster";
	private static final String PRIORITY＿KEY = "prioritet";
	private static final String TRANSACTION_TYPE_KEY = "vilkentypavtransaktionärdet";
	private static final String SIGNATURE = "signaturellere-post";
	private static final String EXPENSE_CATEGORY = "utgiftskategori";

	private static final String COLON_SPLIT = ":";

	/**
	 * @param col
	 * @return
	 */
	public AddFilterRuleAction mapActiveFilterRule(
			final HashMap<String, String> row) {
		// Only work with active filter rules
		if (isActive(row)) {
			final String name = row.remove(NAME_KEY);
			final String description = row.remove(DESCRIPTION＿KEY);
			final String pattern = row.remove(PATTERN＿KEY);
			final int priority = Integer.parseInt(row.remove(PRIORITY＿KEY));

			return new AddFilterRuleAction(name, description, pattern, true,
					priority, 0);
		}
		return null;
	}

	/**
	 * @param row
	 * @return
	 */
	private boolean isActive(final HashMap<String, String> row) {
		return row.containsKey(AKTIV_KEY)
				&& row.get(AKTIV_KEY).equals(AKTIV_JA);
	}

	/**
	 * @param col
	 * @return
	 */
	public String mapTag(final HashMap<String, String> row) {
		System.out.println("Mapping tag for " + row);

		// Remove unnessecary columns
		row.remove(TRANSACTION_TYPE_KEY);
		row.remove(EXPENSE_CATEGORY);
		row.remove(AKTIV_KEY);
		row.remove(SIGNATURE);

		// After this either income category column (which only have one tag
		// each with the same name) or the expense tag column will be left.

		// We should now have only one column left.
		final Collection<String> values = row.values();

		return values.iterator().next();
	}

	/**
	 * @param worksheet
	 * @return
	 */
	public Map<String, List<AddFilterRuleAction>> mapActiveFilterRules(
			final GoogleFilterRulesWorksheet worksheet) {
		final Map<String, List<AddFilterRuleAction>> activeFilterRules = new HashMap<String, List<AddFilterRuleAction>>();

		// Make sure we actually have values
		if (weHaveData(worksheet)) {

			Log.d(TAG, "Parsed worksheet, converting to domain object");

			// Iterate over entries.
			for (final GoogleFilterRulesWorksheet.Entry e : worksheet.feed.entry) {
				Log.d(TAG, "Parsing entry [ " + e.content.value + " ]");

				final HashMap<String, String> row = getFilterRuleValues(e);

				final AddFilterRuleAction action = mapActiveFilterRule(row);

				if (action != null) {
					final String tag = mapTag(row);

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
	 * @param worksheet
	 * @return
	 */
	private boolean weHaveData(final GoogleFilterRulesWorksheet worksheet) {
		return (worksheet != null)
				&& (worksheet.feed != null)
				&& ((worksheet.feed.entry != null) && (worksheet.feed.entry
						.size() > 0));
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
}
