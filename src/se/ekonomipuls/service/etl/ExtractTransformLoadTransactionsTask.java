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
package se.ekonomipuls.service.etl;

import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.EkonomipulsHome;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.PropertiesConstants;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AnalyticsDbFacade;
import se.ekonomipuls.database.ExternalModelMapper;
import se.ekonomipuls.database.Transaction;
import se.ekonomipuls.database.staging.StagingDbFacade;
import se.ekonomipuls.proxy.BankDroidTransaction;
import se.ekonomipuls.util.EkonomipulsUtil;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 15 mar 2011
 */
public class ExtractTransformLoadTransactionsTask extends
		AsyncTask<Void, Void, Void> implements LogTag, PropertiesConstants {

	private final ProgressDialog dialog;
	private final Resources res;
	private final EkonomipulsHome parent;

	/**
	 * @param view
	 * 
	 */
	public ExtractTransformLoadTransactionsTask(final EkonomipulsHome parent) {
		this.parent = parent;
		dialog = new ProgressDialog(parent);
		res = parent.getResources();

	}

	/** {@inheritDoc} */
	@Override
	protected void onPreExecute() {
		dialog.setMessage(res.getText(R.string.dialog_stage_import_message));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();
	}

	/** {@inheritDoc} */
	@Override
	protected Void doInBackground(final Void... params) {

		try {
			Thread.sleep(3000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final List<BankDroidTransaction> stagedTransactions = StagingDbFacade
				.getStagedTransactions(parent);

		Log.d(TAG, "Loaded staged transactions: " + stagedTransactions);

		final List<Transaction> transactions = ExternalModelMapper
				.fromBdTransactionsToTransactions(stagedTransactions);

		final List<ApplyFilterTagAction> filteredTransactions = applyDefaultFilter(transactions);

		// Do filtering

		AnalyticsDbFacade.insertTransactionsAssignTags(parent,
				filteredTransactions);

		EkonomipulsUtil.setNewTransactionStatus(parent, false);

		return null;
	}

	/**
	 * @param transactions
	 * @return
	 */
	private List<ApplyFilterTagAction> applyDefaultFilter(
			final List<Transaction> transactions) {
		final List<ApplyFilterTagAction> filteredTransactions = new ArrayList<ApplyFilterTagAction>();

		for (final Transaction t : transactions) {
			Log.d(TAG, "Applying filter to Transaction " + t);

			// Only if no filters matched, set default tag.

			final SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(parent);
			final long tagId = pref.getLong(CONF_DEF_TAG, -1);

			assert (tagId != -1);

			t.setFiltered(true); // A filter have been applied.

			filteredTransactions.add(new ApplyFilterTagAction(t, tagId));

		}

		return filteredTransactions;
	}

	/** {@inheritDoc} */
	@Override
	protected void onPostExecute(final Void result) {
		parent.refreshView();

		if (this.dialog.isShowing()) {
			this.dialog.dismiss();
		}

	}
}
