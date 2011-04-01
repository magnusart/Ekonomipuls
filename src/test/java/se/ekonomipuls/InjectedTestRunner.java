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
package se.ekonomipuls;

import org.junit.runners.model.InitializationError;

import roboguice.inject.ContextScope;
import android.app.Application;

import com.google.inject.Injector;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
public class InjectedTestRunner extends RobolectricTestRunner {

	public InjectedTestRunner(final Class<?> testClass)
			throws InitializationError {
		super(testClass);
	}

	@Override
	protected Application createApplication() {
		final EkonomipulsApplication application = (EkonomipulsApplication) super
				.createApplication();
		return application;
	}

	@Override
	public void prepareTest(final Object test) {
		final EkonomipulsApplication application = (EkonomipulsApplication) Robolectric.application;

		// This project's application does not extend GuiceInjectableApplication
		// therefore we need to enter the ContextScope manually.
		final Injector injector = application.getInjector();
		final ContextScope scope = injector.getInstance(ContextScope.class);
		scope.enter(application);

		injector.injectMembers(test);
	}
}