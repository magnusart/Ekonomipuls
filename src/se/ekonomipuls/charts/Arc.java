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

/**
 * @author Magnus Andersson
 * @since 8 feb 2011
 */
final class Arc {

	private final float sweep;
	private final float categorySum;
	private final SeriesEntry entry;
	private final float degrees;

	/**
	 * @param entry
	 * @param degrees
	 * @param bigDecimal
	 */
	Arc(final SeriesEntry entry, final float degrees, final BigDecimal totalAmt) {
		this.entry = entry;
		this.degrees = degrees % 360;

		// Total sum of all pieces of the chart
		categorySum = entry.getCategory().getSum().abs().floatValue();

		// Calculate the arc angle.
		sweep = categorySum == 0.0F ? 0.0F : 360 * categorySum
				/ totalAmt.floatValue();

	}

	/**
	 * @return the sweep
	 */
	public float getSweep() {
		return sweep;
	}

	/**
	 * @return the categorySum
	 */
	public float getCategorySum() {
		return categorySum;
	}

	/**
	 * @return the entry
	 */
	public SeriesEntry getEntry() {
		return entry;
	}

	/**
	 * @return the degrees
	 */
	public float getDegrees() {
		return degrees;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Arc [sweep=" + sweep + ", categorySum=" + categorySum
				+ ", entry=" + entry + ", degrees=" + degrees + "]";
	}

}
