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

import se.ekonomipuls.actions.AddCategoryReportAction;
import se.ekonomipuls.database.analytics.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.util.EkonomipulsUtil;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * @author Magnus Andersson
 * @since 21 feb 2011
 */
public class AddEditCategory extends Activity implements LogTag {

	/** {@inheritDoc} */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_category);

	}

	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 
	 * @param v
	 */
	public void addEditCategory(final View v) {

		final EditText categoryName = (EditText) findViewById(R.id.editCategoryName);
		final String name = categoryName.getEditableText().toString();

		final AddCategoryReportAction categoryReport = new AddCategoryReportAction(
				Color.CYAN, name, EkonomipulsUtil.getEconomicOverviewId(this));

		AnalyticsCategoriesDbFacade.insertAssignCategoryReport(this,
				categoryReport);

		// Return from method.
		finish();
	}

}
