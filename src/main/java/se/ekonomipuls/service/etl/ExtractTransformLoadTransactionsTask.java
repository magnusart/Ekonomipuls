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

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.PropertiesConstants.CONF_DEF_TAG;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContextScoped;
import roboguice.inject.InjectResource;
import roboguice.util.RoboAsyncTask;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.staging.StagingDbFacade;
import se.ekonomipuls.model.ExternalModelMapper;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.proxy.BankDroidTransaction;
import se.ekonomipuls.util.EkonomipulsUtil;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 15 mar 2011
 */
@ContextScoped
public class ExtractTransformLoadTransactionsTask extends RoboAsyncTask<Void> {

	private ProgressDialog dialog;

	@Inject
	private EkonomipulsUtil ekonomipulsUtil;

	@InjectResource(R.string.dialog_stage_import_message)
	protected String importMessage;

	final private StagingDbFacade stagingDbFacade;
	final private AnalyticsTransactionsDbFacade analyticsTransactionsDbFacade;

	@Inject
	public ExtractTransformLoadTransactionsTask(
			final StagingDbFacade stagingDbFacade,
			final AnalyticsTransactionsDbFacade analyticsTransactionsDbFacade) {
		this.stagingDbFacade = stagingDbFacade;
		this.analyticsTransactionsDbFacade = analyticsTransactionsDbFacade;
	}

	/** {@inheritDoc} */
	@Override
	protected void onPreExecute() {
		if (dialog instanceof ProgressDialog) {
			dialog.setMessage(importMessage);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
			dialog.show();
		}
	}

	/** {@inheritDoc} */
	@Override
	public Void call() {

		// Get staged transactions with external model
		final List<BankDroidTransaction> stagedTransactions = stagingDbFacade
				.getStagedTransactions();

		Log.d(TAG, "Loaded staged transactions: " + stagedTransactions);

		// Transform to internal model
		final List<Transaction> transactions = new ExternalModelMapper()
				.fromBdTransactionsToTransactions(stagedTransactions);

		// Transform and apply filters (separate this step later)
		final List<ApplyFilterTagAction> filteredTransactions = applyDefaultFilter(transactions);

		// Do filtering

		// Load the transactions into the Analytics table
		analyticsTransactionsDbFacade
				.insertTransactionsAssignTags(filteredTransactions);

		// Now purge the staging table
		stagingDbFacade.purgeStagingTable();

		// Reset the new transactions toggle
		ekonomipulsUtil.setNewTransactionStatus(false);

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
					.getDefaultSharedPreferences(context);

			final long tagId = pref.getLong(CONF_DEF_TAG, -1);

			assert (tagId != -1);

			t.setFiltered(true); // A filter have been applied.

			filteredTransactions.add(new ApplyFilterTagAction(t, tagId));

		}

		return filteredTransactions;
	}

	/**
	 * @param dialog
	 *            the dialog to set
	 * @return reference to self.
	 */
	public ExtractTransformLoadTransactionsTask setDialog(
			final ProgressDialog dialog) {
		this.dialog = dialog;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	protected void onFinally() {
		if ((dialog instanceof ProgressDialog) && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
