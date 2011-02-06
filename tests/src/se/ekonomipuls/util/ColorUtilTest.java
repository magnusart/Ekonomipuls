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

import junit.framework.TestCase;

/**
 * @author Magnus Andersson
 * @since 5 feb 2011
 */
public class ColorUtilTest extends TestCase {

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

	/**
	 * Test so that we get all the 37 increments (0 - 360, increments in 10)
	 * of the hue scale.
	 * 
	 * @throws Exception
	 */
	public void testColorsShouldReturnAll36Hues() throws Exception {
		final boolean[] foundValues = new boolean[36];

		for (int i = 0; i < 36; i++) {
			final int increment = (int) ColorUtil.getNextHueIncrement();
			foundValues[increment / 10] = true;
		}

		for (int i = 0; i < foundValues.length; i++) {
			assertTrue("Did not get the increment number " + i, foundValues[i]);
		}
	}

	/**
	 * Tests so that the color increments for Pie Chart Slices are not adjacent.
	 * That means that there should never be a sequence like 10, 20, 30 etc.
	 * 
	 * @throws Exception
	 */
	public void testColorsIncrementsNotAdjacent() throws Exception {
		final int[] valuesOrder = new int[36];

		for (int i = 0; i < 36; i++) {
			final int increment = (int) ColorUtil.getNextHueIncrement();
			valuesOrder[increment / 10] = i;
		}

		for (int i = 0; i < valuesOrder.length; i++) {
			if (i < valuesOrder.length - 1) {
				assertTrue("The increment (" + i + " = " + valuesOrder[i]
						+ ") was located before (" + (i + 1) + " = "
						+ valuesOrder[i + 1] + ")",
						(valuesOrder[i] + 1) != valuesOrder[i + 1]);
			} else { // Increment 35 should not be 
				assertTrue("The increment (" + i + " = " + valuesOrder[i]
						+ ") was located before (" + 0 + " = " + valuesOrder[0]
						+ ")", (valuesOrder[i]) != valuesOrder[0]);
			}
		}
	}
}
