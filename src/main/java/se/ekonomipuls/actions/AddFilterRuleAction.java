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
package se.ekonomipuls.actions;

import se.ekonomipuls.model.FilterRule;

/**
 * @author Magnus Andersson
 * @since 20 maj 2011
 */
public class AddFilterRuleAction extends FilterRule {

	private long tagId;

	/**
	 * 
	 * @param name
	 * @param description
	 * @param pattern
	 * @param markFiltered
	 * @param priority
	 * @param tagId
	 */
	public AddFilterRuleAction(final String name, final String description,
			final String pattern, final boolean markFiltered,
			final int priority, final long tagId) {
		super(0L, name, description, pattern, null, markFiltered, priority);
		this.tagId = tagId;
	}

	/**
	 * @return the tagId
	 */
	public long getTagId() {
		return tagId;
	}

	/**
	 * @param tagId
	 *            the tagId to set
	 */
	public void setTagId(final long tagId) {
		this.tagId = tagId;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (tagId ^ (tagId >>> 32));
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof AddFilterRuleAction)) {
			return false;
		}
		final AddFilterRuleAction other = (AddFilterRuleAction) obj;
		if (tagId != other.tagId) {
			return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "AddFilterRuleAction [tagId=" + tagId + ", getId()=" + getId()
				+ ", getName()=" + getName() + ", getDescription()="
				+ getDescription() + ", getPattern()=" + getPattern()
				+ ", isMarkFiltered()=" + isMarkFiltered() + ", getPriority()="
				+ getPriority() + ", getTag()=" + getTag() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + "]";
	}

}
