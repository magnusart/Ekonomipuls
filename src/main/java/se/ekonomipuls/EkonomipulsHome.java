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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import se.ekonomipuls.database.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.debug.BackupDatabaseUtil;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.service.async.BankDroidImportIntentService;
import se.ekonomipuls.service.async.ExtractTransformLoadAsyncTask;
import se.ekonomipuls.views.adapter.LegendAdapter;
import se.ekonomipuls.views.charts.PieChartView;
import se.ekonomipuls.views.charts.SeriesEntry;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.liato.bankdroid.provider.IBankTransactionsProvider;

/**
 * @author Magnus Andersson
 * @since 9 jan 2011
 */
public class EkonomipulsHome extends RoboActivity implements LogTag {
	String DEBUG_DB_BACKUP_ERROR_MESSAGE = "Got an error when trying to create a backup of database.";

	@Inject
	private EkonomipulsUtil util;

	@Inject
	private AnalyticsCategoriesDbFacade analyticsCategoriesDbFacade;

	@Inject
	private AnalyticsTransactionsDbFacade analyticsTransactionsDbFacade;

	@Inject
	private ExtractTransformLoadAsyncTask etlService;

	@Inject
	private BackupDatabaseUtil debugUtil;

	private LegendAdapter legendAdapter;

	@InjectView(R.id.newTransactions)
	private TextView newTransactions;
	@InjectView(R.id.pieChart)
	private PieChartView pieChart;
	@InjectView(R.id.legendList)
	private ListView legendList;
	@InjectView(R.id.noCategories)
	private TextView noData;

	@InjectResource(R.string.app_name)
	private String appName;

	@InjectResource(R.string.action_pair_application)
	private String pairApplicationAction;
	@InjectResource(R.string.extras_key_pair_app_name)
	private String pairApplicationNameKey;

	@InjectResource(R.string.action_notify_home_screen)
	private String notifyHomeScreenAction;
	@InjectResource(R.string.extras_key_home_screen_task)
	private String homeScreenTaskKey;

	@InjectResource(R.integer.request_code_default)
	private int defaultRequestCode;

	private IntentFilter filter = null;;

	private final BroadcastReceiver homeScreenTaskListener = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			final String task = intent.getStringExtra(homeScreenTaskKey);
			final HomeScreenTask action = HomeScreenTask.valueOf(task);

			switch (action) {
			case UPDATE_TRANSACTIONS_NOTIFICATION:
				showNewTransactionsNotification();
				break;
			}
		}
	};

	public void refreshView() {
		showNewTransactionsNotification();

		populateData(); // TODO: Fix so that PieChart has an adapter.

		legendAdapter.notifyDataSetInvalidated();

		pieChart.invalidate();
	}

	/**
	 * The onClick even for you have new transactions notification.
	 * 
	 * @param view
	 */
	public void importStagingTransactions(final View view) {
		final ProgressDialog dialog = new ProgressDialog(this);
		etlService.setDialog(dialog);
		etlService.setCallback(new Callback() {
			@Override
			public boolean handleMessage(final Message msg) {

				verifyTransactions();

				return true;
			}
		});
		etlService.execute();
	}

	public void verifyTransactions() {
		final Intent intent = new Intent(this, VerifyTransactions.class);
		this.startActivityForResult(intent, defaultRequestCode);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home); // Injection doesn't happen until you
										// call setContentView()
		util.removeGradientBanding(getWindow());
	}

	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		super.onResume();

		registerTaskRecievier();

		// Make sure BankDroi is available and installed
		if (util.isIntentAvailable(this, pairApplicationAction)) {
			// If we are not paired with BankDroid, make sure we get paired and
			// retrieve a API-key, also make sure that BankDroid is installed!
			if (!util.isPairedBankDroid()) {
				pairWithBankDroid();
			}
		} else {
			// Otherwise prompt the user with this information.
			// FIXME Implement info screen
		}

		refreshView();

	}

	/** {@inheritDoc} */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterTaskReciver();
	}

	private void registerTaskRecievier() {
		if (filter == null) {
			filter = new IntentFilter(notifyHomeScreenAction);
		}
		registerReceiver(homeScreenTaskListener, filter);
	}

	private void unregisterTaskReciver() {
		unregisterReceiver(homeScreenTaskListener);
	};

	private void fullBankDroidImport() {
		Log.v(TAG, "Setting up import service");

		final Intent importService = new Intent(this,
				BankDroidImportIntentService.class);

		importService
				.putExtra(BankDroidImportIntentService.IMPORT_ACTION, BankDroidImportIntentService.ImportAction.ALL_AVAILABLE_ACCOUNTS
						.toString());

		Log.v(TAG, "Handing over to import service");
		// Hand over to service queue since broadcast receivers have a very
		// short timeout.
		startService(importService);

		Log.v(TAG, "Handed over to import service");
	}

	/** {@inheritDoc} */
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode == RESULT_OK) {
			final String apiKey = data
					.getStringExtra(IBankTransactionsProvider.API_KEY);
			Log.d(TAG, "User accepted pairing. Got an API key back: " + apiKey);

			util.setApiKey(apiKey);
			util.setPairedBankDroid(true);

			fullBankDroidImport();

		} else if (resultCode == RESULT_CANCELED) {
			Log.d(TAG, "User did not accept pairing.");
		}
	}

	private void showNewTransactionsNotification() {
		if (util.getNewTransactionsStatus()) {
			newTransactions.setVisibility(View.VISIBLE);
		} else {
			newTransactions.setVisibility(View.GONE);
		}
		newTransactions.invalidate();
	}

	/** {@inheritDoc} */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.debug_menu, menu);

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Intent intent = null;

		switch (item.getItemId()) {
		case (R.id.debug_backup_db):
			try {
				debugUtil.doBackup();
			} catch (final FileNotFoundException e) {
				// FIXME Throw typed Exceptions!
				throw new RuntimeException(DEBUG_DB_BACKUP_ERROR_MESSAGE, e);
			} catch (final IOException e) {
				throw new RuntimeException(DEBUG_DB_BACKUP_ERROR_MESSAGE, e);
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		// this.startActivity(intent);
		return true;
	}

	private void pairWithBankDroid() {

		Log.d(TAG, "Attempting to pair with BankDroid");
		final Intent intent = new Intent(pairApplicationAction);
		intent.putExtra(pairApplicationNameKey, appName);
		this.startActivityForResult(intent, defaultRequestCode);
	}

	private void populateData() {

		populateSeriesEntries(pieChart);

		populateLegendList(legendList, pieChart.getSeries(), pieChart.getTotalAmt());

		// pieChart.requestLayout();

	}

	private void populateSeriesEntries(final PieChartView pieChart) {

		final long reportId = util.getEconomicOverviewId();

		assert (reportId != -1);

		List<Category> categories;

		categories = analyticsCategoriesDbFacade
				.getCategoriesByReport(reportId);

		final ArrayList<SeriesEntry> series = new ArrayList<SeriesEntry>();
		BigDecimal total = new BigDecimal(0.0);

		// Get the transactions given this category's tags
		for (final Category cat : categories) {

			final List<Transaction> catTransactions = analyticsTransactionsDbFacade
					.getTransactionsByCategory(cat);

			final SeriesEntry ser = new SeriesEntry(cat, catTransactions);

			total = total.add(ser.getSum());
			series.add(ser);
		}

		pieChart.setSeries(series);
		pieChart.setSeriesTotal(total);

	}

	private void populateLegendList(final ListView legendList,
			final List<SeriesEntry> series, final BigDecimal total) {

		if (total.longValue() == 0) {
			legendList.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
		} else {
			legendList.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
		}

		legendAdapter = new LegendAdapter(this, R.layout.legend_row, series,
				total, util);

		legendList.setAdapter(legendAdapter);

	}

}
