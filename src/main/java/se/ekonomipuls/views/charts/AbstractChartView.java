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
package se.ekonomipuls.views.charts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public abstract class AbstractChartView extends View {

	protected final Context context;
	protected List<SeriesEntry> series;
	protected BigDecimal seriesTotal;
	protected int position;

	{
		series = new ArrayList<SeriesEntry>();
		seriesTotal = new BigDecimal(0.0);
	}

	/**
	 * @param context
	 */
	public AbstractChartView(final Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AbstractChartView(final Context context, final AttributeSet attrs,
								final int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AbstractChartView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	/**
	 * @param series
	 */
	public void setSeries(final List<SeriesEntry> series) {
		this.series = series;

	}

	/**
	 * @return the series
	 */
	public List<SeriesEntry> getSeries() {
		return series;
	}

	/**
	 * @return the totalAmt
	 */
	public BigDecimal getTotalAmt() {
		return seriesTotal;
	}

	/**
	 * @param seriesTotal
	 *            the totalAmt to set
	 */
	public void setSeriesTotal(final BigDecimal seriesTotal) {
		this.seriesTotal = seriesTotal;
	}

}
