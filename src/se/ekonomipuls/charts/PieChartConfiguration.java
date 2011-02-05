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

/**
 * JSON Structure for PieChart Flot. Use builder to setup.
 * 
 * @author Magnus Andersson
 * @since 13 jan 2011
 */
public class PieChartConfiguration {

	final private String seriesPieStrokeColor;
	final private int seriesPieStrokeWidth;
	final private double seriesPieRadius;
	final private boolean seriesPieShow;
	final private double seriesLabelRadius;
	final private boolean seriesLabelShow;
	final private boolean ledgendShow;

	PieChartConfiguration(final String seriesPieStrokeColor, final int seriesPieStrokeWidth,
			final double seriesPieRadius, final boolean seriesPieShow,
			final double seriesLabelRadius, final boolean seriesLabelShow,
			final boolean ledgendShow) {

		this.seriesPieStrokeColor = seriesPieStrokeColor;
		this.seriesPieStrokeWidth = seriesPieStrokeWidth;
		this.seriesPieRadius = seriesPieRadius;
		this.seriesPieShow = seriesPieShow;
		this.seriesLabelRadius = seriesLabelRadius;
		this.seriesLabelShow = seriesLabelShow;
		this.ledgendShow = ledgendShow;
	}

	/**
	 * @return the seriesPieStrokeColor
	 */
	public String getSeriesPieStrokeColor() {
		return seriesPieStrokeColor;
	}

	/**
	 * @return the seriesPieStrokeWidth
	 */
	public int getSeriesPieStrokeWidth() {
		return seriesPieStrokeWidth;
	}

	/**
	 * @return the seriesPieRadius
	 */
	public double getSeriesPieRadius() {
		return seriesPieRadius;
	}

	/**
	 * @return the seriesPieShow
	 */
	public boolean isSeriesPieShow() {
		return seriesPieShow;
	}

	/**
	 * @return the seriesLabelRadius
	 */
	public double getSeriesLabelRadius() {
		return seriesLabelRadius;
	}

	/**
	 * @return the seriesLabelShow
	 */
	public boolean isSeriesLabelShow() {
		return seriesLabelShow;
	}

	/**
	 * @return the ledgendShow
	 */
	public boolean isLedgendShow() {
		return ledgendShow;
	}
}
