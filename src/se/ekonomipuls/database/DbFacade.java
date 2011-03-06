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
import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

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
			final List<BankDroidTransaction> transactions)
			throws RemoteException {
		final Uri uri = Uri.parse(Provider.TRANSACTIONS_URI);
		final ContentProviderClient client = ctx.getContentResolver()
				.acquireContentProviderClient(uri);
		try {

			final ContentValues[] values = new ContentValues[transactions
					.size()];
			int i = 0;
			for (final BankDroidTransaction trans : transactions) {
				final ContentValues value = new ContentValues(6);

				value.put(Transactions.GLOBAL_ID, trans.getId());
				value.put(Transactions.DATE, trans.getDate());
				value.put(Transactions.DESCRIPTION, trans.getDescription());
				value.put(Transactions.AMOUNT, trans.getAmount().toString());
				value.put(Transactions.CURRENCY, trans.getCurrency());
				value.put(Transactions.BD_ACCOUNT, trans.getAccountId());

				values[i] = value;
				i++;
			}

			client.bulkInsert(uri, values);

		} finally {
			client.release();
		}
	}

	/**
	 * @param action
	 * @throws RemoteException
	 */
	public static void insertAssignCategoryReport(final Context ctx,
			final AddCategoryReportAction action) throws RemoteException {

		final Uri uriRep = Uri.parse(Provider.CATEGORIES_URI);
		final Uri uriCatRep = Uri.parse(Provider.CATEGORIES_REPORT_URI);
		final ContentProviderClient client = ctx.getContentResolver()
				.acquireContentProviderClient(uriCatRep);

		try {
			ContentValues values = new ContentValues(2);

			// Add the category
			values.put(Categories.NAME, action.getCategory().getName());
			values.put(Categories.COLOR, action.getCategory().getColor());

			final Uri uriId = client.insert(uriRep, values);

			// Add the category to the report
			values = new ContentValues(2);
			values.put(Joins.REP_FK, action.getReportId());
			values.put(Joins.CAT_FK_2,
					Long.parseLong(uriId.getPathSegments().get(1)));

			client.insert(uriCatRep, values);

		} finally {
			client.release();
		}
	}

	/**
	 * @param modTrans
	 * @param defaultTagId
	 * @throws RemoteException
	 */
	public static void updateTransactionsAssignTags(final Context ctx,
			final List<ApplyFilterTagAction> actions) throws RemoteException {

		final Uri uri = Uri.parse(Provider.TRANSACTIONS_TAGS_URI);

		final ContentProviderClient client = ctx.getContentResolver()
				.acquireContentProviderClient(uri);

		try {

			final ContentValues[] values = new ContentValues[actions.size()];
			int i = 0;

			for (final ApplyFilterTagAction action : actions) {
				final Transaction transaction = action.getTransaction();
				final long tagId = action.getTagId();

				final ContentValues value = new ContentValues(3);
				final int filtered = (transaction.isFiltered()) ? 1 : 0;
				value.put(Transactions.FILTERED, filtered);
				value.put(Joins.TRANS_FK, transaction.getId());
				value.put(Joins.TAG_FK_2, tagId);

				values[i] = value;
				i++;
			}

			client.bulkInsert(uri, values);

		} finally {
			client.release();
		}
	}

	/**
	 * 
	 * @param ctx
	 * @param bdAccountId
	 * @return
	 * @throws RemoteException
	 */
	public static List<Transaction> getAllTransactions(final Context ctx)
			throws RemoteException {
		final Uri uri = Uri.parse(Provider.TRANSACTIONS_URI);
		return getTransactions(ctx, uri, null, null);
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 * @throws RemoteException
	 */
	public static List<Transaction> getUnfilteredTransactions(final Context ctx)
			throws RemoteException {

		final Uri uri = Uri.parse(Provider.TRANSACTIONS_URI);
		return getTransactions(ctx, uri, Transactions.FILTERED + " = 0", null);
	}

	/**
	 * 
	 * @param ctx
	 * @param cat
	 * @return
	 * @throws RemoteException
	 */
	public static List<Transaction> getTransactionsByCategory(
			final Context ctx, final Category cat) throws RemoteException {
		final Uri uri = Uri.parse(Provider.TRANSACTIONS_CATEGORY_URI);
		return getTransactions(ctx, uri,
				Views.TRANS_CAT_V_CAT_ID + " = " + cat.getId(), null);
	}

	private static List<Transaction> getTransactions(final Context ctx,
			final Uri uri, final String selection, final String[] selectionArgs)
			throws RemoteException {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		final ContentProviderClient client = ctx.getContentResolver()
				.acquireContentProviderClient(uri);

		try {
			final Cursor cur = client.query(uri, null, selection,
					selectionArgs, Transactions.DATE + " DESC");

			final int tId = cur.getColumnIndexOrThrow(Transactions.ID);
			final int tGlob = cur.getColumnIndexOrThrow(Transactions.GLOBAL_ID);
			final int tDate = cur.getColumnIndexOrThrow(Transactions.DATE);
			final int tDesc = cur
					.getColumnIndexOrThrow(Transactions.DESCRIPTION);
			final int tCmnt = cur.getColumnIndexOrThrow(Transactions.COMMENT);
			final int tAmt = cur.getColumnIndexOrThrow(Transactions.AMOUNT);
			final int tCur = cur.getColumnIndexOrThrow(Transactions.CURRENCY);
			final int tFilt = cur.getColumnIndexOrThrow(Transactions.FILTERED);
			final int tBdAcc = cur
					.getColumnIndexOrThrow(Transactions.BD_ACCOUNT);

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
		} catch (final IllegalArgumentException e) {
			Log.e(TAG, "Could not find required database columns", e);
		} catch (final RuntimeException e) {
			Log.e(TAG, "Unkown runtime exception in DbFacade.", e);
			throw e;
		} finally {
			client.release();
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
	 * @param report
	 * @return
	 * @throws RemoteException
	 */
	public static List<Category> getCategoriesByReport(final Context ctx,
			final long reportId) throws RemoteException {
		final Uri uri = Uri.parse(Provider.CATEGORIES_REPORT_URI);
		return getCategories(ctx, uri, Views.REP_CAT_REP_ID + " = " + reportId,
				null);
	}

	/**
	 * @param ctx
	 * @return
	 * @throws RemoteException
	 */
	public static List<Category> getAllCategories(final Context ctx)
			throws RemoteException {
		final Uri uri = Uri.parse(Provider.CATEGORIES_URI);
		return getCategories(ctx, uri, null, null);
	}

	private static List<Category> getCategories(final Context ctx,
			final Uri uri, final String selection, final String[] selectionArgs)
			throws RemoteException {

		final List<Category> categories = new ArrayList<Category>();

		final ContentProviderClient client = ctx.getContentResolver()
				.acquireContentProviderClient(uri);

		Cursor cur;
		try {
			cur = client.query(uri, null, selection, selectionArgs, null);

			final int tId = cur.getColumnIndexOrThrow(Categories.ID);
			final int tColor = cur.getColumnIndexOrThrow(Categories.COLOR);
			final int tName = cur.getColumnIndexOrThrow(Categories.NAME);

			while (cur.moveToNext()) {
				final int id = cur.getInt(tId);
				final int color = cur.getInt(tColor);
				final String name = cur.getString(tName);

				final Category cat = new Category(id, color, name);

				categories.add(cat);

			}
			cur.close();
		} finally {
			client.release();
		}
		return categories;
	}

}
