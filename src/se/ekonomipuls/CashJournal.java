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

import se.ekonomipuls.charting.BudgetPieChartFactory;
import se.ekonomipuls.charting.PieChartConfiguration;
import se.ekonomipuls.charting.PieHandler;
import se.ekonomipuls.charting.Slice;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * @author Magnus Andersson
 * @since 9 jan 2011
 */
public class CashJournal extends Activity implements LogTag {

	private static final String STROKE_COLOR = "#FFF";
	private static final int STROKE_WIDTH = 1;
	private static final double PIE_RADIUS = 0.7;
	private static final double LABEL_RADIUS = 1D;
	private static final String PIE_HTML = "file:///android_asset/charts/pie.html";
	private static final String HANDLER_NAME = "budgetHandler";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.budget_overview);

		setUpBudgetPieChart();

	}

	private void setUpBudgetPieChart() {
		final WebView budget = (WebView) findViewById(R.id.budgetWebview);

		final Slice[] slices = { new Slice(15000D, "Balance", "#31b320"),
				new Slice(5000D, "Savings", "#2078b3"),
				new Slice(12000D, "Expenses", "#b32020") };

		final PieChartConfiguration configuration = BudgetPieChartFactory
				.getConfiguration(STROKE_COLOR, STROKE_WIDTH, PIE_RADIUS,
						LABEL_RADIUS);
		final PieHandler budgetHandler = new PieHandler(budget, configuration,
				slices);

		budget.getSettings().setJavaScriptEnabled(true);
		budget.addJavascriptInterface(budgetHandler, HANDLER_NAME);
		budget.setScrollContainer(false);
		budget.setBackgroundColor(0);
		budget.loadUrl(PIE_HTML);

	}
}
