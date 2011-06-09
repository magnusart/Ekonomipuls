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

import java.util.List;

import roboguice.adapter.IterableAdapter;
import se.ekonomipuls.R;
import se.ekonomipuls.model.Transaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * @author Magnus Andersson
 * @since 30 maj 2011
 */
public class VerifyTransactionsAdapter extends IterableAdapter<Transaction> {

	/**
	 * @author Magnus Andersson
	 * @since 30 maj 2011
	 */
	public class ViewHolder {

		public CheckBox verifyCheckBox;
		public TextView transactionName;
		public TextView tagsText;

	}

	private final int layoutViewResourceId;
	private final LayoutInflater inflater;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param transactions
	 */
	public VerifyTransactionsAdapter(final Context context,
			final int layoutViewResourceId, final List<Transaction> transactions) {
		super(context, layoutViewResourceId, transactions);
		this.layoutViewResourceId = layoutViewResourceId;
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

			holder.verifyCheckBox = (CheckBox) convertView
					.findViewById(R.id.verifyCheckBox);
			holder.transactionName = (TextView) convertView
					.findViewById(R.id.verificationNameText);
			holder.tagsText = (TextView) convertView
					.findViewById(R.id.verificationTagsText);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Transaction transaction = getItem(position);

		holder.verifyCheckBox.setChecked(true);
		holder.transactionName.setText(transaction.getDescription());
		holder.tagsText.setText(transaction.getGlobalId());

		return convertView;
	}
}
