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

/**
 * This class represents a slice of a pie chart.
 * 
 * @author Magnus Andersson
 * @since 13 jan 2011
 */
public class Slice {
	private final Double amount;
	private final String label;
	private final String color;

	/**
	 * 
	 * @param amount
	 * @param label
	 * @param color
	 */
	public Slice(final Double amount, final String label, final String color) {
		super();
		this.amount = amount;
		this.label = label;
		this.color = color;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

}
