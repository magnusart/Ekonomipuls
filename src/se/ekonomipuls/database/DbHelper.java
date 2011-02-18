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
import se.ekonomipuls.PropertiesConstants;
import se.ekonomipuls.R;
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
final class DbHelper extends SQLiteOpenHelper implements DbConstants,
		PropertiesConstants, LogTag {
	private static final String DB_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TRANSACTIONS_TABLE
			+ " ( "
			+ TRANS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TRANS_GLOBAL_ID
			+ " STRING NOT NULL, "
			+ TRANS_DATE
			+ " TEXT NOT NULL, "
			+ TRANS_AMOUNT
			+ " TEXT NOT NULL, "
			+ TRANS_DESCRIPTION
			+ " TEXT NOT NULL, "
			+ TRANS_COMMENT
			+ " TEXT, "
			+ TRANS_CURRENCY
			+ " TEXT NOT NULL, "
			+ TRANS_FILTERED
			+ " INTEGER NOT NULL DEFAULT 0, "
			+ TRANS_BD_ACCOUNT
			+ " TEXT NOT NULL, " + "UNIQUE( " + TRANS_GLOBAL_ID + " ) " + ")";

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

	private static final String DB_CREATE_REPORTS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ REPORTS_TABLE
			+ " ( "
			+ REP_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ REP_NAME
			+ " TEXT NOT NULL, "
			+ REP_DESC
			+ " TEXT, "
			+ REP_DATE_FROM
			+ " TEXT NOT NULL, " + REP_DATE_TO + " TEXT NOT NULL " + " )";

	private static final String DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ CATEGORIES_TAGS_TABLE
			+ " ( "
			+ CAT_FK_1
			+ " INTEGER NOT NULL, "
			+ TAG_FK_1
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ CAT_FK_1
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
			+ CAT_FK_1 + ", " + TAG_FK_1 + " )" + ")";

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

	private static final String DB_CREATE_REPORTS_CATEGORIES_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ REPORTS_CATEGORIES_TABLE
			+ " ( "
			+ REP_FK
			+ " INTEGER NOT NULL, "
			+ CAT_FK_2
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ REP_FK
			+ ") REFERENCES "
			+ REPORTS_TABLE
			+ " ("
			+ REP_ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ CAT_FK_2
			+ ") REFERENCES "
			+ CATEGORIES_TABLE
			+ "( "
			+ CAT_ID
			+ " ) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( "
			+ REP_FK + ", " + CAT_FK_2 + " )" + ")";

	private static final String DB_CREATE_TRANSACTIONS_CATEGORY_VIEW = "CREATE VIEW IF NOT EXISTS "
			+ TRANSACTIONS_CATEGORY_VIEW
			+ " AS "
			+ "SELECT "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_ID
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_GLOBAL_ID
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_DATE
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_DESCRIPTION
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_COMMENT
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_AMOUNT
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_CURRENCY
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_BD_ACCOUNT
			+ ", "
			+ TRANSACTIONS_TABLE
			+ "."
			+ TRANS_FILTERED
			+ ", "
			+ CATEGORIES_TABLE
			+ "."
			+ CAT_ID
			+ " AS "
			+ TRANS_CAT_V_CAT_ID
			+ " FROM "
			+ CATEGORIES_TABLE
			+ " INNER JOIN "
			+ CATEGORIES_TAGS_TABLE
			+ " ON "
			+ CATEGORIES_TABLE
			+ "."
			+ CAT_ID
			+ " = "
			+ CATEGORIES_TAGS_TABLE
			+ "."
			+ CAT_FK_1
			+ " INNER JOIN "
			+ TRANSACTIONS_TAGS_TABLE
			+ " ON "
			+ CATEGORIES_TAGS_TABLE
			+ "."
			+ TAG_FK_1
			+ " = "
			+ TRANSACTIONS_TAGS_TABLE
			+ "."
			+ TAG_FK_2
			+ " INNER JOIN "
			+ TRANSACTIONS_TABLE
			+ " ON "
			+ TRANSACTIONS_TAGS_TABLE
			+ "."
			+ TRANS_FK
			+ " = "
			+ TRANSACTIONS_TABLE + "." + TRANS_ID;

	private static final String DB_CREATE_CATEGORIES_REPORT_VIEW = "CREATE VIEW IF NOT EXISTS "
			+ CATEGORIES_REPORT_VIEW
			+ " AS "
			+ "SELECT "
			+ CATEGORIES_TABLE
			+ "."
			+ CAT_ID
			+ ", "
			+ CATEGORIES_TABLE
			+ "."
			+ CAT_NAME
			+ ", "
			+ REPORTS_TABLE
			+ "."
			+ REP_ID
			+ " AS "
			+ REP_CAT_REP_ID
			+ " FROM "
			+ REPORTS_TABLE
			+ " INNER JOIN "
			+ REPORTS_CATEGORIES_TABLE
			+ " ON "
			+ REPORTS_TABLE
			+ "."
			+ REP_ID
			+ " = "
			+ REPORTS_CATEGORIES_TABLE
			+ "."
			+ REP_FK
			+ " INNER JOIN "
			+ CATEGORIES_TABLE
			+ " ON "
			+ REPORTS_CATEGORIES_TABLE
			+ "."
			+ CAT_FK_2 + " = " + CATEGORIES_TABLE + "." + CAT_ID;

	private final String defaultCategoryName;
	private final String defaultTagName;

	private final Context context;

	private final String reportName;

	private final String reportDesc;

	private final String reportFrom;

	private final String reportTo;

	public DbHelper(final Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		defaultCategoryName = context.getString(R.string.default_category_name);
		defaultTagName = context.getString(R.string.default_tag_name);
		reportName = context.getString(R.string.economic_overview_name);
		reportDesc = context.getString(R.string.economic_overview_desc);
		reportFrom = context.getString(R.string.economic_overview_date_from);
		reportTo = context.getString(R.string.economic_overview_date_to);
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

		Log.d(TAG, "Creating table with " + DB_CREATE_REPORTS_TABLE);
		db.execSQL(DB_CREATE_REPORTS_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE);
		db.execSQL(DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE);
		db.execSQL(DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_REPORTS_CATEGORIES_JOIN_TABLE);
		db.execSQL(DB_CREATE_REPORTS_CATEGORIES_JOIN_TABLE);

		Log.d(TAG, "Creating Transactions by Category View with "
				+ DB_CREATE_TRANSACTIONS_CATEGORY_VIEW);
		db.execSQL(DB_CREATE_TRANSACTIONS_CATEGORY_VIEW);

		Log.d(TAG, "Creating Categories by Report View wiht "
				+ DB_CREATE_CATEGORIES_REPORT_VIEW);
		db.execSQL(DB_CREATE_CATEGORIES_REPORT_VIEW);
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
		catTagsJoinValues.put(CAT_FK_1, catId);
		catTagsJoinValues.put(TAG_FK_1, tagId);
		db.insert(CATEGORIES_TAGS_TABLE, null, catTagsJoinValues);

		// Add the default report on the home screen
		final ContentValues reportValues = new ContentValues(4);
		reportValues.put(REP_NAME, reportName);
		reportValues.put(REP_DESC, reportDesc);
		reportValues.put(REP_DATE_FROM, reportFrom);
		reportValues.put(REP_DATE_TO, reportTo);
		final long repId = db.insert(REPORTS_TABLE, null, reportValues);

		// Add the default category to the default report
		final ContentValues repCatJoinValues = new ContentValues(2);
		repCatJoinValues.put(REP_FK, repId);
		repCatJoinValues.put(CAT_FK_2, catId);
		db.insert(REPORTS_CATEGORIES_TABLE, null, repCatJoinValues);

		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();

		// Commit to preferences
		editor.putLong(CONF_DEF_CAT, catId);
		editor.putLong(CONF_DEF_TAG, tagId);
		editor.putLong(ECONOMIC_OVERVIEW_REPORT_ID, repId);
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