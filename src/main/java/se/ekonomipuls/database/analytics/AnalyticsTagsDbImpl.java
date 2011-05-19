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

import com.google.inject.Inject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.database.AnalyticsTagsDbFacade;
import se.ekonomipuls.database.abstr.AbstractDb;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Joins;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Tags;
import se.ekonomipuls.model.ModelSqlMapper;

/**
 * @author Magnus Andersson
 * @since 20 maj 2011
 */
public class AnalyticsTagsDbImpl extends AbstractDb implements
		AnalyticsTagsDbFacade {
	@Inject
	private AnalyticsDbHelper helper;

	@Inject
	private ModelSqlMapper mapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long insertAssignTagCategory(final AddTagAction action,
			final long catId) {
		final SQLiteDatabase db = helper.getWritableDatabase();

		try {
			final long tagId = insertAssignTagCategoryCore(action, catId, db);

			db.setTransactionSuccessful();

			return tagId;
		} finally {
			shutdownDb(db, helper);
		}
	}

	/**
	 * @param action
	 * @param catId
	 * @param db
	 * @return
	 */
	long insertAssignTagCategoryCore(final AddTagAction action,
			final long catId, final SQLiteDatabase db) {
		ContentValues values = mapper.mapTagSql(action);
		values.remove(Tags.ID);

		final long tagId = db.insert(Tags.TABLE, null, values);

		values = mapper.mapCategoryTagsSql(catId, tagId);
		db.insert(Joins.CATEGORIES_TAGS_TABLE, null, values);

		return tagId;
	}

}
