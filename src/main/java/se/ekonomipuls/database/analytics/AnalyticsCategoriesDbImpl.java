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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.database.AbstractDbFacade;
import se.ekonomipuls.database.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.ModelSqlMapper;

import java.util.ArrayList;
import java.util.List;

import static se.ekonomipuls.database.analytics.AnalyticsDbConstants.*;

public class AnalyticsCategoriesDbImpl extends AbstractDbFacade implements AnalyticsCategoriesDbFacade {

	/** {@inheritDoc} */
	@Override
	public List<Category> getCategoriesByReport(final long reportId) {
		return getCategories(Views.CATEGORIES_REPORT_VIEW, Views.REP_CAT_REP_ID + " = " + reportId);
	}

	/** {@inheritDoc} */
	@Override
	public List<Category> getAllCategories() {
		return getCategories(Categories.TABLE, null);
	}

	private List<Category> getCategories(final String table, final String selection) {

		final String[] selectionArgs = null;
		final String having = null;
		final String sortOrder = null;
		final String[] columns = Categories.COLUMNS;

		final AnalyticsDbHelper helper = new AnalyticsDbHelper();
		final SQLiteDatabase db = helper.getReadableDatabase();

		final List<Category> categories = new ArrayList<Category>();

		try {
			final Cursor cur = query(db, table, columns, selection,
					selectionArgs, having, sortOrder, null);

			final int[] indices = ModelSqlMapper.getCategoryCursorIndices(cur);

			while (cur.moveToNext()) {
				categories.add(ModelSqlMapper.mapCategoryModel(cur, indices));
			}

			cur.close();
		} finally {
			shutdownDb(db, helper);
		}
		return categories;
	}

	/** {@inheritDoc} */
	@Override
	public void insertAssignCategoryReport(final AddCategoryReportAction action) {

		final AnalyticsDbHelper helper = new AnalyticsDbHelper();
		final SQLiteDatabase db = helper.getWritableDatabase();

		try {
			ContentValues values = ModelSqlMapper.mapCategorySql(action);

			final long catId = insert(db, Categories.TABLE, values);

			final long reportId = action.getReportId();

			values = ModelSqlMapper.mapCategoryReportSql(reportId, catId);

			insert(db, Joins.REPORTS_CATEGORIES_TABLE, values);

			db.setTransactionSuccessful();

		} finally {
			shutdownDb(db, helper);
		}
	}

}