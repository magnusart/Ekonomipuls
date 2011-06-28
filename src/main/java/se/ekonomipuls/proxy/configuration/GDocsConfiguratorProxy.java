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
package se.ekonomipuls.proxy.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import roboguice.inject.InjectResource;
import se.ekonomipuls.LogTag;
import se.ekonomipuls.R;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * @author Magnus Andersson
 * @since 15 jun 2011
 */
public class GDocsConfiguratorProxy implements LogTag, ConfiguratorProxy {
	@InjectResource(R.string.gdocs_filter_rules_document_url)
	private String url;

	public final Map<String, List<AddFilterRuleAction>> getFilterRules() {
		final String jsonResult = queryRESTurl(url);
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<AddCategoryAction> getCategories() throws JsonIOException,
			JsonSyntaxException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<AddTagAction>> getTags() throws JsonIOException,
			JsonSyntaxException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	String queryRESTurl(final String url) {
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpGet httpget = new HttpGet(url);
		String response = null;
		final ResponseHandler<String> handler = new BasicResponseHandler();

		try {
			response = httpclient.execute(httpget, handler);
			Log.i(TAG, "Status:[" + response + "]");

		} catch (final ClientProtocolException e) {
			Log.e(TAG, "There was a protocol based error", e);
		} catch (final IOException e) {
			Log.e(TAG, "There was an IO Stream related error", e);
		}
		return response;
	}

}
