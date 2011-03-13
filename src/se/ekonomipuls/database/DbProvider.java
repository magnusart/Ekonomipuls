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
package se.ekonomipuls.database;

import java.util.HashMap;
import java.util.Map;

import se.ekonomipuls.LogTag;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 26 feb 2011
 */
public class DbProvider extends ContentProvider implements DbConstants, LogTag {

	private static final int TRANSACTIONS_ACTION = 0;
	private static final int TRANSACTIONS_CATEGORY_ACTION = 1;
	private static final int REPORTS_ACTION = 2;
	private static final int CATEGORIES_REPORT_ACTION = 3;
	private static final int CATEGORIES_ACTION = 4;
	private static final int TRANSACTIONS_TAGS_ACTION = 5;

	private static UriMatcher uriMatcher;
	private static final Map<String, String> categoriesProj;
	private static final Map<String, String> reportsProj;
	private static final Map<String, String> tagsProj;
	private static final Map<String, String> transactionsProj;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(Provider.AUTHORITY, Provider.TRANSACTIONS,
				TRANSACTIONS_ACTION);

		uriMatcher.addURI(Provider.AUTHORITY, Provider.TRANSACTIONS_CATEGORY,
				TRANSACTIONS_CATEGORY_ACTION);

		uriMatcher.addURI(Provider.AUTHORITY, Provider.REPORTS, REPORTS_ACTION);

		uriMatcher.addURI(Provider.AUTHORITY, Provider.CATEGORIES,
				CATEGORIES_ACTION);

		uriMatcher.addURI(Provider.AUTHORITY, Provider.CATEGORIES_REPORT,
				CATEGORIES_REPORT_ACTION);

		uriMatcher.addURI(Provider.AUTHORITY, Provider.TRANSACTIONS_TAGS,
				TRANSACTIONS_TAGS_ACTION);

		transactionsProj = createProjMap(Transactions.COLUMNS);
		categoriesProj = createProjMap(Categories.COLUMNS);
		reportsProj = createProjMap(Reports.COLUMNS);
		tagsProj = createProjMap(Tags.COLUMNS);

	}

	/**
	 * @param columns
	 * @return
	 */
	private static Map<String, String> createProjMap(final String[] columns) {
		final Map<String, String> colMap = new HashMap<String, String>();

		for (final String col : columns) {
			colMap.put(col, col);
		}

		return colMap;
	}

	private DbHelper dbHelper;

	/** {@inheritDoc} */
	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String getType(final Uri uri) {
		Log.d(TAG, "Got URI " + uri.toString());

		switch (uriMatcher.match(uri)) {
			case TRANSACTIONS_ACTION:
				return Provider.TRANSACTIONS_MIME;
			case TRANSACTIONS_CATEGORY_ACTION:
				return Provider.TRANSACTIONS_MIME;
			case REPORTS_ACTION:
				return Provider.REPORTS_MIME;
			case CATEGORIES_REPORT_ACTION:
				return Provider.CATEGORIES_MIME;
			case TRANSACTIONS_TAGS_ACTION:
				return Provider.TRANSACTIONS_MIME;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Cursor query(final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) {

		final SQLiteDatabase db = dbHelper.getReadableDatabase();

		String table = null;
		String[] columns = null;
		Map<String, String> projMap = null;
		final String groupBy = null;
		final String having = null;
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		final int match = uriMatcher.match(uri);
		// Setup the query based on what action is to be performed
		switch (match) {
			case TRANSACTIONS_ACTION:
				table = Transactions.TABLE;
				columns = Transactions.COLUMNS;
				projMap = transactionsProj;
				break;
			case TRANSACTIONS_CATEGORY_ACTION:
				table = Views.TRANSACTIONS_CATEGORY_VIEW;
				columns = Transactions.COLUMNS;
				projMap = transactionsProj;
				break;
			case CATEGORIES_REPORT_ACTION:
				table = Views.CATEGORIES_REPORT_VIEW;
				columns = Categories.COLUMNS;
				projMap = categoriesProj;
				break;
			case CATEGORIES_ACTION:
				table = Categories.TABLE;
				columns = Categories.COLUMNS;
				projMap = categoriesProj;
				break;
			case REPORTS_ACTION:
				table = Reports.TABLE;
				columns = Reports.COLUMNS;
				projMap = reportsProj;
			default:
				throw new IllegalArgumentException("Unsupported Query URI: "
						+ uri);
		}

		final String query = SQLiteQueryBuilder.buildQueryString(false, table,
				columns, selection, groupBy, having, sortOrder, having);

		Log.v(TAG, "Querystring used: " + query);
		Log.v(TAG, "In transaction: " + db.inTransaction());

		qb.setProjectionMap(projMap);
		qb.setTables(table);
		final Cursor cur = qb.query(db, columns, selection, selectionArgs,
				groupBy, having, sortOrder);

		// Notification URI
		cur.setNotificationUri(getContext().getContentResolver(), uri);

		return cur;
	}

	/** {@inheritDoc} */
	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		String table = null;
		String uriString = null;
		// Setup the query based on what action is to be performed
		switch (uriMatcher.match(uri)) {
			case TRANSACTIONS_ACTION:
				table = Transactions.TABLE;
				uriString = Provider.TRANSACTIONS_URI;
				break;
			case TRANSACTIONS_CATEGORY_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Insert Action: " + uri);
			case CATEGORIES_REPORT_ACTION:
				table = Joins.REPORTS_CATEGORIES_TABLE;
				uriString = Provider.CATEGORIES_REPORT_URI;
				break;
			case CATEGORIES_ACTION:
				table = Categories.TABLE;
				uriString = Provider.CATEGORIES_URI;
				break;
			case REPORTS_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Insert Action: " + uri);
			default:
				throw new IllegalArgumentException("Unsupported Insert URI: "
						+ uri);
		}

		final long id = db.insert(table, null, values);
		Log.v(TAG, "In transaction: " + db.inTransaction());

		shutdownDb(db);

		return Uri.parse(uriString + "/" + id);
	}

	/** {@inheritDoc} */
	@Override
	public int delete(final Uri uri, final String selection,
			final String[] selectionArgs) {
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public int update(final Uri uri, final ContentValues values,
			final String selection, final String[] selectionArgs) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table = null;
		// Setup the query based on what action is to be performed
		switch (uriMatcher.match(uri)) {
			case TRANSACTIONS_ACTION:
				table = Transactions.TABLE;
				break;
			case TRANSACTIONS_CATEGORY_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Update Action: " + uri);
			case CATEGORIES_REPORT_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Update Action: " + uri);
			case CATEGORIES_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Update Action: " + uri);
			case REPORTS_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Update Action: " + uri);
			default:
				throw new IllegalArgumentException("Unsupported Update URI: "
						+ uri);
		}

		final int updates = db.update(table, values, selection, selectionArgs);

		shutdownDb(db);

		return updates;
	}

	/** {@inheritDoc} */
	@Override
	public int bulkInsert(final Uri uri, final ContentValues[] values) {
		Log.v(TAG, "Entering bulk insert");
		//		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		int numInsert = 0;
		// Setup the query based on what action is to be performed
		switch (uriMatcher.match(uri)) {
			case TRANSACTIONS_ACTION:
				numInsert = bulkInsertOneTable(dbHelper.getWritableDatabase(),
						Transactions.TABLE, values);
				break;
			case TRANSACTIONS_TAGS_ACTION:
				final String table1 = Transactions.TABLE;
				final String table2 = Joins.TRANSACTIONS_TAGS_TABLE;
				numInsert = bulkUpdateTransactionInsertTags(
						dbHelper.getWritableDatabase(), table1, table2, values);
				break;
			case TRANSACTIONS_CATEGORY_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Insert Action: " + uri);
			case CATEGORIES_REPORT_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Insert Action: " + uri);
			case CATEGORIES_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Insert Action: " + uri);
			case REPORTS_ACTION:
				throw new IllegalArgumentException(
						"Unsupported Insert Action: " + uri);
			default:
				throw new IllegalArgumentException("Unsupported Insert URI: "
						+ uri);
		}

		return numInsert;
	}

	/**
	 * @param db
	 * @param transTable
	 * @param tagsJoinTable
	 * @param values
	 * @return
	 */
	private int bulkUpdateTransactionInsertTags(final SQLiteDatabase db,
			final String transTable, final String tagsJoinTable,
			final ContentValues[] values) {
		Log.v(TAG, "In transaction: " + db.inTransaction());
		Log.v(TAG, "Bulk inserting/updating " + transTable + "/"
				+ tagsJoinTable);
		int numInsert = 0;

		for (final ContentValues value : values) {

			// Clear out the Tag values
			final ContentValues transValue = new ContentValues(value);
			transValue.remove(Joins.TRANS_FK);
			transValue.remove(Joins.TAG_FK_2);

			db.update(transTable, transValue,
					Transactions.ID + " = " + value.getAsLong(Transactions.ID),
					null);

			final ContentValues tagsJoinValue = new ContentValues(value);
			tagsJoinValue.remove(Transactions.FILTERED);

			db.insert(tagsJoinTable, null, tagsJoinValue);

			numInsert++;
		}

		shutdownDb(db);

		return numInsert;
	}

	private int bulkInsertOneTable(final SQLiteDatabase db, final String table,
			final ContentValues[] values) {
		Log.v(TAG, "In transaction: " + db.inTransaction());
		Log.v(TAG, "Standard bulk insert into " + table);
		int numInsert = 0;

		for (final ContentValues value : values) {
			db.insert(table, null, value);
			numInsert++;
		}

		shutdownDb(db);

		return numInsert;
	}

	/**
	 * @param db
	 */
	private static void shutdownDb(final SQLiteDatabase db) {
		if (!db.isReadOnly() && db.isOpen()) {
			if (db.inTransaction()) {
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			db.close();
		}
	}
}
