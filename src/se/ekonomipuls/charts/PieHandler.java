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
package se.ekonomipuls.charts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.ekonomipuls.LogTag;
import android.util.Log;
import android.webkit.WebView;

/**
 * @author Magnus Andersson
 * @since 10 jan 2011
 */
public class PieHandler implements LogTag {

	private final WebView chartWebView;
	private Slice[] slices;
	private final PieChartConfiguration conf;

	/**
	 * Handler class that acts a interface to the webview and charts
	 * 
	 * @param chartWebView
	 *            Budget WebView
	 */
	public PieHandler(final WebView chartWebView,
						final PieChartConfiguration configuration,
						final Slice[] slices) {
		this.chartWebView = chartWebView;
		this.slices = slices;
		this.conf = configuration;
	}

	public void pieClick(final String label) {
		Log.d(TAG, "User picked label: " + label);
	}

	/**
	 * Load the graph with the slices.
	 */
	public void loadGraph() {

		try {
			final JSONArray data = dataToFlowData();
			final JSONObject series = flowPiePluginSettings();

			Log.d(TAG, data.toString(4));
			Log.d(TAG, series.toString(4));

			chartWebView.loadUrl("javascript:gotGraph(" + data.toString()
					+ ", " + series.toString() + ")");
		} catch (final Exception ex) {
			// do something
		}

	}

	/**
	 * @return the slices
	 */
	public Slice[] getSlices() {
		return slices;
	}

	/**
	 * @param slices
	 *            the slices to set
	 */
	public void setSlices(final Slice[] slices) {
		this.slices = slices;
	}

	private JSONObject flowPiePluginSettings() throws JSONException {
		final JSONObject root = new JSONObject();
		final JSONObject series = new JSONObject();
		final JSONObject stroke = new JSONObject();
		final JSONObject pie = new JSONObject();

		final JSONObject legend = new JSONObject();

		stroke.put("width", conf.getSeriesPieStrokeWidth());
		stroke.put("color", conf.getSeriesPieStrokeColor());
		pie.put("show", true);
		pie.put("radius", conf.getSeriesPieRadius());
		pie.put("stroke", stroke);
		series.put("pie", pie);

		legend.put("show", true);

		root.put("series", series);
		root.put("legend", legend);

		return root;
	}

	private JSONArray dataToFlowData() throws JSONException {
		final JSONArray dataArray = new JSONArray();

		for (final Slice s : slices) {
			final JSONObject slice = new JSONObject();
			slice.put("data", s.getAmount());
			slice.put("label", s.getLabel());
			slice.put("color", s.getColor());
			dataArray.put(slice);
		}

		return dataArray;
	}
}
