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

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public interface AnalyticsDbScripts {
	String DB_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ Transactions.TABLE + " ( " + Transactions.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + Transactions.GLOBAL_ID
			+ " TEXT NOT NULL DEFAULT '', " + Transactions.DATE
			+ " TEXT NOT NULL, " + Transactions.AMOUNT + " TEXT NOT NULL, "
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
			+ " TEXT NOT NULL, " + Categories.COLOR + " TEXT NOT NULL, "
			+ Categories.TYPE + " TEXT NOT NULL, " + "UNIQUE( "
			+ Categories.NAME + ", " + Categories.TYPE + " ) " + " )";

	String DB_CREATE_TAGS_TABLE = "CREATE TABLE IF NOT EXISTS " + Tags.TABLE
			+ " ( " + Tags.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Tags.NAME + " TEXT NOT NULL, " + Tags.TYPE + " TEXT NOT NULL, "
			+ "UNIQUE ( " + Tags.NAME + ", " + Tags.TYPE + " )" + " )";

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

	String DB_CREATE_TRIGGER_TRANSACTIONS_DELETE_CASCADE = "CREATE TRIGGER [delete_"
			+ Transactions.TABLE
			+ "] "
			+ "BEFORE DELETE ON ["
			+ Transactions.TABLE
			+ "] "
			+ "FOR EACH ROW BEGIN "
			+ "DELETE FROM "
			+ Joins.TRANSACTIONS_TAGS_TABLE
			+ " WHERE "
			+ Joins.TRANS_FK + " = old." + Transactions.ID + "; " + "END";

}
