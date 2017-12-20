package com.workmarket.domains.reports.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.reports.dao.ReportDAO;
import com.workmarket.domains.reports.dao.WorkReportDAO;
import com.workmarket.domains.reports.dao.WorkReportDecoratorDAO;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkReportServiceImplTest {

	@Mock AuthenticationService authenticationService;
	@Mock UserService userService;
	@Mock ReportDAO reportDAO;
	@Mock WorkReportDAO workReportDAO;
	@Mock WorkCustomFieldDAO workCustomFieldDAO;
	@Mock WorkReportDecoratorDAO workReportDecoratorDAO;
	@InjectMocks WorkReportServiceImpl workReportService;

	CustomFieldReportFilters customFieldReportFilters;
	User user;
	Company company;

	@Before
	public void setUp() throws Exception {
		company = mock(Company.class);
		user = mock(User.class);
		when(user.getCompany()).thenReturn(company);
		when(userService.getUser(anyLong())).thenReturn(user);

		customFieldReportFilters = new CustomFieldReportFilters();
		List<Long> workIds = Lists.newArrayList();
		for (long i = 0; i <= 1000; i++) {
			workIds.add(i);
		}
		customFieldReportFilters.setWorkIds(workIds);
	}

	@Test
	public void findAllWorkCustomFields_withHundredsOfWork_success() throws Exception {
		workReportService.findAllWorkCustomFields(1L, customFieldReportFilters);
		verify(workCustomFieldDAO, atLeast(5)).findAllWorkCustomFields(anyLong(), anyLong(), any(CustomFieldReportFilters.class));
	}

	@Test
	public void findAllWorkCustomFields_withLessThan200workIds_success() throws Exception {
		List<Long> workIds = Lists.newArrayList();
		for (long i = 0; i <= 20; i++) {
			workIds.add(i);
		}
		customFieldReportFilters.setWorkIds(workIds);
		workReportService.findAllWorkCustomFields(1L, customFieldReportFilters);
		verify(workCustomFieldDAO, times(1)).findAllWorkCustomFields(anyLong(), anyLong(), any(CustomFieldReportFilters.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void partitionAndFindAllWorkCustomFields_withNullArguments_fails() throws Exception {
		workReportService.findAllWorkCustomFieldsByWorkId(0, 0, null);
	}

	@Test
	public void getWorkCustomFieldsMapForBuyer_assertCorrectnessOfPayloadShape() {
		final Map<String, List<Long>> params = Maps.newHashMap();
		final Map<String, Object> workCustomFieldsMap = workReportService.getWorkCustomFieldsMapForBuyer(params);
		Assert.assertTrue(workCustomFieldsMap.containsKey("workIdToCustomFields"));
		Assert.assertTrue(workCustomFieldsMap.containsKey("customFields"));

		final Map<Long, List<Map<String, Object>>> workIdToCustomFields = (Map<Long, List<Map<String, Object>>>)workCustomFieldsMap.get("workIdToCustomFields");
		final List<Map<String, Object>> workCustomFields = ((List<Map<String, Object>>)workCustomFieldsMap.get("customFields"));

		Assert.assertEquals(0, workIdToCustomFields.size());
		Assert.assertEquals(0, workCustomFields.size());
	}
}