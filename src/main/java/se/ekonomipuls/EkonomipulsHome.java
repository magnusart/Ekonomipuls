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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import se.ekonomipuls.database.analytics.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.service.etl.ExtractTransformLoadTransactionsTask;
import se.ekonomipuls.util.EkonomipulsUtil;
import se.ekonomipuls.views.adapter.LegendAdapter;
import se.ekonomipuls.views.charts.PieChartView;
import se.ekonomipuls.views.charts.SeriesEntry;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Magnus Andersson
 * @since 9 jan 2011
 */
public class EkonomipulsHome extends RoboActivity implements LogTag {

    @Inject
    private EkonomipulsUtil ekonomipulsUtil;


	private static final int VERIFY_TRANSACTIONS = 0;

	private LegendAdapter legendAdapter;
	private TextView newTransactions;
	private PieChartView pieChart;
	private ListView legendList;
	private TextView noData;

	public void refreshView() {
		showNewTransactionsNotification();

		populateData(); // TODO: Fix so that PieChart has an adapter.

        legendAdapter.notifyDataSetInvalidated();

		pieChart.invalidate();
	}

	/**
	 * The onClick even for you have new transactions notification.
	 * 
	 * @param v
	 */
	public void importStagingTransactions(final View v) {
		new ExtractTransformLoadTransactionsTask(this).execute();
		final Intent intent = new Intent(this, VerifyTransactions.class);
		this.startActivityForResult(intent, VERIFY_TRANSACTIONS);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		newTransactions = (TextView) findViewById(R.id.newTransactions);
		pieChart = (PieChartView) findViewById(R.id.pieChart);
		legendList = (ListView) findViewById(R.id.legendList);
		noData = (TextView) findViewById(R.id.noCategories);

		ekonomipulsUtil.removeGradientBanding(getWindow());
	}

	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		super.onResume();
		refreshView();

	}

	private void showNewTransactionsNotification() {
		if (ekonomipulsUtil.getNewTransactionsStatus(this)) {
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
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Intent intent = null;

		switch (item.getItemId()) {
			case (R.id.settings_item):
				intent = new Intent(this, OverviewSettings.class);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}

		this.startActivity(intent);
		return true;
	}

	private void populateData() {

		populateSeriesEntries(pieChart);

		populateLegendList(legendList, pieChart.getSeries(),
				pieChart.getTotalAmt());

		//		pieChart.requestLayout();

	}

	private void populateSeriesEntries(final PieChartView pieChart) {

		final long reportId = ekonomipulsUtil.getEconomicOverviewId(this);

		assert (reportId != -1);

		List<Category> categories;

		categories = AnalyticsCategoriesDbFacade.getCategoriesByReport(this,
				reportId);

		final ArrayList<SeriesEntry> series = new ArrayList<SeriesEntry>();
		BigDecimal total = new BigDecimal(0.0);

		// Get the transactions given this category's tags
		for (final Category cat : categories) {

			final List<Transaction> catTransactions = AnalyticsTransactionsDbFacade
					.getTransactionsByCategory(this, cat);

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
				total);

		legendList.setAdapter(legendAdapter);

	}

}
