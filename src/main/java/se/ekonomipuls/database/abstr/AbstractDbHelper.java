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

import static se.ekonomipuls.LogTag.TAG;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public abstract class AbstractDbHelper extends SQLiteOpenHelper {

	@Inject
	protected static Context context;

	/**
	 * @param name
	 * @param factory
	 * @param version
	 */
	public AbstractDbHelper(final String name, final CursorFactory factory,
			final int version) {
		super(context, name, factory, version);
	}

	/** {@inheritDoc} */
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.beginTransaction();
		createTables(db);
		initTables(db);
		db.setTransactionSuccessful();
		db.endTransaction();

		initConfiguration();
	}

	/** {@inheritDoc} */
	@Override
	public void onOpen(final SQLiteDatabase db) {
		if (!db.isReadOnly()) {
			Log.v(TAG, "Beginning transaction for " + db.toString());
			db.beginTransaction();
		}
	}

	/**
	 * Populate tables.
	 * 
	 * @param db
	 *            Database to populate.
	 */
	abstract protected void createTables(SQLiteDatabase db);

	/**
	 * Populate tables with initial data
	 * 
	 * @param db
	 *            Database to populate.
	 */
	abstract protected void initTables(SQLiteDatabase db);

	/**
	 * Init system configuration.
	 */
	abstract protected void initConfiguration();
}
