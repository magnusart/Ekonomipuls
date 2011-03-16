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
package se.ekonomipuls.database.staging;

import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.database.AbstractDbFacade;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.proxy.BankDroidTransaction;
import se.ekonomipuls.proxy.BankDroidModelSqlMapper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public class StagingDbFacade extends AbstractDbFacade implements
		StagingDbConstants, LogTag {
	/**
	 * Bulk insert transactions.
	 * 
	 * @param ctx
	 * @param transactions
	 */
	public static void bulkInsertBdTransactions(final Context ctx,
			final List<BankDroidTransaction> transactions) {

		final ContentValues[] values = ModelSqlMapper
				.mapBankDroidTransactionSql(transactions);

		final StagingDbHelper helper = new StagingDbHelper(ctx);
		final SQLiteDatabase db = helper.getWritableDatabase();

		try {
			insert(db, Staging.TABLE, values);

			Log.d(TAG, "Transaction set to successful for " + db.toString());

			db.setTransactionSuccessful();

		} finally {
			shutdownDb(db, helper);
		}
	}

	/**
	 * @param transactionsFilterService
	 * @return
	 */
	public static List<BankDroidTransaction> getStagedTransactions(
			final Context ctx) {

		final String table = Staging.TABLE;
		final String selection = null;
		final String[] selectionArgs = null;
		final String having = null;
		final String groupBy = null;
		final String sortOrder = Staging.DATE + " DESC";
		final String[] columns = Staging.COLUMNS;

		final StagingDbHelper helper = new StagingDbHelper(ctx);
		final SQLiteDatabase db = helper.getReadableDatabase();

		final List<BankDroidTransaction> stagedTransactions = new ArrayList<BankDroidTransaction>();

		try {
			final Cursor cur = query(db, table, columns, selection,
					selectionArgs, groupBy, having, sortOrder);

			final int[] indices = BankDroidModelSqlMapper
					.getStagedTransactionCursorIndices(cur);

			while (cur.moveToNext()) {

				final BankDroidTransaction transaction = BankDroidModelSqlMapper
						.mapStagedTransactionModel(cur, indices);

				Log.d(TAG, "Fetching staged transaction " + transaction);
				stagedTransactions.add(transaction);
			}
			cur.close();
		} catch (final IllegalArgumentException e) {
			Log.e(TAG, "Could not find required database columns", e);
			throw e;
		} finally {
			shutdownDb(db, helper);
		}

		return stagedTransactions;
	}

}
