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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.inject.Inject;
import se.ekonomipuls.database.AbstractDbFacade;
import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.proxy.BankDroidModelSqlMapper;
import se.ekonomipuls.proxy.BankDroidTransaction;

import java.util.ArrayList;
import java.util.List;

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.database.staging.StagingDbConstants.Staging;
/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public class StagingDbImpl extends AbstractDbFacade implements StagingDbFacade {

    @Inject
    private StagingDbHelper stagingDbHelper;

	/**
	 * Bulk insert transactions.
	 *
	 * @param transactions
	 */

	public void bulkInsertBdTransactions(final List<BankDroidTransaction> transactions) {

		final ContentValues[] values = ModelSqlMapper.mapBankDroidTransactionSql(transactions);
		final SQLiteDatabase db = stagingDbHelper.getWritableDatabase();

		try {
			insert(db, Staging.TABLE, values);

			Log.d(TAG, "Transaction set to successful for " + db.toString());

			db.setTransactionSuccessful();

		} finally {
			shutdownDb(db, stagingDbHelper);
		}
	}

    /**
     * @return
     */
    public List<BankDroidTransaction> getStagedTransactions() {

        final String table = Staging.TABLE;
        final String selection = null;
        final String[] selectionArgs = null;
        final String having = null;
        final String groupBy = null;
        final String sortOrder = Staging.DATE + " DESC";
        final String[] columns = Staging.COLUMNS;

        final SQLiteDatabase db = stagingDbHelper.getReadableDatabase();

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
            shutdownDb(db, stagingDbHelper);
        }

        return stagedTransactions;
    }

	/**
	 * Delete all the transactions in the staging table.
     */
	public int purgeStagingTable() {
		final SQLiteDatabase db = stagingDbHelper.getReadableDatabase();

		final String table = Staging.TABLE;
		final String whereClause = null;
		final String[] whereArgs = null;

		int numDeleted = -1;

		try {
			numDeleted = delete(db, table, whereClause, whereArgs);
		} finally {
			shutdownDb(db, stagingDbHelper);
		}

		return numDeleted;
	}

}
