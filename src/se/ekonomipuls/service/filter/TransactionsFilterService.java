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
package se.ekonomipuls.service.filter;

import se.ekonomipuls.LogTag;
import android.app.IntentService;
import android.content.Intent;

/**
 * @author Magnus Andersson
 * @since 29 jan 2011
 */
public class TransactionsFilterService extends IntentService implements LogTag {

	public static final int IMPORT_FILTER_TYPES = 0;
	// Not supported yet.
	// public static final int GUI_FILTER_TYPES = 1;
	// public static final int ALL_FILTER_TYPES = 2;

	public static final String FILTER_CHAIN = "filterChain";

	/**
	 * 
	 */
	public TransactionsFilterService() {
		super(TransactionsFilterService.class.getClass().getName());
	}

	/** {@inheritDoc} */
	@Override
	protected void onHandleIntent(final Intent intent) {
		final int filterType = intent.getIntExtra(FILTER_CHAIN,
				IMPORT_FILTER_TYPES);

		switch (filterType) {
			case IMPORT_FILTER_TYPES:
				break;
			default:
				assert false : "Unrechable clause: default is import filters"; // We should never reach this state.
		}

		// TODO: Read filters based on type

		// TODO: Read and apply filters on unfiltered transactions

	}
}
