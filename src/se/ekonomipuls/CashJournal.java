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

import se.ekonomipuls.adapter.EkonomipulsDbAdapter;
import se.ekonomipuls.adapter.LegendAdapter;
import se.ekonomipuls.adapter.Transaction;
import se.ekonomipuls.charts.BudgetPieChartFactory;
import se.ekonomipuls.charts.PieChartConfiguration;
import se.ekonomipuls.charts.PieHandler;
import se.ekonomipuls.charts.Slice;
import se.ekonomipuls.util.ColorUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ListView;

/**
 * @author Magnus Andersson
 * @since 9 jan 2011
 */
public class CashJournal extends Activity implements LogTag {

	private static final String STROKE_COLOR = "#FFF";
	private static final int STROKE_WIDTH = 1;
	private static final double PIE_RADIUS = 0.9D;
	private static final String PIE_HTML = "file:///android_asset/charts/pie.html";
	private static final String HANDLER_NAME = "budgetHandler";
	private static final String REMAINING_COLOR = "#b3b3b3";
	private static final String ACCOUNT_ID = "1_1";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cash_journal);

		removeGradientBanding();

		setUpBudgetPieChart(ACCOUNT_ID);

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

	private void setUpBudgetPieChart(final String accountId) {
		final WebView budget = (WebView) findViewById(R.id.pieChart);

		final List<Transaction> transactions = EkonomipulsDbAdapter
				.getTransactions(getBaseContext(), accountId);

		final Slice[] slices = new Slice[transactions.size()];

		final ColorUtil color = new ColorUtil();

		int i = 0;
		for (final Transaction trans : transactions) {
			final Slice s = new Slice((double) trans.getAmount().abs()
					.floatValue(), trans.getDescription(), color.getNextColor());
			slices[i] = s;
			i++;
		}

		final PieChartConfiguration configuration = BudgetPieChartFactory
				.getConfiguration(STROKE_COLOR, STROKE_WIDTH, PIE_RADIUS);
		final PieHandler budgetHandler = new PieHandler(budget, configuration,
				slices);

		budget.getSettings().setJavaScriptEnabled(true);
		budget.addJavascriptInterface(budgetHandler, HANDLER_NAME);
		budget.setScrollContainer(false);
		budget.setBackgroundColor(0);
		budget.loadUrl(PIE_HTML);

	}

	private void populateLegendList(final String accountId) {
		final ListView legendList = (ListView) findViewById(R.id.legendList);
		final Context ctx = getBaseContext();
		final List<Transaction> transactions = EkonomipulsDbAdapter
				.getTransactions(ctx, accountId);

		final LegendAdapter adapter = new LegendAdapter(ctx,
				R.layout.legend_row, transactions);

		legendList.setAdapter(adapter);
	}

}
