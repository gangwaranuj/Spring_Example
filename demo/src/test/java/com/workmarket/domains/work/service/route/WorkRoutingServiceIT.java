package com.workmarket.domains.work.service.route;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkRoutingServiceIT extends BaseServiceIT {

	@Test
	public void addToWorkResources_withoutInstantWorkerPool() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		User contractorNotAllowed = newContractor();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithPaymentTerms(employee.getId(), 30);

		WorkRoutingResponseSummary responseSummary = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber(), contractorNotAllowed.getUserNumber()));
		Assert.notNull(responseSummary);
		Assert.isTrue(responseSummary.getResponse().containsKey(WorkAuthorizationResponse.DISABLED_WORKER_POOL));
	}


	@Test
	public void addToWorkResources_withoutMoney() throws Exception {
		User employee = newFirstEmployeeWithAPLimit();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		User contractorNotAllowed = newContractor();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithPaymentTerms(employee.getId(), 0);

		WorkRoutingResponseSummary responseSummary = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber(), contractorNotAllowed.getUserNumber()));
		Assert.notNull(responseSummary);
		Assert.isTrue(responseSummary.getResponse().containsKey(WorkAuthorizationResponse.INSUFFICIENT_FUNDS));
		Assert.isTrue(responseSummary.getResponse().get(WorkAuthorizationResponse.INSUFFICIENT_FUNDS).size()== 2);
	}


	@Test
	public void addToWorkResources_withTwoUsers_success() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		User contractor2 = newContractor();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithPaymentTerms(employee.getId(), 30);

		WorkRoutingResponseSummary responseSummary = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber(), contractor2.getUserNumber()));
		Assert.notNull(responseSummary);
		Assert.isTrue(responseSummary.getResponse().containsKey(WorkAuthorizationResponse.SUCCEEDED));
		Assert.isTrue(responseSummary.getResponse().size()== 2);
	}

}
