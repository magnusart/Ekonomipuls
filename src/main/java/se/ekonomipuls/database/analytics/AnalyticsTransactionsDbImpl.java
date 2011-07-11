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

import static se.ekonomipuls.LogTag.TAG;

import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.abstr.AbstractDb;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Categories;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Joins;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Transactions;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Views;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.model.Tag;
import se.ekonomipuls.model.Transaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
@Singleton
public class AnalyticsTransactionsDbImpl extends AbstractDb implements
		AnalyticsTransactionsDbFacade {

	@Inject
	private AnalyticsDbHelper helper;

	@Inject
	private ModelSqlMapper mapper;

	/** {@inheritDoc} */
	@Override
	public List<Transaction> getUnfilteredTransactions() {
		return getTransactions(Transactions.TABLE, Transactions.FILTERED
				+ " = 0");
	}

	/** {@inheritDoc} */
	@Override
	public List<Transaction> getUnverifiedTransactions() {
		return getTransactions(Transactions.TABLE, Transactions.VERIFIED
				+ " = 0");
	}

	/** {@inheritDoc} */
	@Override
	public List<Transaction> getTransactionsByCategory(final Category cat) {
		return getTransactions(Views.TRANSACTIONS_CATEGORY_FROM_STMT, Categories.TABLE
				+ "." + Categories.ID + " = " + cat.getId());
	}

	/** {@inheritDoc} */
	@Override
	public List<Transaction> getAllTransactions() {
		return getTransactions(Transactions.TABLE, null);
	}

	private List<Transaction> getTransactions(final String table,
			final String selection) {

		final String[] selectionArgs = null;
		final String having = null;
		final String groupBy = null;
		final String sortOrder = Transactions.DATE + " DESC";
		final String[] columns = prefixColumns(Transactions.TABLE, Transactions.COLUMNS);
		;

		final SQLiteDatabase db = helper.getReadableDatabase();

		final List<Transaction> transactions = new ArrayList<Transaction>();

		try {
			final Cursor cur = query(db, table, columns, selection, selectionArgs, groupBy, having, sortOrder);

			final int[] indices = mapper.getTransactionCursorIndices(cur);

			while (cur.moveToNext()) {
				transactions.add(mapper.mapTransactionModel(cur, indices));
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

	/** {@inheritDoc} */
	@Override
	public void insertTransactionsAssignTags(
			final List<ApplyFilterTagAction> actions) {
		final SQLiteDatabase db = helper.getWritableDatabase();

		final String table = Transactions.TABLE;

		try {
			for (final ApplyFilterTagAction action : actions) {

				final Transaction transaction = action.getTransaction();
				final Tag tag = action.getTag();

				ContentValues values = mapper.mapTransactionSql(transaction);

				final long transId = insert(db, table, values);

				values = mapper.mapTransactionTagJoinSql(transId, tag.getId());

				insert(db, Joins.TRANSACTIONS_TAGS_TABLE, values);
			}

			db.setTransactionSuccessful();

		} finally {
			shutdownDb(db, helper);
		}

	}

	/** {@inheritDoc} */
	@Override
	public int purgeNonGlobalIDTransactions() {
		final SQLiteDatabase db = helper.getWritableDatabase();

		final String table = Transactions.TABLE;
		final String whereClause = "LENGTH(" + Transactions.GLOBAL_ID + ") = 0";
		final String[] whereArgs = new String[] {};
		int numDeletions = 0;

		try {
			numDeletions = delete(db, table, whereClause, whereArgs);
		} finally {
			shutdownDb(db, helper);
		}

		return numDeletions;
	}
}