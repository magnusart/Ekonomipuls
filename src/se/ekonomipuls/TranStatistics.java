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
package se.ekonomipuls;

import se.ekonomipuls.proxy.BankDroidProxy;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.liato.bankdroid.provider.IBankTransactionsProvider;
import com.magnusart.transtatistics.R;

/**
 * 
 * @author Magnus Andersson
 * @since 30 dec 2010
 */
public class TranStatistics extends Activity implements
		IBankTransactionsProvider, LogTag {

	private Spinner spnrAccounts;
	private ListView transactionsListView;

	private final OnItemSelectedListener readBankDroidContentProvider = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(final AdapterView<?> parent,
				final View view, final int position, final long id) {

			final String accountId = getAccountId(parent, position);

			populateTransactionsList(accountId);
		}

		@Override
		public void onNothingSelected(final AdapterView<?> parent) {

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
		Cursor cur;
		try {
			cur = BankDroidProxy.getManagedBankAccountsCursor(this);
		} catch (final IllegalAccessException e) {
			Toast.makeText(spnrAccounts.getContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
			return;
		}

		final SimpleCursorAdapter bankAccountsAdapter = setupSpinnerAdapter(cur);

		bankAccountsAdapter.setDropDownViewResource(R.layout.bank_account_item);

		spnrAccounts.setAdapter(bankAccountsAdapter);
	}

	private void populateTransactionsList(final String accountId) {
		Cursor cur;
		try {
			cur = BankDroidProxy.getManagedTransactionsCursor(this, accountId);
		} catch (final IllegalAccessException e) {
			Toast.makeText(spnrAccounts.getContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
			return;
		}

		final SimpleCursorAdapter transactionsAdapter = setupListAdapter(
				this.getBaseContext(), cur);

		transactionsListView.setAdapter(transactionsAdapter);
	}

	private SimpleCursorAdapter setupSpinnerAdapter(final Cursor cursor) {
		// the desired columns to be bound
		final String[] columns = new String[] { BANK_NAME, ACC_NAME };

		// the XML defined views which the data will be bound to
		final int[] to = new int[] { R.id.bankName, R.id.accountName };

		// Populate the list
		return new SimpleCursorAdapter(this, R.layout.bank_account_spinner,
				cursor, columns, to);

	}

	private SimpleCursorAdapter setupListAdapter(final Context c,
			final Cursor cur) {
		// the desired columns to be bound
		final String[] columns = new String[] { TRANS_DATE, TRANS_DESC,
				TRANS_AMT };

		// the XML defined views which the data will be bound to
		final int[] to = new int[] { R.id.tDate, R.id.tDesc, R.id.tAmt };

		// Populate the list
		return new SimpleCursorAdapter(c, R.layout.transaction_list_item, cur,
				columns, to);
	}

	private String getAccountId(final AdapterView<?> parent, final int position) {

		final Cursor acc_cursor = (Cursor) parent.getItemAtPosition(position);
		final int acc_id_idx = acc_cursor.getColumnIndex(ACC_ID);
		return acc_cursor.getString(acc_id_idx);
	}

}