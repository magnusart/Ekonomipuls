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
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.proxy.bankdroid.BankDroidAccount;
import se.ekonomipuls.proxy.bankdroid.BankDroidBank;
import se.ekonomipuls.proxy.bankdroid.BankDroidProxy;
import se.ekonomipuls.proxy.bankdroid.BankDroidTransaction;

import com.liato.bankdroid.provider.IAccountTypes;
import com.liato.bankdroid.provider.IBankTypes;
import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 * @author Michael Svensson
 * @author Magnus Andersson
 */

@RunWith(RobolectricTestRunner.class)
public class BankDroidImportTest {

	private static final String ACC_ID = "1_1";

	private static final String BANK_NAME = "BankDroidBank";

	private static final String LAST_UPDATED = "DummyDate";

	private static final String ACC_NAME = null;

	private static final long BANK_ID = 2L;

	private static final BigDecimal ACCOUNT_BALANCE = new BigDecimal(1000.0);

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
	public void importTransactionsFromSingleAccount()
			throws IllegalAccessException {

		final List<BankDroidTransaction> mockedTransactions = setupMockedTranscationsList();
		when(bankDroidProxy.getBankDroidTransactions(isA(String.class)))
				.thenReturn(mockedTransactions);

		bankDroidImportService.importSingleAccount(ACC_ID);

		// verify that transactions are loaded from provider
		verify(bankDroidProxy).getBankDroidTransactions(eq(ACC_ID));

		// verify entries are put in staging
		verify(stagingDbFacade)
				.bulkInsertBdTransactions(eq(mockedTransactions));

		// Now we should have new transactions
		verify(ekonomipulsUtil).setNewTransactionStatus(eq(true));

	}

	@Test
	public void importTransactionsFromAllAccounts()
			throws IllegalAccessException {

		final Map<Long, BankDroidBank> mockedBankAccounts = setupMockedBanksMap();
		when(bankDroidProxy.getBankDroidBanks()).thenReturn(mockedBankAccounts);

		final List<BankDroidTransaction> mockedTransactions = setupMockedTranscationsList();
		when(bankDroidProxy.getBankDroidTransactions(isA(String.class)))
				.thenReturn(mockedTransactions);

		bankDroidImportService.importAllAccounts();

		final InOrder inOrder = inOrder(bankDroidProxy, stagingDbFacade);

		inOrder.verify(bankDroidProxy).getBankDroidBanks();

		for (int i = 0; i < 3; i++) {
			// verify that transactions are loaded from provider
			inOrder.verify(bankDroidProxy).getBankDroidTransactions(eq(ACC_ID
					+ "_0"));
			inOrder.verify(bankDroidProxy).getBankDroidTransactions(eq(ACC_ID
					+ "_1"));
			inOrder.verify(bankDroidProxy).getBankDroidTransactions(eq(ACC_ID
					+ "_2"));

			// verify entries are put in staging
			inOrder.verify(stagingDbFacade)
					.bulkInsertBdTransactions(eq(mockedTransactions));
		}

		// Now we should have new transactions
		verify(ekonomipulsUtil).setNewTransactionStatus(eq(true));

	}

	private Map<Long, BankDroidBank> setupMockedBanksMap() {
		final Map<Long, BankDroidBank> bankDroidBanks = new LinkedHashMap<Long, BankDroidBank>();

		for (int i = 0; i < 3; i++) {
			final BankDroidBank bank = new BankDroidBank(BANK_ID + i, BANK_NAME
					+ i, IBankTypes.TESTBANK, LAST_UPDATED);
			for (int j = 0; j < 3; j++) {
				final BankDroidAccount acc = new BankDroidAccount(ACC_ID + "_"
						+ j, ACCOUNT_BALANCE, ACC_NAME + j,
						IAccountTypes.REGULAR);
				bank.getAccounts().add(acc);
			}
			bankDroidBanks.put(bank.getId(), bank);
		}

		return bankDroidBanks;
	}

	private List<BankDroidTransaction> setupMockedTranscationsList() {
		final ArrayList<BankDroidTransaction> bankDroidTransactions = new ArrayList<BankDroidTransaction>();
		final BankDroidTransaction transaction = new BankDroidTransaction(
				"478", "2011-03-21", "Res. köp", new BigDecimal("-1103.00"),
				"SEK", ACC_ID);
		bankDroidTransactions.add(transaction);
		return bankDroidTransactions;
	}
}
