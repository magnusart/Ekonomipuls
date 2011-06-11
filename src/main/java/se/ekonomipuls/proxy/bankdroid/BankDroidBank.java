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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Magnus Andersson
 * @since 11 jun 2011
 */
public class BankDroidBank {
	private final long id;
	private final String name;
	private final int type;
	String lastUpdated;

	private final List<BankDroidAccount> accounts = new ArrayList<BankDroidAccount>();

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param lastUpdated
	 */
	public BankDroidBank(final long id, final String name, final int type,
			final String lastUpdated) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return the lastUpdated
	 */
	public String getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated
	 *            the lastUpdated to set
	 */
	public void setLastUpdated(final String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the accounts
	 */
	public List<BankDroidAccount> getAccounts() {
		return accounts;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accounts == null) ? 0 : accounts.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + type;
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
		final BankDroidBank other = (BankDroidBank) obj;
		if (accounts == null) {
			if (other.accounts != null) {
				return false;
			}
		} else if (!accounts.equals(other.accounts)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (lastUpdated == null) {
			if (other.lastUpdated != null) {
				return false;
			}
		} else if (!lastUpdated.equals(other.lastUpdated)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "BankDroidBank [id=" + id + ", name=" + name + ", type=" + type
				+ ", lastUpdated=" + lastUpdated + ", accounts=" + accounts
				+ "]";
	}
}
