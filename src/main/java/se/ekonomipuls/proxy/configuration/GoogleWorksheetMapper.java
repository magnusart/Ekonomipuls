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

import java.util.Collection;
import java.util.HashMap;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddFilterRuleAction;

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
}
