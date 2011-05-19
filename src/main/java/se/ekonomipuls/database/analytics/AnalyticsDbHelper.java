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
package se.ekonomipuls.database.analytics;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.database.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.database.AnalyticsTagsDbFacade;
import se.ekonomipuls.database.abstr.AbstractDbHelper;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.FilterRule;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.model.Report;
import se.ekonomipuls.model.Tag;
import se.ekonomipuls.proxy.InitialConfiguratorProxy;

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.database.analytics.AnalyticsDbConstants.*;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
@Singleton
public class AnalyticsDbHelper extends AbstractDbHelper implements
		AnalyticsDbScripts {

	final Context context = contextProvider.get();

	@Inject
	EkonomipulsUtil util;

	@Inject
	ModelSqlMapper mapper;

	@Inject
	InitialConfiguratorProxy config;

	@Inject
	Provider<AnalyticsCategoriesDbFacade> categoriesProvider;

	@Inject
	Provider<AnalyticsTagsDbFacade> tagsProvider;

	public AnalyticsDbHelper() {
		super(ANALYTICS_DB_NAME, null, ANALYTICS_DB_VERSION);
	}

	/** {@inheritDoc} */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {

	}

	/** {@inheritDoc} */
	@Override
	protected void createTables(final SQLiteDatabase db) {
		Log.d(TAG, "Creating table with " + DB_CREATE_TRANSACTIONS_TABLE);
		db.execSQL(DB_CREATE_TRANSACTIONS_TABLE);

		Log.d(TAG, "Creating table with " + DB_CREATE_CATEGORIES_TABLE);
		db.execSQL(DB_CREATE_CATEGORIES_TABLE);

		Log.d(TAG, "Creating table with " + DB_CREATE_TAGS_TABLE);
		db.execSQL(DB_CREATE_TAGS_TABLE);

		Log.d(TAG, "Creating table with " + DB_CREATE_REPORTS_TABLE);
		db.execSQL(DB_CREATE_REPORTS_TABLE);

		Log.d(TAG, "Creating Filter Rules table with "
				+ DB_CREATE_FILTER_RULES_TABLE);
		db.execSQL(DB_CREATE_FILTER_RULES_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE);
		db.execSQL(DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE);
		db.execSQL(DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_REPORTS_CATEGORIES_JOIN_TABLE);
		db.execSQL(DB_CREATE_REPORTS_CATEGORIES_JOIN_TABLE);

		Log.d(TAG, "Creating join table with "
				+ DB_CREATE_FILTER_RULES_TAGS_JOIN_TABLE);
		db.execSQL(DB_CREATE_FILTER_RULES_TAGS_JOIN_TABLE);

		Log.d(TAG, "Creating Transactions by Category View with "
				+ DB_CREATE_TRANSACTIONS_CATEGORY_VIEW);
		db.execSQL(DB_CREATE_TRANSACTIONS_CATEGORY_VIEW);

		Log.d(TAG, "Creating Categories by Report View with "
				+ DB_CREATE_CATEGORIES_REPORT_VIEW);
		db.execSQL(DB_CREATE_CATEGORIES_REPORT_VIEW);

		Log.d(TAG, "Creating Categories by Report View with "
				+ DB_CREATE_FILTER_RULE_TAGS_VIEW);
		db.execSQL(DB_CREATE_FILTER_RULE_TAGS_VIEW);

	}

	/** {@inheritDoc} */
	@Override
	protected void initTables(final SQLiteDatabase db) {

		Tag tag = util.getDefaultExpenseTag();
		ContentValues values = mapper.mapTagSql(tag);
		values.remove(Tags.ID); // We do not want this when inserting
		final long expensesTagId = db.insert(Tags.TABLE, null, values);

		tag = util.getDefaultIncomeTag();
		values = mapper.mapTagSql(tag);
		values.remove(Tags.ID); // We do not want this when inserting
		final long incomesTagId = db.insert(Tags.TABLE, null, values);

		final Report report = util.getDefaultReport();
		values = mapper.mapReportSql(report);
		values.remove(Reports.ID); // We do not want this when inserting
		final long repId = db.insert(Reports.TABLE, null, values);

		Log.d(TAG, "Adding Categories");

		try {
			final List<AddCategoryAction> categoryActions = config
					.getCategories();

			final Map<String, List<AddTagAction>> tagsActions = config
					.getTags();

			// Assign Categories to Report
			for (final AddCategoryAction categoryAction : categoryActions) {
				// Make sure we have corresponding tags
				if (tagsActions.containsKey(categoryAction.getName())) {

					Log.d(TAG, "Assigning Category " + categoryAction.getName()
							+ " to default report");

					final AddCategoryReportAction action = new AddCategoryReportAction(
							categoryAction, repId);

					final AnalyticsCategoriesDbImpl categoriesFacade = (AnalyticsCategoriesDbImpl) categoriesProvider
							.get();

					final long catId = categoriesFacade
							.insertAssignCategoryReportCore(action, db);

					final List<AddTagAction> tagActions = tagsActions
							.get(categoryAction.getName());

					// Assign tags to categories
					for (final AddTagAction tagAction : tagActions) {
						Log.d(TAG, "Assigning Tag " + tagAction.getName()
								+ " to Category " + categoryAction.getName());

						final AnalyticsTagsDbImpl tagsFacade = (AnalyticsTagsDbImpl) tagsProvider
								.get();

						final long tagId = tagsFacade
								.insertAssignTagCategoryCore(tagAction, catId, db);
					}
				}
			}

		} catch (final JsonIOException e) {
			e.printStackTrace();
		} catch (final JsonSyntaxException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		Category category = util.getDefaultExpenseCategory();
		values = mapper.mapCategorySql(category);
		values.remove(Categories.ID); // We do not want this when inserting
		final long expensesCatId = db.insert(Categories.TABLE, null, values);

		category = util.getDefaultIncomesCategory();
		values = mapper.mapCategorySql(category);
		values.remove(Categories.ID); // We do not want this when inserting
		final long incomesCatId = db.insert(Categories.TABLE, null, values);

		util.setDefaults(expensesTagId, expensesCatId, incomesTagId, incomesCatId, repId);
		Log.d(TAG, "Added default values for Category and Tag");

		FilterRule rule = util.getDefaultExpensesFilterRule(util
				.getDefaultExpenseTag());
		values = mapper.mapFilterRuleSql(rule);
		values.remove(FilterRules.ID); // We do not want this when inserting
		final long expensesRuleId = db.insert(FilterRules.TABLE, null, values);

		rule = util.getDefaultIncomesFilterRule(util.getDefaultExpenseTag());
		values = mapper.mapFilterRuleSql(rule);
		values.remove(FilterRules.ID); // We do not want this when inserting
		final long incomesRuleId = db.insert(FilterRules.TABLE, null, values);

		values = mapper.mapCategoryTagsSql(expensesCatId, expensesTagId);
		db.insert(Joins.CATEGORIES_TAGS_TABLE, null, values);

		values = mapper.mapCategoryTagsSql(incomesCatId, incomesTagId);
		db.insert(Joins.CATEGORIES_TAGS_TABLE, null, values);

		values = mapper.mapReportCategoriesSql(repId, expensesCatId);
		db.insert(Joins.REPORTS_CATEGORIES_TABLE, null, values);

		values = mapper.mapReportCategoriesSql(repId, incomesCatId);
		db.insert(Joins.REPORTS_CATEGORIES_TABLE, null, values);

		values = mapper.mapFilterRuleTagSql(expensesRuleId, expensesTagId);
		db.insert(Joins.FILTER_RULES_TAGS_TABLE, null, values);

		values = mapper.mapFilterRuleTagSql(incomesRuleId, incomesTagId);
		db.insert(Joins.FILTER_RULES_TAGS_TABLE, null, values);

	}

	/** {@inheritDoc} */
	@Override
	protected void initConfiguration() {

	}
}
