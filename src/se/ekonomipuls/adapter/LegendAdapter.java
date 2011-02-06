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

import java.util.List;

import se.ekonomipuls.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class LegendAdapter extends ArrayAdapter<Transaction> {

	private final List<Transaction> objects;
	private final int layoutViewResourceId;
	private final LayoutInflater inflater;
	private final Context context;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public LegendAdapter(final Context context, final int layoutViewResourceId,
							final List<Transaction> objects) {
		super(context, layoutViewResourceId, objects);
		this.context = context;
		this.layoutViewResourceId = layoutViewResourceId;
		// TODO Auto-generated constructor stub
		this.objects = objects;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/** {@inheritDoc} */
	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View view;
		final LinearLayout layout;

		if (convertView == null) {
			view = inflater.inflate(layoutViewResourceId, parent, false);
		} else {
			view = convertView;
		}

		final Transaction trans = getItem(position);

		final TextView date = (TextView) view.findViewById(R.id.tDate);
		final TextView desc = (TextView) view.findViewById(R.id.tDesc);
		final TextView amt = (TextView) view.findViewById(R.id.tAmt);

		date.setText(trans.getDate());
		desc.setText(trans.getDescription());
		amt.setText(trans.getAmount().toPlainString());

		return view;
	}
}
