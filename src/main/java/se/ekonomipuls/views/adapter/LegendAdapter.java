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
package se.ekonomipuls.views.adapter;

import java.math.BigDecimal;
import java.util.List;

import roboguice.adapter.IterableAdapter;
import se.ekonomipuls.R;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.views.charts.SeriesEntry;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class LegendAdapter extends IterableAdapter<SeriesEntry> {

	@Inject
	private final EkonomipulsUtil util;

	/**
	 * @author Magnus Andersson
	 * @since 19 feb 2011
	 */
	private static class ViewHolder {
		GradientDrawable color;
		TextView categoryName;
		TextView line2Text;
	}

	private final int layoutViewResourceId;
	private final LayoutInflater inflater;
	private final BigDecimal total;

	/**
	 * @param context
	 * @param layoutViewResourceId
	 * @param series
	 * @param total
	 */
	public LegendAdapter(final Context context, final int layoutViewResourceId,
			final List<SeriesEntry> series, final BigDecimal total,
			final EkonomipulsUtil util) {
		super(context, layoutViewResourceId, series);
		this.layoutViewResourceId = layoutViewResourceId;
		this.total = total;
		this.util = util;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/** {@inheritDoc} */
	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(layoutViewResourceId, parent, false);
			holder = new ViewHolder();

			holder.color = (GradientDrawable) convertView
					.findViewById(R.id.colorShape).getBackground();

			holder.categoryName = (TextView) convertView
					.findViewById(R.id.categoryNameText);
			holder.line2Text = (TextView) convertView
					.findViewById(R.id.line2Text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final SeriesEntry entry = getItem(position);

		holder.color.setColor(util.getDarkColor(entry.getBaseColor(), entry
				.isSelected()));

		final Category cat = entry.getCategory();

		holder.categoryName.setText(cat.getName());

		final int percentage = util
				.getPercentage(entry.getSum().floatValue(), total.floatValue());

		holder.line2Text.setText(percentage + "%, "
				+ Math.round(entry.getSum().floatValue()) + " SEK, "
				+ entry.getNumTransactions() + " Transaktioner");

		return convertView;
	}
}
