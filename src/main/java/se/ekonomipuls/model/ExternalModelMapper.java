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
import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.proxy.bankdroid.BankDroidTransaction;

/**
 * This utility class is responsible for mapping between external model objects
 * (BankDroid) and internal model objects.
 * 
 * @author Magnus Andersson
 * @since 16 mar 2011
 */
public class ExternalModelMapper {

	/**
	 * @param bdTransactions
	 * @return
	 */
	public List<Transaction> fromBdTransactionsToTransactions(
			final List<BankDroidTransaction> bdTransactions) {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		for (final BankDroidTransaction bdTransaction : bdTransactions) {

			transactions.add(fromBdTransactionToTransaction(bdTransaction));
		}

		return transactions;
	}

	/**
	 * @param bdTransaction
	 * @return
	 */
	private Transaction fromBdTransactionToTransaction(
			final BankDroidTransaction bdTransaction) {

		final String accId = bdTransaction.getAccountId();
		final String date = bdTransaction.getDate();
		final String desc = bdTransaction.getDescription();
		final BigDecimal amt = bdTransaction.getAmount();
		final String currency = bdTransaction.getCurrency();
		final String globalId = bdTransaction.getId();

		return new Transaction(0, globalId, date, desc, "", amt, currency,
				false, false, accId);

	}
}
