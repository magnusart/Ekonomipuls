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
package se.ekonomipuls.service;

import static se.ekonomipuls.LogTag.TAG;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.proxy.bankdroid.BankDroidAccount;
import se.ekonomipuls.proxy.bankdroid.BankDroidBank;
import se.ekonomipuls.proxy.bankdroid.BankDroidProxy;
import se.ekonomipuls.proxy.bankdroid.BankDroidTransaction;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.liato.bankdroid.provider.IAccountTypes;

/**
 * @author Magnus Andersson
 * @since 14 jun 2011
 */
@Singleton
public class BankDroidImportService {

	@Inject
	private StagingDbFacade stagingDbFacade;

	@Inject
	private EkonomipulsUtil util;

	@Inject
	private BankDroidProxy bankDroidProxy;

	public void importAllAccounts() throws IllegalAccessException {
		Log.v(TAG, "Fetching transactions from BankDroid content provider");
		final Map<Long, BankDroidBank> bankMap = bankDroidProxy
				.getBankDroidBanks();

		final Collection<BankDroidBank> banks = bankMap.values();

		for (final BankDroidBank bank : banks) {
			for (final BankDroidAccount account : bank.getAccounts()) {
				if (correctAccountType(account.getType())) {
					importAccount(account.getId());
				} else {
					Log.d(TAG, "Skipping account of incorrect type. Account = "
							+ account);
				}
			}
		}

		// Make sure we see that there are new transactions in the GUI.
		util.setNewTransactionStatus(true);
	}

	public void importSingleAccount(final String accountId)
			throws IllegalAccessException {

		if (correctAccountType(getAccountType(accountId))) {
			importAccount(accountId);
			// Make sure we see that there are new transactions in the GUI.
			util.setNewTransactionStatus(true);
		} else {
			Log.d(TAG, "Skipping account of incorrect type. Account id = "
					+ accountId);
		}
	}

	private int getAccountType(final String accountId)
			throws IllegalAccessException {
		final Map<Long, BankDroidBank> bankMap = bankDroidProxy
				.getBankDroidBanks();

		// This is an O(n²) algo, which is bad. However we will most likely
		// never handle more than n = 5 so performance hit is negligible.
		for (final BankDroidBank bank : bankMap.values()) {
			for (final BankDroidAccount acc : bank.getAccounts()) {
				if (acc.getId().equals(accountId)) {
					return acc.getType();
				}
			}
		}

		return -1;
	}

	/**
	 * @param accountId
	 * @throws IllegalAccessException
	 */
	private void importAccount(final String accountId)
			throws IllegalAccessException {
		Log.v(TAG, "Fetching transactions from BankDroid content provider");
		final List<BankDroidTransaction> transactions = bankDroidProxy
				.getBankDroidTransactions(accountId);

		if (transactions.size() > 0) {
			Log.v(TAG, "Bulk inserting transactions");
			stagingDbFacade.bulkInsertBdTransactions(transactions);
		} else {
			Log.d(TAG, "No transactions for the account " + accountId
					+ ", skipping");
		}

	}

	private boolean correctAccountType(final int accountType) {
		// Only ímport transactions from Credit Cards or Regular Accounts.
		return (accountType == IAccountTypes.CCARD)
				|| (accountType == IAccountTypes.REGULAR);
	}

}
