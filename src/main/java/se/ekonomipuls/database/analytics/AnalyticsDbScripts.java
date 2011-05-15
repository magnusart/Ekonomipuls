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
package se.ekonomipuls.database.analytics;

import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Categories;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.FilterRules;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Joins;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Reports;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Tags;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Transactions;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants.Views;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public interface AnalyticsDbScripts {
	String DB_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Transactions.TABLE + " ( " + Transactions.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + Transactions.GLOBAL_ID
			+ " TEXT NOT NULL, " + Transactions.DATE + " TEXT NOT NULL, "
			+ Transactions.AMOUNT + " TEXT NOT NULL, "
			+ Transactions.DESCRIPTION + " TEXT NOT NULL, "
			+ Transactions.COMMENT + " TEXT, " + Transactions.CURRENCY
			+ " TEXT NOT NULL, " + Transactions.FILTERED
			+ " INTEGER NOT NULL DEFAULT 0, " + Transactions.VERIFIED
			+ " INTEGER NOT NULL DEFAULT 0, " + Transactions.BD_ACCOUNT
			+ " TEXT NOT NULL, " + "UNIQUE( " + Transactions.GLOBAL_ID + " ) "
			+ ")";

	String DB_CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Categories.TABLE + " ( " + Categories.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + Categories.NAME
			+ " TEXT NOT NULL, " + Categories.COLOR + " INTEGER NOT NULL, "
			+ Categories.TYPE + " TEXT NOT NULL, " + "UNIQUE( "
			+ Categories.NAME + " ) " + " )";

	String DB_CREATE_TAGS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tags.TABLE
			+ " ( " + Tags.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Tags.NAME + " TEXT NOT NULL, " + Tags.TYPE + " TEXT NOT NULL, "
			+ "UNIQUE ( " + Tags.NAME + " )" + " )";

	String DB_CREATE_FILTER_RULES_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ FilterRules.TABLE + " ( " + FilterRules.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FilterRules.NAME
			+ " TEXT NOT NULL, " + FilterRules.DESC + " TEXT, "
			+ FilterRules.PATTERN + " TEXT NOT NULL, "
			+ FilterRules.MARK_FILTER + " INTEGER NOT NULL DEFAULT 0, "
			+ FilterRules.PRIORITY + " INTEGER NOT NULL, " + "UNIQUE ( "
			+ FilterRules.PRIORITY + " )" + " )";

	String DB_CREATE_REPORTS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Reports.TABLE + " ( " + Reports.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + Reports.NAME
			+ " TEXT NOT NULL, " + Reports.DESC + " TEXT, " + Reports.DATE_FROM
			+ " TEXT NOT NULL, " + Reports.DATE_TO + " TEXT NOT NULL " + " )";

	String DB_CREATE_CATEGORIES_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Joins.CATEGORIES_TAGS_TABLE + " ( " + Joins.CAT_FK_1
			+ " INTEGER NOT NULL, " + Joins.TAG_FK_1 + " INTEGER NOT NULL, "
			+ "FOREIGN KEY(" + Joins.CAT_FK_1 + ") REFERENCES "
			+ Categories.TABLE + " (" + Categories.ID
			+ ") ON DELETE RESTRICT, " + "FOREIGN KEY(" + Joins.TAG_FK_1
			+ ") REFERENCES " + Tags.TABLE + "( " + Tags.ID
			+ " ) ON DELETE RESTRICT, " + "UNIQUE ( " + Joins.CAT_FK_1 + ", "
			+ Joins.TAG_FK_1 + " )" + ")";

	String DB_CREATE_TRANSACTIONS_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Joins.TRANSACTIONS_TAGS_TABLE
			+ " ( "
			+ Joins.TRANS_FK
			+ " INTEGER NOT NULL, "
			+ Joins.TAG_FK_2
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ Joins.TRANS_FK
			+ ") REFERENCES "
			+ Transactions.TABLE
			+ " ("
			+ Transactions.ID
			+ ") ON DELETE RESTRICT, "
			+ "FOREIGN KEY("
			+ Joins.TAG_FK_2
			+ ") REFERENCES "
			+ Tags.TABLE
			+ "( "
			+ Tags.ID
			+ " ) ON DELETE RESTRICT, "
			+ "UNIQUE ( "
			+ Joins.TRANS_FK
			+ ", "
			+ Joins.TAG_FK_2 + " )" + ")";

	String DB_CREATE_REPORTS_CATEGORIES_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Joins.REPORTS_CATEGORIES_TABLE
			+ " ( "
			+ Joins.REP_FK
			+ " INTEGER NOT NULL, "
			+ Joins.CAT_FK_2
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ Joins.REP_FK
			+ ") REFERENCES "
			+ Reports.TABLE
			+ " ("
			+ Reports.ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ Joins.CAT_FK_2
			+ ") REFERENCES "
			+ Categories.TABLE
			+ "( "
			+ Categories.ID
			+ " ) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( "
			+ Joins.REP_FK + ", " + Joins.CAT_FK_2 + " )" + ")";

	String DB_CREATE_FILTER_RULES_TAGS_JOIN_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Joins.FILTER_RULES_TAGS_TABLE
			+ " ( "
			+ Joins.FILTER_RULE_FK
			+ " INTEGER NOT NULL, "
			+ Joins.TAG_FK_3
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ Joins.FILTER_RULE_FK
			+ ") REFERENCES "
			+ FilterRules.TABLE
			+ " ("
			+ FilterRules.ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "FOREIGN KEY("
			+ Joins.TAG_FK_3
			+ ") REFERENCES "
			+ Tags.TABLE
			+ " ("
			+ Tags.ID
			+ ") ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "UNIQUE ( "
			+ Joins.FILTER_RULE_FK + ", " + Joins.TAG_FK_3 + " )" + ")";

	String DB_CREATE_TRANSACTIONS_CATEGORY_VIEW = "CREATE VIEW IF NOT EXISTS "
			+ Views.TRANSACTIONS_CATEGORY_VIEW + " AS " + "SELECT "
			+ Transactions.TABLE + "." + Transactions.ID + ", "
			+ Transactions.TABLE + "." + Transactions.GLOBAL_ID + ", "
			+ Transactions.TABLE + "." + Transactions.DATE + ", "
			+ Transactions.TABLE + "." + Transactions.DESCRIPTION + ", "
			+ Transactions.TABLE + "." + Transactions.COMMENT + ", "
			+ Transactions.TABLE + "." + Transactions.AMOUNT + ", "
			+ Transactions.TABLE + "." + Transactions.CURRENCY + ", "
			+ Transactions.TABLE + "." + Transactions.BD_ACCOUNT + ", "
			+ Transactions.TABLE + "." + Transactions.FILTERED + ", "
			+ Transactions.VERIFIED + ", " + Categories.TABLE + "."
			+ Categories.ID + " AS " + Views.TRANS_CAT_V_CAT_ID + " FROM "
			+ Categories.TABLE + " INNER JOIN " + Joins.CATEGORIES_TAGS_TABLE
			+ " ON " + Categories.TABLE + "." + Categories.ID + " = "
			+ Joins.CATEGORIES_TAGS_TABLE + "." + Joins.CAT_FK_1
			+ " INNER JOIN " + Joins.TRANSACTIONS_TAGS_TABLE + " ON "
			+ Joins.CATEGORIES_TAGS_TABLE + "." + Joins.TAG_FK_1 + " = "
			+ Joins.TRANSACTIONS_TAGS_TABLE + "." + Joins.TAG_FK_2
			+ " INNER JOIN " + Transactions.TABLE + " ON "
			+ Joins.TRANSACTIONS_TAGS_TABLE + "." + Joins.TRANS_FK + " = "
			+ Transactions.TABLE + "." + Transactions.ID;

	String DB_CREATE_CATEGORIES_REPORT_VIEW = "CREATE VIEW IF NOT EXISTS "
			+ Views.CATEGORIES_REPORT_VIEW + " AS " + "SELECT "
			+ Categories.TABLE + "." + Categories.ID + ", " + Categories.TABLE
			+ "." + Categories.NAME + ", " + Categories.TABLE + "."
			+ Categories.COLOR + ", " + Categories.TABLE + "."
			+ Categories.TYPE + ", " + Reports.TABLE + "." + Reports.ID
			+ " AS " + Views.REP_CAT_REP_ID + " FROM " + Reports.TABLE
			+ " INNER JOIN " + Joins.REPORTS_CATEGORIES_TABLE + " ON "
			+ Reports.TABLE + "." + Reports.ID + " = "
			+ Joins.REPORTS_CATEGORIES_TABLE + "." + Joins.REP_FK
			+ " INNER JOIN " + Categories.TABLE + " ON "
			+ Joins.REPORTS_CATEGORIES_TABLE + "." + Joins.CAT_FK_2 + " = "
			+ Categories.TABLE + "." + Categories.ID;

	String DB_CREATE_FILTER_RULE_TAGS_VIEW = "CREATE VIEW IF NOT EXISTS "
			+ Views.FILTER_RULES_TAGS_VIEW + " AS " + "SELECT " + Tags.TABLE
			+ "." + Tags.ID + ", " + Tags.TABLE + "." + Tags.NAME + ", "
			+ Tags.TABLE + "." + Tags.TYPE + ", " + FilterRules.TABLE + "."
			+ FilterRules.ID + " AS " + Views.FILTER_RULE_TAGS_FILTER_ID
			+ " FROM " + FilterRules.TABLE + " INNER JOIN "
			+ Joins.FILTER_RULES_TAGS_TABLE + " ON " + FilterRules.TABLE + "."
			+ FilterRules.ID + " = " + Joins.FILTER_RULES_TAGS_TABLE + "."
			+ Joins.FILTER_RULE_FK + " INNER JOIN " + Tags.TABLE + " ON "
			+ Joins.FILTER_RULES_TAGS_TABLE + "." + Joins.TAG_FK_3 + " = "
			+ Tags.TABLE + "." + Tags.ID;
}
