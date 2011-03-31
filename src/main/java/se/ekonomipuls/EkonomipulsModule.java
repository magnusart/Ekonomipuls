package se.ekonomipuls;

import roboguice.config.AbstractAndroidModule;
import se.ekonomipuls.database.AbstractDbHelper;
import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.database.analytics.AnalyticsTransactionsDbFacadeImpl;
import se.ekonomipuls.database.staging.StagingDbFacade;
import se.ekonomipuls.database.staging.StagingDbFacadeImpl;
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
				.to(AnalyticsTransactionsDbFacadeImpl.class);
		bind(StagingDbFacade.class).to(StagingDbFacadeImpl.class);
	}
}