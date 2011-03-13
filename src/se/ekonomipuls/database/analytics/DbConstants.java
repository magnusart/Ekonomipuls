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
 * @since 17 feb 2011
 */
public interface DbConstants {

	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "ekonomipuls.db";
	public static final String TURN_ON_FK = "PRAGMA foreign_keys = ON;";

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

		public static final String[] COLUMNS = new String[] { ID, GLOBAL_ID,
				DATE, DESCRIPTION, COMMENT, AMOUNT, CURRENCY, FILTERED,
				BD_ACCOUNT };
	};

	static interface Categories {

		public final static String TABLE = "categories";
		// Columns for Categories
		public final static String ID = "_id";
		public final static String NAME = "name";
		public final static String COLOR = "color";

		public static final String[] COLUMNS = new String[] { ID, COLOR, NAME };
	}

	static interface Tags {
		public static final String TABLE = "tags";
		// Columns for Tags
		public final static String ID = "_id";
		public final static String NAME = "name";

		public static final String[] COLUMNS = new String[] { ID, NAME };
	}

	static interface Reports {
		public final static String TABLE = "reports";
		// Columns for reports Table
		public final static String ID = "_id";
		public final static String NAME = "name";
		public final static String DESC = "description";
		public final static String DATE_FROM = "date_from";
		public final static String DATE_TO = "date_to";

		public static final String[] COLUMNS = new String[] { ID, NAME, DESC,
				DATE_FROM, DATE_TO };
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
	}

	static interface Views {
		public final static String TRANSACTIONS_CATEGORY_VIEW = "transactions_category_view";
		// Special columns for this view
		public final static String TRANS_CAT_V_CAT_ID = "cat_id";

		public final static String CATEGORIES_REPORT_VIEW = "categories_report_view";
		// Special columns for this view
		public final static String REP_CAT_REP_ID = "rep_id";
	}

	static interface Provider {
		// Content provider URI:s
		public static final String AUTHORITY = "se.ekonomipuls.provider";

		public static final String TRANSACTIONS = Transactions.TABLE;
		public static final String TRANSACTIONS_CATEGORY = Transactions.TABLE
				+ "/" + Categories.TABLE;
		public static final String CATEGORIES_REPORT = Categories.TABLE + "/"
				+ Reports.TABLE;
		public static final String CATEGORIES = Categories.TABLE;
		public static final String TRANSACTIONS_TAGS = Joins.TRANSACTIONS_TAGS_TABLE;

		public static final String AUTH_URI_PART = "content://" + AUTHORITY;

		// URI:s
		public static final String TRANSACTIONS_URI = AUTH_URI_PART + "/"
				+ TRANSACTIONS;
		public static final String TRANSACTIONS_CATEGORY_URI = AUTH_URI_PART
				+ "/" + TRANSACTIONS_CATEGORY;
		public static final String CATEGORIES_REPORT_URI = AUTH_URI_PART + "/"
				+ CATEGORIES_REPORT;
		public static final String CATEGORIES_URI = AUTH_URI_PART + "/"
				+ CATEGORIES;
		public static final String TRANSACTIONS_TAGS_URI = AUTH_URI_PART + "/"
				+ TRANSACTIONS_TAGS;

		public static final String REPORTS = "/" + Reports.TABLE;

		public static final String TRANSACTIONS_MIME = "vnd.android.cursor.dir/vnd.ekonomipuls.transactions";
		public static final String REPORTS_MIME = "vnd.android.cursor.dir/vnd.ekonomipuls.reports";
		public static final String CATEGORIES_MIME = "vnd.android.cursor.dir/vnd.ekonomipuls.categories";
	}
}