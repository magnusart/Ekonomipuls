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
package se.ekonomipuls.views.charts;

import java.math.BigDecimal;
import java.util.List;

import android.graphics.Color;

import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.Transaction;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class SeriesEntry {

	private final int baseColor;
	private final Category category;
	private boolean selected;

	private final BigDecimal sum;
	private final int numTransactions;

	/**
	 * 
	 * @param category
	 * @param baseColor
	 */
	public SeriesEntry(final Category category,
			final List<Transaction> transactions) {
		this.category = category;
		this.baseColor = Color.parseColor(category.getColor()); // TODO Fix so
																// that other
																// Classes uses
																// category to
																// get the
																// color.
		this.selected = false;

		this.sum = sumTranscations(transactions);
		this.numTransactions = transactions.size();
	}

	/**
	 * Run this on creation
	 * 
	 * @param transactions
	 * 
	 * @return
	 */
	private BigDecimal sumTranscations(final List<Transaction> transactions) {
		BigDecimal total = new BigDecimal(0.0);

		// Get the total
		for (final Transaction trans : transactions) {
			total = total.add(trans.getAmount());
		}

		return total.abs();
	}

	/**
	 * @return the baseColor
	 */
	public int getBaseColor() {
		return baseColor;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public SeriesEntry setSelected(final boolean selected) {
		this.selected = selected;
		return this;
	}

	/**
	 * @return the totalSum
	 */
	public BigDecimal getSum() {
		return sum;
	}

	/**
	 * @return the numTransactions
	 */
	public int getNumTransactions() {
		return numTransactions;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "SeriesEntry [baseColor=" + baseColor + ", category=" + category
				+ ", selected=" + selected + ", sum=" + sum
				+ ", numTransactions=" + numTransactions + "]";
	}

}
