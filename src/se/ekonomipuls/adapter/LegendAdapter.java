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

import se.ekonomipuls.R;
import se.ekonomipuls.charts.SeriesEntry;
import se.ekonomipuls.util.GraphUtil;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class LegendAdapter extends ArrayAdapter<SeriesEntry> {

	private final List<SeriesEntry> objects;
	private final int layoutViewResourceId;
	private final LayoutInflater inflater;
	private final Context context;
	private final BigDecimal total;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param series
	 * @param total
	 */
	public LegendAdapter(final Context context, final int layoutViewResourceId,
							final ArrayList<SeriesEntry> series,
							final BigDecimal total) {
		super(context, layoutViewResourceId, series);
		this.context = context;
		this.layoutViewResourceId = layoutViewResourceId;
		this.objects = series;
		this.total = total;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/** {@inheritDoc} */
	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = inflater.inflate(layoutViewResourceId, parent, false);
		} else {
			view = convertView;
		}

		final SeriesEntry entry = getItem(position);

		final GradientDrawable color = (GradientDrawable) view.findViewById(
				R.id.colorShape).getBackground();

		color.setColor(GraphUtil.getDarkColor(entry.getBaseColor(),
				entry.isSelected()));

		final TextView categoryName = (TextView) view
				.findViewById(R.id.categoryNameText);
		final TextView line2Text = (TextView) view.findViewById(R.id.line2Text);

		final Category cat = entry.getCategory();

		categoryName.setText(cat.getName());

		final int percentage = GraphUtil.getPercentage(cat.getSum()
				.floatValue(), total.floatValue());
		line2Text.setText(percentage + "%, "
				+ Math.round(cat.getSum().floatValue()) + " SEK, "
				+ cat.getNumTransactions() + " Transaktioner");

		//		date.setText(trans.getDate());
		//		desc.setText(trans.getDescription());
		//		amt.setText(trans.getAmount().toPlainString());

		return view;
	}
}
