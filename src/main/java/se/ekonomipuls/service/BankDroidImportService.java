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

import roboguice.service.RoboIntentService;
import se.ekonomipuls.HomeScreenTask;
import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.proxy.bankdroid.BankDroidAccount;
import se.ekonomipuls.proxy.bankdroid.BankDroidBank;
import se.ekonomipuls.proxy.bankdroid.BankDroidProxy;
import se.ekonomipuls.proxy.bankdroid.BankDroidTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;

/**
 * This service is responsible for inserting BankDroid Transactions into the
 * Staging area.
 * 
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 25 jan 2011
 */
public class BankDroidImportService extends RoboIntentService {

	public static enum ImportAction {
		ALL_AVAILABLE_ACCOUNTS, SINGLE_ACCOUNT;
	}

	public static final String IMPORT_ACTION = "se.ekonomipuls.service.action.IMPORT_ACTION";

	@Inject
	private StagingDbFacade stagingDbFacade;

	@Inject
	private EkonomipulsUtil util;

	@Inject
	private BankDroidProxy bankDroidProxy;

	@Inject
	public Context context;

	private static final String ACCOUNT_ID = "accountId";

	public BankDroidImportService() {
		super(BankDroidImportService.class.getClass().getName());
	}

	/** {@inheritDoc} */
	@Override
	protected void onHandleIntent(final Intent intent) {

		Log.v(TAG, "Starting Import Service");

		final Bundle bundle = intent.getExtras();

		final ImportAction action = ImportAction.valueOf(bundle
				.getString(IMPORT_ACTION));

		try {
			switch (action) {
			case ALL_AVAILABLE_ACCOUNTS:
				Log.v(TAG, "Commencing full import.");
				importAllAccounts();
				break;
			case SINGLE_ACCOUNT:
				Log.v(TAG, "Importing from a single account.");
				final String accountId = bundle.getString(ACCOUNT_ID);
				importSingleAccount(accountId);
				break;
			}
		} catch (final IllegalAccessException e) {
			Log.e(TAG, "Unable to access the content provider. Resetting paired status to false.", e);
			util.setPairedBankDroid(false);
		}

		util.notifyHomeScreen(HomeScreenTask.UPDATE_TRANSACTIONS_NOTIFICATION);
	}

	private void importAllAccounts() throws IllegalAccessException {
		Log.v(TAG, "Fetching transactions from BankDroid content provider");
		final Map<Long, BankDroidBank> bankMap = bankDroidProxy
				.getBankDroidBanks();

		final Collection<BankDroidBank> banks = bankMap.values();

		for (final BankDroidBank bank : banks) {
			for (final BankDroidAccount account : bank.getAccounts()) {
				importSingleAccount(account.getId());
			}
		}

	}

	private void importSingleAccount(final String accountId)
			throws IllegalAccessException {

		Log.v(TAG, "Fetching transactions from BankDroid content provider");
		final List<BankDroidTransaction> transactions = bankDroidProxy
				.getBankDroidTransactions(accountId);

		if (transactions.size() > 0) {
			Log.v(TAG, "Bulk inserting transactions");
			stagingDbFacade.bulkInsertBdTransactions(transactions);

			// Make sure we see that there are new transactions in the GUI.
			util.setNewTransactionStatus(true);
		} else {
			Log.d(TAG, "No transactions for the account " + accountId
					+ ", skipping");
		}

	}
}
