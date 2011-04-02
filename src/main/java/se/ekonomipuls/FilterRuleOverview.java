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

import roboguice.activity.RoboActivity;
import se.ekonomipuls.model.EkonomipulsUtil;

import com.google.inject.Inject;

import android.os.Bundle;

/**
 * @author Magnus Andersson
 * @since 13 feb 2011
 */
public class FilterRuleOverview extends RoboActivity implements LogTag {

	@Inject
	private EkonomipulsUtil util;

	/** {@inheritDoc} */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_rules_overview); // Injection doesn't
														// happen until you
		// call setContentView()
		util.removeGradientBanding(getWindow());
	}

}
