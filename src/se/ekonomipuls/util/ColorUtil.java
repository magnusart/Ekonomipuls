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

import android.graphics.Color;

/**
 * @author Magnus Andersson
 * @since 5 feb 2011
 */
public class ColorUtil {
	private static final float BRIGHTNESS = 0.8F;
	private static final float SATURATION = 0.75F;

	private static final int MAX_HUE_INCREMENT = 36; // Hue increments has to be <= to this.
	private static final int HUE_STEP_SIZE = 10;
	private static final int HUE_OFFSET = 1;

	private static int hueCounter = 0;

	private ColorUtil() {
		// No constructor
	}

	/**
	 * @return HTML Hexadecimal value for color.
	 */
	public static String getNextHexColor() {
		final int color = getNextColor();
		final String hex = Integer.toHexString(color);
		final String hexColor = "#" + hex.substring(2); // Strip alpha information
		return hexColor;
	}

	public static int getNextColor() {
		final float hue = getNextHueIncrement();

		final int color = Color.HSVToColor(new float[] { hue, SATURATION,
				BRIGHTNESS });

		return color;
	}

	/**
	 * @return
	 */
	static float getNextHueIncrement() {
		if (hueCounter < MAX_HUE_INCREMENT) {
			hueCounter++;
		} else {
			hueCounter = 0;
		}

		// With offset so that we don't get colors right next to each other
		final int degree = ((hueCounter * HUE_STEP_SIZE) * HUE_OFFSET);
		return (degree % 360); // Put the degree back on the 360-scale again.
	}
}