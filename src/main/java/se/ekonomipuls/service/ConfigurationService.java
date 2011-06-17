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
package se.ekonomipuls.service;

import java.util.List;
import java.util.Map;

import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.proxy.configuration.GDocsConfiguratorProxy;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @since 14 jun 2011
 */
public class ConfigurationService {

	@Inject
	GDocsConfiguratorProxy gdocsProxy;

	public final Map<String, List<AddFilterRuleAction>> getFilterRules() {
		return gdocsProxy.getFilterRules();
	}
}
