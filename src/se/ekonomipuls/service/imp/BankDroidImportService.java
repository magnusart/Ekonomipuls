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

import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.database.DbFacade;
import se.ekonomipuls.proxy.BankDroidProxy;
import se.ekonomipuls.proxy.BankDroidTransaction;
import se.ekonomipuls.service.filter.TransactionsFilterService;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 25 jan 2011
 */
public class BankDroidImportService extends IntentService implements LogTag {

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
		Log.d(TAG, "Recieved intent for importing transactions");

		final String accountId = intent.getExtras().getString(ACCOUNT_ID);
		try {
			final List<BankDroidTransaction> transactions = BankDroidProxy
					.getBankDroidTransactions(getBaseContext(), accountId);

			// TODO: Cleanse incoming transactions from duplicates.

			DbFacade.bulkInsertBdTransactions(getBaseContext(), transactions);

			// Hand over to filtering.

			final Intent importFilter = new Intent(this,
					TransactionsFilterService.class);

			this.startService(importFilter);

		} catch (final IllegalAccessException e) {
			Log.e(TAG, "Unable to access the content provider.", e);
		}
	}

}
