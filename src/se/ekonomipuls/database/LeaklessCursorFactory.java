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

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQuery;

/**
 * @author Magnus Andersson
 * @since 6 mar 2011
 */
class LeaklessCursorFactory implements CursorFactory {

	/** {@inheritDoc} */
	@Override
	public Cursor newCursor(final SQLiteDatabase db,
			final SQLiteCursorDriver masterQuery, final String editTable,
			final SQLiteQuery query) {
		return new LeaklessCursor(db, masterQuery, editTable, query);
	}

}
