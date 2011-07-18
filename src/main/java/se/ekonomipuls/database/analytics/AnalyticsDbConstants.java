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
	int ANALYTICS_DB_VERSION = 2;
	String ANALYTICS_DB_NAME = "ekonomipuls_analytics.db";

	String[] DROP_ALL_TABLES = new String[] { Joins.CATEGORIES_TAGS_TABLE,
			Joins.TRANSACTIONS_TAGS_TABLE, Joins.REPORTS_CATEGORIES_TABLE,
			Joins.FILTER_RULES_TAGS_TABLE, FilterRules.TABLE, Categories.TABLE,
			Transactions.TABLE, Tags.TABLE, Reports.TABLE };

	String DROP_TABLE_PREFIX = "DROP TABLE IF EXISTS ";

	static interface Transactions {
		String TABLE = "transactions";
		// Columns for transaction table
		String ID = "_id";
		String GLOBAL_ID = "global_id";
		String DATE = "t_date";
		String DESCRIPTION = "description";
		String COMMENT = "comment";
		String AMOUNT = "amount";
		String CURRENCY = "currency";
		String BD_ACCOUNT = "bd_account_id";
		String FILTERED = "filtered";
		String VERIFIED = "verified";

		String[] COLUMNS = new String[] { ID, GLOBAL_ID, DATE, DESCRIPTION,
				COMMENT, AMOUNT, CURRENCY, FILTERED, VERIFIED, BD_ACCOUNT };
	};

	static interface Categories {

		String TABLE = "categories";
		// Columns for Categories
		String ID = "_id";
		String NAME = "name";
		String COLOR = "color";
		String TYPE = "entity_type";

		String[] COLUMNS = new String[] { ID, COLOR, NAME, TYPE };
	}

	static interface Tags {
		String TABLE = "tags";
		// Columns for Tags
		String ID = "_id";
		String NAME = "name";
		String TYPE = "entity_type";

		String[] COLUMNS = new String[] { ID, NAME, TYPE };
	}

	static interface Reports {
		String TABLE = "reports";
		// Columns for Reports table
		String ID = "_id";
		String NAME = "name";
		String DESC = "description";
		String DATE_FROM = "date_from";
		String DATE_TO = "date_to";

		String[] COLUMNS = new String[] { ID, NAME, DESC, DATE_FROM, DATE_TO };
	}

	static interface FilterRules {
		String TABLE = "filter_rules";
		// Columns for Filter Rules table
		String ID = "_id";
		String NAME = "name";
		String DESC = "description";
		String PATTERN = "pattern";
		String MARK_FILTER = "mark_filtered";
		String PRIORITY = "priority";

		String[] COLUMNS = new String[] { ID, NAME, DESC, PATTERN, MARK_FILTER,
				PRIORITY };
	}

	static interface Joins {
		String CATEGORIES_TAGS_TABLE = "categories_tags";
		// Columns for Categories/Tags join table
		String CAT_FK_1 = "category_fk";
		String TAG_FK_1 = "tag_fk";

		String TRANSACTIONS_TAGS_TABLE = "transactions_tags";
		// Columns for Transactions/Tags join table
		String TRANS_FK = "transaction_fk";
		String TAG_FK_2 = "tag_fk";

		String REPORTS_CATEGORIES_TABLE = "reports_categories";
		// Columns for Reports/Categories join table
		String REP_FK = "report_fk";
		String CAT_FK_2 = "category_fk";

		String FILTER_RULES_TAGS_TABLE = "filter_rules_tags_table";
		// Columns for Filter Rules/Tags join table
		String FILTER_RULE_FK = "filter_rule_fk";
		String TAG_FK_3 = "tag_fk";

	}

	static interface Views {
		String TRANSACTIONS_CATEGORY_FROM_STMT = Categories.TABLE
				+ " INNER JOIN " + Joins.CATEGORIES_TAGS_TABLE + " ON "
				+ Categories.TABLE + "." + Categories.ID + " = "
				+ Joins.CATEGORIES_TAGS_TABLE + "." + Joins.CAT_FK_1
				+ " INNER JOIN " + Joins.TRANSACTIONS_TAGS_TABLE + " ON "
				+ Joins.CATEGORIES_TAGS_TABLE + "." + Joins.TAG_FK_1 + " = "
				+ Joins.TRANSACTIONS_TAGS_TABLE + "." + Joins.TAG_FK_2
				+ " INNER JOIN " + Transactions.TABLE + " ON "
				+ Joins.TRANSACTIONS_TAGS_TABLE + "." + Joins.TRANS_FK + " = "
				+ Transactions.TABLE + "." + Transactions.ID;;

		String CATEGORIES_REPORT_FROM_STMT = Reports.TABLE + " INNER JOIN "
				+ Joins.REPORTS_CATEGORIES_TABLE + " ON " + Reports.TABLE + "."
				+ Reports.ID + " = " + Joins.REPORTS_CATEGORIES_TABLE + "."
				+ Joins.REP_FK + " INNER JOIN " + Categories.TABLE + " ON "
				+ Joins.REPORTS_CATEGORIES_TABLE + "." + Joins.CAT_FK_2 + " = "
				+ Categories.TABLE + "." + Categories.ID;

		String FILTER_RULES_TAGS_FROM_STMT = FilterRules.TABLE + " INNER JOIN "
				+ Joins.FILTER_RULES_TAGS_TABLE + " ON " + FilterRules.TABLE
				+ "." + FilterRules.ID + " = " + Joins.FILTER_RULES_TAGS_TABLE
				+ "." + Joins.FILTER_RULE_FK + " INNER JOIN " + Tags.TABLE
				+ " ON " + Joins.FILTER_RULES_TAGS_TABLE + "." + Joins.TAG_FK_3
				+ " = " + Tags.TABLE + "." + Tags.ID;

	}
}
