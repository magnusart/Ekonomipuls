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
package se.ekonomipuls.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.google.inject.Inject;
import android.content.Context;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AnalyticsFilterRulesDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.FilterRule;
import se.ekonomipuls.model.Transaction;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public class FilterRuleService {

	@Inject
	Context context;

	@Inject
	EkonomipulsUtil util;

	@Inject
	AnalyticsFilterRulesDbFacade filterRulesDbFacade;

	/**
	 * @param deduplicatedTransactions
	 * @return
	 */
	public List<ApplyFilterTagAction> applyFilters(
			final List<Transaction> transactions) {

		final List<FilterRule> rules = filterRulesDbFacade.getFilterRules();

		final List<ApplyFilterTagAction> actions = new ArrayList<ApplyFilterTagAction>();

		for (final Transaction transaction : transactions) {
			if (!transaction.isFiltered()) { // Process unfiltered transactions
				actions.addAll(applyFilter(transaction, rules));
			}
		}

		return actions;
	}

	/**
	 * @param transaction
	 * @param rules
	 * @return
	 */
	private List<ApplyFilterTagAction> applyFilter(
			final Transaction transaction, final List<FilterRule> rules) {

		// One transaction can be filtered several times
		final List<ApplyFilterTagAction> filterActions = new ArrayList<ApplyFilterTagAction>();

		// Start applying rules, starting with the highest priority
		for (final FilterRule rule : rules) {
			if (matches(transaction, rule)) {

				// The rule matched the transaction, add it.
				filterActions.add(new ApplyFilterTagAction(transaction, rule
						.getTag()));

				// This determines if we should continue processing
				if (rule.isMarkFiltered()) {
					transaction.setFiltered(true); // A filter have been
													// applied.
					break; // Break the loop
				}
			}
		}

		assert transaction.isFiltered() : "Catch all rule never applied!";

		return filterActions;
	}

	/**
	 * @param transaction
	 * @param rule
	 * @return
	 */
	private boolean matches(final Transaction transaction, final FilterRule rule) {

		// Catch all rules, until the day proper regexp matching is needed.
		// If we have an Expense (less than zero or zero)
		if (rule.getPattern().equals("<")
				&& lessThanOrZero(transaction.getAmount())) {
			return true;
		} else if (rule.getPattern().equals(">")
				&& greaterThanZero(transaction.getAmount())) {
			return true;
		} else if (rule.getPattern().equals("*")) {
			return true;
		}

		// Simplistic matching, lets start out easy. Match is case insensitive
		return transaction.getDescription().toUpperCase()
				.contains(rule.getPattern().toUpperCase());
	}

	/**
	 * @param amount
	 * @return
	 */
	private boolean greaterThanZero(final BigDecimal amount) {
		return amount.compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 * @param amount
	 * @return
	 */
	private boolean lessThanOrZero(final BigDecimal amount) {
		return (amount.compareTo(BigDecimal.ZERO) < 0)
				|| (amount.compareTo(BigDecimal.ZERO) == 0);
	}
}
