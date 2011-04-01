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
package se.ekonomipuls.model;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public class FilterRule {

	private final Long id;
	private final String name;
	private final String description;
	private final boolean markFiltered;
	private final String pattern;
	private final int priority;
	private Tag tag;

	/**
	 * 
	 * @param id
	 *            The database id.
	 * @param name
	 *            The name of the filter rule.
	 * @param description
	 *            The description of the filter rule.
	 * @param pattern
	 *            Regular expression to use for matching transaction
	 *            description.
	 * @param tag
	 *            Tags to apply to transaction if a match is found.
	 * @param markFiltered
	 *            Mark transaction as filtered after match. If false
	 *            transactions with matches will be applicable for any remaining
	 *            filter rules
	 */
	FilterRule(final Long id, final String name, final String description,
			final String pattern, final Tag tag, final boolean markFiltered,
			final int priority) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.pattern = pattern;
		this.tag = tag;
		this.markFiltered = markFiltered;
		this.priority = priority;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the markFiltered
	 */
	public boolean isMarkFiltered() {
		return markFiltered;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
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
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (markFiltered ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + priority;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		final FilterRule other = (FilterRule) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (markFiltered != other.markFiltered) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (pattern == null) {
			if (other.pattern != null) {
				return false;
			}
		} else if (!pattern.equals(other.pattern)) {
			return false;
		}
		if (priority != other.priority) {
			return false;
		}
		if (tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!tag.equals(other.tag)) {
			return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "FilterRule [id=" + id + ", name=" + name + ", description="
				+ description + ", markFiltered=" + markFiltered + ", pattern="
				+ pattern + ", priority=" + priority + ", tag=" + tag + "]";
	}

}
