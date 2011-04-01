package se.ekonomipuls;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;
import se.ekonomipuls.database.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.database.abstr.AbstractDbHelper;
import se.ekonomipuls.database.analytics.AnalyticsCategoriesDbImpl;
import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbImpl;
import se.ekonomipuls.database.staging.StagingDbImpl;
import se.ekonomipuls.proxy.BankDroidProxy;

/**
 * Serves the purpose of telling Guice how to satisfy dependencies
 * 
 * @author Michael Svensson
 */
public class EkonomipulsModule extends AbstractAndroidModule {

	@Override
	protected void configure() {
		// custom bindings
		requestStaticInjection(AbstractDbHelper.class);
		requestStaticInjection(BankDroidProxy.class);

		bind(AnalyticsTransactionsDbFacade.class)
				.to(AnalyticsTransactionsDbImpl.class);

		bind(StagingDbFacade.class).to(StagingDbImpl.class);

		bind(AnalyticsCategoriesDbFacade.class)
				.to(AnalyticsCategoriesDbImpl.class);

		bindConstant().annotatedWith(SharedPreferencesName.class)
				.to("se.ekonomipuls.ekonomipuls_preferences");
	}
}