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
package se.ekonomipuls.service;

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.PropertiesConstants.CONF_DEF_TAG;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.model.Transaction;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public class FilterService {

	@Inject
	Context context;

	/**
	 * @param deduplicatedTransactions
	 * @return
	 */
	public List<ApplyFilterTagAction> applyFilters(
			final List<Transaction> transactions) {
		return catchAllFilter(transactions);
	}

	/**
	 * @param transactions
	 * @return
	 */
	private List<ApplyFilterTagAction> catchAllFilter(
			final List<Transaction> transactions) {
		final List<ApplyFilterTagAction> filteredTransactions = new ArrayList<ApplyFilterTagAction>();

		for (final Transaction t : transactions) {
			Log.d(TAG, "Applying filter to Transaction " + t);

			final long tagId = getTagId();

			assert (tagId != -1);

			t.setFiltered(true); // A filter have been applied.

			filteredTransactions.add(new ApplyFilterTagAction(t, tagId));
		}

		return filteredTransactions;
	}

	/**
	 * @return
	 */
	long getTagId() {
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);

		final long tagId = pref.getLong(CONF_DEF_TAG, -1);
		return tagId;
	}

}
