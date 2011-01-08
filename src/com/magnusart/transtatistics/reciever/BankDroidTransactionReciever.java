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
package com.magnusart.transtatistics.reciever;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.magnusart.transtatistics.TranStatistics;

/**
 * This class recieves changes to transactions and makes sure they are stored
 * for later processing.
 * 
 * @author Magnus Andersson
 * @since 30 dec 2010
 */
public class BankDroidTransactionReciever extends BroadcastReceiver {

	private static final String UPDATE_TRANSACTIONS = "com.magnusart.transtatistics.action.UPDATE_TRANSACTIONS";

	/** {@inheritDoc} */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.d(TranStatistics.TAG, "Recieved intent");

		if (UPDATE_TRANSACTIONS.equals(intent.getAction())) {
			Log.d(TranStatistics.TAG, "Begin retrieving updated transactions");
			final String banks = intent.getExtras().getString("banks");
			JSONArray jsBanks = null;
			try {
				jsBanks = (JSONArray) new JSONTokener(banks).nextValue();
				Log.d(TranStatistics.TAG, jsBanks.toString(4));
			} catch (final JSONException e) {
				throw new RuntimeException(e);
			}

		}

	}

}
