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
import android.os.RemoteException;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public interface AnalyticsTransactionsDbFacade {

	/**
	 * 
	 * @return
	 */
	public abstract List<Transaction> getUnfilteredTransactions();

	/**
	 * 
	 * @return
	 */
	public abstract List<Transaction> getUnverifiedTransactions();

	/**
	 * 
	 * @param cat
	 * @return
	 */
	public abstract List<Transaction> getTransactionsByCategory(
			final Category cat);

	/**
	 * 
	 * @return
	 */
	public abstract List<Transaction> getAllTransactions()
			throws RemoteException;

	/**
	 * @param actions
	 */
	public abstract void insertTransactionsAssignTags(
			final List<ApplyFilterTagAction> actions);

	/**
	 * 
	 */
	public abstract int purgeNonGlobalIDTransactions();

}