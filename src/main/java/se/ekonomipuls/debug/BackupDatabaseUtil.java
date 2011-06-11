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
package se.ekonomipuls.debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import se.ekonomipuls.LogTag;
import se.ekonomipuls.database.analytics.AnalyticsDbConstants;
import se.ekonomipuls.database.staging.StagingDbConstants;
import android.os.Environment;
import android.util.Log;

import com.google.inject.Singleton;

/**
 * @author Magnus Andersson
 * @since 3 jun 2011
 */
@Singleton
public class BackupDatabaseUtil implements LogTag {

	private final String ANALYTICS_DB_PATH = "/data/se.ekonomipuls/databases/"
			+ AnalyticsDbConstants.ANALYTICS_DB_NAME;
	private final String BACKUP_ANAYTICS_DB_PATH = "/"
			+ AnalyticsDbConstants.ANALYTICS_DB_NAME;

	private final String STAGING_DB_PATH = "/data/se.ekonomipuls/databases/"
			+ StagingDbConstants.STAGING_DB_NAME;
	private final String BACKUP_STAGING_DB_PATH = "/"
			+ StagingDbConstants.STAGING_DB_NAME;

	public void doBackup() throws FileNotFoundException, IOException {
		Log.d(TAG, "Preparing to backup analytics database");
		final File sd = Environment.getExternalStorageDirectory();
		final File data = Environment.getDataDirectory();
		if (sd.canWrite()) {

			final File currentAnalyticsDB = new File(data, ANALYTICS_DB_PATH);
			final File backupAnalyticsDB = new File(sd, BACKUP_ANAYTICS_DB_PATH);

			Log.d(TAG, "Trying to access database file at " + data
					+ ANALYTICS_DB_PATH);

			copyFileToSd(sd, currentAnalyticsDB, backupAnalyticsDB);

			final File currentStagingDB = new File(data, STAGING_DB_PATH);
			final File backupStagingDB = new File(sd, BACKUP_STAGING_DB_PATH);

			Log.d(TAG, "Trying to access database file at " + data
					+ STAGING_DB_PATH);

			copyFileToSd(sd, currentStagingDB, backupStagingDB);

		} else {
			Log.d(TAG, "Could not write to SD Card");
		}
	}

	/**
	 * @param sd
	 * @param currentDB
	 * @param backupDB
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void copyFileToSd(final File sd, final File currentDB,
			final File backupDB) throws FileNotFoundException, IOException {
		if (currentDB.exists()) {
			final FileChannel src = new FileInputStream(currentDB).getChannel();
			final FileChannel dst = new FileOutputStream(backupDB).getChannel();
			Log.d(TAG, "Transferring database to " + sd
					+ BACKUP_ANAYTICS_DB_PATH);

			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();
		} else {
			Log.d(TAG, "Could not find the database file");
		}
	}
}
