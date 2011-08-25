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
import se.ekonomipuls.service.ExtractTransformLoadService;
import android.app.ProgressDialog;
import android.os.Handler.Callback;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 15 mar 2011
 */
public class ExtractTransformLoadAsyncTask extends RoboAsyncTask<Boolean> {

	@InjectResource(R.string.dialog_stage_import_message)
	protected String importMessage;

	@Inject
	ExtractTransformLoadService service;

	private ProgressDialog dialog;

	private Callback callback;

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
	protected void onSuccess(final Boolean t) throws Exception {
		if (callback instanceof Callback) {
			callback.handleMessage(null);
		}
	}

	/** {@inheritDoc} */
	public Boolean call() throws Exception {
		return service.performETL();
	}

	/**
	 * @param callback
	 */
	public void setCallback(final Callback callback) {
		this.callback = callback;
	}

	/**
	 * @param dialog
	 */
	public void setDialog(final ProgressDialog dialog) {
		this.dialog = dialog;
	}

}
