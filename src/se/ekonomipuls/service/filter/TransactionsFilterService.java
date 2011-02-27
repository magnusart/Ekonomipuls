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
package se.ekonomipuls.service.filter;

import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.PropertiesConstants;
import se.ekonomipuls.actions.UpdateTransactionAction;
import se.ekonomipuls.database.DbFacade;
import se.ekonomipuls.database.Transaction;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 29 jan 2011
 */
public class TransactionsFilterService extends IntentService implements
		PropertiesConstants, LogTag {

	/**
	 * 
	 */
	public TransactionsFilterService() {
		super(TransactionsFilterService.class.getClass().getName());
	}

	/** {@inheritDoc} */
	@Override
	protected void onHandleIntent(final Intent intent) {
		Log.d(TAG, "Applying filters");

		List<Transaction> transactions;
		try {
			transactions = DbFacade.getUnfilteredTransactions(getBaseContext());

			for (final Transaction t : transactions) {

				final UpdateTransactionAction modTrans = new UpdateTransactionAction(
						t);

				// TODO Apply filters

				modTrans.setFiltered(true);

				// Only if no filters matched, set default tag.

				final SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(this);
				final long tagId = pref.getLong(CONF_DEF_TAG, -1);

				assert (tagId != -1);

				DbFacade.updateTransactionsAssignTags(this, modTrans, tagId);
			}
		} catch (final RemoteException e) {
			// TODO Handle exception
			e.printStackTrace();

		}
	}
}
