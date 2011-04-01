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
package se.ekonomipuls.database.analytics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AbstractDbFacade;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.model.Transaction;

import java.util.ArrayList;
import java.util.List;

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.database.analytics.AnalyticsDbConstants.*;

public class AnalyticsTransactionsDbImpl extends AbstractDbFacade implements
		AnalyticsTransactionsDbFacade {

	/**
	 * 
	 * @return
	 */
	public List<Transaction> getUnfilteredTransactions() throws RemoteException {
		return getTransactions(Transactions.TABLE, Transactions.FILTERED
				+ " = 0");
	}

	@Override
	public List<Transaction> getUnverifiedTransactions() {
		return getTransactions(Transactions.TABLE, Transactions.VERIFIED
				+ " = 0");
	}

	/**
	 * 
	 * @param cat
	 * @return
	 */
	public List<Transaction> getTransactionsByCategory(final Category cat) {
		return getTransactions(Views.TRANSACTIONS_CATEGORY_VIEW, Views.TRANS_CAT_V_CAT_ID
				+ " = " + cat.getId());
	}

	/**
	 * 
	 * @return
	 */
	public List<Transaction> getAllTransactions() throws RemoteException {
		return getTransactions(Transactions.TABLE, null);
	}

	private List<Transaction> getTransactions(final String table,
			final String selection) {

		final String[] selectionArgs = null;
		final String having = null;
		final String groupBy = null;
		final String sortOrder = Transactions.DATE + " DESC";
		final String[] columns = Transactions.COLUMNS;

		final AnalyticsDbHelper helper = new AnalyticsDbHelper();
		final SQLiteDatabase db = helper.getReadableDatabase();

		final List<Transaction> transactions = new ArrayList<Transaction>();

		try {
			final Cursor cur = query(db, table, columns, selection, selectionArgs, groupBy, having, sortOrder);

			final int[] indices = ModelSqlMapper
					.getTransactionCursorIndices(cur);

			while (cur.moveToNext()) {
				transactions.add(ModelSqlMapper
						.mapTransactionModel(cur, indices));
			}
			cur.close();
		} catch (final IllegalArgumentException e) {
			Log.e(TAG, "Could not find required database columns", e);
			throw e;
		} finally {
			shutdownDb(db, helper);
		}
		return transactions;
	}

	/**
	 * @param actions
	 */
	public void insertTransactionsAssignTags(
			final List<ApplyFilterTagAction> actions) {
		final AnalyticsDbHelper helper = new AnalyticsDbHelper();
		final SQLiteDatabase db = helper.getReadableDatabase();

		final String table = Transactions.TABLE;

		try {
			for (final ApplyFilterTagAction action : actions) {

				final Transaction transaction = action.getTransaction();
				final long tagId = action.getTagId();

				ContentValues values = ModelSqlMapper
						.mapTransactionSql(transaction);

				final long transId = insert(db, table, values);

				values = ModelSqlMapper
						.mapTransactionTagJoinSql(transId, tagId);

				insert(db, Joins.TRANSACTIONS_TAGS_TABLE, values);
			}

			db.setTransactionSuccessful();

		} finally {
			shutdownDb(db, helper);
		}

	}

}