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
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants;
import se.ekonomipuls.database.staging.StagingDbConstants;
import se.ekonomipuls.proxy.BankDroidTransaction;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * This utility class is responsible for mapping model objects to SQL and vice
 * versa.
 * 
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public class ModelSqlMapper implements LogTag, AnalyticsDbConstants,
		StagingDbConstants {

	public static ContentValues[] mapBankDroidTransactionSql(
			final List<BankDroidTransaction> transactions) {
		final ContentValues[] values = new ContentValues[transactions.size()];
		int i = 0;
		for (final BankDroidTransaction trans : transactions) {
			final ContentValues value = new ContentValues(6);

			value.put(Staging.GLOBAL_ID, trans.getId());
			value.put(Staging.DATE, trans.getDate());
			value.put(Staging.DESCRIPTION, trans.getDescription());
			value.put(Staging.AMOUNT, trans.getAmount().toString());
			value.put(Staging.CURRENCY, trans.getCurrency());
			value.put(Staging.BD_ACCOUNT, trans.getAccountId());

			values[i] = value;
			i++;
		}

		return values;
	}

	/**
	 * @param transaction
	 * @return
	 */
	public static ContentValues mapTransactionFilterSql(
			final Transaction transaction) {
		final ContentValues values = new ContentValues(1);
		final int filtered = (transaction.isFiltered()) ? 1 : 0;
		values.put(Transactions.FILTERED, filtered);

		return values;
	}

	/**
	 * @param transId
	 * @param tagId
	 * @return
	 */
	public static ContentValues mapTransactionTagJoinSql(final long transId,
			final long tagId) {
		final ContentValues values = new ContentValues(2);
		values.put(Joins.TRANS_FK, transId);
		values.put(Joins.TAG_FK_2, tagId);

		return values;
	}

	/**
	 * @param action
	 */
	public static ContentValues mapCategorySql(
			final AddCategoryReportAction action) {
		final ContentValues values = new ContentValues(2);

		// Add the category
		values.put(Categories.NAME, action.getCategory().getName());
		values.put(Categories.COLOR, action.getCategory().getColor());

		return values;
	}

	/**
	 * @param action
	 * @param catId
	 * @return
	 */
	public static ContentValues mapCategoryReportSql(final long repId,
			final long catId) {
		// Add the category to the report
		final ContentValues values = new ContentValues(2);
		values.put(Joins.REP_FK, repId);
		values.put(Joins.CAT_FK_2, catId);

		return values;
	}

	/**
	 * 
	 * @param cur
	 * @param indices
	 * @return
	 */
	public static Category mapCategoryModel(final Cursor cur,
			final int[] indices) {
		final int id = cur.getInt(indices[0]);
		final int color = cur.getInt(indices[1]);
		final String name = cur.getString(indices[2]);

		return new Category(id, color, name);
	}

	/**
	 * @param cur
	 * @return
	 */
	public static int[] getCategoryCursorIndices(final Cursor cur) {
		final int[] indices = new int[3];

		indices[0] = cur.getColumnIndexOrThrow(Categories.ID);
		indices[1] = cur.getColumnIndexOrThrow(Categories.COLOR);
		indices[2] = cur.getColumnIndexOrThrow(Categories.NAME);

		return indices;
	}

	/**
	 * 
	 * @param cur
	 * @param indices
	 * @return
	 */
	public static Transaction mapTransactionModel(final Cursor cur,
			final int[] indices) {
		final int id = cur.getInt(indices[0]);
		final String glob = cur.getString(indices[2]);
		final String date = cur.getString(indices[3]);
		final String desc = cur.getString(indices[4]);
		final String cmnt = cur.getString(indices[5]);
		final BigDecimal amt = new BigDecimal(cur.getString(indices[6]));
		final String curr = cur.getString(indices[7]);
		final boolean filt = (cur.getInt(indices[8]) != 0) ? true : false;
		final String bdAcc = cur.getString(indices[9]);

		return new Transaction(id, glob, date, desc, cmnt, amt, curr, filt,
				bdAcc);
	}

	/**
	 * @param cur
	 * @return
	 */
	public static int[] getTransactionCursorIndices(final Cursor cur) {
		final int[] indices = new int[10];

		indices[0] = cur.getColumnIndexOrThrow(Transactions.ID);
		indices[1] = cur.getColumnIndexOrThrow(Transactions.GLOBAL_ID);
		indices[3] = cur.getColumnIndexOrThrow(Transactions.DATE);
		indices[4] = cur.getColumnIndexOrThrow(Transactions.DESCRIPTION);
		indices[5] = cur.getColumnIndexOrThrow(Transactions.COMMENT);
		indices[6] = cur.getColumnIndexOrThrow(Transactions.AMOUNT);
		indices[7] = cur.getColumnIndexOrThrow(Transactions.CURRENCY);
		indices[8] = cur.getColumnIndexOrThrow(Transactions.FILTERED);
		indices[9] = cur.getColumnIndexOrThrow(Transactions.BD_ACCOUNT);

		return indices;
	}

}
