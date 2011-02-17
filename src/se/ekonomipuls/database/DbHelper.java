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
import se.ekonomipuls.R;
import se.ekonomipuls.database.DbFacade.DbConstants;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 16 feb 2011
 */
final class DbHelper extends SQLiteOpenHelper implements DbConstants, LogTag {
	private static final String DB_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TRANSACTIONS_TABLE
			+ " ( "
			+ TRANS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TRANS_DATE
			+ " TEXT NOT NULL, "
			+ TRANS_AMOUNT
			+ " TEXT NOT NULL, "
			+ TRANS_DESCRIPTION
			+ " TEXT, "
			+ TRANS_CURRENCY
			+ " TEXT NOT NULL, " + TRANS_BD_ACCOUNT + " TEXT NOT NULL)";

	private static final String DB_CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ CATEGORIES_TABLE
			+ " ( "
			+ CAT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CAT_NAME
			+ " TEXT NOT NULL, " + "UNIQUE( " + CAT_NAME + " ) " + " )";

	private static final String DB_CREATE_TAGS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TAGS_TABLE
			+ " ( "
			+ TAG_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TAG_NAME
			+ " TEXT NOT NULL, " + "UNIQUE ( " + TAG_NAME + " )" + " )";

	private static final String DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ CATEGORIES_TAGS_TABLE
			+ " ( "
			+ CAT_FK
			+ " INTEGER NOT NULL, "
			+ TAG_FK_1
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ CAT_FK
			+ ") REFERENCES "
			+ CATEGORIES_TABLE
			+ " ("
			+ CAT_ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ TAG_FK_1
			+ ") REFERENCES "
			+ TAGS_TABLE
			+ "( "
			+ TAG_ID
			+ " ) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( "
			+ CAT_FK + ", " + TAG_FK_1 + " )" + ")";

	private static final String DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TRANSACTIONS_TAGS_TABLE
			+ " ( "
			+ TRANS_FK
			+ " INTEGER NOT NULL, "
			+ TAG_FK_2
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ TRANS_FK
			+ ") REFERENCES "
			+ TRANSACTIONS_TABLE
			+ " ("
			+ TRANS_ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ TAG_FK_2
			+ ") REFERENCES "
			+ TAGS_TABLE
			+ "( "
			+ TAG_ID
			+ " ) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( " + TRANS_FK + ", " + TAG_FK_2 + " )" + ")";

	private final String defaultCategoryName;
	private final String defaultTagName;

	private final Context context;

	public DbHelper(final Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		defaultCategoryName = context.getString(R.string.default_category_name);
		defaultTagName = context.getString(R.string.default_tag_name);
	}

	/** {@inheritDoc} */
	@Override
	public void onOpen(final SQLiteDatabase db) {
		if (!db.isReadOnly()) {
			db.execSQL(DbConstants.TURN_ON_FK);
			db.beginTransaction();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.beginTransaction();
		db.execSQL(TURN_ON_FK);

		setupTables(db);

		populateWithDefaultCategoryAndTag(db);

		db.setTransactionSuccessful();
		db.endTransaction();
	}

	private void setupTables(final SQLiteDatabase db) {
		Log.d(TAG, "Creating table with " + DB_CREATE_TRANSACTIONS_TABLE);
		db.execSQL(DB_CREATE_TRANSACTIONS_TABLE);

		Log.d(TAG, "Creating table with " + DB_CREATE_CATEGORIES_TABLE);
		db.execSQL(DB_CREATE_CATEGORIES_TABLE);

		Log.d(TAG, "Creating table with " + DB_CREATE_TAGS_TABLE);
		db.execSQL(DB_CREATE_TAGS_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE);
		db.execSQL(DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE);
		db.execSQL(DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE);
	}

	private void populateWithDefaultCategoryAndTag(final SQLiteDatabase db) {

		// Add default category
		final ContentValues catValues = new ContentValues(1);
		catValues.put(CAT_NAME, defaultCategoryName);
		final long catId = db.insert(CATEGORIES_TABLE, null, catValues);

		// Add default tag
		final ContentValues tagsValues = new ContentValues(1);
		tagsValues.put(TAG_NAME, defaultTagName);
		final long tagId = db.insert(TAGS_TABLE, null, tagsValues);

		// Add the values to the join table
		final ContentValues catTagsJoinValues = new ContentValues(2);
		catTagsJoinValues.put(CAT_FK, catId);
		catTagsJoinValues.put(TAG_FK_1, tagId);
		db.insert(CATEGORIES_TAGS_TABLE, null, catTagsJoinValues);

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		final SharedPreferences.Editor editor = prefs.edit();

		// Commit to preferences
		editor.putLong(CONF_DEF_CAT, catId);
		editor.putLong(CONF_DEF_TAG, tagId);
		editor.commit();

		Log.d(TAG, "Added default values for Category and Tag");
	}

	/** {@inheritDoc} */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		//		db.execSQL(DB_UPGRADE_TRANSACTIONS_TABLE);
		//		db.execSQL(DB_CREATE_TRANSACTIONS_TABLE);
	}

}