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
package se.ekonomipuls.model;

import android.content.ContentValues;
import android.database.Cursor;
import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Joins;
import se.ekonomipuls.proxy.BankDroidTransaction;

import java.math.BigDecimal;
import java.util.List;

import com.google.inject.Singleton;

import static se.ekonomipuls.database.analytics.AnalyticsDbConstants.*;
import static se.ekonomipuls.database.staging.StagingDbConstants.Staging;

/**
 * This utility class is responsible for mapping between model objects and SQL.
 * 
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
@Singleton
public class ModelSqlMapper {

	public ContentValues[] mapBankDroidTransactionSql(
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
	 * @param transId
	 * @param tagId
	 * @return
	 */
	public ContentValues mapTransactionTagJoinSql(final long transId,
			final long tagId) {
		final ContentValues values = new ContentValues(2);
		values.put(Joins.TRANS_FK, transId);
		values.put(Joins.TAG_FK_2, tagId);

		return values;
	}

	/**
	 * @param action
	 */
	public ContentValues mapCategorySql(final AddCategoryReportAction action) {
		return mapCategorySql(action.getCategory());
	}

	/**
	 * @param category
	 */
	public ContentValues mapCategorySql(final Category category) {
		final ContentValues values = new ContentValues(3);

		// Add the category
		values.put(Categories.ID, category.getId());
		values.put(Categories.NAME, category.getName());
		values.put(Categories.COLOR, category.getColor());

		return values;
	}

	/**
	 * @param repId
	 * @param catId
	 * @return
	 */
	public ContentValues mapCategoryReportSql(final long repId, final long catId) {
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
	public Category mapCategoryModel(final Cursor cur, final int[] indices) {
		final int id = cur.getInt(indices[0]);
		final int color = cur.getInt(indices[1]);
		final String name = cur.getString(indices[2]);

		return new Category(id, color, name);
	}

	/**
	 * 
	 * @param trans
	 * @return
	 */
	public ContentValues mapTransactionSql(final Transaction trans) {
		final ContentValues values = new ContentValues(8);

		values.put(Transactions.GLOBAL_ID, trans.getGlobalId());
		values.put(Transactions.DATE, trans.getDate());
		values.put(Transactions.DESCRIPTION, trans.getDescription());
		values.put(Transactions.COMMENT, trans.getComment());
		values.put(Transactions.AMOUNT, trans.getAmount().toString());
		values.put(Transactions.CURRENCY, trans.getCurrency());
		values.put(Transactions.FILTERED, trans.isFiltered());
		values.put(Transactions.VERIFIED, trans.isVerified());
		values.put(Transactions.BD_ACCOUNT, trans.getBankdroidAccount());

		return values;
	}

	/**
	 * 
	 * @param cur
	 * @param indices
	 * @return
	 */
	public Transaction mapTransactionModel(final Cursor cur, final int[] indices) {
		final int id = cur.getInt(indices[0]);
		// FIXME Something seems wrong with the indices here? index 1 is missing
		final String glob = cur.getString(indices[1]);
		final String date = cur.getString(indices[2]);
		final String desc = cur.getString(indices[3]);
		final String cmnt = cur.getString(indices[4]);
		final BigDecimal amt = new BigDecimal(cur.getString(indices[5]));
		final String curr = cur.getString(indices[6]);
		final boolean filt = (cur.getInt(indices[7]) != 0) ? true : false;
		final boolean verif = (cur.getInt(indices[8]) != 0) ? true : false;
		final String bdAcc = cur.getString(indices[9]);

		return new Transaction(id, glob, date, desc, cmnt, amt, curr, filt,
				verif, bdAcc);
	}

	public ContentValues mapTagSql(final Tag tag) {
		final ContentValues values = new ContentValues(2);

		values.put(Tags.ID, tag.getId());
		values.put(Tags.NAME, tag.getName());

		return values;
	}

	/**
	 * 
	 * @param cur
	 * @param indices
	 * @return
	 */
	public Tag mapTagModel(final Cursor cur, final int[] indices) {
		final int id = cur.getInt(indices[0]);
		final String name = cur.getString(indices[1]);

		return new Tag(id, name);
	}

	/**
	 * @param report
	 * @return
	 */
	public ContentValues mapReportSql(final Report report) {
		final ContentValues values = new ContentValues(5);

		values.put(Reports.ID, report.getId());
		values.put(Reports.NAME, report.getName());
		values.put(Reports.DESC, report.getDescription());
		values.put(Reports.DATE_FROM, report.getFrom());
		values.put(Reports.DATE_TO, report.getUntil());

		return values;
	}

	/**
	 * @param filterRule
	 * @return
	 */
	public ContentValues mapFilterRuleSql(final FilterRule filterRule) {
		final ContentValues values = new ContentValues(6);

		values.put(FilterRules.ID, filterRule.getId());
		values.put(FilterRules.NAME, filterRule.getName());
		values.put(FilterRules.DESC, filterRule.getDescription());
		values.put(FilterRules.PATTERN, filterRule.getPattern());
		values.put(FilterRules.MARK_FILTER, filterRule.isMarkFiltered());
		values.put(FilterRules.PRIORITY, filterRule.getPriority());

		return values;
	}

	/**
	 * @param cur
	 * @param indices
	 * @return
	 */
	public FilterRule mapFilterRuleModel(final Cursor cur, final int[] indices) {
		final long id = cur.getLong(indices[0]);
		final String name = cur.getString(indices[1]);
		final String desc = cur.getString(indices[2]);
		final String pattern = cur.getString(indices[3]);
		final boolean markFilter = (cur.getInt(indices[4]) != 0) ? true : false;
		final int priority = cur.getInt(indices[5]);

		return new FilterRule(id, name, desc, pattern, null, markFilter,
				priority);
	}

	/**
	 * @param catId
	 * @param tagId
	 * @return
	 */
	public ContentValues mapCategoryTagsSql(final long catId, final long tagId) {
		final ContentValues values = new ContentValues(2);

		values.put(Joins.CAT_FK_1, catId);
		values.put(Joins.TAG_FK_1, tagId);

		return values;
	}

	/**
	 * @param repId
	 * @param catId
	 * @return
	 */
	public ContentValues mapReportCategoriesSql(final long repId,
			final long catId) {
		final ContentValues values = new ContentValues(2);

		values.put(Joins.REP_FK, repId);
		values.put(Joins.CAT_FK_2, catId);

		return values;
	}

	/**
	 * @param repId
	 * @param tagId
	 * @return
	 */
	public ContentValues mapFilterRuleTagSql(final long ruleId, final long tagId) {
		final ContentValues values = new ContentValues(2);

		values.put(Joins.FILTER_RULE_FK, ruleId);
		values.put(Joins.TAG_FK_3, tagId);

		return values;
	}

	private int[] getIndices(final Cursor cur, final String[] columns) {
		final int[] indices = new int[columns.length];

		for (int i = 0; i < indices.length; i++) {
			indices[i] = cur.getColumnIndexOrThrow(columns[i]);
		}

		return indices;
	}

	/**
	 * @param cur
	 * @return
	 */
	public int[] getTransactionCursorIndices(final Cursor cur) {
		return getIndices(cur, Transactions.COLUMNS);
	}

	/**
	 * @param cur
	 * @return
	 */
	public int[] getCategoryCursorIndices(final Cursor cur) {
		return getIndices(cur, Categories.COLUMNS);
	}

	/**
	 * @param cur
	 * @return
	 */
	public int[] getTagsCursorIndices(final Cursor cur) {
		return getIndices(cur, Tags.COLUMNS);
	}

	/**
	 * @param cur
	 * @return
	 */
	public int[] getFilterRuleCursorIndices(final Cursor cur) {
		return getIndices(cur, FilterRules.COLUMNS);
	}
}
