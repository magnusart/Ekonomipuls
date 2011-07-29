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
package se.ekonomipuls.wizard;

import roboguice.activity.RoboActivity;
import se.ekonomipuls.R;
import se.ekonomipuls.model.EkonomipulsUtil;
import android.os.Bundle;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 20 jul 2011
 */
public class WelcomeWizard extends RoboActivity {
	@Inject
	EkonomipulsUtil util;

	/** {@inheritDoc} */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_wizard); // Injection doesn't happen
													// until you
		// call setContentView()
		util.removeGradientBanding(getWindow());
	}

}
