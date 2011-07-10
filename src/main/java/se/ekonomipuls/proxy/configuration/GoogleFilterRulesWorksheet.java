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
package se.ekonomipuls.proxy.configuration;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Magnus Andersson
 * @since 7 jul 2011
 */
class GoogleFilterRulesWorksheet {

	/**
	 * @author Magnus Andersson
	 * @since 7 jul 2011
	 */
	static class Entry {
		Value updated;
		Value content;

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "Entry [updated=" + updated + ", content=" + content + "]";
		}

	}

	/**
	 * @author Magnus Andersson
	 * @since 7 jul 2011
	 */
	static class Value {
		@SerializedName("$t")
		String value;

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * @author Magnus Andersson
	 * @since 7 jul 2011
	 */
	static class Feed {
		Value title;
		Value updated;
		List<Entry> entry;

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "Feed [title=" + title + ", entry=" + entry + "]";
		}

	}

	String version;
	String encoding;
	Feed feed;

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "FilterRuleWorksheet [version=" + version + ", encoding="
				+ encoding + ", feed=" + feed + "]";
	}

}
