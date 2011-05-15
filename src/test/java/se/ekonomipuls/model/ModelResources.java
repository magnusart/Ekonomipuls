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
	private Tag salaryTag;

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
			actions.add(new ApplyFilterTagAction(trans, getDefaultExpenseTag()));
		}

		return actions;
	}

	public Tag getDefaultExpenseTag() {
		return new Tag(TAG_ID, TAG_NAME, EntityType.EXPENSE);
	}

	/**
	 * @return
	 */
	private Tag getDefaultIncomeTag() {
		return new Tag(TAG_ID, TAG_NAME, EntityType.INCOME);
	}

	/**
	 * @return
	 */
	public List<FilterRule> getFilterRules() {
		final List<FilterRule> rules = new ArrayList<FilterRule>();

		foodTag = new Tag(34, "Food", EntityType.EXPENSE);
		salaryTag = new Tag(35, "Salary", EntityType.INCOME);

		final FilterRule matchICA = new FilterRule(1L, "ICA Stores",
				"Match agains anything with ICA in it", "ICA", foodTag, true,
				200);

		final FilterRule matchLIDL = new FilterRule(1L, "LIDL Stores",
				"Match agains anything with LIDL in it", "LIDL", foodTag, true,
				100);

		final FilterRule matchHemkop = new FilterRule(1L, "Hemkop Stores",
				"Match agains anything with Hemkop in it", "Hemkop", foodTag,
				true, 40);

		final FilterRule matchSalary = new FilterRule(1L, "Montly salary",
				"Match against anything with LÖN in it", "lön", salaryTag,
				true, 30);

		final FilterRule catchExpensesRule = new FilterRule(1L,
				"Default Expense rule", "Built in default catch all rule", "<",
				getDefaultExpenseTag(), true, Integer.MIN_VALUE);

		final FilterRule catchIncomeRule = new FilterRule(1L,
				"Default Income rule", "Built in default catch all rule", ">",
				getDefaultIncomeTag(), true, Integer.MIN_VALUE);

		rules.add(matchICA);
		rules.add(matchLIDL);
		rules.add(matchHemkop);
		rules.add(matchSalary);
		rules.add(catchExpensesRule);
		rules.add(catchIncomeRule);

		return rules;
	}

	/**
	 * @return
	 */
	public List<Transaction> getUnfilteredTransactions() {
		final List<Transaction> transactions = new ArrayList<Transaction>();

		// Expenses
		transactions.add(new Transaction(1, "GLOBAL_ID_1", "2011-04-14",
				"ICA Kvantum", "", new BigDecimal(-200), CURRENCY, false,
				false, ACC_ID));

		transactions.add(new Transaction(2, "GLOBAL_ID_2", "2011-04-14",
				"ICA Nära", "", new BigDecimal(-400), CURRENCY, false, false,
				ACC_ID));

		transactions.add(new Transaction(3, "GLOBAL_ID_3", "2011-04-14",
				"Hemkop", "", new BigDecimal(-400), CURRENCY, false, false,
				ACC_ID));

		transactions.add(new Transaction(4, "GLOBAL_ID_4", "2011-04-14",
				"LiDl Linné", "", new BigDecimal(-400), CURRENCY, false, false,
				ACC_ID));

		transactions.add(new Transaction(5, "GLOBAL_ID_5", "2011-04-14",
				"LIDL 2", "", new BigDecimal(-200), CURRENCY, false, false,
				ACC_ID));

		transactions.add(new Transaction(6, "GLOBAL_ID_6", "2011-04-14",
				"Willys", "", new BigDecimal(-400), CURRENCY, false, false,
				ACC_ID));

		// Income
		transactions.add(new Transaction(7, "GLOBAL_ID_7", "2011-03-25", "LÖN",
				"", new BigDecimal(25000), CURRENCY, false, false, ACC_ID));

		transactions.add(new Transaction(8, "GLOBAL_ID_8", "2011-03-25",
				"Engångsgåva", "", new BigDecimal(5000), CURRENCY, false,
				false, ACC_ID));

		// Already filtered rule, should not be filtered again
		transactions.add(new Transaction(9, "GLOBAL_ID_9", "2011-02-25",
				"GAMMAL LÖN", "", new BigDecimal(25000), CURRENCY, true, true,
				ACC_ID));

		transactions.add(new Transaction(10, "GLOBAL_ID_10", "2011-02-26",
				"Hemkop", "", new BigDecimal(-110), CURRENCY, true, true,
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

		// Expenses
		actions.add(new ApplyFilterTagAction(ftrans.get(0), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(1), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(2), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(3), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(4), foodTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(5),
				getDefaultExpenseTag()));

		// Income
		actions.add(new ApplyFilterTagAction(ftrans.get(6), salaryTag));
		actions.add(new ApplyFilterTagAction(ftrans.get(7),
				getDefaultIncomeTag()));

		return actions;
	}

}
