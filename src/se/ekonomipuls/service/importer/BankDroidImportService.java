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
package se.ekonomipuls.service.importer;

import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.PropertiesConstants;
import se.ekonomipuls.database.staging.StagingDbFacade;
import se.ekonomipuls.proxy.BankDroidProxy;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * This service is responsible for inserting BankDroid Transactions into the
 * Staging area.
 * 
 * @author Magnus Andersson
 * @since 25 jan 2011
 */
public class BankDroidImportService extends IntentService implements
		PropertiesConstants, LogTag {

	private static final String ACCOUNT_ID = "accountId";

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

			if (transactions.size() > 0) {
				Log.v(TAG, "Bulk inserting transactions");
				StagingDbFacade.bulkInsertBdTransactions(getBaseContext(),
						transactions);
			} else {
				Log.d(TAG, "No transactions for the account " + accountId
						+ ", skipping");
			}

		} catch (final IllegalAccessException e) {
			Log.e(TAG, "Unable to access the content provider.", e);
		}
	}

}
