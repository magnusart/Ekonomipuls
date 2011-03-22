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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.Shader.TileMode;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import se.ekonomipuls.R;
import se.ekonomipuls.views.charts.SeriesEntry;

import static se.ekonomipuls.LogTag.TAG;
import static se.ekonomipuls.PropertiesConstants.ECONOMIC_OVERVIEW_REPORT_ID;

/**
 * Global utility methods for Ekonomipuls.
 * 
 * @author Magnus Andersson
 * @since 13 feb 2011
 */
public class EkonomipulsUtil {

	private static final float DARK_GRAD_SATURATION = 1.0f;
	private static final float DARK_GRAD_BRIGHTNESS = 0.6f;

	private static final float LIGHT_GRAD_SATURATION = 0.85f;
	private static final float LIGHT_GRAD_BRIGHTNESS = 1.0f;

	// When a slice is unselected
	private static final float SELECT_DESATURATION = 0.2f;
	private static final float SELECT_DIM = 0.1f;

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
	public static Shader createGradientFromBaseColor(final RectF oval,
			final SeriesEntry entry) {
		final int baseColor = entry.getBaseColor();
		int dark = 0;
		int light = 0;

		dark = getDarkColor(baseColor, entry.isSelected());
		light = getLightColor(baseColor, entry.isSelected());

		final float skewTop = (oval.width() / 3);
		final float skewBottom = (oval.width() / 3) * 2;

		return new LinearGradient(skewBottom, oval.height(), skewTop, 0L,
				new int[] { dark, light }, null, TileMode.CLAMP);

		//		return new RadialGradient(oval.centerX(), oval.centerY(),
		//				oval.width() / 2, dark, light, TileMode.CLAMP);
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
		float brightness = LIGHT_GRAD_BRIGHTNESS;

		if ((baseColor == Color.GRAY) || (baseColor == Color.WHITE)
				|| (baseColor == Color.BLACK)) {
			saturation = 0.0f;
			brightness += 0.2f;
		}

		hsv[1] = saturation;
		hsv[2] = brightness;

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
		final float brightness = DARK_GRAD_BRIGHTNESS;
		if ((baseColor == Color.GRAY) || (baseColor == Color.WHITE)
				|| (baseColor == Color.BLACK)) {
			saturation = 0.0f;
		}

		hsv[1] = saturation;
		hsv[2] = brightness;

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
	public void removeGradientBanding(final Window window) {
		window.setFormat(PixelFormat.RGBA_8888);
	}

	/**
	 * @return The Report Id for Economic Overview
	 */
	public long getEconomicOverviewId(final Context ctx) {
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getLong(ECONOMIC_OVERVIEW_REPORT_ID, -1);
	}

	public void toastDbError(final Context ctx, final RemoteException e) {
		final String message = "Unable to complete database query";
		final Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
		toast.show();
		Log.d(TAG, message, e);
	}

	/**
	 * Set the status of new transactions in the staging database.
	 * 
	 * @param context
	 *            the Context.
	 * @param status
	 *            new status.
	 */
	public static void setNewTransactionStatus(final Context context, final boolean status) {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();

		final String containsUpdates = context
				.getString(R.string.setting_staging_contains_updates);
		editor.putBoolean(containsUpdates, status); // No updates when initiated.
		editor.commit();
	}

	/**
	 * Returns the status of new transactions in the staging database.
	 * 
	 * @param context
	 *            the Context.
	 * @return True if new transactions exists.
	 */
	public boolean getNewTransactionsStatus(final Context context) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return prefs.getBoolean(
				context.getString(R.string.setting_staging_contains_updates),
				false);
	}

}
