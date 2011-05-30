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

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.views.adapter.VerifyTransactionsAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 18 mar 2011
 */
public class VerifyTransactions extends RoboActivity {

	@Inject
	private AnalyticsTransactionsDbFacade analyticsTransactionsDbFacade;

	@InjectView(R.id.verification_list)
	private ListView verifications;

	private VerifyTransactionsAdapter verifyAdapter;

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
		verifyAdapter.notifyDataSetInvalidated();
	}

	/**
	 * 
	 */
	private void populateVerificationList() {
		final List<Transaction> unverifiedTransactions = analyticsTransactionsDbFacade
				.getUnverifiedTransactions(); // TODO Fetch tags as well.

		verifyAdapter = new VerifyTransactionsAdapter(this,
				R.layout.checkbox_verify_row, unverifiedTransactions);

		verifications.setAdapter(verifyAdapter);
	}

	/**
	 * 
	 * @param v
	 */
	public void finishVerification(final View v) {
		finish();
	}

}
