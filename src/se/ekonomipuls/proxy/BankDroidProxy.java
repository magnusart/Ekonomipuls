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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.liato.bankdroid.provider.IBankTransactionsProvider;

/**
 * @author Magnus Andersson
 * @since 15 jan 2011
 */
public class BankDroidProxy implements IBankTransactionsProvider, LogTag {

	private static final String CONTENT_PROVIDER_API_KEY = "content_provider_api_key";

	private BankDroidProxy() {
		// Private Constructor
	}

	/**
	 * 
	 * @param a
	 *            The activity that should manage the cursor.
	 * @return a managed cursor to the Bank Accounts Provider.
	 * @throws IllegalAccessException
	 */
	public static Cursor getManagedBankAccountsCursor(final Activity a)
			throws IllegalAccessException {
		Log.d(TAG, "Preparing To Read Bank Accounts from Provider");

		final Cursor cur = getUnmanagedBankAccountsCursor(a.getBaseContext());

		a.startManagingCursor(cur);

		return cur;
	}

	public static Cursor getManagedTransactionsCursor(final Activity a,
			final String accountId) throws IllegalAccessException {
		Log.d(TAG, "Preparing To Read Transactions from Provider");

		final Cursor cur = getUnmanagedTransactionsCursor(a.getBaseContext(),
				accountId);

		a.startManagingCursor(cur);

		return cur;
	}

	/**
	 * Get a list of BankDroidTransactions.
	 * 
	 * @param ctx
	 *            The Context.
	 * @param accountId
	 *            The Account Id.
	 * @return List of Transactions
	 * @throws IllegalAccessException
	 */
	public static List<BankDroidTransaction> getBankDroidTransactions(
			final Context ctx, final String accountId)
			throws IllegalAccessException {

		final List<BankDroidTransaction> transactions = new ArrayList<BankDroidTransaction>();
		Cursor cur = null;

		try {
			cur = getUnmanagedTransactionsCursor(ctx, accountId);

			final int tId = cur.getColumnIndexOrThrow(TRANS_ID);
			final int tDate = cur.getColumnIndexOrThrow(TRANS_DATE);
			final int tDesc = cur.getColumnIndexOrThrow(TRANS_DESC);
			final int tAmt = cur.getColumnIndexOrThrow(TRANS_AMT);
			final int tCur = cur.getColumnIndexOrThrow(TRANS_CUR);
			final int tAcc = cur.getColumnIndexOrThrow(TRANS_ACCNT);

			Log.d(TAG, "Aquired transactions cursor with " + cur.getCount()
					+ " of rows");

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

	private static Cursor getUnmanagedBankAccountsCursor(final Context ctx)
			throws IllegalAccessException {
		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ BANK_ACCOUNTS_CAT + "/" + API_KEY + getApiKey(ctx));

		final Cursor cur = ctx.getContentResolver().query(uri,
				BANK_ACCOUNT_PROJECTION, NO_HIDDEN_ACCOUNTS_FILTER, null, null);

		if (cur == null) {
			throw new IllegalAccessException(
					"The API-key was either empty or did not match BankDroid's API-key");
		}

		return cur;
	}

	private static Cursor getUnmanagedTransactionsCursor(final Context ctx,
			final String accountId) throws IllegalAccessException {
		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ TRANSACTIONS_CAT + "/" + API_KEY + getApiKey(ctx));

		Log.d(TAG, "Picked Account with id " + accountId);

		final Cursor cur = ctx.getContentResolver().query(uri,
				TRANSACTIONS_PROJECTION, ACCOUNT_SELECTION_FILTER,
				new String[] { accountId }, null);

		if (cur == null) {
			throw new IllegalAccessException(
					"The API-key was either empty or did not match BankDroid's API-key");
		}

		return cur;
	}

	private static String getApiKey(final Context ctx) {
		// FIXME: Implement API-key functionality

		// final SharedPreferences prefs =
		// PreferenceManager.getDefaultSharedPreferences(ctx);

		// final String apiKey = prefs.getString(CONTENT_PROVIDER_API_KEY, "");

		// return apiKey;

		return "3FEBC";
	}
}
