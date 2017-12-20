package com.workmarket.redis;

import com.google.common.base.Optional;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.redis.repositories.WorkSearchRequestRepository;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardClient;
import com.workmarket.search.response.work.DashboardInternalOwner;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkSearchRequestRepositoryIT extends BaseServiceIT {

	@Autowired WorkSearchRequestRepository workSearchRequestRepository;

	private WorkSearchRequest createWorkSearchRequest(User user) {

		WorkSearchRequest request = new WorkSearchRequest();

		DateRange dateRange = new DateRange();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE,-60);
		dateRange.setFrom(cal);
		dateRange.setThrough(Calendar.getInstance());

		request.setDateRange(dateRange)
			.setClients(new HashSet<DashboardClient>())
			.setInternalOwners(new HashSet<DashboardInternalOwner>())
			.setAssignedResources(new HashSet<DashboardResource>())
			.setUserNumber(user.getUserNumber())
			.setStatusFilter(new DashboardStatusFilter())
			.setIncludeCounts(false)
			.setSubStatusFilter(new DashboardStatusFilter())
			.setKeyword("");

		return request;
	}

	@Test
	public void testSetandGet() throws Exception {
		User companyUser = newWMEmployee();

		WorkSearchRequest request = createWorkSearchRequest(companyUser);
		workSearchRequestRepository.set(companyUser.getId(), request);

		Optional<WorkSearchRequest> optRequest = workSearchRequestRepository.get(companyUser.getId());
		Assert.assertTrue(optRequest.isPresent());
		WorkSearchRequest testRequest = optRequest.get();
		Assert.assertTrue(testRequest.getUserNumber().equals(request.getUserNumber()));

	}

	@Test(expected = Exception.class)
	public void testSetandGet_NullWorkSearchRequest() throws Exception {
		User companyUser = newWMEmployee();
		workSearchRequestRepository.set(companyUser.getId(), null);
		Optional<WorkSearchRequest> testRequest = workSearchRequestRepository.get(companyUser.getId());
		Assert.assertFalse(testRequest.isPresent());
	}
}
