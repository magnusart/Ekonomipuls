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
package se.ekonomipuls.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectResource;
import se.ekonomipuls.HomeScreenTask;
import se.ekonomipuls.PropertiesConstants;
import se.ekonomipuls.R;
import se.ekonomipuls.views.charts.SeriesEntry;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.view.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Global utility methods for Ekonomipuls aka "Slasktratten".
 * 
 * @author Magnus Andersson
 * @since 13 feb 2011
 */
@Singleton
public class EkonomipulsUtil implements PropertiesConstants {

	/**
	 * @author Magnus Andersson
	 * @since 19 maj 2011
	 */
	public static enum ConfigurationFileType {
		CATEGORIES("categories.json"), TAGS("tags.json"), FILTER_RULES(
				"filter_rules.json");

		private final String fileName;

		ConfigurationFileType(final String fileName) {
			this.fileName = fileName;
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}
	}

	@Inject
	private Context context;

	@Inject
	private SharedPreferences preferences;

	@InjectResource(R.string.default_expense_tag_name)
	private String defaultExpensesTagName;

	@InjectResource(R.string.default_incomes_tag_name)
	private String defaultIncomesTagName;

	@InjectResource(R.string.economic_overview_name)
	private String reportName;

	@InjectResource(R.string.economic_overview_desc)
	private String reportDesc;

	@InjectResource(R.string.economic_overview_date_from)
	private String reportFrom;

	@InjectResource(R.string.economic_overview_date_to)
	private String reportTo;

	@InjectResource(R.string.action_notify_home_screen)
	private String notifyHomeScreenAction;

	@InjectResource(R.string.extras_key_home_screen_task)
	private String homeScreenTaskKey;

	private static final float DARK_GRAD_SATURATION = 1.0f;
	private static final float DARK_GRAD_BRIGHTNESS = 0.4f;

	private static final float LIGHT_GRAD_SATURATION = 0.85f;
	private static final float LIGHT_GRAD_BRIGHTNESS = 1.0f;

	// When a slice is unselected
	private static final float SELECT_DESATURATION = 0.2f;
	private static final float SELECT_DIM = 0.1f;

	/**
	 * 
	 * @param part
	 * @param total
	 * @return
	 */
	public int getPercentage(final float part, final float total) {
		return Math.round((part / total) * 100);
	}

	/**
	 * 
	 * @param oval
	 * @param entry
	 * @return
	 */
	public Shader createGradientFromBaseColor(final RectF oval,
			final SeriesEntry entry) {
		final int baseColor = entry.getBaseColor();
		int dark = 0;
		int light = 0;

		dark = getDarkColor(baseColor, entry.isSelected());
		light = getLightColor(baseColor, entry.isSelected());

		final float skewTop = (oval.width() / 3);
		final float skewBottom = (oval.width() / 3) * 2;

		return new LinearGradient(skewBottom, oval.height(), skewTop, 0L,
				new int[] { dark, light }, null, TileMode.CLAMP);

		// return new RadialGradient(oval.centerX(), oval.centerY(),
		// oval.width() / 2, dark, light, TileMode.CLAMP);
	}

	/**
	 * @param baseColor
	 * @param entrySelected
	 * @return
	 */
	public int getLightColor(final int baseColor, final boolean entrySelected) {
		final float[] hsv = new float[3];

		Color.colorToHSV(baseColor, hsv);

		float saturation = LIGHT_GRAD_SATURATION;
		float brightness = LIGHT_GRAD_BRIGHTNESS;

		if ((baseColor == Color.GRAY) || (baseColor == Color.WHITE)
				|| (baseColor == Color.BLACK)) {
			saturation = 0.0f;
			brightness += 0.2f;
		}

		hsv[1] = saturation;
		hsv[2] = brightness;

		if (!entrySelected) {
			hsv[1] -= SELECT_DESATURATION;
			hsv[2] -= SELECT_DIM;
		}

		return Color.HSVToColor(hsv);
	}

	/**
	 * @param baseColor
	 * @param entrySelected
	 * @return
	 */
	public int getDarkColor(final int baseColor, final boolean entrySelected) {
		final float[] hsv = new float[3];

		Color.colorToHSV(baseColor, hsv);

		float saturation = DARK_GRAD_SATURATION;
		final float brightness = DARK_GRAD_BRIGHTNESS;
		if ((baseColor == Color.GRAY) || (baseColor == Color.WHITE)
				|| (baseColor == Color.BLACK)) {
			saturation = 0.0f;
		}

		hsv[1] = saturation;
		hsv[2] = brightness;

		if (!entrySelected) {
			hsv[1] -= SELECT_DESATURATION;
			hsv[2] -= SELECT_DIM;
		}

		return Color.HSVToColor(hsv);
	}

	/**
	 * Some Voodoo that prevents color banding on the background gradient.
	 * http://stuffthathappens.com/blog/2010/06/04/android-color-banding/
	 * 
	 * @param window
	 */
	public void removeGradientBanding(final Window window) {
		window.setFormat(PixelFormat.RGBA_8888);
	}

	/**
	 * @return The Report Id for Economic Overview
	 */
	public long getEconomicOverviewId() {
		return preferences.getLong(ECONOMIC_OVERVIEW_REPORT_ID, -1);
	}

	/**
	 * Set the status of new transactions in the staging database.
	 * 
	 * @param status
	 *            new status.
	 */
	public void setNewTransactionStatus(final boolean status) {
		final SharedPreferences.Editor editor = preferences.edit();

		final String containsUpdates = context
				.getString(R.string.setting_staging_contains_updates);
		editor.putBoolean(containsUpdates, status); // No updates when
													// initiated.
		editor.commit();
	}

	/**
	 * Returns the status of new transactions in the staging database.
	 * 
	 * @return True if new transactions exists.
	 */
	public boolean getNewTransactionsStatus() {
		return preferences.getBoolean(context
				.getString(R.string.setting_staging_contains_updates), false);
	}

	/**
	 * Returns the default category tag id
	 * 
	 * @return tag id
	 */
	public Tag getDefaultExpenseTag() {
		final long tagId = preferences.getLong(CONF_EXPENSES_DEF_TAG, 0L);

		return new Tag(tagId, defaultExpensesTagName, EntityType.EXPENSE);
	}

	/**
	 * Returns the default category tag id
	 * 
	 * @return tag id
	 */
	public Tag getDefaultIncomeTag() {
		final long tagId = preferences.getLong(CONF_EXPENSES_DEF_TAG, 0L);

		return new Tag(tagId, defaultIncomesTagName, EntityType.INCOME);
	}

	/**
	 * @return
	 */
	public Report getDefaultReport() {
		final long repId = preferences.getLong(ECONOMIC_OVERVIEW_REPORT_ID, 0L);

		return new Report(repId, reportName, reportDesc, reportFrom, reportTo);
	}

	/**
	 * @param tagId
	 * @param catId
	 * @param repId
	 */
	public void setDefaults(final Map<String, Long> tagIds, final long repId) {
		final SharedPreferences.Editor editor = preferences.edit();

		// Commit to preferences
		editor.putLong(CONF_EXPENSES_DEF_TAG, tagIds
				.get(defaultExpensesTagName));
		editor.putLong(CONF_INCOMES_DEF_TAG, tagIds.get(defaultIncomesTagName));
		editor.putLong(ECONOMIC_OVERVIEW_REPORT_ID, repId);
		editor.commit();
	}

	public String getConfigurationFile(final ConfigurationFileType type)
			throws IOException {
		final InputStream is = context.getAssets().open(type.getFileName());
		return convertStreamToString(is);
	}

	public String convertStreamToString(final InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			final Writer writer = new StringWriter();

			final char[] buffer = new char[1024];
			try {
				final Reader reader = new BufferedReader(new InputStreamReader(
						is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * 
	 * @param context
	 *            The application's environment.
	 * @param action
	 *            The Intent action to check for availability.
	 * 
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public boolean isIntentAvailable(final Context context, final String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		final List<ResolveInfo> list = packageManager
				.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * @param apiKey
	 */
	public void setApiKey(final String apiKey) {
		final SharedPreferences.Editor editor = preferences.edit();

		// Commit to preferences
		editor.putString(CONF_API_KEY, apiKey);
		editor.commit();
	}

	/**
	 * @return
	 */
	public String getApiKey() {
		return preferences.getString(CONF_API_KEY, "");
	}

	/**
	 * @return
	 */
	public boolean isPairedBankDroid() {
		return preferences.getBoolean(CONF_IS_PAIRED, false);
	}

	/**
	 * @param b
	 */
	public void setPairedBankDroid(final boolean paired) {
		final SharedPreferences.Editor editor = preferences.edit();

		// Commit to preferences
		editor.putBoolean(CONF_IS_PAIRED, paired);
		editor.commit();
	}

	public void notifyHomeScreen(final HomeScreenTask task) {
		final Intent intent = new Intent(notifyHomeScreenAction);
		intent.putExtra(homeScreenTaskKey, task.toString());

		context.sendOrderedBroadcast(intent, null);
	}

}
