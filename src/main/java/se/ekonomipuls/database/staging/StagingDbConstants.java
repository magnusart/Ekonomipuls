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
package se.ekonomipuls.database.staging;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public interface StagingDbConstants {
	// Database configuration for Staging.
	public static final int STAGING_DB_VERSION = 1;
	public static final String STAGING_DB_NAME = "ekonomipuls_staging.db";

	static interface Staging {
		public final static String TABLE = "bankdroid_staging";
		// Columns for transaction table
		public final static String ID = "_id";
		public final static String GLOBAL_ID = "global_id";
		public final static String DATE = "t_date";
		public final static String DESCRIPTION = "description";
		public final static String AMOUNT = "amount";
		public final static String CURRENCY = "currency";
		public final static String BD_ACCOUNT = "bd_account_id";

		public static final String[] COLUMNS = new String[] { ID, GLOBAL_ID,
				DATE, DESCRIPTION, AMOUNT, CURRENCY, BD_ACCOUNT };
	};
}
