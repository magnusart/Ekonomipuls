package se.ekonomipuls;

import roboguice.config.AbstractAndroidModule;
import se.ekonomipuls.database.AnalyticsCategoriesDbFacade;
import se.ekonomipuls.database.AnalyticsFilterRulesDbFacade;
import se.ekonomipuls.database.AnalyticsTagsDbFacade;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.StagingDbFacade;
import se.ekonomipuls.database.abstr.AbstractDbHelper;
import se.ekonomipuls.database.analytics.AnalyticsCategoriesDbImpl;
import se.ekonomipuls.database.analytics.AnalyticsFilterRulesDbImpl;
import se.ekonomipuls.database.analytics.AnalyticsTagsDbImpl;
import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbImpl;
import se.ekonomipuls.database.staging.StagingDbImpl;
import se.ekonomipuls.proxy.bankdroid.BankDroidProxy;
import se.ekonomipuls.proxy.configuration.ConfiguratorProxy;
import se.ekonomipuls.proxy.configuration.FileConfiguratorProxy;

/**
 * Serves the purpose of telling Guice how to satisfy dependencies
 * 
 * @author Magnus Andersson
 * @author Michael Svensson
 */
public class EkonomipulsModule extends AbstractAndroidModule {

	@Override
	protected void configure() {
		// custom bindings
		requestStaticInjection(AbstractDbHelper.class);
		requestStaticInjection(BankDroidProxy.class);

		bind(AnalyticsTransactionsDbFacade.class).to(
				AnalyticsTransactionsDbImpl.class);

		bind(StagingDbFacade.class).to(StagingDbImpl.class);

		bind(AnalyticsCategoriesDbFacade.class).to(
				AnalyticsCategoriesDbImpl.class);

		bind(AnalyticsFilterRulesDbFacade.class).to(
				AnalyticsFilterRulesDbImpl.class);

		bind(AnalyticsTagsDbFacade.class).to(AnalyticsTagsDbImpl.class);

		bind(ConfiguratorProxy.class).to(FileConfiguratorProxy.class);

	}
}