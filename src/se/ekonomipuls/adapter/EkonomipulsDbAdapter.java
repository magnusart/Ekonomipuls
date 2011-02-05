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
package se.ekonomipuls.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 24 jan 2011
 */
public class EkonomipulsDbAdapter implements LogTag {
	/**
	 * @author Magnus Andersson
	 * @since 24 jan 2011
	 */
	public static interface DbConstants {
		public static final int DB_VERSION = 1;

		// Columns
		public final static String ID = "_id";
		public final static String DATE = "t_date";
		public final static String DESCRIPTION = "description";
		public static final String AMOUNT = "amount";
		public final static String CURRENCY = "currency";
		public final static String BD_ACCOUNT = "bd_account_id";

		public static final String DB_NAME = "ekonomipuls.db";
		public static final String TRANSACTIONS_TABLE = "transactions";
	}

	private static class DbHelper extends SQLiteOpenHelper implements
			DbConstants, LogTag {
		private static final String DB_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TRANSACTIONS_TABLE
				+ " ( "
				+ ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DATE
				+ " TEXT NOT NULL,"
				+ AMOUNT
				+ " TEXT NOT NULL, "
				+ DESCRIPTION
				+ " TEXT, "
				+ CURRENCY
				+ " TEXT NOT NULL, "
				+ BD_ACCOUNT
				+ " TEXT NOT NULL)";

		private static final String DB_UPGRADE_TABLE = "DROP TABLE IF EXISTS "
				+ TRANSACTIONS_TABLE;

		public DbHelper(final Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/** {@inheritDoc} */
		@Override
		public void onCreate(final SQLiteDatabase db) {
			Log.d(TAG, "Creating table with " + DB_CREATE_TABLE);
			db.execSQL(DB_CREATE_TABLE);
		}

		/** {@inheritDoc} */
		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			db.execSQL(DB_UPGRADE_TABLE);
			db.execSQL(DB_CREATE_TABLE);
		}

	}

	/**
	 * Bulk insert transactions.
	 * 
	 * @param ctx
	 * @param transactions
	 */
	public static void bulkInsertBdTransactions(final Context ctx,
			final List<BankDroidTransaction> transactions) {

		final DbHelper dbHelper = new DbHelper(ctx);
		final SQLiteDatabase db = prepareWriteDb(dbHelper);
		try {
			for (final BankDroidTransaction trans : transactions) {
				final ContentValues values = new ContentValues();

				values.put(DbConstants.DATE, trans.getDate());
				values.put(DbConstants.DESCRIPTION, trans.getDescription());
				values.put(DbConstants.AMOUNT, trans.getAmount().toString());
				values.put(DbConstants.CURRENCY, trans.getCurrency());
				values.put(DbConstants.BD_ACCOUNT, trans.getAccountId());

				db.insert(DbConstants.TRANSACTIONS_TABLE, null, values);
			}

			db.setTransactionSuccessful();
		} finally {
			shutdownDb(db, dbHelper);
		}
	}

	/**
	 * 
	 * @param ctx
	 * @param bdAccountId
	 * @return
	 */
	public static List<Transaction> getTransactions(final Context ctx,
			final String bdAccountId) {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		final DbHelper dbHelper = new DbHelper(ctx);
		final SQLiteDatabase db = prepareReadDb(dbHelper);

		try {
			final Cursor c = db.query(DbConstants.TRANSACTIONS_TABLE,
					new String[] { DbConstants.ID, DbConstants.DATE,
							DbConstants.AMOUNT, DbConstants.CURRENCY },
					DbConstants.BD_ACCOUNT + "= ? ",
					new String[] { bdAccountId }, "", null, DbConstants.DATE
							+ " DESC");

			final int idIdx = c.getColumnIndexOrThrow(DbConstants.ID);
			final int dateIdx = c.getColumnIndexOrThrow(DbConstants.DATE);
			final int descIdx = c
					.getColumnIndexOrThrow(DbConstants.DESCRIPTION);
			final int amtIdx = c.getColumnIndexOrThrow(DbConstants.AMOUNT);
			final int currIdx = c.getColumnIndexOrThrow(DbConstants.CURRENCY);

			while (c.moveToNext()) {
				final Transaction trans = new Transaction(c.getLong(idIdx),
						c.getString(dateIdx), c.getString(descIdx),
						new BigDecimal(c.getString(amtIdx)),
						c.getString(currIdx));

				transactions.add(trans);
			}
		} finally {
			shutdownDb(db, dbHelper);
		}

		return transactions;
	}

	/**
	 * @param db
	 */
	private static void shutdownDb(final SQLiteDatabase db,
			final DbHelper dbHelper) {
		db.endTransaction();
		db.close();
		dbHelper.close();
	}

	/**
	 * @param ctx
	 * @return
	 * 
	 */
	private static SQLiteDatabase prepareWriteDb(final DbHelper dbHelper) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		return db;
	}

	/**
	 * @param dbHelper
	 * @return
	 */
	private static SQLiteDatabase prepareReadDb(final DbHelper dbHelper) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction();
		return db;
	}

}
