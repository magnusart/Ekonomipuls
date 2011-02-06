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
package se.ekonomipuls.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.charts.AbstractChartView;
import se.ekonomipuls.charts.SeriesEntry;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class ChartSeriesAdaper extends ArrayAdapter<Transaction> implements
		LogTag {

	private final List<Transaction> transactions;
	private final int chartViewId;
	private final LayoutInflater inflater;
	private final int chartLayoutResourceId;

	/**
	 * @param context
	 * @param chartViewResourceId
	 * @param objects
	 */
	public ChartSeriesAdaper(final Context context, final int layoutResourceId,
								final int chartViewResourceId,
								final List<Transaction> objects) {
		super(context, 0, objects);
		this.chartLayoutResourceId = layoutResourceId;
		this.chartViewId = chartViewResourceId;
		this.transactions = objects;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/** {@inheritDoc} */
	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = inflater.inflate(chartLayoutResourceId, parent, false);
		} else {
			view = convertView;
		}

		populateChartSeries(view);

		return view;
	}

	private void populateChartSeries(final View view) {
		final AbstractChartView chart;

		try {
			if (chartViewId == 0) {
				Log.e(TAG, "You must supply a resource ID for a ChartView");
				throw new IllegalStateException(
						"You must supply a resource ID for a ChartView");
			} else {
				//  Otherwise, find the TextView field within the layout
				chart = (AbstractChartView) view.findViewById(chartViewId);
			}
		} catch (final ClassCastException e) {
			Log.e(TAG, "You must supply a resource ID for a ChartView");
			throw new IllegalStateException(
					"You must supply a resource ID for a ChartView");
		}

		final List<SeriesEntry> series = new ArrayList<SeriesEntry>();

		final Category cat1 = new Category(0, "Cat1", transactions.subList(0,
				transactions.size() / 4));

		final Category cat2 = new Category(0, "Cat2", transactions.subList(
				transactions.size() / 4, (transactions.size() / 4) * 2));

		final Category cat3 = new Category(0, "Cat3", transactions.subList(
				(transactions.size() / 4) * 2, (transactions.size() / 4) * 3));

		final Category cat4 = new Category(0, "Cat4", transactions.subList(
				(transactions.size() / 4) * 3, transactions.size()));

		BigDecimal total = new BigDecimal(0.0);

		series.add(new SeriesEntry(cat1, Color.CYAN));
		total = total.add(cat1.getSum());

		series.add(new SeriesEntry(cat2, Color.YELLOW));
		total = total.add(cat2.getSum());

		series.add(new SeriesEntry(cat3, Color.MAGENTA));
		total = total.add(cat3.getSum());

		series.add(new SeriesEntry(cat4, Color.LTGRAY));
		total = total.add(cat4.getSum());

		chart.setSeries(series);
		chart.setSeriesTotal(total);

	}
}
