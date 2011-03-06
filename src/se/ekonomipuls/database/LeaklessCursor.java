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
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 6 mar 2011
 */
class LeaklessCursor extends SQLiteCursor implements LogTag {
	final SQLiteDatabase mDatabase;

	/**
	 * 
	 * @param database
	 * @param driver
	 * @param table
	 * @param query
	 */
	public LeaklessCursor(final SQLiteDatabase database,
							final SQLiteCursorDriver driver,
							final String table, final SQLiteQuery query) {
		super(database, driver, table, query);
		mDatabase = database;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		Log.v(TAG, "Closing LeaklessCursor: " + mDatabase.getPath());
		super.close();
		if (mDatabase != null) {
			if (!mDatabase.isReadOnly()) {
				Log.v(TAG, "Closing transaction for Cursor " + toString());
				mDatabase.endTransaction();
			}
			mDatabase.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "LeaklessCursor [mDatabase=" + mDatabase + ", mWindow="
				+ mWindow + ", mUpdatedRows=" + mUpdatedRows
				+ ", mRowIdColumnIndex=" + mRowIdColumnIndex + ", mPos=" + mPos
				+ ", mCurrentRowID=" + mCurrentRowID + ", mContentResolver="
				+ mContentResolver + ", mClosed=" + mClosed + "]";
	}
}
