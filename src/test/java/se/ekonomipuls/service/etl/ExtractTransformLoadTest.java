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
package se.ekonomipuls.service.etl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import se.ekonomipuls.model.ExternalModelMapper;
import se.ekonomipuls.model.TestResourcesCreator;
import se.ekonomipuls.model.Transaction;
import se.ekonomipuls.proxy.BankDroidTransaction;
import se.ekonomipuls.util.EkonomipulsUtil;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
@RunWith(InjectedTestRunner.class)
public class ExtractTransformLoadTest {

	@Inject
	TestResourcesCreator resources;

	@Inject
	@InjectMocks
	ExtractTransformLoadTransactionsTask task;

	@Mock
	private AnalyticsTransactionsDbFacade analyticsTransDbFacade;
	@Mock
	private StagingDbFacade stagingDbFacade;

	@Mock
	private ExternalModelMapper mapper;

	@Mock
	private EkonomipulsUtil ekonomipulsUtil;

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
				.getConvertedBdTransactions();

		final List<ApplyFilterTagAction> mockedActions = resources
				.getTagFilterActions(mockedTransactions);

		when(stagingDbFacade.getStagedTransactions())
				.thenReturn(mockedStagingTrans);

		when(mapper.fromBdTransactionsToTransactions(eq(mockedStagingTrans)))
				.thenReturn(mockedTransactions);

		// Emulate .execute call but stay in this thread.
		task.onPreExecute();
		task.call();
		task.onFinally();

		verify(analyticsTransDbFacade)
				.insertTransactionsAssignTags(eq(mockedActions));

		verify(stagingDbFacade).purgeStagingTable();

		verify(ekonomipulsUtil).setNewTransactionStatus(eq(false));
	}
}
