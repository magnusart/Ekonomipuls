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

import java.util.List;

import se.ekonomipuls.model.Transaction;
import com.google.inject.Singleton;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
@Singleton
public class DeduplicationService {

	/**
	 * @param transactions
	 * @return
	 */
	public List<Transaction> deduplicate(final List<Transaction> transactions) {
		// FIXME Implement deduplication
		return transactions;
	}

}
