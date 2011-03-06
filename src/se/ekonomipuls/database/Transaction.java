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
package se.ekonomipuls.database;

import java.math.BigDecimal;

/**
 * Bank transaction.
 * 
 * @author Magnus Andersson
 * @since 5 feb 2011
 */
public class Transaction {

	protected long id;
	protected String date;
	protected String description;
	protected BigDecimal amount;
	protected String currency;
	protected String comment;
	protected boolean filtered;
	protected final String bankdroidAccount;
	protected final String globalId;

	/**
	 * 
	 * @param id
	 *            the id.
	 * @param date
	 *            the date.
	 * @param description
	 *            the description.
	 * @param comment
	 *            user comment.
	 * @param amount
	 *            the amount.
	 * @param currency
	 *            the currency.
	 * @param bankdroidAccount
	 *            the bankdroid account.
	 */
	protected Transaction(final long id, final String globalId,
							final String date, final String description,
							final String comment, final BigDecimal amount,
							final String currency, final boolean filtered,
							final String bankdroidAccount) {
		this.id = id;
		this.globalId = globalId;
		this.date = date;
		this.description = description;
		this.comment = comment;
		this.amount = amount;
		this.currency = currency;
		this.filtered = filtered;
		this.bankdroidAccount = bankdroidAccount;
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
	 * @return the comment
	 */
	public String getComment() {
		return comment;
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

	/**
	 * @return the filtered
	 */
	public boolean isFiltered() {
		return filtered;
	}

	/**
	 * @return the globalId
	 */
	public String getGlobalId() {
		return globalId;
	}

	/**
	 * @return the bankdroidAccount
	 */
	public String getBankdroidAccount() {
		return bankdroidAccount;
	}

	/**
	 * @param filtered
	 *            the filtered to set
	 */
	public void setFiltered(final boolean filtered) {
		this.filtered = filtered;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Transaction [id=" + id + ", date=" + date + ", description="
				+ description + ", amount=" + amount + ", currency=" + currency
				+ ", comment=" + comment + ", filtered=" + filtered
				+ ", bankdroidAccount=" + bankdroidAccount + ", globalId="
				+ globalId + "]";
	}
}
