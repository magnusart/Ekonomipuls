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
package se.ekonomipuls.service.async;

import static se.ekonomipuls.LogTag.TAG;

import roboguice.service.RoboIntentService;
import se.ekonomipuls.HomeScreenTask;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.service.BankDroidImportService;
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
public class BankDroidImportIntentService extends RoboIntentService {

	public static enum ImportAction {
		ALL_AVAILABLE_ACCOUNTS, SINGLE_ACCOUNT;
	}

	public static final String IMPORT_ACTION = "se.ekonomipuls.service.action.IMPORT_ACTION";

	@Inject
	private EkonomipulsUtil util;

	@Inject
	public Context context;

	@Inject
	public BankDroidImportService service;

	private static final String ACCOUNT_ID = "accountId";

	public BankDroidImportIntentService() {
		super(BankDroidImportIntentService.class.getClass().getName());
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
				service.importAllAccounts();
				break;
			case SINGLE_ACCOUNT:
				Log.v(TAG, "Importing from a single account.");
				final String accountId = bundle.getString(ACCOUNT_ID);
				service.importSingleAccount(accountId);
				break;
			}
		} catch (final IllegalAccessException e) {
			Log.e(TAG, "Unable to access the content provider. Resetting paired status to false.", e);
			util.setPairedBankDroid(false);
		}

		util.notifyHomeScreen(HomeScreenTask.UPDATE_TRANSACTIONS_NOTIFICATION);
	}

}
