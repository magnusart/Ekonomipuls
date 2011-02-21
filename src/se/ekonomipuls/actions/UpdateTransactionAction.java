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
package se.ekonomipuls.actions;

import java.math.BigDecimal;

import se.ekonomipuls.database.Transaction;

/**
 * This class encapsulates a command
 * 
 * @author Magnus Andersson
 * @since 17 feb 2011
 */
public final class UpdateTransactionAction extends Transaction {

	/**
	 * @param transaction
	 *            Transaction that should be modified
	 */
	public UpdateTransactionAction(final Transaction t) {
		// Pass through
		super(t.getId(), t.getGlobalId(), t.getDate(), t.getDescription(), t
				.getComment(), new BigDecimal(t.getAmount().toString()), t
				.getCurrency(), t.isFiltered(), t.getBankdroidAccount());
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(final String date) {
		this.date = date;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(final String comment) {
		this.comment = comment;
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
		return "ModifiedTransaction [id=" + id + ", date=" + date
				+ ", description=" + description + ", amount=" + amount
				+ ", currency=" + currency + ", comment=" + comment
				+ ", filtered=" + filtered + ", bankdroidAccount="
				+ bankdroidAccount + ", globalId=" + globalId + "]";
	}

}
