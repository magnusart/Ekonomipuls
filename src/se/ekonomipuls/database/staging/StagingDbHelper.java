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

import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.database.AbstractDbHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public class StagingDbHelper extends AbstractDbHelper implements LogTag,
		StagingDbConstants {

	private static final int DB_VERSION = 1;
	private static String DB_NAME = "ekonomipuls_staging.db";

	private static final String DB_CREATE_STAGING_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Staging.TABLE
			+ " ( "
			+ Staging.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Staging.GLOBAL_ID
			+ " STRING NOT NULL, "
			+ Staging.DATE
			+ " TEXT NOT NULL, "
			+ Staging.AMOUNT
			+ " TEXT NOT NULL, "
			+ Staging.DESCRIPTION
			+ " TEXT NOT NULL, "
			+ Staging.CURRENCY
			+ " TEXT NOT NULL, "
			+ Staging.BD_ACCOUNT
			+ " TEXT NOT NULL, "
			+ "UNIQUE( "
			+ Staging.GLOBAL_ID + " ) " + ")";

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public StagingDbHelper(final Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/** {@inheritDoc} */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		throw new IllegalAccessError("Upgrade is not yet implemented.");
	}

	/** {@inheritDoc} */
	@Override
	protected void createTables(final SQLiteDatabase db) {
		Log.d(TAG, "Creating table with " + DB_CREATE_STAGING_TABLE);
		db.execSQL(DB_CREATE_STAGING_TABLE);
	}

	/** {@inheritDoc} */
	@Override
	protected void initTables(final SQLiteDatabase db) {

	}

	/** {@inheritDoc} */
	@Override
	protected void initConfiguration() {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();

		final String containsUpdates = context
				.getString(R.string.setting_staging_contains_updates);
		editor.putBoolean(containsUpdates, false); // No updates when initiated.
		editor.commit();
	}

}
