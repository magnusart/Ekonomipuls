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
package se.ekonomipuls.charting;

/**
 * 
 * @author Magnus Andersson
 * @since 15 jan 2011
 */
public class BudgetPieChartFactory {
	private BudgetPieChartFactory() {
		// Private constructor
	}

	/**
	 * Get default Budget PieChartConfiguration.
	 * 
	 * @param strokeColor
	 *            the color of the stroke
	 * @param strokeWidth
	 *            the width of the stroke
	 * @param pieRadius
	 *            the radius of the pie chart
	 * @param labelRadius
	 *            the radius of the labels (in relation to the chart radius)
	 * @return the PieChartConfiguration
	 * 
	 * @see PieChartConfiguration
	 */
	public static PieChartConfiguration getConfiguration(
			final String strokeColor, final int strokeWidth,
			final double pieRadius, final double labelRadius) {
		final boolean seriesPieShow = true;
		final boolean labelShow = true;
		final boolean legendShow = false;
		final PieChartConfiguration config = new PieChartConfiguration(
				strokeColor, strokeWidth, pieRadius, seriesPieShow,
				labelRadius, labelShow, legendShow);
		return config;
	}
}
