package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.service.dashboard.MobileDashboardService;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class MobileDashboardServiceIT extends BaseServiceIT {

	@Autowired MobileDashboardService mobileDashboardService;
	@Autowired UserService userService;
	@Autowired ExtendedUserDetailsService extendedUserDetailsService;

	@Test
	public void executeSearch() throws Exception {

		User user = newContractorIndependentlane4Ready();

		DashboardStatusFilter filter = new DashboardStatusFilter()
				.setStatusCode(WorkStatusType.ALL);
		WorkSearchRequest workSearchRequest = new WorkSearchRequest()
				.setUserNumber(user.getUserNumber())
				.setStartRow(0)
				.setPageSize(0)
				.setShowAllAtCompany(true)
				.setStatusFilter(filter)
				.setIncludeLabelDrilldownFacet(false);
		WorkSearchResponse response = mobileDashboardService.executeSearch(user.getId(),workSearchRequest);
		assertNotNull(response);
	}

	@Test
	public void getMobileHomeCounts() throws Exception {
		User user = newContractorIndependentlane4Ready();
		DashboardResponseSidebar sidebar = mobileDashboardService.getMobileHomeCounts(user.getId(), user.getUserNumber());
		assertNotNull(sidebar);
	}

	@Test
	public void getAssignmentListByStatus_availableStatus() throws Exception {
		User user = newContractorIndependentlane4Ready();
		ExtendedUserDetails extendedUserDetails = (ExtendedUserDetails)extendedUserDetailsService.loadUserByUsername(user.getEmail());
		DashboardResponse response = mobileDashboardService.getAssignmentListByStatus(extendedUserDetails,new WorkStatusType(WorkStatusType.AVAILABLE), 1, 10, "");
		assertNotNull(response);
	}

	@Test
	public void getAssignmentListByStatus_paidStatus() throws Exception {
		User user = newContractorIndependentlane4Ready();
		ExtendedUserDetails extendedUserDetails = (ExtendedUserDetails)extendedUserDetailsService.loadUserByUsername(user.getEmail());
		DashboardResponse response = mobileDashboardService.getAssignmentListByStatus(extendedUserDetails,new WorkStatusType(WorkStatusType.PAID), 1, 10, "");
		assertNotNull(response);
	}

	@Test
	public void getAssignmentListByStatus_appliedStatus() throws Exception {
		User user = newContractorIndependentlane4Ready();
		ExtendedUserDetails extendedUserDetails = (ExtendedUserDetails)extendedUserDetailsService.loadUserByUsername(user.getEmail());
		DashboardResponse response = mobileDashboardService.getAssignmentListByStatus(extendedUserDetails, new WorkStatusType(WorkStatusType.APPLIED), 1, 10, "");
		assertNotNull(response);
	}
}
