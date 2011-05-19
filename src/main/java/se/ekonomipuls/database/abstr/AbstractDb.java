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
package se.ekonomipuls.database.abstr;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import static se.ekonomipuls.LogTag.TAG;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public abstract class AbstractDb {

	private SQLiteDatabase db;

	/**
	 * @param db
	 * @param helper
	 */
	protected void shutdownDb(final SQLiteDatabase db,
			final SQLiteOpenHelper helper) {
		if (!db.isReadOnly() && db.isOpen()) {
			if (db.inTransaction()) {
				Log.d(TAG, "In transaction, ending current transaction for "
						+ db.toString());
				db.endTransaction();
			}
			db.close(); // TODO: Not sure this is necessary
			helper.close();
		}
	}

	/**
	 * 
	 * @param db
	 * @param table
	 * @param values
	 */
	protected void insert(final SQLiteDatabase db, final String table,
			final ContentValues[] values) {
		for (final ContentValues value : values) {
			insert(db, table, value);
		}
	}

	/**
	 * 
	 * @param db
	 * @param table
	 * @param values
	 */
	protected long insert(final SQLiteDatabase db, final String table,
			final ContentValues values) {
		Log.d(TAG, "Inserting " + values + " into " + table);
		return db.insert(table, null, values);
	}

	/**
	 * 
	 * @param db
	 * @param table
	 * @param values
	 * @param selection
	 * @return
	 */
	protected int update(final SQLiteDatabase db, final String table,
			final ContentValues values, final String selection) {
		Log.d(TAG, "Updating " + values + " in " + table + " where "
				+ selection);
		return db.update(table, values, selection, null);

	}

	/**
	 * 
	 * @param db
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param sortOrder
	 * @return
	 */
	protected Cursor query(final SQLiteDatabase db, final String table,
			final String[] columns, final String selection,
			final String[] selectionArgs, final String groupBy,
			final String having, final String sortOrder) {

		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(table);

		final String sql = SQLiteQueryBuilder
				.buildQueryString(false, table, columns, selection, groupBy, having, sortOrder, null);

		Log.d(TAG, "Querying with " + sql);

		return qb
				.query(db, columns, selection, selectionArgs, groupBy, having, sortOrder);
	}

	/**
	 * @param db
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	protected int delete(final SQLiteDatabase db, final String table,
			final String whereClause, final String[] whereArgs) {

		Log.d(TAG, "Deleting from " + table + " where " + whereClause + " = "
				+ whereArgs);

		return db.delete(table, whereClause, whereArgs);

	}
}
