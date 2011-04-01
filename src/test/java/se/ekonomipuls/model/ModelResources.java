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
	private Tag foodTag;

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

		foodTag = new Tag(34, "Food");

		final FilterRule matchICA = new FilterRule(1L, "ICA Stores",
				"Match agains anything with ICA in it", "ICA", foodTag, true,
				200);

		final FilterRule matchLIDL = new FilterRule(1L, "LIDL Stores",
				"Match agains anything with LIDL in it", "LIDL", foodTag, true,
				100);

		final FilterRule matchHemkop = new FilterRule(1L, "Hemkop Stores",
				"Match agains anything with Hemkop in it", "Hemkop", foodTag,
				true, 40);

		final FilterRule catchAllRule = new FilterRule(1L, "Default rule",
				"Built in default catch all rule", "*", getDefaultTag(), true,
				Integer.MIN_VALUE);

		rules.add(matchICA);
		rules.add(matchLIDL);
		rules.add(matchHemkop);
		rules.add(catchAllRule);

		return rules;
	}

	/**
	 * @return
	 */
	public List<Transaction> getUnfilteredTransactions() {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		transactions.add(new Transaction(1, "GLOBAL_ID_1", "2011-04-14",
				"ICA Kvantum", "", new BigDecimal(-200), CURRENCY, true, false,
				ACC_ID));

		transactions.add(new Transaction(2, "GLOBAL_ID_2", "2011-04-14",
				"ICA Nära", "", new BigDecimal(-400), CURRENCY, true, false,
				ACC_ID));

		transactions.add(new Transaction(3, "GLOBAL_ID_3", "2011-04-14",
				"Hemkop", "", new BigDecimal(-400), CURRENCY, true, false,
				ACC_ID));

		transactions.add(new Transaction(4, "GLOBAL_ID_4", "2011-04-14",
				"LiDl Linné", "", new BigDecimal(-400), CURRENCY, true, false,
				ACC_ID));

		transactions.add(new Transaction(5, "GLOBAL_ID_5", "2011-04-14",
				"LIDL 2", "", new BigDecimal(-200), CURRENCY, true, false,
				ACC_ID));

		transactions.add(new Transaction(6, "GLOBAL_ID_6", "2011-04-14",
				"Willys", "", new BigDecimal(-400), CURRENCY, true, false,
				ACC_ID));

		return transactions;
	}

	/**
	 * @return
	 */
	public List<ApplyFilterTagAction> getFilteredTransactions() {
		final List<ApplyFilterTagAction> actions = new ArrayList<ApplyFilterTagAction>();

		final ArrayList<Transaction> ftrans = new ArrayList<Transaction>(
				getUnfilteredTransactions());

		for (final Transaction t : ftrans) {
			t.setFiltered(true);
		}

		actions.add(new ApplyFilterTagAction(ftrans.get(0), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(1), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(2), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(3), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(4), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(5), getDefaultTag()));

		return actions;
	}
}
