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
import java.util.List;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class Category {

	private final int id;
	private final String name;
	private final BigDecimal sum;
	private final int numTransactions;

	/**
	 * @param i
	 * @param string
	 * @param subList
	 */
	public Category(final int id, final String name,
					final List<Transaction> transactions) {
		this.id = id;
		this.name = name;
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
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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
		return "Category [id=" + id + ", name=" + name + ", sum=" + sum
				+ ", numTransactions=" + numTransactions + "]";
	}

}
