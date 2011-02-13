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

import se.ekonomipuls.adapter.Category;
import se.ekonomipuls.adapter.EkonomipulsDbAdapter;
import se.ekonomipuls.adapter.LegendAdapter;
import se.ekonomipuls.adapter.Transaction;
import se.ekonomipuls.charts.PieChartView;
import se.ekonomipuls.charts.SeriesEntry;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Magnus Andersson
 * @since 9 jan 2011
 */
public class CashJournal extends Activity implements LogTag {

	private static final String ACCOUNT_ID = "1_1";
	private ArrayList<SeriesEntry> series;
	private BigDecimal total;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cash_journal);

		removeGradientBanding();

		//setUpReportPieChart(ACCOUNT_ID);

		populatePieChart(ACCOUNT_ID);
		populateLegendList(ACCOUNT_ID);
	}

	/**
	 * Some Voodoo that prevents color banding on the background
	 * gradient.
	 * http://stuffthathappens.com/blog/2010/06/04/android-color-banding/
	 */
	private void removeGradientBanding() {
		final Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	private void populatePieChart(final String accountId) {
		final PieChartView pieChart = (PieChartView) findViewById(R.id.pieChart);

		final Context ctx = getBaseContext();
		final List<Transaction> transactions = EkonomipulsDbAdapter
				.getTransactions(ctx, accountId);

		//		final ChartSeriesAdaper chartAdapter = new ChartSeriesAdaper(ctx,
		//				R.layout.cash_journal, R.id.pieChart, transactions);

		tmpSetSeriesEntries(transactions, pieChart);

		//pieChart.setAdapter(chartAdapter);
	}

	/**
	 * @param transactions
	 * @param pieChart
	 */
	private void tmpSetSeriesEntries(final List<Transaction> transactions,
			final PieChartView pieChart) {
		series = new ArrayList<SeriesEntry>();

		final Category cat1 = new Category(0, "Mat", transactions.subList(0,
				transactions.size() / 6));

		final Category cat2 = new Category(0, "Ospecificierat",
				transactions.subList(transactions.size() / 6,
						(transactions.size() / 6) * 2));

		final Category cat3 = new Category(0, "Hyra", transactions.subList(
				(transactions.size() / 6) * 2, (transactions.size() / 6) * 3));

		final Category cat4 = new Category(0, "Kläder", transactions.subList(
				(transactions.size() / 6) * 3, (transactions.size() / 6) * 4));

		final Category cat5 = new Category(0, "Fest", transactions.subList(
				(transactions.size() / 6) * 4, (transactions.size() / 6) * 5));

		final Category cat6 = new Category(0, "El", transactions.subList(
				(transactions.size() / 6) * 5, transactions.size()));

		total = new BigDecimal(0.0);

		series.add(new SeriesEntry(cat1, Color.CYAN).setSelected(true)); //Color.CYAN
		total = total.add(cat1.getSum());

		series.add(new SeriesEntry(cat2, Color.GRAY).setSelected(true));
		total = total.add(cat2.getSum());

		series.add(new SeriesEntry(cat3, Color.RED).setSelected(true));
		total = total.add(cat3.getSum());

		series.add(new SeriesEntry(cat4, Color.YELLOW).setSelected(true));
		total = total.add(cat4.getSum());

		series.add(new SeriesEntry(cat5, Color.GREEN).setSelected(true));
		total = total.add(cat5.getSum());

		series.add(new SeriesEntry(cat6, Color.BLUE).setSelected(true));
		total = total.add(cat6.getSum());

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

		final Context ctx = getBaseContext();

		final LegendAdapter adapter = new LegendAdapter(ctx,
				R.layout.legend_row, series, total);

		legendList.setAdapter(adapter);
	}

}
