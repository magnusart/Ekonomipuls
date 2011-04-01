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

import se.ekonomipuls.model.Tag;
import se.ekonomipuls.model.Transaction;

/**
 * This class encapsulates a command that applies an filter to a transaction. It
 * will set the filtered value to true.
 * 
 * @author Magnus Andersson
 * @since 17 feb 2011
 */
public final class ApplyFilterTagAction {

	private Transaction transaction;
	private Tag tag;

	/**
	 * @param transaction
	 *            Transaction that should be modified
	 * @param tag
	 */
	public ApplyFilterTagAction(final Transaction transaction, final Tag tag) {
		this.transaction = transaction;
		this.tag = tag;

	}

	/**
	 * @return the transaction
	 */
	public Transaction getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction
	 *            the transaction to set
	 */
	public void setTransaction(final Transaction transaction) {
		this.transaction = transaction;
	}

	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(final Tag tag) {
		this.tag = tag;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result
				+ ((transaction == null) ? 0 : transaction.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ApplyFilterTagAction other = (ApplyFilterTagAction) obj;
		if (tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!tag.equals(other.tag)) {
			return false;
		}
		if (transaction == null) {
			if (other.transaction != null) {
				return false;
			}
		} else if (!transaction.equals(other.transaction)) {
			return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "ApplyFilterTagAction [transaction=" + transaction + ", tag="
				+ tag + "]";
	}

}
