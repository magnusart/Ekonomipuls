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
package se.ekonomipuls.actions;

import se.ekonomipuls.database.Transaction;

/**
 * This class encapsulates a command that applies an filter to a transaction. It
 * will set the filtered value to true.
 * 
 * @author Magnus Andersson
 * @since 17 feb 2011
 */
public final class ApplyFilterTagAction {

	private final Transaction transaction;
	private final long tagId;

	/**
	 * @param transaction
	 *            Transaction that should be modified
	 * @param tagId
	 */
	public ApplyFilterTagAction(final Transaction transaction, final long tagId) {
		this.transaction = transaction;
		this.tagId = tagId;
	}

	/**
	 * @return the transaction
	 */
	public Transaction getTransaction() {
		return transaction;
	}

	/**
	 * @return the tagId
	 */
	public long getTagId() {
		return tagId;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "ApplyFilterTagAction [transaction=" + transaction + ", tagId="
				+ tagId + "]";
	}
}
