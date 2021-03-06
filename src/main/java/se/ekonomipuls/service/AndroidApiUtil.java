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
package se.ekonomipuls.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import se.ekonomipuls.LogTag;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class acts as a facade to Android API calls for the services since to
 * simplify testing and mocking of classes.
 * 
 * @author Magnus Andersson
 * @since 1 jul 2011
 */
@Singleton
public class AndroidApiUtil {
	/**
	 * @author Magnus Andersson
	 * @since 19 maj 2011
	 */
	public static enum ConfigurationType {

		CATEGORIES("categories.json"), TAGS("tags.json"), FILTER_RULES(
				"filter_rules.json");

		private final String fileName;

		ConfigurationType(final String fileName) {
			this.fileName = fileName;
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}
	}

	@Inject
	private Context context;

	public String queryRestUrl(final String url) {
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpGet httpget = new HttpGet(url);
		String response = null;
		final ResponseHandler<String> handler = new BasicResponseHandler();

		try {
			response = httpclient.execute(httpget, handler);
			Log.v(LogTag.TAG, "Status:[" + response + "]");

		} catch (final ClientProtocolException e) {
			httpclient.getConnectionManager().shutdown();
			Log.e(LogTag.TAG, "There was a protocol based error", e);
		} catch (final IOException e) {
			httpclient.getConnectionManager().shutdown();
			Log.e(LogTag.TAG, "There was an IO Stream related error", e);
		}
		return response;
	}

	public InputStream queryRestUrlStream(final String url) {
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpGet httpget = new HttpGet(url);

		HttpResponse httpResponse;
		InputStream instream = null;
		try {
			httpResponse = httpclient.execute(httpget);
			final HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				instream = entity.getContent();
			}

		} catch (final ClientProtocolException e) {
			httpclient.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (final IOException e) {
			httpclient.getConnectionManager().shutdown();
			e.printStackTrace();
		}

		return instream;
	}

	public InputStream getConfigurationFile(final ConfigurationType type)
			throws IOException {
		return context.getAssets().open(type.getFileName());

	}

	public void toastMessage(final String message) {
		final int duration = Toast.LENGTH_LONG;

		final Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}
}
