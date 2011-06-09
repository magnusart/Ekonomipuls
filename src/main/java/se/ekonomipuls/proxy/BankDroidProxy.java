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
package se.ekonomipuls.proxy;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.google.inject.Inject;
import com.liato.bankdroid.provider.IBankTransactionsProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.model.EkonomipulsUtil;

import static se.ekonomipuls.LogTag.TAG;

/**
 * @author Magnus Andersson
 * @since 15 jan 2011
 */
public class BankDroidProxy implements IBankTransactionsProvider {

	@Inject
	private Context context;

	@Inject
	EkonomipulsUtil util;

	private static final String CONTENT_PROVIDER_API_KEY = "content_provider_api_key";

	/**
	 * Get a list of BankDroidTransactions.
	 * 
	 * @param accountId
	 *            The Account Id.
	 * @return List of Transactions
	 * @throws IllegalAccessException
	 */
	public List<BankDroidTransaction> getBankDroidTransactions(
			final String accountId) throws IllegalAccessException {

		final List<BankDroidTransaction> transactions = new ArrayList<BankDroidTransaction>();
		Cursor cur = null;

		try {
			cur = getUnmanagedTransactionsCursor(accountId);

			final int tId = cur.getColumnIndexOrThrow(TRANS_ID);
			final int tDate = cur.getColumnIndexOrThrow(TRANS_DATE);
			final int tDesc = cur.getColumnIndexOrThrow(TRANS_DESC);
			final int tAmt = cur.getColumnIndexOrThrow(TRANS_AMT);
			final int tCur = cur.getColumnIndexOrThrow(TRANS_CUR);
			final int tAcc = cur.getColumnIndexOrThrow(TRANS_ACCNT);

			Log.d(TAG, "Aquired transactions cursor with " + cur.getCount()
					+ " rows");

			while (cur.moveToNext()) {
				final String id = cur.getString(tId);
				final String date = cur.getString(tDate);
				final String desc = cur.getString(tDesc);
				final BigDecimal amt = new BigDecimal(cur.getString(tAmt));
				final String curr = cur.getString(tCur);
				final String acc = cur.getString(tAcc);

				final BankDroidTransaction trans = new BankDroidTransaction(id,
						date, desc, amt, curr, acc);

				transactions.add(trans);
			}
		} finally {
			if (cur != null) {
				cur.close(); // Clean up.
			}
		}

		return transactions;
	}

	Cursor getUnmanagedBankAccountsCursor(final Context ctx)
			throws IllegalAccessException {
		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ BANK_ACCOUNTS_CAT + "/" + API_KEY + util.getApiKey());

		final Cursor cur = ctx
				.getContentResolver()
				.query(uri, BANK_ACCOUNT_PROJECTION, NO_HIDDEN_ACCOUNTS_FILTER, null, null);

		if (cur == null) {
			throw new IllegalAccessException(
					"The API-key was either empty or did not match BankDroid's API-key");
		}

		return cur;
	}

	private Cursor getUnmanagedTransactionsCursor(final String accountId)
			throws IllegalAccessException {
		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ TRANSACTIONS_CAT + "/" + API_KEY + util.getApiKey());

		Log.d(TAG, "Picked Account with id " + accountId);

		final Cursor cur = context
				.getContentResolver()
				.query(uri, TRANSACTIONS_PROJECTION, ACCOUNT_SELECTION_FILTER, new String[] { accountId }, null);

		if (cur == null) {
			throw new IllegalAccessException(
					"The API-key was either empty or did not match BankDroid's API-key");
		}

		return cur;
	}
}
