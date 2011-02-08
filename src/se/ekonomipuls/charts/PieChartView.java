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

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Pie chart diagram. Use as a normal view.
 * 
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class PieChartView extends AbstractChartView implements OnTouchListener {
	protected static final int STROKE_WIDTH = 2;

	private static final boolean ENABLE_GRADIENT = true;
	private static final boolean SKEW_CHART = false;
	private static final boolean DROP_SHADOW = true;

	private static final int SHADOW_COLOR = Color.BLACK;
	private static final int BLUR_RADIUS = 8;
	private static final int SHADOW_OFFSET = 6;

	private static final float DARK_GRAD_SATURATION = 0.8f;
	private static final float DARK_GRAD_BRIGHTNESS = 0.6f;

	private static final float LIGHT_GRAD_SATURATION = 0.5f;
	private static final float LIGHT_GRAD_BRIGHTNESS = 0.95f;

	// When a slice is unselected
	private static final float SELECT_DESATURATION = 0.3f;
	private static final float SELECT_DIM = 0.1f;

	private ArrayList<Arc> arcs;

	{
		arcs = new ArrayList<Arc>();
		this.setOnTouchListener(this);
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
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PieChartView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
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
			paintDropShadow(canvas, oval, height, width);
		}

		paintPieChartArcs(canvas, oval, height, width);
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

			//			def in_circle(center_x, center_y, radius, x, y):
			//			    square_dist = (center_x - x) ** 2 + (center_y - y) ** 2
			//			    return square_dist <= radius ** 2

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

	private void paintDropShadow(final Canvas canvas, final RectF oval,
			final int height, final int width) {

		// Setup blur filter
		final BlurMaskFilter blurFilter = new BlurMaskFilter(BLUR_RADIUS,
				BlurMaskFilter.Blur.NORMAL);

		// Apply filter mask to paint
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setMaskFilter(blurFilter);
		paint.setColor(SHADOW_COLOR);

		// Create a copy of the oval
		final RectF offsetOval = new RectF(oval);

		// Shrink (elevate) the oval by the same amount as the offset
		offsetOval.inset(SHADOW_OFFSET, SHADOW_OFFSET);

		// Determine light direction (offset shadow)
		offsetOval.offset(SHADOW_OFFSET, SHADOW_OFFSET);

		canvas.drawOval(offsetOval, paint);
	}

	private void paintPieChartArcs(final Canvas canvas, final RectF oval,
			final int height, final int width) {
		float arcPosDegrees = 0.0F;

		// The style we will paint with
		final Style[] styles = new Style[] { Style.FILL, Style.STROKE };

		arcs.clear();

		for (final Style style : styles) {
			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(style);

			for (final SeriesEntry entry : series) {
				if (style == Style.STROKE) {
					paint.setColor(Color.WHITE);
					paint.setStrokeWidth(STROKE_WIDTH);
					paint.setStrokeCap(Cap.SQUARE);
				} else {
					paint.setColor(entry.getBaseColor());

					if (ENABLE_GRADIENT) {
						final RadialGradient radGrad = createGradientFromBaseColor(
								oval, entry);

						paint.setShader(radGrad);
					}
				}

				final Arc arc = new Arc(entry, arcPosDegrees, getTotalAmt());
				arcs.add(arc);

				canvas.drawArc(oval, arc.getDegrees(), arc.getSweep(), true,
						paint);

				// Increment for next arc sweep.
				arcPosDegrees += arc.getSweep();
			}
		}
	}

	private RadialGradient createGradientFromBaseColor(final RectF oval,
			final SeriesEntry entry) {
		final int baseColor = entry.getBaseColor();
		int dark = 0;
		int light = 0;
		if (baseColor != Color.GRAY) {

			final float[] hsv = new float[3];

			Color.colorToHSV(baseColor, hsv);

			hsv[1] = DARK_GRAD_SATURATION;
			hsv[2] = DARK_GRAD_BRIGHTNESS;

			if (!entry.isSelected()) {
				hsv[1] -= SELECT_DESATURATION;
				hsv[2] -= SELECT_DIM;
			}

			dark = Color.HSVToColor(hsv);

			hsv[1] = LIGHT_GRAD_SATURATION;
			hsv[2] = LIGHT_GRAD_BRIGHTNESS;

			if (!entry.isSelected()) {
				hsv[1] -= SELECT_DESATURATION;
				hsv[2] -= SELECT_DIM;
			}

			light = Color.HSVToColor(hsv);

		} else {
			dark = Color.DKGRAY;
			if (!entry.isSelected()) {
				dark = Color.GRAY;
			}
			light = Color.LTGRAY;
		}

		return new RadialGradient(oval.centerX(), oval.centerY(),
				oval.width() / 2, dark, light, TileMode.CLAMP);
	}

	private void perfectlyRoundAndCenter(int width, int height,
			final Rect canvasRect) {
		// Perfectly round piechart.
		if (width > height) {

			width = height; // Width is greater
			final int padding = (getWidth() - width) / 2;

			final int calculatedLeft = canvasRect.left + padding;
			final int calulatedWidth = width - STROKE_WIDTH - SHADOW_OFFSET
					+ padding;

			// Translate PieChart to center
			canvasRect.set(calculatedLeft, canvasRect.top, calulatedWidth,
					canvasRect.height());

		} else {
			height = width; // Height is greater
			final int padding = (getHeight() - width) / 2;

			final int calculatedTop = canvasRect.top + padding;
			final int calulatedHeight = height - STROKE_WIDTH - SHADOW_OFFSET
					+ padding;

			// Translate PieChart to center
			canvasRect.set(canvasRect.left, calculatedTop, canvasRect.width(),
					calulatedHeight);

		}
	}

}
