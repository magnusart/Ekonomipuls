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

import se.ekonomipuls.util.ColorUtil;
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
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;

/**
 * Pie chart diagram. Use as a normal view.
 * 
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class PieChartView extends AbstractChartView {
	protected static final int STROKE_WIDTH = 2;

	private static final boolean SKEW_CHART = false;
	private static final boolean DROP_SHADOW = true;

	private static final int SHADOW_COLOR = Color.BLACK;
	private static final int BLUR_RADIUS = 8;
	private static final int SHADOW_OFFSET = 6;

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

		final Paint paint = new Paint();
		paint.setAntiAlias(true);

		for (final Style style : styles) {
			paint.setStyle(style);

			for (final SeriesEntry entry : series) {
				if (style == Style.STROKE) {
					paint.setColor(Color.WHITE);
					paint.setStrokeWidth(STROKE_WIDTH);
					paint.setStrokeCap(Cap.SQUARE);
				} else {
					paint.setColor(entry.getBaseColor());
					//paint.setShader(getNextRadialGradient(x, y, radius, mode));
				}

				final float categorySum = entry.getCategory().getSum()
						.abs().floatValue();

				final float arcSweep = categorySum == 0.0F ? 0.0F : 360
						* categorySum / getTotalAmt().floatValue();

				canvas.drawArc(oval, arcPosDegrees, arcSweep, true, paint);

				// Increment for next arc sweep.
				arcPosDegrees += arcSweep;
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

	//	    final float x = (width - STROKE_WIDTH * 2) / 2;
	//		final float y = (height - STROKE_WIDTH * 2) / 2;
	//		final float radius = width;
	//		final TileMode mode = TileMode.CLAMP;
	private Shader getNextRadialGradient(final float x, final float y,
			final float radius, final TileMode mode) {

		final int color = ColorUtil.getNextColor();
		// TODO Gör så att det går från en mörk del av en färg till en ljus

		final RadialGradient radGrad = new RadialGradient(x, y, radius, color,
				color, mode);

		return radGrad;

	}

}
