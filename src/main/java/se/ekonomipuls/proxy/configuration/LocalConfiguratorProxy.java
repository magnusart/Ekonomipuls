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
package se.ekonomipuls.proxy.configuration;

import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationType;
import se.ekonomipuls.proxy.configuration.ConfigurationRemapUtil.SourceType;

/**
 * This class loads the default categories for initial configuration of the
 * application.
 * 
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
public class LocalConfiguratorProxy extends AbstractConfiguratorProxy {
	{
		sourceMapping.put(ConfigurationType.CATEGORIES, SourceType.LOCAL_JSON);
		sourceMapping.put(ConfigurationType.TAGS, SourceType.LOCAL_JSON);
		sourceMapping
				.put(ConfigurationType.FILTER_RULES, SourceType.LOCAL_SPREADSHEET_JSON);
	}
}
