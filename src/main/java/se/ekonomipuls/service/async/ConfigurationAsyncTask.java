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
package se.ekonomipuls.service.async;

import roboguice.inject.InjectResource;
import roboguice.util.RoboAsyncTask;
import se.ekonomipuls.R;
import se.ekonomipuls.service.AndroidApiUtil;
import se.ekonomipuls.service.ConfigurationService;
import android.app.ProgressDialog;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 17 jul 2011
 */
public class ConfigurationAsyncTask extends RoboAsyncTask<Boolean> {

	@InjectResource(R.string.home_menu_sync_filer_rules_dialog_message)
	protected String importMessage;

	@InjectResource(R.string.error_configuration_service_async)
	protected String errorMessage;

	@InjectResource(R.string.success_configuration_service_async)
	protected String successMessage;

	@Inject
	ConfigurationService service;

	@Inject
	AndroidApiUtil util;

	private ProgressDialog dialog;

	/** {@inheritDoc} */
	@Override
	protected void onPreExecute() {
		if (dialog instanceof ProgressDialog) {
			dialog.setMessage(importMessage);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
			dialog.show();
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void onFinally() throws RuntimeException {
		if ((dialog instanceof ProgressDialog) && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/** {@inheritDoc} */
	@Override
	public Boolean call() throws Exception {
		return service.importRemoteFilterRulesLocalCategoriesTags();
	}

	/** {@inheritDoc} */
	@Override
	protected void onSuccess(final Boolean t) throws Exception {
		if (!t) {
			// FIXME: Do not use toast but use a notification bar at the bottom
			// instead.
			util.toastMessage(errorMessage);
		} else {
			util.toastMessage(successMessage);
		}
	}

	/**
	 * @param dialog
	 */
	public void setDialog(final ProgressDialog dialog) {
		this.dialog = dialog;
	}

}
