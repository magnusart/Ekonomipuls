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
package se.ekonomipuls.service;

import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.adapter.EkonomipulsDbAdapter;
import se.ekonomipuls.proxy.BankDroidProxy;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 25 jan 2011
 */
public class BankDroidTransactionsImportService extends IntentService implements
		LogTag {

	private static final String ACCOUNT_ID = "accountId";

	/**
	 * @param name
	 */
	public BankDroidTransactionsImportService() {
		super(BankDroidTransactionsImportService.class.getClass().getName());
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

			EkonomipulsDbAdapter.bulkInsertBdTransactions(getBaseContext(),
					transactions);

		} catch (final IllegalAccessException e) {
			Log.e(TAG, "Unable to access the content provider.", e);
		}
	}

}
