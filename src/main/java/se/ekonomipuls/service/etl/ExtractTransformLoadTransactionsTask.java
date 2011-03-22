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

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.inject.Inject;
import roboguice.util.RoboAsyncTask;
import se.ekonomipuls.EkonomipulsHome;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.staging.StagingDbFacade;
import se.ekonomipuls.model.ExternalModelMapper;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.proxy.BankDroidTransaction;
import se.ekonomipuls.util.EkonomipulsUtil;

import java.util.ArrayList;
import java.util.List;

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.PropertiesConstants.CONF_DEF_TAG;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 15 mar 2011
 */

//TODO: Plenty of testing needed
public class ExtractTransformLoadTransactionsTask extends RoboAsyncTask<Void> {

    @Inject
	private ProgressDialog dialog;
	//private final Resources res;
	private EkonomipulsHome parent;
    @Inject
    private StagingDbFacade stagingDbFacade; 
    @Inject
    private ExternalModelMapper externalModelMapper;
    @Inject
    private EkonomipulsUtil ekonomipulsUtil;

	/**
	 * @param parent
	 * 
	 */
	public ExtractTransformLoadTransactionsTask(EkonomipulsHome parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	protected void onPreExecute() {
		dialog.setMessage(parent.getResources().getText(R.string.dialog_stage_import_message));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();
	}

	/** {@inheritDoc} */
	@Override
	public Void call() {

		// Get staged transacions with external model
		final List<BankDroidTransaction> stagedTransactions =
                stagingDbFacade.getStagedTransactions(context);

		Log.d(TAG, "Loaded staged transactions: " + stagedTransactions);

		// Transform to internal model
		final List<Transaction> transactions =
                externalModelMapper.fromBdTransactionsToTransactions(stagedTransactions);

		// Transform and apply filters (separate this step later)
		final List<ApplyFilterTagAction> filteredTransactions = applyDefaultFilter(transactions);

		// Do filtering

		// Load the transactions into the Analytics table
		AnalyticsTransactionsDbFacade.insertTransactionsAssignTags(context,
				filteredTransactions);

		// Now purge the staging table
		stagingDbFacade.purgeStagingTable(context);

		// Reset the new transactions toggle
		ekonomipulsUtil.setNewTransactionStatus(context, false);

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

			final SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(context);

			final long tagId = pref.getLong(CONF_DEF_TAG, -1);

			assert (tagId != -1);

			t.setFiltered(true); // A filter have been applied.

			filteredTransactions.add(new ApplyFilterTagAction(t, tagId));

		}

		return filteredTransactions;
	}

    @Override
    protected void onSuccess(Void result) {
        // do this in the UI thread if call() succeeds
    }

	/** {@inheritDoc} */
	@Override
	protected void onFinally() {
		parent.refreshView();

		if (this.dialog.isShowing()) {
			this.dialog.dismiss();
		}

	}
}
