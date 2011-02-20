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
package se.ekonomipuls.util;

import se.ekonomipuls.PropertiesConstants;
import se.ekonomipuls.charts.SeriesEntry;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.preference.PreferenceManager;
import android.view.Window;

/**
 * @author Magnus Andersson
 * @since 13 feb 2011
 */
public final class GuiUtil implements PropertiesConstants {

	private static final float DARK_GRAD_SATURATION = 0.5f;
	private static final float DARK_GRAD_BRIGHTNESS = 0.7f;

	private static final float LIGHT_GRAD_SATURATION = 0.4f;
	private static final float LIGHT_GRAD_BRIGHTNESS = 1.1f;

	// When a slice is unselected
	private static final float SELECT_DESATURATION = 0.2f;
	private static final float SELECT_DIM = 0.1f;

	private GuiUtil() {
		// Private constructor
	}

	/**
	 * 
	 * @param part
	 * @param total
	 * @return
	 */
	public static int getPercentage(final float part, final float total) {
		return Math.round((part / total) * 100);
	}

	/**
	 * 
	 * @param oval
	 * @param entry
	 * @return
	 */
	public static RadialGradient createGradientFromBaseColor(final RectF oval,
			final SeriesEntry entry) {
		final int baseColor = entry.getBaseColor();
		int dark = 0;
		int light = 0;

		dark = getDarkColor(baseColor, entry.isSelected());
		light = getLightColor(baseColor, entry.isSelected());

		return new RadialGradient(oval.centerX(), oval.centerY(),
				oval.width() / 2, dark, light, TileMode.CLAMP);
	}

	/**
	 * @param baseColor
	 * @param entrySelected
	 * @return
	 */
	public static int getLightColor(final int baseColor,
			final boolean entrySelected) {
		final float[] hsv = new float[3];

		Color.colorToHSV(baseColor, hsv);

		float saturation = LIGHT_GRAD_SATURATION;

		if ((baseColor == Color.GRAY) || (baseColor == Color.WHITE)
				|| (baseColor == Color.BLACK)) {
			saturation = 0.0f;
		}

		hsv[1] = saturation;
		hsv[2] = LIGHT_GRAD_BRIGHTNESS;

		if (!entrySelected) {
			hsv[1] -= SELECT_DESATURATION;
			hsv[2] -= SELECT_DIM;
		}

		return Color.HSVToColor(hsv);
	}

	/**
	 * @param baseColor
	 * @param entrySelected
	 * @return
	 */
	public static int getDarkColor(final int baseColor,
			final boolean entrySelected) {
		final float[] hsv = new float[3];

		Color.colorToHSV(baseColor, hsv);

		float saturation = DARK_GRAD_SATURATION;

		if ((baseColor == Color.GRAY) || (baseColor == Color.WHITE)
				|| (baseColor == Color.BLACK)) {
			saturation = 0.0f;
		}

		hsv[1] = saturation;
		hsv[2] = DARK_GRAD_BRIGHTNESS;

		if (!entrySelected) {
			hsv[1] -= SELECT_DESATURATION;
			hsv[2] -= SELECT_DIM;
		}

		return Color.HSVToColor(hsv);
	}

	/**
	 * Some Voodoo that prevents color banding on the background
	 * gradient.
	 * http://stuffthathappens.com/blog/2010/06/04/android-color-banding/
	 * 
	 * @param window
	 */
	public static void removeGradientBanding(final Window window) {
		window.setFormat(PixelFormat.RGBA_8888);
	}

	/**
	 * @return The Report Id for Economic Overview
	 */
	public static long getEconomicOverviewId(final Context ctx) {
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getLong(ECONOMIC_OVERVIEW_REPORT_ID, -1);
	}

}
