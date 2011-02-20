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
public class EkonomipulsHome extends Activity implements LogTag {

	private LegendAdapter legendAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		GuiUtil.removeGradientBanding(getWindow());

		populateData();
	}

	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		super.onResume();

		legendAdapter.notifyDataSetInvalidated();
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
		final PieChartView pieChart = (PieChartView) findViewById(R.id.pieChart);
		final ListView legendList = (ListView) findViewById(R.id.legendList);

		final List<Transaction> transactions = DbFacade
				.getAllTransactions(this);

		populateSeriesEntries(transactions, pieChart);
		populateLegendList(legendList, pieChart.getSeries(),
				pieChart.getTotalAmt());

		pieChart.requestLayout();

	}

	private void populateSeriesEntries(final List<Transaction> transactions,
			final PieChartView pieChart) {

		final long reportId = GuiUtil.getEconomicOverviewId(this);

		assert (reportId != -1);

		final List<Category> categories = DbFacade.getCategoriesByReport(this,
				reportId);

		final ArrayList<SeriesEntry> series = new ArrayList<SeriesEntry>();
		BigDecimal total = new BigDecimal(0.0);

		// Get the transactions given this category's tags
		for (final Category cat : categories) {

			final List<Transaction> catTransactions = DbFacade
					.getTransactionsByCategory(this, cat);

			final SeriesEntry ser = new SeriesEntry(cat, catTransactions);

			total = total.add(ser.getSum());
			series.add(ser);
		}

		pieChart.setSeries(series);
		pieChart.setSeriesTotal(total);

		if (total.longValue() == 0) {
			pieChart.setVisibility(View.GONE);
		}
	}

	private void populateLegendList(final ListView legendList,
			final List<SeriesEntry> series, final BigDecimal total) {

		if (total.longValue() == 0) {
			legendList.setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.noCategories))
					.setVisibility(View.GONE);
		}

		legendAdapter = new LegendAdapter(this, R.layout.legend_row, series,
				total);

		legendList.setAdapter(legendAdapter);

	}

}
