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
public class DbFacade implements LogTag, DbConstants {

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
				final ContentValues values = new ContentValues(6);

				values.put(TRANS_GLOBAL_ID, trans.getId());
				values.put(TRANS_DATE, trans.getDate());
				values.put(TRANS_DESCRIPTION, trans.getDescription());
				values.put(TRANS_AMOUNT, trans.getAmount().toString());
				values.put(TRANS_CURRENCY, trans.getCurrency());
				values.put(TRANS_BD_ACCOUNT, trans.getAccountId());

				db.insert(TRANSACTIONS_TABLE, null, values);
			}
			// Transaction begun in DbHelper.open();
			db.setTransactionSuccessful();
		} finally {
			shutdownDb(db, dbHelper);
		}
	}

	/**
	 * @param modTrans
	 * @param defaultTagId
	 */
	public static void modifyTransactionsAssignTags(final Context ctx,
			final Transaction transaction, final long tagId) {
		final DbHelper dbHelper = new DbHelper(ctx);
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues(3);
			values.put(TRANS_COMMENT, transaction.getDescription());
			values.put(TRANS_CURRENCY, transaction.getCurrency());
			final int filtered = (transaction.isFiltered()) ? 1 : 0;
			values.put(TRANS_FILTERED, filtered);

			db.update(TRANSACTIONS_TABLE, values, TRANS_ID + " = "
					+ transaction.getId(), null);

			values = new ContentValues(2);
			values.put(TRANS_FK, transaction.getId());
			values.put(TAG_FK_2, tagId);

			db.insert(TRANSACTIONS_TAGS_TABLE, null, values);

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
	public static List<Transaction> getTransactionsByAccount(final Context ctx,
			final String bdAccountId) {
		return getTransactions(ctx, TRANS_BD_ACCOUNT + " = ? ",
				new String[] { bdAccountId });
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static List<Transaction> getUnfilteredTransactions(final Context ctx) {
		return getTransactions(ctx, TRANS_FILTERED + " = 0 ", null);
	}

	private static List<Transaction> getTransactions(final Context ctx,
			final String selection, final String[] selectionArgs) {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		final DbHelper dbHelper = new DbHelper(ctx);
		final SQLiteDatabase db = dbHelper.getReadableDatabase();

		try {
			final Cursor cur = db.query(TRANSACTIONS_TABLE, new String[] {
					TRANS_ID, TRANS_GLOBAL_ID, TRANS_DATE, TRANS_DESCRIPTION,
					TRANS_COMMENT, TRANS_AMOUNT, TRANS_CURRENCY,
					TRANS_FILTERED, TRANS_BD_ACCOUNT }, selection,
					selectionArgs, null, null, TRANS_DATE + " DESC");

			final int tId = cur.getColumnIndexOrThrow(TRANS_ID);
			final int tGlob = cur.getColumnIndexOrThrow(TRANS_GLOBAL_ID);
			final int tDate = cur.getColumnIndexOrThrow(TRANS_DATE);
			final int tDesc = cur.getColumnIndexOrThrow(TRANS_DESCRIPTION);
			final int tCmnt = cur.getColumnIndexOrThrow(TRANS_COMMENT);
			final int tAmt = cur.getColumnIndexOrThrow(TRANS_AMOUNT);
			final int tCur = cur.getColumnIndexOrThrow(TRANS_CURRENCY);
			final int tFilt = cur.getColumnIndexOrThrow(TRANS_FILTERED);
			final int tBdAcc = cur.getColumnIndexOrThrow(TRANS_BD_ACCOUNT);

			while (cur.moveToNext()) {
				final int id = cur.getInt(tId);
				final String glob = cur.getString(tGlob);
				final String date = cur.getString(tDate);
				final String desc = cur.getString(tDesc);
				final String cmnt = cur.getString(tCmnt);
				final BigDecimal amt = new BigDecimal(cur.getString(tAmt));
				final String curr = cur.getString(tCur);
				final boolean filt = (cur.getInt(tFilt) != 0) ? true : false;
				final String bdAcc = cur.getString(tBdAcc);

				final Transaction trans = new Transaction(id, glob, date, desc,
						cmnt, amt, curr, filt, bdAcc);

				transactions.add(trans);
			}
			cur.close();
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
			final Cursor cur = db.query(CATEGORIES_TABLE, new String[] {
					CAT_ID, CAT_NAME }, null, null, null, null, null);

			final int tId = cur.getColumnIndexOrThrow(CAT_ID);
			final int tName = cur.getColumnIndexOrThrow(CAT_NAME);

			while (cur.moveToNext()) {
				final int id = cur.getInt(tId);
				final String name = cur.getString(tName);

				final Category cat = new Category(id, name);

				categories.add(cat);

			}
			cur.close();
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
