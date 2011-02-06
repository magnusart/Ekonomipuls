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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import se.ekonomipuls.LogTag;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public abstract class AbstractChartView extends View implements LogTag {

	protected final Context context;
	protected List<SeriesEntry> series;
	protected BigDecimal seriesTotal;
	protected int position;

	private ListAdapter adapter;

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
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AbstractChartView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	//	/** {@inheritDoc} */
	//	@Override
	//	public ListAdapter getAdapter() {
	//		return adapter;
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	public void setAdapter(final ListAdapter adapter) {
	//		this.adapter = adapter;
	//		requestLayout();
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	public View getSelectedView() {
	//		throw new UnsupportedOperationException("Not supported");
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	public void setSelection(final int position) {
	//		throw new UnsupportedOperationException("Not supported");
	//	}

	//	/** {@inheritDoc} */
	//	@Override
	//	protected void onLayout(final boolean changed, final int left,
	//			final int top, final int right, final int bottom) {
	//		// if we don't have an adapter, we don't need to do anything
	//		if (adapter == null) {
	//			return;
	//		}
	//
	//		Log.d(TAG, "In onLayout.");
	//		Log.d(TAG, "Changed = " + changed);
	//		Log.d(TAG, "Left = " + left);
	//		Log.d(TAG, "top = " + top);
	//		Log.d(TAG, "right = " + right);
	//		Log.d(TAG, "bottom = " + bottom);
	//
	//		final View view = adapter.getView(0, this, this);
	//		view.forceLayout();
	//
	//	}

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
