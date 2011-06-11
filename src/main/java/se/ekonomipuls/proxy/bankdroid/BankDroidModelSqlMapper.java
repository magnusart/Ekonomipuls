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
package se.ekonomipuls.proxy.bankdroid;

import java.math.BigDecimal;

import com.google.inject.Singleton;
import com.liato.bankdroid.provider.IBankTransactionsProvider;

import se.ekonomipuls.database.abstr.AbstractMapper;
import android.database.Cursor;

/**
 * @author Magnus Andersson
 * @since 16 mar 2011
 */
@Singleton
public class BankDroidModelSqlMapper extends AbstractMapper {

	/**
	 * @param cur
	 * @param indices
	 * @return
	 */
	public BankDroidTransaction mapBdTransactionModel(final Cursor cur,
			final int[] indices) {
		final String id = cur.getString(indices[0]);
		final String date = cur.getString(indices[1]);
		final String desc = cur.getString(indices[2]);
		final BigDecimal amt = new BigDecimal(cur.getString(indices[3]));
		final String curr = cur.getString(indices[4]);
		final String acc = cur.getString(indices[5]);

		return new BankDroidTransaction(id, date, desc, amt, curr, acc);
	}

	/**
	 * @param cur
	 * @param indices
	 * @return
	 */
	public BankDroidBank mapBdBankModel(final Cursor cur, final int[] indices) {
		final long id = cur.getInt(indices[0]);
		final String name = cur.getString(indices[1]);
		final int type = cur.getInt(indices[2]);
		final String date = cur.getString(indices[3]);

		return new BankDroidBank(id, name, type, date);
	}

	/**
	 * @param cur
	 * @param indices
	 * @return
	 */
	public BankDroidAccount mapBdAccountModel(final Cursor cur,
			final int[] indices) {
		final String accId = cur.getString(indices[4]);
		final BigDecimal bal = new BigDecimal(cur.getString(indices[5]));
		final String name = cur.getString(indices[6]);
		final int type = cur.getInt(7);

		return new BankDroidAccount(accId, bal, name, type);
	}

	/**
	 * @param cur
	 * @return
	 */
	public int[] getBdTransactionIndices(final Cursor cur) {
		return getIndices(cur, IBankTransactionsProvider.TRANSACTIONS_PROJECTION);
	}

	/**
	 * @param cur
	 * @return
	 */
	public int[] getBdBankAccountIndices(final Cursor cur) {
		return getIndices(cur, IBankTransactionsProvider.BANK_ACCOUNT_PROJECTION);
	}

}
