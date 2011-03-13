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
package se.ekonomipuls.service.imp;

import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.PropertiesConstants;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.DbFacade;
import se.ekonomipuls.database.Transaction;
import se.ekonomipuls.proxy.BankDroidProxy;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 25 jan 2011
 */
public class BankDroidImportService extends IntentService implements
		PropertiesConstants, LogTag {

	private static final String ACCOUNT_ID = "accountId";

	/**
	 * 
	 */
	public BankDroidImportService() {
		super(BankDroidImportService.class.getClass().getName());
	}

	/** {@inheritDoc} */
	@Override
	protected void onHandleIntent(final Intent intent) {
		Log.v(TAG, "Starting to import transactions");

		final String accountId = intent.getExtras().getString(ACCOUNT_ID);
		try {
			Log.v(TAG, "Fetching transactions from BankDroid content provider");
			final List<BankDroidTransaction> transactions = BankDroidProxy
					.getBankDroidTransactions(getBaseContext(), accountId);

			// TODO: Cleanse incoming transactions from duplicates.

			Log.v(TAG, "Bulk inserting transactions");
			DbFacade.bulkInsertBdTransactions(getBaseContext(), transactions);

			//			filterLocal();

			// Hand over to filtering.

			//			Log.v(TAG, "Handing over to filering service");
			//			final Intent importFilter = new Intent(this,
			//					TransactionsFilterService.class);

			//			this.startService(importFilter);

		} catch (final IllegalAccessException e) {
			Log.e(TAG, "Unable to access the content provider.", e);
		} catch (final RemoteException e) {
			Log.e(TAG, "Unable to access the content provider.", e);
		}
	}

	/**
	 * 
	 */
	private void filterLocal() {
		Log.v(TAG, "Starting to apply filters to unfiltered transactions");

		List<Transaction> transactions;
		try {
			Log.v(TAG, "Fetching all unfiltered transactions");
			transactions = DbFacade.getUnfilteredTransactions(getBaseContext());
			Log.d(TAG, transactions.size()
					+ " transactions for filter application.");
			final List<ApplyFilterTagAction> filteredTransactions = new ArrayList<ApplyFilterTagAction>();

			for (final Transaction t : transactions) {
				Log.v(TAG, "Applying filter to Transaction " + t);

				// Only if no filters matched, set default tag.

				final SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(this);
				final long tagId = pref.getLong(CONF_DEF_TAG, -1);

				assert (tagId != -1);

				filteredTransactions.add(new ApplyFilterTagAction(t, tagId));

			}

			DbFacade.updateTransactionsAssignTags(this, filteredTransactions);

		} catch (final RemoteException e) {
			// TODO Handle exception
			e.printStackTrace();

		}
	}

}
