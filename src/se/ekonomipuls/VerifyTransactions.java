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
package se.ekonomipuls;

import java.util.List;

import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.model.Transaction;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Magnus Andersson
 * @since 18 mar 2011
 */
public class VerifyTransactions extends Activity implements LogTag {

	private List<Transaction> unverifiedTransactions;
	private ArrayAdapter<Transaction> tmpAdapter;

	/** {@inheritDoc} */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify_transactions_layout);
	}

	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		super.onResume();
		populateVerificationList();
		tmpAdapter.notifyDataSetInvalidated();
	}

	/**
	 * 
	 */
	private void populateVerificationList() {
		unverifiedTransactions = AnalyticsTransactionsDbFacade
				.getUnverifiedTransactions(this);

		final ListView verifications = (ListView) findViewById(R.id.verification_list);

		tmpAdapter = new ArrayAdapter<Transaction>(this,
				R.layout.checkbox_verify_row, R.id.verificationNameText,
				unverifiedTransactions);

		verifications.setAdapter(tmpAdapter);
	}

	/**
	 * 
	 * @param v
	 */
	public void finishVerification(final View v) {
		finish();
	}

}
