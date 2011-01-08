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
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.liato.bankdroid.provider.IBankTransactionsProvider;

/**
 * 
 * @author Magnus Andersson
 * @since 30 dec 2010
 */
public class TranStatistics extends Activity implements
		IBankTransactionsProvider {

	public static final String TAG = "TranStatistics";
	private CursorAdapter transactionAdapter;

	private final OnClickListener readBankDroidContentProvider = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			Log.d(TAG, "Preparing To Read from Provider");
			final ContentResolver cr = getContentResolver();
			final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
					+ TRANSACTIONS_CAT + "/");

			final Cursor cur = managedQuery(uri, TRANSACTIONS_PROJECTION, null,
					null, null);
			startManagingCursor(cur);

			final int t_id_idx = cur.getColumnIndexOrThrow(TRANS_ID);
			final int t_date_idx = cur.getColumnIndexOrThrow(TRANS_DATE);
			final int t_desc_idx = cur.getColumnIndexOrThrow(TRANS_DESC);
			final int t_amt_idx = cur.getColumnIndexOrThrow(TRANS_AMT);
			final int t_cur_idx = cur.getColumnIndexOrThrow(TRANS_CUR);

			// the desired columns to be bound
			final String[] columns = new String[] { TRANS_DATE, TRANS_DESC,
					TRANS_AMT };

			// the XML defined views which the data will be bound to
			final int[] to = new int[] { R.id.tDate, R.id.tDesc, R.id.tAmt };

			// Populate the list
			transactionAdapter = new SimpleCursorAdapter(v.getContext(),
					R.layout.transaction_list_item, cur, columns, to);

			transactionsListView.setAdapter(transactionAdapter);

			/*
			 * if (cur.moveToFirst()) { while (cur.moveToNext()) { Log.d(TAG,
			 * "T: " + cur.getString(t_id_idx) + ", " +
			 * cur.getString(t_date_idx) + ", " + cur.getString(t_desc_idx) +
			 * ", " + cur.getString(t_amt_idx) + ", " + cur.getString(t_cur_idx)
			 * + "."); } }
			 */
		}
	};
	private ListView transactionsListView;

	/** {@inheritDoc} */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Button btnBroadcast = (Button) findViewById(R.id.btnFetchFromProvider);
		btnBroadcast.setOnClickListener(readBankDroidContentProvider);

		transactionsListView = (ListView) findViewById(R.id.transactionsListView);

	}
}