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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;

import com.google.inject.BindingAnnotation;

/**
 * @author Magnus Andersson
 * @since 29 jun 2011
 */
public interface ConfiguratorProxy {

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public static @interface RemoteConfiguration {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public static @interface LocalConfiguration {
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public abstract List<AddCategoryAction> getCategories() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	public abstract Map<String, List<AddTagAction>> getTags()
			throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	public abstract Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws IOException;

}