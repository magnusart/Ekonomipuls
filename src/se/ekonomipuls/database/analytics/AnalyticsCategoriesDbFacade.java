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

import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.database.AbstractDbFacade;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.ModelSqlMapper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;

public class AnalyticsCategoriesDbFacade extends AbstractDbFacade implements
		LogTag, AnalyticsDbConstants {

	/**
	 * 
	 * @param ctx
	 * @param report
	 * @return
	 * @throws RemoteException
	 */
	public static List<Category> getCategoriesByReport(final Context ctx,
			final long reportId) throws RemoteException {
		return getCategories(ctx, Views.CATEGORIES_REPORT_VIEW,
				Views.REP_CAT_REP_ID + " = " + reportId);
	}

	/**
	 * @param ctx
	 * @return
	 * @throws RemoteException
	 */
	public static List<Category> getAllCategories(final Context ctx)
			throws RemoteException {
		return getCategories(ctx, Categories.TABLE, null);
	}

	private static List<Category> getCategories(final Context ctx,
			final String table, final String selection) {

		final String[] selectionArgs = null;
		final String having = null;
		final String sortOrder = null;
		final String[] columns = Categories.COLUMNS;

		final AnalyticsDbHelper helper = new AnalyticsDbHelper(ctx);
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

	/**
	 * @param action
	 * @throws RemoteException
	 */
	public static void insertAssignCategoryReport(final Context ctx,
			final AddCategoryReportAction action) throws RemoteException {

		final AnalyticsDbHelper helper = new AnalyticsDbHelper(ctx);
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