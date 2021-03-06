/**
 * Copyright 2010 Magnus Andersson 
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
package se.ekonomipuls.reciever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import roboguice.receiver.RoboBroadcastReceiver;
import se.ekonomipuls.service.async.BankDroidImportIntentService;

import static se.ekonomipuls.LogTag.TAG;

/**
 * This class receives changes to transactions and makes sure they are stored
 * for later processing.
 * 
 * @author Magnus Andersson
 * @since 30 dec 2010
 */
public class BankDroidTransactionsReceiver extends RoboBroadcastReceiver {

	private static final String UPDATE_TRANSACTIONS = "com.liato.bankdroid.action.TRANSACTIONS";

	/** {@inheritDoc} */
	@Override
	protected void handleReceive(final Context context, final Intent intent) {
		Log.v(TAG, "Recived broadcast: " + intent.getAction());

		if (UPDATE_TRANSACTIONS.equals(intent.getAction())) {
			Log.v(TAG, "Setting up import service");

			final Bundle bundle = intent.getExtras();

			// Define the action
			bundle.putString(BankDroidImportIntentService.IMPORT_ACTION, BankDroidImportIntentService.ImportAction.SINGLE_ACCOUNT
					.toString());

			final Intent importService = new Intent(context,
					BankDroidImportIntentService.class);

			importService.putExtras(bundle);

			Log.v(TAG, "Handing over to import service");
			// Hand over to service queue since broadcast receivers have a very
			// short timeout.
			context.startService(importService);

			Log.v(TAG, "Handed over to import service");
		}
	}

}
