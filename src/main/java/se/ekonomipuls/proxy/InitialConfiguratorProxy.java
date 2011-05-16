/**
 * Copyright 2011 Magnus Andersson, Michael Svensson
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
package se.ekonomipuls.proxy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.model.ModelResources;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import se.ekonomipuls.model.EkonomipulsUtil;

/**
 * This class loads the default categories for initial configuration of the
 * application.
 * 
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
public class InitialConfiguratorProxy {
	@Inject
	EkonomipulsUtil util;

	@Inject
	Gson gson;

	@Inject
	ModelResources resources;

	List<AddCategoryAction> getCategories() throws JsonIOException,
			JsonSyntaxException, IOException {

		final String json = util.getCategoriesConfigurationFile();

		final Type listType = new TypeToken<List<AddCategoryAction>>() {
		}.getType();

		final List<AddCategoryAction> categories = gson
				.fromJson(json, listType);

		return categories;
	}
}
