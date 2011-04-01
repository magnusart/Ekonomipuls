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

import static se.ekonomipuls.LogTag.TAG;

import java.util.ArrayList;

import roboguice.activity.RoboActivity;
import se.ekonomipuls.model.EkonomipulsUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.inject.Inject;

/**
 * Pie chart diagram. Use as a normal view.
 * 
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class PieChartView extends AbstractChartView implements OnTouchListener {
	protected static final int STROKE_WIDTH = 4; // TODO: Make this into
													// DIP-units.

	private static final boolean ENABLE_GRADIENT = true;
	private static final boolean SKEW_CHART = false;
	private static final boolean DROP_SHADOW = true;

	private static final int CHART_SHADOW_ALPHA = 100;
	private static final int BLUR_RADIUS = 8;
	private static final int SHADOW_OFFSET = 6;

	private static final boolean PAINT_PERCENTAGES = true;
	private static final int TEXT_SHADOW_ALPHA = 110;
	private static final float TEXT_BLUR_RADIUS = 2.0f;
	private static final float TEXT_SHADOW_OFFSET = 1.0f;

	private static final int PERCENTAGE_LOWER_LIMIT = 6;

	@Inject
	private EkonomipulsUtil util;

	private ArrayList<Arc> arcs;

	{
		arcs = new ArrayList<Arc>();
		// TODO Fix the on touch listener.
		// this.setOnTouchListener(this);
	}

	/**
	 * @param context
	 */
	public PieChartView(final Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PieChartView(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		inject();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PieChartView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		inject();
	}

	/** {@inheritDoc} */
	@Override
	protected void onDraw(final Canvas canvas) {

		final int width = getWidth();
		final int height = getHeight();

		// Make sure the drawable area can contain the shadow and outline
		// TODO Make these dependent on configuration values
		final Rect canvasRect = new Rect(STROKE_WIDTH + SHADOW_OFFSET,
				STROKE_WIDTH + SHADOW_OFFSET, width - STROKE_WIDTH
						- SHADOW_OFFSET, height - STROKE_WIDTH - SHADOW_OFFSET);

		if (!SKEW_CHART) {
			perfectlyRoundAndCenter(width, height, canvasRect);
		}

		final RectF oval = new RectF(canvasRect);

		if (DROP_SHADOW) {
			paintDropShadow(canvas, oval);
		}

		paintPieChartArcs(canvas, oval);

		if (PAINT_PERCENTAGES) {
			paintPercentages(canvas, oval, this.arcs);
		}
	}

	private void paintDropShadow(final Canvas canvas, final RectF oval) {

		// Apply filter mask to paint
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.argb(0, 0, 0, 0));

		final int shadowColor = Color.argb(CHART_SHADOW_ALPHA, 0, 0, 0);
		paint.setShadowLayer(BLUR_RADIUS, SHADOW_OFFSET, SHADOW_OFFSET, shadowColor);

		final RectF offsetOval = new RectF(oval);

		canvas.drawOval(offsetOval, paint);
	}

	private void paintPieChartArcs(final Canvas canvas, final RectF oval) {
		float arcPosDegrees = 0.0F;

		// The style we will paint with
		final Style[] styles = new Style[] { Style.FILL, Style.STROKE };

		arcs.clear();

		for (final Style style : styles) {
			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(style);

			// FIXME These should be sorted in the order of smallest to largest
			// to avoid drawing bug
			for (final SeriesEntry entry : series) {
				if (style == Style.STROKE) {
					paint.setColor(Color.WHITE);
					paint.setStrokeWidth(STROKE_WIDTH);
					paint.setStrokeCap(Cap.SQUARE);
				} else {
					paint.setColor(entry.getBaseColor());

					if (ENABLE_GRADIENT) {
						final Shader grad = util
								.createGradientFromBaseColor(oval, entry);

						paint.setShader(grad);
					}
				}

				final Arc arc = new Arc(entry, arcPosDegrees, getTotalAmt());
				arcs.add(arc);

				canvas.drawArc(oval, arc.getDegrees(), arc.getSweep(), true, paint);

				// Increment for next arc sweep.
				arcPosDegrees += arc.getSweep();
			}
		}
	}

	private void paintPercentages(final Canvas canvas, final RectF oval,
			final ArrayList<Arc> arcs) {

		final int textSize = Math.round(oval.height() / 10);

		final Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextSize(textSize);
		paint.setTextAlign(Align.CENTER);

		final int shadowColor = Color.argb(TEXT_SHADOW_ALPHA, 0, 0, 0);
		paint.setShadowLayer(TEXT_BLUR_RADIUS, TEXT_SHADOW_OFFSET, TEXT_SHADOW_OFFSET, shadowColor);

		final double radius = oval.width() / 3;

		for (final Arc arc : arcs) {

			final int percentage = util
					.getPercentage(arc.getCategorySum(), seriesTotal
							.floatValue());
			if (percentage >= PERCENTAGE_LOWER_LIMIT) {
				final double angle = arc.getDegrees() + arc.getSweep() / 2;

				final double x = Math.cos(Math.toRadians(angle)) * radius;
				final double y = Math.sin(Math.toRadians(angle)) * radius;

				final String outText = percentage + "%";

				final Rect textBounds = new Rect();
				paint.getTextBounds(outText, 0, outText.length(), textBounds);

				final float alignY = (float) y + oval.centerY()
						+ textBounds.height() / 2;

				canvas.drawText(outText, (float) x + oval.centerX(), alignY, paint);
			}
		}
	}

	private void perfectlyRoundAndCenter(int width, int height,
			final Rect canvasRect) {
		// Perfectly round piechart.
		if (width > height) {

			width = height; // Width is greater
			final int padding = (getWidth() - width) / 2;

			final int calculatedLeft = canvasRect.left + padding;
			final int calculatedWidth = width - STROKE_WIDTH - SHADOW_OFFSET
					+ padding;

			// Translate PieChart to center
			canvasRect
					.set(calculatedLeft, canvasRect.top, calculatedWidth, canvasRect
							.height());

		} else {
			height = width; // Height is greater
			final int padding = (getHeight() - width) / 2;

			final int calculatedTop = canvasRect.top + padding;
			final int calulatedHeight = height - STROKE_WIDTH - SHADOW_OFFSET
					+ padding;

			// Translate PieChart to center
			canvasRect
					.set(canvasRect.left, calculatedTop, canvasRect.width(), calulatedHeight);

		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		final float x = event.getX();
		final float y = event.getY();

		Log.d(TAG, "Clicked at [" + x + ", " + y + "]");
		double angle = Math.atan2(y, x);
		angle *= 100;
		Log.d(TAG, "Angle is: " + angle);

		if (angle < 0.0D) {
			angle = -angle;
		}

		boolean success = false;

		for (final Arc arc : arcs) {

			// TODO Check if within circle.

			// def in_circle(center_x, center_y, radius, x, y):
			// square_dist = (center_x - x) ** 2 + (center_y - y) ** 2
			// return square_dist <= radius ** 2

			// Ex: 20 < 25 < 30 means that the point is within the arc.
			if ((arc.getDegrees() < (float) angle)
					&& ((float) angle < (arc.getDegrees() + arc.getSweep()))) {
				Log.d(TAG, "Found at (" + arc.getDegrees() + ", " + angle
						+ ", " + (arc.getDegrees() + arc.getSweep() + ")"));

				arc.getEntry().setSelected(true);
				success = true;
			} else {
				arc.getEntry().setSelected(false);
			}
		}

		this.invalidate();
		this.forceLayout();

		return success;
	}

	private void inject() {
		((RoboActivity) context).getInjector().injectMembers(this);
	}

}
