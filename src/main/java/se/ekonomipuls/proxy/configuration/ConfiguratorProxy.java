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
package se.ekonomipuls.proxy.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * @author Magnus Andersson
 * @since 29 jun 2011
 */
public interface ConfiguratorProxy {

	public abstract List<AddCategoryAction> getCategories()
			throws JsonIOException, JsonSyntaxException, IOException;

	/**
	 * @return
	 */
	public abstract Map<String, List<AddTagAction>> getTags()
			throws JsonIOException, JsonSyntaxException, IOException;

	/**
	 * @return
	 */
	public abstract Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws JsonIOException, JsonSyntaxException, IOException;

}