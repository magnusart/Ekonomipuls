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
package se.ekonomipuls.tasks;

import se.ekonomipuls.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

/**
 * @author Magnus Andersson
 * @since 15 mar 2011
 */
public class ImportStagingTransactionsTask extends AsyncTask<Void, Void, Void> {

	private final Context context;
	private final ProgressDialog dialog;
	private final Resources res;

	/**
	 * 
	 */
	public ImportStagingTransactionsTask(final Context context) {
		this.context = context;
		dialog = new ProgressDialog(context);
		res = context.getResources();

	}

	/** {@inheritDoc} */
	@Override
	protected void onPreExecute() {
		dialog.setMessage(res.getText(R.string.dialog_stage_import_message));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		dialog.show();
	}

	/** {@inheritDoc} */
	@Override
	protected Void doInBackground(final Void... params) {
		try {
			Thread.sleep(3000);
		} catch (final InterruptedException e) {
			return null;
		}

		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected void onPostExecute(final Void result) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
