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
package se.ekonomipuls.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.proxy.BankDroidTransaction;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public class ModelResources {

	private static final String ACC_ID = "1_1";
	private static final String CURRENCY = "SEK";
	private static final String BASE_DATE = "2011-04-1";
	private static final String BASE_DESC = "Desc";
	public static final long TAG_ID = 20L;
	private static final String TAG_NAME = "Default Tag";

	/**
	 * 
	 */
	public List<BankDroidTransaction> getStagingTransactions() {
		final List<BankDroidTransaction> list = new ArrayList<BankDroidTransaction>();

		for (int i = 0; i < 10; i++) {
			list.add(new BankDroidTransaction("" + i + 1, BASE_DATE + i,
					BASE_DESC + i, new BigDecimal(-i * 100), CURRENCY, ACC_ID));
		}
		return list;
	}

	/**
	 * @return
	 */
	public List<Transaction> getTransactions() {
		final List<Transaction> transactions = new ArrayList<Transaction>();
		for (int i = 0; i < 10; i++) {
			transactions.add(new Transaction(0, "" + i + 1, BASE_DATE + i,
					BASE_DESC + i, "", new BigDecimal(-i * 100), CURRENCY,
					true, false, ACC_ID));
		}

		return transactions;
	}

	public List<ApplyFilterTagAction> getTagFilterActions(
			final List<Transaction> transactions) {
		final List<ApplyFilterTagAction> actions = new ArrayList<ApplyFilterTagAction>();
		for (final Transaction trans : transactions) {
			actions.add(new ApplyFilterTagAction(trans, getDefaultTag()));
		}

		return actions;
	}

	public Tag getDefaultTag() {
		return new Tag(TAG_ID, TAG_NAME);
	}

	/**
	 * @return
	 */
	public List<FilterRule> getFilterRules() {
		final List<FilterRule> rules = new ArrayList<FilterRule>();

		final List<Tag> tags = new ArrayList<Tag>();
		tags.add(getDefaultTag());

		final FilterRule catchAllRule = new FilterRule(1L, "Default rule",
				"Built in default catch all rule", "*", tags, true,
				Integer.MIN_VALUE);

		rules.add(catchAllRule);

		return rules;
	}
}
