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
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 16 feb 2011
 */
final class DbHelper extends SQLiteOpenHelper implements DbConstants,
		PropertiesConstants, LogTag {
	private static final String DB_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Transactions.TABLE
			+ " ( "
			+ Transactions.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Transactions.GLOBAL_ID
			+ " STRING NOT NULL, "
			+ Transactions.DATE
			+ " TEXT NOT NULL, "
			+ Transactions.AMOUNT
			+ " TEXT NOT NULL, "
			+ Transactions.DESCRIPTION
			+ " TEXT NOT NULL, "
			+ Transactions.COMMENT
			+ " TEXT, "
			+ Transactions.CURRENCY
			+ " TEXT NOT NULL, "
			+ Transactions.FILTERED
			+ " INTEGER NOT NULL DEFAULT 0, "
			+ Transactions.BD_ACCOUNT
			+ " TEXT NOT NULL, "
			+ "UNIQUE( "
			+ Transactions.GLOBAL_ID
			+ " ) "
			+ ")";

	private static final String DB_CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Categories.TABLE
			+ " ( "
			+ Categories.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Categories.NAME
			+ " TEXT NOT NULL, "
			+ Categories.COLOR
			+ " INTEGER NOT NULL, "
			+ "UNIQUE( " + Categories.NAME + " ) " + " )";

	private static final String DB_CREATE_TAGS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Tags.TABLE
			+ " ( "
			+ Tags.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Tags.NAME
			+ " TEXT NOT NULL, " + "UNIQUE ( " + Tags.NAME + " )" + " )";

	private static final String DB_CREATE_REPORTS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Reports.TABLE
			+ " ( "
			+ Reports.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Reports.NAME
			+ " TEXT NOT NULL, "
			+ Reports.DESC
			+ " TEXT, "
			+ Reports.DATE_FROM
			+ " TEXT NOT NULL, " + Reports.DATE_TO + " TEXT NOT NULL " + " )";

	private static final String DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Joins.CATEGORIES_TAGS_TABLE
			+ " ( "
			+ Joins.CAT_FK_1
			+ " INTEGER NOT NULL, "
			+ Joins.TAG_FK_1
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ Joins.CAT_FK_1
			+ ") REFERENCES "
			+ Categories.TABLE
			+ " ("
			+ Categories.ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ Joins.TAG_FK_1
			+ ") REFERENCES "
			+ Tags.TABLE
			+ "( "
			+ Tags.ID
			+ " ) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( "
			+ Joins.CAT_FK_1 + ", " + Joins.TAG_FK_1 + " )" + ")";

	private static final String DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Joins.TRANSACTIONS_TAGS_TABLE
			+ " ( "
			+ Joins.TRANS_FK
			+ " INTEGER NOT NULL, "
			+ Joins.TAG_FK_2
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ Joins.TRANS_FK
			+ ") REFERENCES "
			+ Transactions.TABLE
			+ " ("
			+ Transactions.ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ Joins.TAG_FK_2
			+ ") REFERENCES "
			+ Tags.TABLE
			+ "( "
			+ Tags.ID
			+ " ) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( "
			+ Joins.TRANS_FK + ", " + Joins.TAG_FK_2 + " )" + ")";

	private static final String DB_CREATE_REPORTS_CATEGORIES_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Joins.REPORTS_CATEGORIES_TABLE
			+ " ( "
			+ Joins.REP_FK
			+ " INTEGER NOT NULL, "
			+ Joins.CAT_FK_2
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ Joins.REP_FK
			+ ") REFERENCES "
			+ Reports.TABLE
			+ " ("
			+ Reports.ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ Joins.CAT_FK_2
			+ ") REFERENCES "
			+ Categories.TABLE
			+ "( "
			+ Categories.ID
			+ " ) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( "
			+ Joins.REP_FK + ", " + Joins.CAT_FK_2 + " )" + ")";

	private static final String DB_CREATE_TRANSACTIONS_CATEGORY_VIEW = "CREATE VIEW IF NOT EXISTS "
			+ Views.TRANSACTIONS_CATEGORY_VIEW
			+ " AS "
			+ "SELECT "
			+ Transactions.TABLE
			+ "."
			+ Transactions.ID
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.GLOBAL_ID
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.DATE
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.DESCRIPTION
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.COMMENT
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.AMOUNT
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.CURRENCY
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.BD_ACCOUNT
			+ ", "
			+ Transactions.TABLE
			+ "."
			+ Transactions.FILTERED
			+ ", "
			+ Categories.TABLE
			+ "."
			+ Categories.ID
			+ " AS "
			+ Views.TRANS_CAT_V_CAT_ID
			+ " FROM "
			+ Categories.TABLE
			+ " INNER JOIN "
			+ Joins.CATEGORIES_TAGS_TABLE
			+ " ON "
			+ Categories.TABLE
			+ "."
			+ Categories.ID
			+ " = "
			+ Joins.CATEGORIES_TAGS_TABLE
			+ "."
			+ Joins.CAT_FK_1
			+ " INNER JOIN "
			+ Joins.TRANSACTIONS_TAGS_TABLE
			+ " ON "
			+ Joins.CATEGORIES_TAGS_TABLE
			+ "."
			+ Joins.TAG_FK_1
			+ " = "
			+ Joins.TRANSACTIONS_TAGS_TABLE
			+ "."
			+ Joins.TAG_FK_2
			+ " INNER JOIN "
			+ Transactions.TABLE
			+ " ON "
			+ Joins.TRANSACTIONS_TAGS_TABLE
			+ "."
			+ Joins.TRANS_FK
			+ " = "
			+ Transactions.TABLE + "." + Transactions.ID;

	private static final String DB_CREATE_CATEGORIES_REPORT_VIEW = "CREATE VIEW IF NOT EXISTS "
			+ Views.CATEGORIES_REPORT_VIEW
			+ " AS "
			+ "SELECT "
			+ Categories.TABLE
			+ "."
			+ Categories.ID
			+ ", "
			+ Categories.TABLE
			+ "."
			+ Categories.NAME
			+ ", "
			+ Categories.TABLE
			+ "."
			+ Categories.COLOR
			+ ", "
			+ Reports.TABLE
			+ "."
			+ Reports.ID
			+ " AS "
			+ Views.REP_CAT_REP_ID
			+ " FROM "
			+ Reports.TABLE
			+ " INNER JOIN "
			+ Joins.REPORTS_CATEGORIES_TABLE
			+ " ON "
			+ Reports.TABLE
			+ "."
			+ Reports.ID
			+ " = "
			+ Joins.REPORTS_CATEGORIES_TABLE
			+ "."
			+ Joins.REP_FK
			+ " INNER JOIN "
			+ Categories.TABLE
			+ " ON "
			+ Joins.REPORTS_CATEGORIES_TABLE
			+ "."
			+ Joins.CAT_FK_2
			+ " = "
			+ Categories.TABLE + "." + Categories.ID;

	private final String defaultCategoryName;
	private final String defaultTagName;

	private final Context context;

	private final String reportName;

	private final String reportDesc;

	private final String reportFrom;

	private final String reportTo;

	public DbHelper(final Context context) {
		// LeaklessCursorFactory gives a cursor that also closes the database.
		// This is a hack: http://stackoverflow.com/questions/4547461/closing-the-database-in-a-contentprovider
		super(context, DB_NAME, new LeaklessCursorFactory(), DB_VERSION);
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
		// The transaction is closed in LeaklessCursor#close()
		if (!db.isReadOnly()) {
			db.execSQL(TURN_ON_FK);
			Log.v(TAG, "Beginning transaction for " + db.toString());
			db.beginTransaction();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL(TURN_ON_FK); // Has to be outside an transaction
		db.beginTransaction();

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
		final ContentValues catValues = new ContentValues(2);
		catValues.put(Categories.NAME, defaultCategoryName);
		catValues.put(Categories.COLOR, Color.GRAY);
		final long catId = db.insert(Categories.TABLE, null, catValues);

		// Add default tag
		final ContentValues tagsValues = new ContentValues(1);
		tagsValues.put(Tags.NAME, defaultTagName);
		final long tagId = db.insert(Tags.TABLE, null, tagsValues);

		// Add the values to the join table
		final ContentValues catTagsJoinValues = new ContentValues(2);
		catTagsJoinValues.put(Joins.CAT_FK_1, catId);
		catTagsJoinValues.put(Joins.TAG_FK_1, tagId);
		db.insert(Joins.CATEGORIES_TAGS_TABLE, null, catTagsJoinValues);

		// Add the default report on the home screen
		final ContentValues reportValues = new ContentValues(4);
		reportValues.put(Reports.NAME, reportName);
		reportValues.put(Reports.DESC, reportDesc);
		reportValues.put(Reports.DATE_FROM, reportFrom);
		reportValues.put(Reports.DATE_TO, reportTo);
		final long repId = db.insert(Reports.TABLE, null, reportValues);

		// Add the default category to the default report
		final ContentValues repCatJoinValues = new ContentValues(2);
		repCatJoinValues.put(Joins.REP_FK, repId);
		repCatJoinValues.put(Joins.CAT_FK_2, catId);
		db.insert(Joins.REPORTS_CATEGORIES_TABLE, null, repCatJoinValues);

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
		//		db.execSQL(DB_UPGRADE_Transactions.TABLE);
		//		db.execSQL(DB_CREATE_Transactions.TABLE);
	}

}