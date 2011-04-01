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
package se.ekonomipuls.proxy;

import java.math.BigDecimal;

import se.ekonomipuls.database.staging.StagingDbConstants.Staging;
import android.database.Cursor;

/**
 * @author Magnus Andersson
 * @since 16 mar 2011
 */
public class BankDroidModelSqlMapper {

	/**
	 * @param cur
	 * @return
	 */
	public static int[] getStagedTransactionCursorIndices(final Cursor cur) {
		final int[] indices = new int[7];

		indices[0] = cur.getColumnIndexOrThrow(Staging.ID);
		indices[1] = cur.getColumnIndexOrThrow(Staging.GLOBAL_ID);
		indices[2] = cur.getColumnIndexOrThrow(Staging.DATE);
		indices[3] = cur.getColumnIndexOrThrow(Staging.DESCRIPTION);
		indices[4] = cur.getColumnIndexOrThrow(Staging.AMOUNT);
		indices[5] = cur.getColumnIndexOrThrow(Staging.CURRENCY);
		indices[6] = cur.getColumnIndexOrThrow(Staging.BD_ACCOUNT);

		return indices;
	}

	/**
	 * @param cur
	 * @param indices
	 * @return
	 */
	public static BankDroidTransaction mapStagedTransactionModel(
			final Cursor cur, final int[] indices) {
		final String glob = cur.getString(indices[1]);
		final String date = cur.getString(indices[2]);
		final String desc = cur.getString(indices[3]);
		final BigDecimal amt = new BigDecimal(cur.getString(indices[4]));
		final String curr = cur.getString(indices[5]);
		final String bdAcc = cur.getString(indices[6]);

		return new BankDroidTransaction(glob, date, desc, amt, curr, bdAcc);
	}

}
