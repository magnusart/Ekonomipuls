/**
 * Copyright 2010 Magnus Andersson 
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
package se.ekonomipuls.reciever;

import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.TranStatistics;
import se.ekonomipuls.proxy.BankDroidProxy;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class recieves changes to transactions and makes sure they are stored
 * for later processing.
 * 
 * @author Magnus Andersson
 * @since 30 dec 2010
 */
public class BankDroidTransactionReciever extends BroadcastReceiver implements
		LogTag {

	private static final String UPDATE_TRANSACTIONS = "com.liato.bankdroid.action.TRANSACTIONS";
	private static final String ACCOUNT_ID = "accountId";

	/** {@inheritDoc} */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.d(TranStatistics.TAG, "Recieved intent");

		if (UPDATE_TRANSACTIONS.equals(intent.getAction())) {
			Log.d(TAG, "Begin retrieving updated transactions");

			final String accountId = intent.getExtras().getString(ACCOUNT_ID);

			try {
				final List<BankDroidTransaction> transactions = BankDroidProxy
						.getBankDroidTransactions(context, accountId);

				Log.d(TAG, "Listing transactions");
				for (final BankDroidTransaction trans : transactions) {
					Log.d(TAG, trans.toString());
				}

			} catch (final IllegalAccessException e) {
				Log.e(TAG, "Unable to access the content provider.", e);
			}
		}

	}
}
