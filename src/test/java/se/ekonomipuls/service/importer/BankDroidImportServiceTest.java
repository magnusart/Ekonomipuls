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
package se.ekonomipuls.service.importer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.proxy.BankDroidProxy;
import se.ekonomipuls.proxy.BankDroidTransaction;
import se.ekonomipuls.util.EkonomipulsUtil;
import android.content.Intent;

import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 * @author Michael Svensson
 * @author Magnus Andersson
 */

@RunWith(RobolectricTestRunner.class)
public class BankDroidImportServiceTest {

	private static final String ACC_ID = "1_1";

	@InjectMocks
	private final BankDroidImportService bankDroidImportService = new BankDroidImportService();

	@Mock
	private BankDroidProxy bankDroidProxy;
	@Mock
	private StagingDbFacade stagingDbFacade;
	@Mock
	private EkonomipulsUtil ekonomipulsUtil;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void transactionsInsertedInStaging() throws IllegalAccessException {

		final Intent intent = new Intent();
		intent.putExtra("accountId", ACC_ID);

		final List<BankDroidTransaction> mockedTransactions = setupMockedList();
		when(bankDroidProxy.getBankDroidTransactions(isA(String.class)))
				.thenReturn(mockedTransactions);

		bankDroidImportService.onHandleIntent(intent);

		// verify that transactions are loaded from provider
		verify(bankDroidProxy).getBankDroidTransactions(eq(ACC_ID));

		// verify entries are put in staging
		verify(stagingDbFacade)
				.bulkInsertBdTransactions(eq(mockedTransactions));

		verify(ekonomipulsUtil).setNewTransactionStatus(eq(true));
	}

	private List<BankDroidTransaction> setupMockedList() {
		final ArrayList<BankDroidTransaction> bankDroidTransactions = new ArrayList<BankDroidTransaction>();
		final BankDroidTransaction transaction = new BankDroidTransaction(
				"478", "2011-03-21", "Res. köp", new BigDecimal("-1103.00"),
				"SEK", ACC_ID);
		bankDroidTransactions.add(transaction);
		return bankDroidTransactions;
	}
}
