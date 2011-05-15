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
package se.ekonomipuls.database.analytics;

/**
 * @author Magnus Andersson
 * @since 13 mar 2011
 */
public interface AnalyticsDbConstants {
	// Database configuration for Analytics.
	public static final int ANALYTICS_DB_VERSION = 1;
	public static final String ANALYTICS_DB_NAME = "ekonomipuls_analytics.db";

	static interface Transactions {
		public final static String TABLE = "transactions";
		// Columns for transaction table
		public final static String ID = "_id";
		public final static String GLOBAL_ID = "global_id";
		public final static String DATE = "t_date";
		public final static String DESCRIPTION = "description";
		public final static String COMMENT = "comment";
		public final static String AMOUNT = "amount";
		public final static String CURRENCY = "currency";
		public final static String BD_ACCOUNT = "bd_account_id";
		public final static String FILTERED = "filtered";
		public final static String VERIFIED = "verified";

		public static final String[] COLUMNS = new String[] { ID, GLOBAL_ID,
				DATE, DESCRIPTION, COMMENT, AMOUNT, CURRENCY, FILTERED,
				VERIFIED, BD_ACCOUNT };
	};

	static interface Categories {

		public final static String TABLE = "categories";
		// Columns for Categories
		public final static String ID = "_id";
		public final static String NAME = "name";
		public final static String COLOR = "color";
		public static final String TYPE = "entity_type";

		public static final String[] COLUMNS = new String[] { ID, COLOR, NAME,
				TYPE };
	}

	static interface Tags {
		public static final String TABLE = "tags";
		// Columns for Tags
		public final static String ID = "_id";
		public final static String NAME = "name";
		public static final String TYPE = "entity_type";

		public static final String[] COLUMNS = new String[] { ID, NAME, TYPE };
	}

	static interface Reports {
		public final static String TABLE = "reports";
		// Columns for Reports table
		public final static String ID = "_id";
		public final static String NAME = "name";
		public final static String DESC = "description";
		public final static String DATE_FROM = "date_from";
		public final static String DATE_TO = "date_to";

		public static final String[] COLUMNS = new String[] { ID, NAME, DESC,
				DATE_FROM, DATE_TO };
	}

	static interface FilterRules {
		public final static String TABLE = "filter_rules";
		// Columns for Filter Rules table
		public final static String ID = "_id";
		public final static String NAME = "name";
		public final static String DESC = "description";
		public final static String PATTERN = "pattern";
		public final static String MARK_FILTER = "mark_filtered";
		public final static String PRIORITY = "priority";

		public static final String[] COLUMNS = new String[] { ID, NAME, DESC,
				PATTERN, MARK_FILTER, PRIORITY };
	}

	static interface Joins {
		public final static String CATEGORIES_TAGS_TABLE = "categories_tags";
		// Columns for Categories/Tags join table
		public final static String CAT_FK_1 = "category_fk";
		public final static String TAG_FK_1 = "tag_fk";

		public final static String TRANSACTIONS_TAGS_TABLE = "transactions_tags";
		// Columns for Transactions/Tags join table
		public final static String TRANS_FK = "transaction_fk";
		public final static String TAG_FK_2 = "tag_fk";

		public final static String REPORTS_CATEGORIES_TABLE = "reports_categories";
		// Columns for Reports/Categories join table
		public final static String REP_FK = "report_fk";
		public final static String CAT_FK_2 = "category_fk";

		public final static String FILTER_RULES_TAGS_TABLE = "filter_rules_tags_table";
		// Columns for Filter Rules/Tags join table
		public final static String FILTER_RULE_FK = "filter_rule_fk";
		public final static String TAG_FK_3 = "tag_fk";

	}

	static interface Views {
		public final static String TRANSACTIONS_CATEGORY_VIEW = "transactions_category_view";
		// Special columns for this view
		public final static String TRANS_CAT_V_CAT_ID = "cat_id";

		public final static String CATEGORIES_REPORT_VIEW = "categories_report_view";
		// Special columns for this view
		public final static String REP_CAT_REP_ID = "rep_id";

		public final static String FILTER_RULES_TAGS_VIEW = "filter_rules_report_view";
		// Special columns for this view
		public final static String FILTER_RULE_TAGS_FILTER_ID = "filter_rule_id";
	}
}
