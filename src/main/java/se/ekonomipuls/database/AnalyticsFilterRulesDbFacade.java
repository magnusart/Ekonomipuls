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
package se.ekonomipuls.database;

import java.util.List;

import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.model.FilterRule;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public interface AnalyticsFilterRulesDbFacade {

	/**
	 * Will return the filter rules sorted based on respective priority
	 * 
	 * @return All the filter rules.
	 */
	List<FilterRule> getFilterRules();

	long insertFilterRule(AddFilterRuleAction action);

}
