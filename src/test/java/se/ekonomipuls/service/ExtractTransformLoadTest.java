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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.ExternalModelMapper;
import se.ekonomipuls.model.ModelResources;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.proxy.bankdroid.BankDroidTransaction;
import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
@RunWith(InjectedTestRunner.class)
public class ExtractTransformLoadTest {

	@Inject
	private ModelResources resources;

	@Inject
	@InjectMocks
	private ExtractTransformLoadService service;

	@Mock
	private FilterRuleService filterService;

	@Mock
	private DeduplicationService dedupService;

	@Mock
	private AnalyticsTransactionsDbFacade analyticsTransDbFacade;

	@Mock
	private StagingDbFacade stagingDbFacade;

	@Mock
	private ExternalModelMapper mapper;

	@Mock
	private EkonomipulsUtil util;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void transactionsStagingIntoAnalytics() throws InterruptedException,
			ExecutionException, TimeoutException {
		final List<BankDroidTransaction> mockedStagingTrans = resources
				.getStagingTransactions();

		final List<Transaction> mockedTransactions = resources
				.getTransactions();

		final List<ApplyFilterTagAction> mockedActions = resources
				.getTagFilterActions(mockedTransactions);

		when(stagingDbFacade.getStagedTransactions())
				.thenReturn(mockedStagingTrans);

		when(mapper.fromBdTransactionsToTransactions(eq(mockedStagingTrans)))
				.thenReturn(mockedTransactions);

		// Simulate that transactions have been removed in dedup.
		final List<Transaction> dedupTransactions = new ArrayList<Transaction>(
				mockedTransactions);
		Collections.copy(dedupTransactions, mockedTransactions);
		dedupTransactions.remove(0);

		when(dedupService.deduplicate(eq(mockedTransactions)))
				.thenReturn(dedupTransactions);

		// Simulate that transactions have been removed in dedup.
		mockedActions.remove(0);
		when(filterService.applyFilters(eq(dedupTransactions)))
				.thenReturn(mockedActions);

		final CountDownLatch latch = new CountDownLatch(1);

		service.performETL();

		verify(stagingDbFacade).getStagedTransactions();

		verify(mapper).fromBdTransactionsToTransactions(eq(mockedStagingTrans));

		verify(dedupService).deduplicate(eq(mockedTransactions));

		verify(filterService).applyFilters(eq(dedupTransactions));

		verify(analyticsTransDbFacade)
				.insertTransactionsAssignTags(eq(mockedActions));

		verify(stagingDbFacade).purgeStagingTable();

		verify(util).setNewTransactionStatus(eq(false));
	}
}
