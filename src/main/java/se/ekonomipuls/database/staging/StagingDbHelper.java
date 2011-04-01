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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.inject.Inject;

import se.ekonomipuls.database.abstr.AbstractDbHelper;
import se.ekonomipuls.util.EkonomipulsUtil;

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.database.staging.StagingDbConstants.Staging;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public class StagingDbHelper extends AbstractDbHelper {

	@Inject
	private EkonomipulsUtil ekonomipulsUtil;

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

	public StagingDbHelper() {
		super(DB_NAME, null, DB_VERSION);
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
		ekonomipulsUtil.setNewTransactionStatus(false);
	}

}
