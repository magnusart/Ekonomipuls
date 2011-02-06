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

import se.ekonomipuls.adapter.Category;

/**
 * @author Magnus Andersson
 * @since 6 feb 2011
 */
public class SeriesEntry {

	private final int baseColor;
	private final Category category;

	/**
	 * 
	 * @param category
	 * @param baseColor
	 */
	public SeriesEntry(final Category category, final int baseColor) {
		this.category = category;
		this.baseColor = baseColor;
	}

	/**
	 * @return the baseColor
	 */
	public int getBaseColor() {
		return baseColor;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "SeriesEntry [baseColor=" + baseColor + ", category=" + category
				+ "]";
	}

}
