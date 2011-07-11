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

import static se.ekonomipuls.LogTag.TAG;

import java.util.List;

import com.google.inject.Inject;

import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.ExternalModelMapper;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.proxy.bankdroid.BankDroidTransaction;
import android.util.Log;

/**
 * @author Magnus Andersson
 * @since 14 jun 2011
 */
public class ExtractTransformLoadService {

	@Inject
	private EkonomipulsUtil util;

	@Inject
	private ExternalModelMapper extMapper;

	@Inject
	private StagingDbFacade stagingDbFacade;

	@Inject
	private AnalyticsTransactionsDbFacade analyticsTransactionsDbFacade;

	@Inject
	private DeduplicationService dedupService;

	@Inject
	private FilterRuleService filterService;

	public boolean performETL() {
		// Clean out Transactions without a Global ID.
		// final int i = analyticsTransactionsDbFacade
		// .purgeNonGlobalIDTransactions();
		//
		// Log.d(TAG, "Purged " + i
		// + " transactions that did not have a Global ID");

		// Get staged transactions with external model
		final List<BankDroidTransaction> stagedTransactions = stagingDbFacade
				.getStagedTransactions();

		Log.d(TAG, "Loaded staged transactions: " + stagedTransactions);

		// Transform to internal model
		final List<Transaction> transactions = extMapper
				.fromBdTransactionsToTransactions(stagedTransactions);

		// Clean out any duplicates before we add them to the staging table
		final List<Transaction> deduplicatedTransactions = dedupService
				.deduplicate(transactions);

		// Apply filters
		final List<ApplyFilterTagAction> filteredTransactions = filterService
				.applyFilters(deduplicatedTransactions);

		// Load the transactions into the Analytics table
		analyticsTransactionsDbFacade
				.insertTransactionsAssignTags(filteredTransactions);

		// Now purge the staging table
		stagingDbFacade.purgeStagingTable();

		// Reset the new transactions toggle
		util.setNewTransactionStatus(false);

		return true;
	}
}
