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
package se.ekonomipuls;

import java.util.List;

import se.ekonomipuls.database.analytics.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.util.EkonomipulsUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Magnus Andersson
 * @since 13 feb 2011
 */
public class OverviewSettings extends Activity implements LogTag {

	private static final int ADD_CATEGORY = 0;
	private List<Category> reportCategories;
	private ArrayAdapter<Category> tmpAdapter;

	/** {@inheritDoc} */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview_settings);
		EkonomipulsUtil.removeGradientBanding(getWindow());
		populateCategoriesList();
	}

	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		super.onResume();
		populateCategoriesList();
		tmpAdapter.notifyDataSetInvalidated();
	}

	/**
	 * 
	 */
	private void populateCategoriesList() {
		try {
			final List<Category> allCategories = AnalyticsCategoriesDbFacade
					.getAllCategories(this);

			final long reportId = EkonomipulsUtil.getEconomicOverviewId(this);
			reportCategories = AnalyticsCategoriesDbFacade
					.getCategoriesByReport(this, reportId);

			final ListView categories = (ListView) findViewById(R.id.categoriesList);

			tmpAdapter = new ArrayAdapter<Category>(this,
					R.layout.checkbox_category_row, R.id.categoryNameText,
					allCategories);

			categories.setAdapter(tmpAdapter);
		} catch (final RemoteException e) {
			EkonomipulsUtil.toastDbError(this, e);
		}
	}

	/**
	 * 
	 * @param v
	 */
	public void addCategory(final View v) {
		final Intent i = new Intent(this, AddEditCategory.class);
		startActivityForResult(i, ADD_CATEGORY);
	}

}
