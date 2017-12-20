package com.workmarket.web.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: micah
 * Date: 9/10/13
 * Time: 2:41 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class MenuHelperTest {
	@InjectMocks MenuHelper menuHelper;

	private static Map<String, Object> navFixtures = null;

	ModelMap model;
	Boolean hasMasqueradeRole;

	static {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			navFixtures = objectMapper.readValue(MenuHelperTest.class.getClassLoader().getResourceAsStream("altered-menu-definitions.json"), Map.class);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Before
	public void setup() {
		model = mock(ModelMap.class);
		hasMasqueradeRole = Boolean.TRUE;

		when(model.get("is_admin")).thenReturn(Boolean.TRUE);
		when(model.get("is_owner")).thenReturn(Boolean.TRUE);
		when(model.get("is_resource")).thenReturn(Boolean.TRUE);
		when(model.get("is_active_resource")).thenReturn(Boolean.TRUE);
		when(model.get("is_internal")).thenReturn(Boolean.TRUE);
		when(model.get("isCompanyResource")).thenReturn(Boolean.TRUE);

		Work work = mock(Work.class);
		when(model.get("work")).thenReturn(work);

		WorkResponse workResponse = mock(WorkResponse.class);
		when(workResponse.isInWorkBundle()).thenReturn(Boolean.FALSE);
		when(model.get("workResponse")).thenReturn(workResponse);

		PricingStrategy pricingStrategy = mock(PricingStrategy.class);
		when(pricingStrategy.getType()).thenReturn(PricingStrategyType.INTERNAL);

		when(work.getPricing()).thenReturn(pricingStrategy);
	}

	@Test
	public void populateNavigationList_HappyPath() {
		List<Object> result = menuHelper.populateNavigationList(model, WorkStatusType.SENT, Boolean.FALSE);
		assertEquals(menuHelper.getMenu("companyResource.ownerAndResourceOrActiveResource.sent"), result);
	}

	@Test
	public void removeItem_TopLevel_StaticMapDefinitionsNotModified() {
		List<Object> originalResult = menuHelper.getMenu("companyResource.ownerAndResourceOrActiveResource.active", navFixtures);

		menuHelper.removeItem(
			"add_resources",
			menuHelper.populateNavigationList(model, WorkStatusType.ACTIVE, Boolean.FALSE));

		assertEquals(menuHelper.getMenu("companyResource.ownerAndResourceOrActiveResource.active", navFixtures), originalResult);
	}

	@Test
	public void removeItem_SubList_StaticMapDefinitionsNotModified() {
		List<Object> originalResult = menuHelper.getMenu("companyResource.ownerAndResourceOrActiveResource.sent", navFixtures);

		menuHelper.removeItem(
			"add_resources",
			menuHelper.populateNavigationList(model, WorkStatusType.SENT, Boolean.FALSE));

		assertEquals(menuHelper.getMenu("companyResource.ownerAndResourceOrActiveResource.sent", navFixtures), originalResult);
	}

}
