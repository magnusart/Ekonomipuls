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

import se.ekonomipuls.LogTag;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

	private static UriMatcher uriMatcher;

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

		try {
			String table = null;
			String[] columns = null;
			final String groupBy = null;
			final String having = null;

			final int match = uriMatcher.match(uri);
			// Setup the query based on what action is to be performed
			switch (match) {
				case TRANSACTIONS_ACTION:
					table = Transactions.TABLE;
					columns = Transactions.COLUMNS;
					break;
				case TRANSACTIONS_CATEGORY_ACTION:
					table = Views.TRANSACTIONS_CATEGORY_VIEW;
					columns = Transactions.COLUMNS;
					break;
				case CATEGORIES_REPORT_ACTION:
					table = Views.CATEGORIES_REPORT_VIEW;
					columns = Categories.COLUMNS;
					break;
				case CATEGORIES_ACTION:
					table = Categories.TABLE;
					columns = Categories.COLUMNS;
					break;
				case REPORTS_ACTION:
					throw new IllegalArgumentException(
							"Unsupported Query Action: " + uri);
				default:
					throw new IllegalArgumentException(
							"Unsupported Query URI: " + uri);
			}

			final Cursor cur = db.query(table, columns, selection,
					selectionArgs, groupBy, having, sortOrder);
			return cur;
		} finally {
			shutdownDb(db);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		try {
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
					throw new IllegalArgumentException(
							"Unsupported Insert URI: " + uri);
			}

			final long id = db.insert(table, null, values);

			return Uri.parse(uriString + "/" + id);
		} finally {
			shutdownDb(db);
		}
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
		try {
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
					throw new IllegalArgumentException(
							"Unsupported Update URI: " + uri);
			}

			return db.update(table, values, selection, selectionArgs);
		} finally {
			shutdownDb(db);
		}
	}

	/**
	 * @param db
	 */
	private static void shutdownDb(final SQLiteDatabase db) {
		if (!db.isReadOnly()) {
			// Transaction begun in DbHelper.open();
			db.endTransaction();
		}
		db.close();
	}

}
