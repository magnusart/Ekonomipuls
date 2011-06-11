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

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.database.analytics.AnalyticsDbConstants.ANALYTICS_DB_NAME;
import static se.ekonomipuls.database.analytics.AnalyticsDbConstants.ANALYTICS_DB_VERSION;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.database.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.database.AnalyticsFilterRulesDbFacade;
import se.ekonomipuls.database.AnalyticsTagsDbFacade;
import se.ekonomipuls.database.abstr.AbstractDbHelper;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Reports;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.ModelSqlMapper;
import se.ekonomipuls.model.Report;
import se.ekonomipuls.proxy.configuration.InitialConfiguratorProxy;
import se.ekonomipuls.proxy.configuration.InitialConfiguratorProxy.ConfigurationError;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
	AnalyticsCategoriesDbFacade categoriesFacade;

	@Inject
	AnalyticsTagsDbFacade tagsFacade;

	@Inject
	AnalyticsFilterRulesDbFacade rulesFacade;

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
	}

	/** {@inheritDoc} */
	@Override
	protected void initTables(final SQLiteDatabase db) {
		Log.d(TAG, "Adding Default Report");
		final long repId = initAddDefaultReport(db);

		Log.d(TAG, "Adding Categories");
		try {
			final List<AddCategoryAction> categoryActions = config
					.getCategories();

			final Map<String, List<AddTagAction>> tagsActions = config
					.getTags();

			final Map<String, List<AddFilterRuleAction>> filterRulesActions = config
					.getFilterRules();

			final Map<String, Long> tagIds = new HashMap<String, Long>();

			// Throws error if not successful.
			config.validateConfiguration(categoryActions, tagsActions,
					filterRulesActions, util.getDefaultExpenseTag().getName(),
					util.getDefaultIncomeTag().getName());

			// Assign Categories to Report
			for (final AddCategoryAction categoryAction : categoryActions) {
				Log.d(TAG, "Assigning Category " + categoryAction.getName()
						+ " to default report");

				final long catId = initAddCategoryToReport(db, repId,
						categoryAction);

				final List<AddTagAction> tagActions = tagsActions
						.get(categoryAction.getName());

				// Assign tags to categories
				for (final AddTagAction tagAction : tagActions) {
					Log.d(TAG, "Assigning Tag " + tagAction.getName()
							+ " to Category " + categoryAction.getName());

					final long tagId = initAssignTagToCategory(db, catId,
							tagAction);

					// If there is any Filter Rules that is going to be
					// associated with this tag, insert them.
					if (filterRulesActions.containsKey(tagAction.getName())) {
						final List<AddFilterRuleAction> ruleActions = filterRulesActions
								.get(tagAction.getName());

						initAddFilterRule(db, tagId, ruleActions);
					}

					// Construct temporary map with Tagname -> tagId for
					// later use.
					tagIds.put(tagAction.getName(), tagId);

				}
			}

			util.setDefaults(tagIds, repId);

			Log.d(TAG, "Added default values for Category and Tag");

		} catch (final JsonIOException e) {
			e.printStackTrace();
		} catch (final JsonSyntaxException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ConfigurationError e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param db
	 * @return
	 */
	private long initAddDefaultReport(final SQLiteDatabase db) {
		final Report report = util.getDefaultReport();
		final ContentValues values = mapper.mapReportSql(report);
		values.remove(Reports.ID); // We do not want this when inserting
		final long repId = db.insert(Reports.TABLE, null, values);
		return repId;
	}

	/**
	 * @param db
	 * @param tagIds
	 * @param tagKey
	 * @param ruleActions
	 */
	private void initAddFilterRule(final SQLiteDatabase db, final long tagId,
			final List<AddFilterRuleAction> ruleActions) {
		for (final AddFilterRuleAction ruleAction : ruleActions) {
			Log.d(TAG, "Assigning Filter Rule " + ruleAction.getName()
					+ " with tag id " + tagId);

			ruleAction.setTagId(tagId);

			final AnalyticsFilterRulesDbImpl rules = (AnalyticsFilterRulesDbImpl) rulesFacade;

			rules.insertFilterRuleCore(ruleAction, db);
		}
	}

	/**
	 * @param db
	 * @param catId
	 * @param tagAction
	 * @return
	 */
	private long initAssignTagToCategory(final SQLiteDatabase db,
			final long catId, final AddTagAction tagAction) {
		final AnalyticsTagsDbImpl tags = (AnalyticsTagsDbImpl) tagsFacade;

		final long tagId = tags.insertAssignTagCategoryCore(tagAction, catId,
				db);
		return tagId;
	}

	/**
	 * @param db
	 * @param repId
	 * @param categoryAction
	 * @return
	 */
	private long initAddCategoryToReport(final SQLiteDatabase db,
			final long repId, final AddCategoryAction categoryAction) {
		final AddCategoryReportAction action = new AddCategoryReportAction(
				categoryAction, repId);

		final AnalyticsCategoriesDbImpl categories = (AnalyticsCategoriesDbImpl) categoriesFacade;

		final long catId = categories
				.insertAssignCategoryReportCore(action, db);
		return catId;
	}

	/** {@inheritDoc} */
	@Override
	protected void initConfiguration() {

	}
}
