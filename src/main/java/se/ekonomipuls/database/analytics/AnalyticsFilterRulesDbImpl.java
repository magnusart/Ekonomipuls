/**
 * Copyright 2011 Magnus Andersson, Michael Svensson
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
package se.ekonomipuls.database.analytics;

import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.database.AnalyticsFilterRulesDbFacade;
import se.ekonomipuls.database.abstr.AbstractDb;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.FilterRules;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Joins;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Tags;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Views;
import se.ekonomipuls.model.FilterRule;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.model.Tag;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public class AnalyticsFilterRulesDbImpl extends AbstractDb implements
		AnalyticsFilterRulesDbFacade {

	@Inject
	private AnalyticsDbHelper helper;

	@Inject
	private ModelSqlMapper mapper;

	/** {@inheritDoc} */
	@Override
	public List<FilterRule> getFilterRules() {

		final String table = FilterRules.TABLE;
		final String selection = null;
		final String[] selectionArgs = null;
		final String groupBy = null;
		final String having = null;
		final String sortOrder = FilterRules.PRIORITY + " DESC";
		final String[] columns = FilterRules.COLUMNS;

		final String view = Views.FILTER_RULES_TAGS_FROM_STMT;

		final String[] viewColumns = prefixColumns(Tags.TABLE, Tags.COLUMNS);
		final String viewSortOrder = null;

		final SQLiteDatabase db = helper.getReadableDatabase();

		final List<FilterRule> rules = new ArrayList<FilterRule>();

		try {
			final Cursor cur = query(db, table, columns, selection,
					selectionArgs, groupBy, having, sortOrder);

			final int[] indices = mapper.getFilterRuleCursorIndices(cur);

			while (cur.moveToNext()) {
				final FilterRule rule = mapper.mapFilterRuleModel(cur, indices);

				final String viewSelection = FilterRules.TABLE + "."
						+ FilterRules.ID + " = " + rule.getId();
				;

				final Cursor cur2 = query(db, view, viewColumns, viewSelection,
						selectionArgs, groupBy, having, viewSortOrder);

				final int[] tagIndices = mapper.getTagsCursorIndices(cur2);

				while (cur2.moveToNext()) {
					final Tag tag = mapper.mapTagModel(cur2, tagIndices);
					rule.setTag(tag);
				}

				rules.add(rule);

				cur2.close();
			}

			cur.close();
		} finally {
			shutdownDb(db, helper);
		}

		return rules;
	}

	/** {@inheritDoc} */
	@Override
	public long insertFilterRule(final AddFilterRuleAction action) {
		final SQLiteDatabase db = helper.getWritableDatabase();

		try {
			final long ruleId = insertFilterRuleCore(action, db);

			db.setTransactionSuccessful();

			return ruleId;
		} finally {
			shutdownDb(db, helper);
		}
	}

	/**
	 * @param action
	 * @param db
	 * @return
	 */
	long insertFilterRuleCore(final AddFilterRuleAction action,
			final SQLiteDatabase db) {

		ContentValues values = mapper.mapFilterRuleSql(action);
		values.remove(FilterRules.ID); // We do not want this when inserting
		final long ruleId = db.insert(FilterRules.TABLE, null, values);

		values = mapper.mapFilterRuleTagSql(ruleId, action.getTagId());
		db.insert(Joins.FILTER_RULES_TAGS_TABLE, null, values);

		return ruleId;
	}

}
