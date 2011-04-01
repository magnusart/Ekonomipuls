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
package se.ekonomipuls.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.ekonomipuls.InjectedTestRunner;
import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AnalyticsFilterRulesDbFacade;
import se.ekonomipuls.model.EkonomipulsUtil;
import se.ekonomipuls.model.ModelResources;

import com.google.inject.Inject;

/**
 * @author Magnus Andersson
 * @author Michael Svensson
 * @since 1 apr 2011
 */
@RunWith(InjectedTestRunner.class)
public class FilterServiceTest {

	@Inject
	ModelResources resources;

	@Inject
	@InjectMocks
	private FilterRuleService service;

	@Mock
	private EkonomipulsUtil util;

	@Mock
	AnalyticsFilterRulesDbFacade filterRulesDbFacade;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void applyFiltersToTransactions() {

		when(util.getDefaultTag()).thenReturn(resources.getDefaultTag());

		when(filterRulesDbFacade.getFilterRules())
				.thenReturn(resources.getFilterRules());

		final List<ApplyFilterTagAction> actions = service
				.applyFilters(resources.getUnfilteredTransactions());

		assertEquals(resources.getFilteredTransactions(), actions);

	}
}
