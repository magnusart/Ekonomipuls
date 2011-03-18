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

import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AbstractDbFacade;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.model.Transaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.util.Log;

public class AnalyticsTransactionsDbFacade extends AbstractDbFacade implements
		LogTag, AnalyticsDbConstants {
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static List<Transaction> getUnfilteredTransactions(final Context ctx)
			throws RemoteException {
		return getTransactions(ctx, Transactions.TABLE, Transactions.FILTERED
				+ " = 0");
	}

	/**
	 * @param verifyTransactions
	 * @return
	 */
	public static List<Transaction> getUnverifiedTransactions(final Context ctx) {
		return getTransactions(ctx, Transactions.TABLE, Transactions.VERIFIED
				+ " = 0");
	}

	/**
	 * 
	 * @param ctx
	 * @param cat
	 * @return
	 */
	public static List<Transaction> getTransactionsByCategory(
			final Context ctx, final Category cat) {
		return getTransactions(ctx, Views.TRANSACTIONS_CATEGORY_VIEW,
				Views.TRANS_CAT_V_CAT_ID + " = " + cat.getId());
	}

	/**
	 * 
	 * @param ctx
	 * @param bdAccountId
	 * @return
	 */
	public static List<Transaction> getAllTransactions(final Context ctx)
			throws RemoteException {
		return getTransactions(ctx, Transactions.TABLE, null);
	}

	private static List<Transaction> getTransactions(final Context ctx,
			final String table, final String selection) {

		final String[] selectionArgs = null;
		final String having = null;
		final String groupBy = null;
		final String sortOrder = Transactions.DATE + " DESC";
		final String[] columns = Transactions.COLUMNS;

		final AnalyticsDbHelper helper = new AnalyticsDbHelper(ctx);
		final SQLiteDatabase db = helper.getReadableDatabase();

		final List<Transaction> transactions = new ArrayList<Transaction>();

		try {
			final Cursor cur = query(db, table, columns, selection,
					selectionArgs, groupBy, having, sortOrder);

			final int[] indices = ModelSqlMapper
					.getTransactionCursorIndices(cur);

			while (cur.moveToNext()) {
				transactions.add(ModelSqlMapper.mapTransactionModel(cur,
						indices));
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
	 * @param modTrans
	 * @param defaultTagId
	 */
	public static void updateTransactionsAssignTags(final Context ctx,
			final List<ApplyFilterTagAction> actions) {

		final AnalyticsDbHelper helper = new AnalyticsDbHelper(ctx);
		final SQLiteDatabase db = helper.getReadableDatabase();

		try {

			for (final ApplyFilterTagAction action : actions) {

				final Transaction transaction = action.getTransaction();
				final long tagId = action.getTagId();
				final long transId = transaction.getId();

				ContentValues values = ModelSqlMapper
						.mapTransactionFilterSql(transaction);
				final String selection = Transactions.ID + " = " + transId;

				update(db, Transactions.TABLE, values, selection);

				values = ModelSqlMapper
						.mapTransactionTagJoinSql(transId, tagId);

				insert(db, Joins.TRANSACTIONS_TAGS_TABLE, values);

			}

			db.setTransactionSuccessful();

		} finally {
			shutdownDb(db, helper);
		}
	}

	/**
	 * @param parent
	 * @param filteredTransactions
	 */
	public static void insertTransactionsAssignTags(final Context ctx,
			final List<ApplyFilterTagAction> actions) {
		final AnalyticsDbHelper helper = new AnalyticsDbHelper(ctx);
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