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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.database.AnalyticsFilterRulesDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.proxy.configuration.ConfigurationValidator;
import se.ekonomipuls.proxy.configuration.ConfiguratorProxy;
import se.ekonomipuls.proxy.configuration.ConfiguratorProxy.RemoteConfiguration;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Magnus Andersson
 * @since 17 jul 2011
 */
@Singleton
public class ConfigurationService implements LogTag {

	@Inject
	@RemoteConfiguration
	private ConfiguratorProxy configProxy;

	@Inject
	private AnalyticsFilterRulesDbFacade filterRules;

	@Inject
	private ConfigurationValidator validator;

	@Inject
	private EkonomipulsUtil util;

	public boolean importRemoteFilterRulesLocalCategoriesTags() {

		long updatedRows = -1;

		try {
			final Map<String, List<AddFilterRuleAction>> rules = configProxy
					.getFilterRules();

			final List<AddCategoryAction> categories = configProxy
					.getCategories();

			final Map<String, List<AddTagAction>> tags = configProxy.getTags();

			validator.validateConfiguration(categories, tags, rules, util
					.getDefaultExpenseTag().getName(), util
					.getDefaultIncomeTag().getName());

			final List<AddFilterRuleAction> actions = new ArrayList<AddFilterRuleAction>();
			for (final List<AddFilterRuleAction> a : rules.values()) {
				actions.addAll(a);
			}

			updatedRows = filterRules.replaceFilterRules(actions);

		} catch (final IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();

			return false;
		}

		if (updatedRows > 0) {
			return true;
		} else {
			return false;
		}
	}
}
