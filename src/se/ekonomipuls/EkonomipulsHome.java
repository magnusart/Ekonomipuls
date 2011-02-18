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

import se.ekonomipuls.adapter.LegendAdapter;
import se.ekonomipuls.charts.PieChartView;
import se.ekonomipuls.charts.SeriesEntry;
import se.ekonomipuls.database.Category;
import se.ekonomipuls.database.DbFacade;
import se.ekonomipuls.database.Transaction;
import se.ekonomipuls.util.GuiUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
public class EkonomipulsHome extends Activity implements PropertiesConstants,
		LogTag {

	private static final String ACCOUNT_ID = "1_1";
	private ArrayList<SeriesEntry> series;
	private BigDecimal total;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		GuiUtil.removeGradientBanding(getWindow());

		populatePieChart(ACCOUNT_ID);
		populateLegendList(ACCOUNT_ID);
	}

	/** {@inheritDoc} */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.categories_filter_menu, menu);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case (R.id.cash_journal_menu_item):
				navigateCashJournal();
				return true;
			case (R.id.filters_menu_item):
				navigateFilters();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void navigateCashJournal() {
		final Intent intent = new Intent(this, Reports.class);
		this.startActivity(intent);
	}

	private void navigateFilters() {
		final Intent intent = new Intent(this, Filters.class);
		this.startActivity(intent);
	}

	private void populatePieChart(final String accountId) {
		final PieChartView pieChart = (PieChartView) findViewById(R.id.pieChart);

		final List<Transaction> transactions = DbFacade
				.getTransactionsByAccount(this, accountId);

		populateSeriesEntries(transactions, pieChart);

	}

	/**
	 * @param transactions
	 * @param pieChart
	 */
	private void populateSeriesEntries(final List<Transaction> transactions,
			final PieChartView pieChart) {

		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		final long reportId = pref.getLong(ECONOMIC_OVERVIEW_REPORT_ID, -1);

		assert (reportId != -1);

		final List<Category> categories = DbFacade.getCategoriesByReport(this,
				reportId);

		series = new ArrayList<SeriesEntry>();
		total = new BigDecimal(0.0);

		// Get the transactions given this category's tags
		for (final Category cat : categories) {

			final List<Transaction> catTransactions = DbFacade
					.getTransactionsByCategory(this, cat);

			final SeriesEntry ser = new SeriesEntry(cat, catTransactions,
					Color.MAGENTA);

			total = total.add(ser.getSum());
			series.add(ser);
		}

		pieChart.setSeries(series);
		pieChart.setSeriesTotal(total);

	}

	private void populateLegendList(final String accountId) {

		final ListView legendList = (ListView) findViewById(R.id.legendList);
		if (series.size() == 0) {
			legendList.setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.noCategories))
					.setVisibility(View.GONE);
		}

		final LegendAdapter adapter = new LegendAdapter(this,
				R.layout.legend_row, series, total);

		legendList.setAdapter(adapter);
	}

}
