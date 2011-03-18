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
package se.ekonomipuls.model;

import java.math.BigDecimal;

/**
 * Bank transaction.
 * 
 * @author Magnus Andersson
 * @since 5 feb 2011
 */
public class Transaction {

	// Data fields
	private final long id;
	private final String date;
	private final String description;
	private final BigDecimal amount;
	private final String currency;
	private final String comment;
	private final String bankdroidAccount;
	private final String globalId;

	// Status fields can be extracted into it's own object.
	private boolean filtered;
	private boolean verified;

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
	Transaction(final long id, final String globalId, final String date,
				final String description, final String comment,
				final BigDecimal amount, final String currency,
				final boolean filtered, final boolean verified,
				final String bankdroidAccount) {
		this.id = id;
		this.globalId = globalId;
		this.date = date;
		this.description = description;
		this.comment = comment;
		this.amount = amount;
		this.currency = currency;
		this.filtered = filtered;
		this.verified = verified;
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

	/**
	 * @return the verified
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * @param verified
	 *            the verified to set
	 */
	public void setVerified(final boolean verified) {
		this.verified = verified;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Transaction [id=" + id + ", date=" + date + ", description="
				+ description + ", amount=" + amount + ", currency=" + currency
				+ ", comment=" + comment + ", bankdroidAccount="
				+ bankdroidAccount + ", globalId=" + globalId + ", filtered="
				+ filtered + ", verified=" + verified + "]";
	}

}
