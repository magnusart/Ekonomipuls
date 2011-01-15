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
package com.magnusart.transtatistics.proxy;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.liato.bankdroid.provider.IBankTransactionsProvider;
import com.magnusart.transtatistics.Tag;

/**
 * @author Magnus Andersson
 * @since 15 jan 2011
 */
public class BankDroidProxy implements IBankTransactionsProvider, Tag {

	private BankDroidProxy() {
		// Private Constructor
	}

	// TODO: API_KEY: Replace this with a user property
	private static final String THE_KEY = "THE_SECRET";

	/**
	 * 
	 * @param a
	 *            The activity that should manage the cursor.
	 * @return a managed cursor to the Bank Accounts Provider.
	 */
	public static Cursor getManagedBankAccountsCursor(final Activity a) {
		Log.d(TAG, "Preparing To Read Bank Accounts from Provider");

		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ BANK_ACCOUNTS_CAT + "/" + API_KEY + THE_KEY);

		final Cursor cur = a.managedQuery(uri, BANK_ACCOUNT_PROJECTION,
				NO_HIDDEN_ACCOUNTS_FILTER, null, null);

		a.startManagingCursor(cur);

		return cur;
	}

	public static Cursor getManagedTransactionsCursor(final Activity a,
			final String accountId) {
		Log.d(TAG, "Preparing To Read Transactions from Provider");

		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ TRANSACTIONS_CAT + "/" + API_KEY + THE_KEY);

		Log.d(TAG, "Picked Account with id " + accountId);

		final Cursor cur = a.managedQuery(uri, TRANSACTIONS_PROJECTION,
				ACCOUNT_SELECTION_FILTER, new String[] { accountId }, null);

		a.startManagingCursor(cur);

		return cur;
	}
}
