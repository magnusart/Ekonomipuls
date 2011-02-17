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
package se.ekonomipuls.database;

/**
 * @author Magnus Andersson
 * @since 17 feb 2011
 */
interface DbConstants {

	public static final int DB_VERSION = 1;

	public final static String TRANSACTIONS_TABLE = "transactions";
	// Columns for transaction table
	public final static String TRANS_ID = "_id";
	public final static String TRANS_GLOBAL_ID = "global_id";
	public final static String TRANS_DATE = "t_date";
	public final static String TRANS_DESCRIPTION = "description";
	public final static String TRANS_COMMENT = "comment";
	public final static String TRANS_AMOUNT = "amount";
	public final static String TRANS_CURRENCY = "currency";
	public final static String TRANS_BD_ACCOUNT = "bd_account_id";
	public final static String TRANS_FILTERED = "filtered";

	public final static String CATEGORIES_TABLE = "categories";
	// Columns for Categories
	public final static String CAT_ID = "_id";
	public final static String CAT_NAME = "name";

	public static final String TAGS_TABLE = "tags";
	// Columns for Tags
	public final static String TAG_ID = "_id";
	public final static String TAG_NAME = "name";

	public final static String CATEGORIES_TAGS_TABLE = "categories_tags";
	// Columns for Categories/Tags join table
	public final static String CAT_FK_1 = "category_fk";
	public final static String TAG_FK_1 = "tag_fk";

	public final static String TRANSACTIONS_TAGS_TABLE = "transactions_tags";
	// Columns for Transactions/Tags join table
	public final static String TRANS_FK = "transaction_fk";
	public final static String TAG_FK_2 = "tag_fk";

	public final static String REPORTS_TABLE = "reports";
	// Columns for reports Table
	public final static String REP_ID = "_id";
	public final static String REP_NAME = "name";
	public final static String REP_DESC = "description";
	public final static String REP_DATE_FROM = "date_from";
	public final static String REP_DATE_TO = "date_to";

	public final static String REPORTS_CATEGORIES_TABLE = "reports_categories";
	// Columns for Reports/Categories join table
	public final static String REP_FK = "report_fk";
	public final static String CAT_FK_2 = "category_fk";

	public final static String TRANSACTIONS_CATEGORY_VIEW = "transactions_category_view";
	// Special columns for this view
	public final static String TRANS_CAT_V_CAT_ID = "cat_id";

	public static final String DB_NAME = "ekonomipuls.db";
	public static final String TURN_ON_FK = "PRAGMA foreign_keys = ON;";
}