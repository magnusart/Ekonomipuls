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
package se.ekonomipuls.actions;

import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.EntityType;

/**
 * 
 * @author Magnus Andersson
 * @since 21 feb 2011
 */
public class AddCategoryReportAction {

	public static class AddCategoryAction extends Category {

		/**
		 * @param color
		 * @param name
		 */
		AddCategoryAction(final int color, final String name,
				final EntityType type) {
			super(0, color, name, type);
		}
	}

	private final AddCategoryAction category;
	private final long reportId;

	public AddCategoryReportAction(final AddCategoryAction categoryAction,
			final long reportId) {
		category = categoryAction;
		this.reportId = reportId;
	}

	/**
	 * @param color
	 * @param name
	 */
	public AddCategoryReportAction(final int color, final String name,
			final EntityType type, final long reportId) {
		this.reportId = reportId;
		category = new AddCategoryAction(color, name, type);
	}

	/**
	 * @return the category
	 */
	public AddCategoryAction getCategory() {
		return category;
	}

	/**
	 * @return the reportId
	 */
	public long getReportId() {
		return reportId;
	}

}
