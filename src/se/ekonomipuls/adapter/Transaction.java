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

/**
 * Bank transaction.
 * 
 * @author Magnus Andersson
 * @since 5 feb 2011
 */
public class Transaction {

	private final long id;
	private final String date;
	private final String description;
	private final BigDecimal amount;
	private final String currency;

	/**
	 * 
	 * @param id
	 *            the id.
	 * @param date
	 *            the date.
	 * @param description
	 *            the description.
	 * @param amount
	 *            the amount.
	 * @param currency
	 *            the currency.
	 */
	Transaction(final long id, final String date, final String description,
				final BigDecimal amount, final String currency) {
		this.id = id;
		this.date = date;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

}
