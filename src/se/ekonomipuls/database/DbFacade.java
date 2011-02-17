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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Magnus Andersson
 * @since 24 jan 2011
 */
public class DbFacade implements LogTag {
	/**
	 * @author Magnus Andersson
	 * @since 24 jan 2011
	 */
	public static interface DbConstants {
		public static final int DB_VERSION = 1;

		public final static String TRANSACTIONS_TABLE = "transactions";
		// Columns for transaction table
		public final static String TRANS_ID = "_id";
		public final static String TRANS_DATE = "t_date";
		public final static String TRANS_DESCRIPTION = "description";
		public static final String TRANS_AMOUNT = "amount";
		public final static String TRANS_CURRENCY = "currency";
		public final static String TRANS_BD_ACCOUNT = "bd_account_id";

		public final static String CATEGORIES_TABLE = "categories";
		// Columns for Categories
		public final static String CAT_ID = "_id";
		public final static String CAT_NAME = "name";

		public static final String TAGS_TABLE = "tags";
		// Columns for Tags
		public final static String TAG_ID = "_id";
		public final static String TAG_NAME = "name";

		public final static String CONF_DEF_CAT = "default.category";
		public final static String CONF_DEF_TAG = "default.tag";

		public final static String CATEGORIES_TAGS_TABLE = "categories_tags";
		// Columns for Categories/Tags join table
		public final static String CAT_FK = "category_fk";
		public final static String TAG_FK_1 = "tag_fk";

		public final static String TRANSACTIONS_TAGS_TABLE = "transactions_tags";
		// Columns for Transactions/Tags join table
		public final static String TRANS_FK = "transaction_fk";
		public final static String TAG_FK_2 = "tag_fk";

		public static final String DB_NAME = "ekonomipuls.db";
		public static final String TURN_ON_FK = "PRAGMA foreign_keys = ON;";

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
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			for (final BankDroidTransaction trans : transactions) {
				final ContentValues values = new ContentValues();

				values.put(DbConstants.TRANS_DATE, trans.getDate());
				values.put(DbConstants.TRANS_DESCRIPTION,
						trans.getDescription());
				values.put(DbConstants.TRANS_AMOUNT, trans.getAmount()
						.toString());
				values.put(DbConstants.TRANS_CURRENCY, trans.getCurrency());
				values.put(DbConstants.TRANS_BD_ACCOUNT, trans.getAccountId());

				db.insert(DbConstants.TRANSACTIONS_TABLE, null, values);
			}
			// Transaction begun in DbHelper.open();
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
		final SQLiteDatabase db = dbHelper.getReadableDatabase();

		try {
			final Cursor cur = db.query(DbConstants.TRANSACTIONS_TABLE,
					new String[] { DbConstants.TRANS_ID,
							DbConstants.TRANS_DATE,
							DbConstants.TRANS_DESCRIPTION,
							DbConstants.TRANS_AMOUNT,
							DbConstants.TRANS_CURRENCY },
					DbConstants.TRANS_BD_ACCOUNT + "= ? ",
					new String[] { bdAccountId }, "", null,
					DbConstants.TRANS_DATE + " DESC");

			final int tId = cur.getColumnIndexOrThrow(DbConstants.TRANS_ID);
			final int tDate = cur.getColumnIndexOrThrow(DbConstants.TRANS_DATE);
			final int tDesc = cur
					.getColumnIndexOrThrow(DbConstants.TRANS_DESCRIPTION);
			final int tAmt = cur
					.getColumnIndexOrThrow(DbConstants.TRANS_AMOUNT);
			final int tCur = cur
					.getColumnIndexOrThrow(DbConstants.TRANS_CURRENCY);

			while (cur.moveToNext()) {
				final int id = cur.getInt(tId);
				final String date = cur.getString(tDate);
				final String desc = cur.getString(tDesc);
				final BigDecimal amt = new BigDecimal(cur.getString(tAmt));
				final String curr = cur.getString(tCur);

				final Transaction trans = new Transaction(id, date, desc, amt,
						curr);

				transactions.add(trans);
			}
		} finally {
			shutdownDb(db, dbHelper);
		}

		return transactions;
	}

	/**
	 * 
	 * @param ctx
	 * @param bdAccountId
	 * @return
	 */
	public static List<Transaction> getTransactions(final Context ctx,
			final List<Category> categories) {

		// FIXME Return transactions based on categories

		return null;
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static List<Category> getCategories(final Context ctx) {
		final DbHelper dbHelper = new DbHelper(ctx);
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		final List<Category> categories = new ArrayList<Category>();

		try {
			final Cursor cur = db.query(DbConstants.CATEGORIES_TABLE,
					new String[] { DbConstants.CAT_ID, DbConstants.CAT_NAME },
					null, null, null, null, null);

			final int tId = cur.getColumnIndexOrThrow(DbConstants.CAT_ID);
			final int tName = cur.getColumnIndexOrThrow(DbConstants.CAT_NAME);

			while (cur.moveToNext()) {
				final int id = cur.getInt(tId);
				final String name = cur.getString(tName);

				final Category cat = new Category(id, name);

				categories.add(cat);
			}

		} finally {
			shutdownDb(db, dbHelper);
		}

		return categories;
	}

	/**
	 * @param db
	 */
	private static void shutdownDb(final SQLiteDatabase db,
			final DbHelper dbHelper) {

		if (!db.isReadOnly()) {
			// Transaction begun in DbHelper.open();
			db.endTransaction();
		}

		db.close();
		dbHelper.close();
	}

}
