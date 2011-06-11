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
package se.ekonomipuls.proxy.bankdroid;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.google.inject.Inject;
import com.liato.bankdroid.provider.IBankTransactionsProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.ekonomipuls.model.EkonomipulsUtil;

import static se.ekonomipuls.LogTag.TAG;

/**
 * @author Magnus Andersson
 * @since 15 jan 2011
 */
public class BankDroidProxy implements IBankTransactionsProvider {

	@Inject
	private Context context;

	@Inject
	EkonomipulsUtil util;

	@Inject
	BankDroidModelSqlMapper mapper;

	private static final String CONTENT_PROVIDER_API_KEY = "content_provider_api_key";

	/**
	 * Get a list of BankDroidTransactions.
	 * 
	 * @param accountId
	 *            The Account Id.
	 * @return List of Transactions
	 * @throws IllegalAccessException
	 */
	public List<BankDroidTransaction> getBankDroidTransactions(
			final String accountId) throws IllegalAccessException {

		final List<BankDroidTransaction> transactions = new ArrayList<BankDroidTransaction>();
		Cursor cur = null;

		try {
			cur = getUnmanagedTransactionsCursor(accountId);

			Log.d(TAG, "Aquired transactions cursor with " + cur.getCount()
					+ " rows");

			final int[] indices = mapper.getBdTransactionIndices(cur);

			while (cur.moveToNext()) {
				transactions.add(mapper.mapBdTransactionModel(cur, indices));
			}
		} finally {
			if (cur != null) {
				cur.close(); // Clean up.
			}
		}

		return transactions;
	}

	/**
	 * Get a list of BankDroidTransactions.
	 * 
	 * @param accountId
	 *            The Account Id.
	 * @return List of Transactions
	 * @throws IllegalAccessException
	 */
	public Map<Long, BankDroidBank> getBankDroidBanks()
			throws IllegalAccessException {

		final Map<Long, BankDroidBank> banks = new HashMap<Long, BankDroidBank>();
		Cursor cur = null;

		try {
			cur = getUnmanagedBankAccountsCursor();

			final int[] indices = mapper.getBdBankAccountIndices(cur);

			Log.d(TAG, "Aquired BankAccount cursor with " + cur.getCount()
					+ " rows");

			while (cur.moveToNext()) {

				BankDroidBank bank = mapper.mapBdBankModel(cur, indices);

				if (!banks.containsKey(bank.getId())) {
					banks.put(bank.getId(), bank);
				} else {
					bank = banks.get(bank.getId());
				}

				bank.getAccounts().add(mapper.mapBdAccountModel(cur, indices));

				Log.d(TAG, "Added following BankAccount " + bank);
			}
		} finally {
			if (cur != null) {
				cur.close(); // Clean up.
			}
		}

		return banks;
	}

	Cursor getUnmanagedBankAccountsCursor() throws IllegalAccessException {
		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ BANK_ACCOUNTS_CAT + "/" + API_KEY + util.getApiKey());

		final Cursor cur = context
				.getContentResolver()
				.query(uri, BANK_ACCOUNT_PROJECTION, NO_HIDDEN_ACCOUNTS_FILTER, null, ORDER_BY_BANK_ACCOUNT);

		if (cur == null) {
			throw new IllegalAccessException(
					"The API-key was either empty or did not match BankDroid's API-key");
		}

		return cur;
	}

	private Cursor getUnmanagedTransactionsCursor(final String accountId)
			throws IllegalAccessException {
		final Uri uri = Uri.parse("content://" + AUTHORITY + "/"
				+ TRANSACTIONS_CAT + "/" + API_KEY + util.getApiKey());

		Log.d(TAG, "Picked Account with id " + accountId);

		final Cursor cur = context
				.getContentResolver()
				.query(uri, TRANSACTIONS_PROJECTION, ACCOUNT_SELECTION_FILTER, new String[] { accountId }, null);

		if (cur == null) {
			throw new IllegalAccessException(
					"The API-key was either empty or did not match BankDroid's API-key");
		}

		return cur;
	}
}
