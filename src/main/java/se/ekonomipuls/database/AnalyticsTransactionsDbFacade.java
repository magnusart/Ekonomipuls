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

import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.model.Category;
import se.ekonomipuls.model.Transaction;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public interface AnalyticsTransactionsDbFacade {

	/**
	 * Get all transactions that have not yet had any filters applied to them.
	 * 
	 * @return List of unfiltered transactions.
	 */
	public abstract List<Transaction> getUnfilteredTransactions();

	/**
	 * Get all unverified transactions.
	 * 
	 * @return List of unverified transactions
	 */
	public abstract List<Transaction> getUnverifiedTransactions();

	/**
	 * Get all transactions belonging to a certain category.
	 * 
	 * @param cat
	 *            Category to search within.
	 * @return List of transactions.
	 */
	public abstract List<Transaction> getTransactionsByCategory(
			final Category cat);

	/**
	 * Get all transaction in the analytics database.
	 * 
	 * @return List of transactions.
	 */
	public abstract List<Transaction> getAllTransactions();

	/**
	 * Insert new transactions and assign tags to it.
	 * 
	 * @param actions
	 *            List of actions to execute.
	 */
	public abstract void insertTransactionsAssignTags(
			final List<ApplyFilterTagAction> actions);

	/**
	 * <p>
	 * Delete all transactions that does not contain a Global ID.
	 * </p>
	 * <p>
	 * Note: implicitly triggers deletion in Join Table via Trigger
	 * </p>
	 * 
	 * @return Number of transactions found and deleted.
	 */
	public abstract int purgeNonGlobalIDTransactions();

}