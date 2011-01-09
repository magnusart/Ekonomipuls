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
package com.magnusart.transtatistics;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.liato.bankdroid.provider.IBankTransactionsProvider;

/**
 * 
 * @author Magnus Andersson
 * @since 30 dec 2010
 */
public class TranStatistics extends Activity implements
		IBankTransactionsProvider {

	public static final String TAG = "TranStatistics";

	private Spinner spnrAccounts;
	private SimpleCursorAdapter bankAccountsAdapter;

	private ListView transactionsListView;
	private CursorAdapter transactionAdapter;

	private final OnItemSelectedListener readBankDroidContentProvider = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(final AdapterView<?> parent,
				final View view, final int position, final long id) {
			Log.d(TAG, "Preparing To Read Transactions from Provider");

			final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
					+ TRANSACTIONS_CAT);

			final Cursor acc_cursor = (Cursor) parent
					.getItemAtPosition(position);
			final int acc_id_idx = acc_cursor.getColumnIndex(ACC_ID);
			final String account_id = acc_cursor.getString(acc_id_idx);

			Log.d(TAG, "Picked Account with id " + account_id);

			final Cursor cur = managedQuery(uri, TRANSACTIONS_PROJECTION,
					ACCOUNT_SELECTION_FILTER, new String[] { account_id }, null);
			startManagingCursor(cur);

			// the desired columns to be bound
			final String[] columns = new String[] { TRANS_DATE, TRANS_DESC,
					TRANS_AMT };

			// the XML defined views which the data will be bound to
			final int[] to = new int[] { R.id.tDate, R.id.tDesc, R.id.tAmt };

			// Populate the list
			transactionAdapter = new SimpleCursorAdapter(view.getContext(),
					R.layout.transaction_list_item, cur, columns, to);

			transactionsListView.setAdapter(transactionAdapter);

		}

		@Override
		public void onNothingSelected(final AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	};

	/** {@inheritDoc} */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		spnrAccounts = (Spinner) findViewById(R.id.spnrAccounts);
		spnrAccounts.setOnItemSelectedListener(readBankDroidContentProvider);

		transactionsListView = (ListView) findViewById(R.id.transactionsListView);

		populateSpinner(spnrAccounts);
	}

	private void populateSpinner(final Spinner spnrAccounts) {
		Log.d(TAG, "Preparing To Read Bank Accounts from Provider");

		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ BANK_ACCOUNTS_CAT);

		final Cursor cur = managedQuery(uri, BANK_ACCOUNT_PROJECTION,
				NO_HIDDEN_ACCOUNTS_FILTER, null, null);

		startManagingCursor(cur);

		// the desired columns to be bound
		final String[] columns = new String[] { BANK_NAME, ACC_NAME };

		// the XML defined views which the data will be bound to
		final int[] to = new int[] { R.id.bankName, R.id.accountName };

		// Populate the list
		bankAccountsAdapter = new SimpleCursorAdapter(this,
				R.layout.bank_account_spinner, cur, columns, to);

		// Layout the dropdown view
		bankAccountsAdapter.setDropDownViewResource(R.layout.bank_account_item);

		spnrAccounts.setAdapter(bankAccountsAdapter);
	}
}