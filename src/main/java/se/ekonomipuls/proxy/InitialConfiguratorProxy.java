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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.ConfigurationException;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.actions.AddCategoryReportAction.AddCategoryAction;
import se.ekonomipuls.actions.AddFilterRuleAction;
import se.ekonomipuls.actions.AddTagAction;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.EkonomipulsUtil.ConfigurationFileType;
import se.ekonomipuls.model.EntityType;
import se.ekonomipuls.model.Tag;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

/**
 * This class loads the default categories for initial configuration of the
 * application.
 * 
 * @author Magnus Andersson
 * @since 16 maj 2011
 */
public class InitialConfiguratorProxy implements LogTag {

	/**
	 * @author Magnus Andersson
	 * @since 22 maj 2011
	 */
	private static class TagNameComparator implements Comparator<Tag> {

		/** {@inheritDoc} */
		@Override
		public int compare(final Tag arg0, final Tag arg1) {
			final int result = arg0.getName().compareTo(arg1.getName());

			Log.d(TAG, "Comparing " + arg0.getName() + " and " + arg1.getName()
					+ " with result " + result);

			return result;
		}
	}

	/**
	 * @author Magnus Andersson
	 * @since 22 maj 2011
	 */
	public class ConfigurationError extends Error {
		private static final long serialVersionUID = -6308790163815696760L;
	}

	@Inject
	EkonomipulsUtil util;

	@Inject
	Gson gson;

	public List<AddCategoryAction> getCategories() throws JsonIOException,
			JsonSyntaxException, IOException {

		final String json = util
				.getConfigurationFile(ConfigurationFileType.CATEGORIES);

		final Type listType = new TypeToken<List<AddCategoryAction>>() {
		}.getType();

		final List<AddCategoryAction> categories = gson
				.fromJson(json, listType);

		return categories;
	}

	/**
	 * @return
	 */
	public Map<String, List<AddTagAction>> getTags() throws JsonIOException,
			JsonSyntaxException, IOException {

		final Type mapType = new TypeToken<Map<String, List<AddTagAction>>>() {
		}.getType();

		final String json = util
				.getConfigurationFile(ConfigurationFileType.TAGS);

		final Map<String, List<AddTagAction>> tags = gson
				.fromJson(json, mapType);

		return tags;
	}

	/**
	 * @return
	 */
	public Map<String, List<AddFilterRuleAction>> getFilterRules()
			throws JsonIOException, JsonSyntaxException, IOException {
		final Type mapType = new TypeToken<Map<String, List<AddFilterRuleAction>>>() {
		}.getType();

		final String json = util
				.getConfigurationFile(ConfigurationFileType.FILTER_RULES);

		final Map<String, List<AddFilterRuleAction>> rules = gson
				.fromJson(json, mapType);

		return rules;
	}

	/**
	 * <strong>Validates the following criteria:</strong>
	 * <ul>
	 * <li>All Categories have at least one tag associated.</li>
	 * <li>The two default tags exists in the tag list.</li>
	 * <li>The two default tags exists have rules associated with them.</li>
	 * <li>All filter rules have existing tags that can be associated</li>
	 * </ul>
	 * 
	 * 
	 * 
	 * @param categoryActions
	 * @param tagsActions
	 * @param filterRulesActions
	 * @param defaultExpensesTagName
	 * @param defaultIncomesTagName
	 * @return
	 * @throws ConfigurationError
	 *             Thrown when one or more errors in the configuration exists.
	 */
	public boolean validateConfiguration(
			final List<AddCategoryAction> categoryActions,
			final Map<String, List<AddTagAction>> tagsActions,
			final Map<String, List<AddFilterRuleAction>> filterRulesActions,
			final String defaultExpensesTagName,
			final String defaultIncomesTagName) throws ConfigurationException {

		// **********************************************************************
		// Validate: All Categories have at least one tag associated.
		for (final AddCategoryAction categoryAction : categoryActions) {
			if (!tagsActions.containsKey(categoryAction.getName())) {
				throw new ConfigurationException(
						"Category that does not have any tag associated. Category name: "
								+ categoryAction.getName() + ".");
			}
		}

		// Setup comparator and put all tags in a tree set.
		final Comparator<Tag> tagCompare = new TagNameComparator();

		final Set<AddTagAction> sourceTagList = new TreeSet<AddTagAction>(
				tagCompare);

		for (final List<AddTagAction> tagActionList : tagsActions.values()) {
			sourceTagList.addAll(tagActionList);
		}

		// **********************************************************************
		// Validate: The two default tags exists in the tag list.
		final List<AddTagAction> defaultTags = new ArrayList<AddTagAction>();
		defaultTags.add(new AddTagAction(defaultExpensesTagName,
				EntityType.EXPENSE));
		defaultTags.add(new AddTagAction(defaultIncomesTagName,
				EntityType.INCOME));

		final int foundDefaultTags = containsTagName(sourceTagList, defaultTags);

		if (foundDefaultTags != defaultTags.size()) {
			throw new ConfigurationException("Default tags " + defaultTags
					+ " not found in the tag list. Default tags: "
					+ sourceTagList);
		}

		// **********************************************************************
		// Validate: The two default tags exists have rules associated with
		// them.
		final Set<String> ruleTagNames = filterRulesActions.keySet();

		if (!ruleTagNames.contains(defaultIncomesTagName)
				|| !ruleTagNames.contains(defaultExpensesTagName)) {
			throw new ConfigurationException("");
		}

		// **********************************************************************
		// Validate: All filter rules have existing tags that can be associated
		final List<AddTagAction> ruleTags = new ArrayList<AddTagAction>();

		for (final String ruleTagName : ruleTagNames) {
			ruleTags.add(new AddTagAction(ruleTagName, null));
		}

		final int foundRuleTags = containsTagName(sourceTagList, ruleTags);

		if (foundRuleTags != ruleTags.size()) {
			throw new ConfigurationException("Rule tag " + ruleTags
					+ " does not exist in the tag list " + sourceTagList);
		}

		return true;
	}

	/**
	 * @param sourceTagList
	 * @param foundDefaultTags
	 * @param searchTags
	 * @param tagCompare
	 * @return
	 */
	private int containsTagName(final Set<AddTagAction> sourceTagList,
			final Collection<AddTagAction> searchTags) {

		int foundTags = 0;

		for (final AddTagAction tagType : searchTags) {
			if (sourceTagList.contains(tagType)) {
				foundTags++;
			}
		}
		return foundTags;
	}
}
