package com.workmarket.domains.work.service;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkContextIT extends BaseServiceIT {

	@Autowired private LaneService laneService;
	@Autowired private WorkService workService;
	@Autowired RegistrationService registrationService;
	@Autowired WorkRoutingService workRoutingService;

	@Test
	@Ignore
	public void test_invitedResource() throws Exception {

		User employee = newEmployeeWithCashBalance();
		User contractor = newContractor();
		User contractor2 = newContractor();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		laneService.addUserToCompanyLane2(contractor2.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		Assert.assertNotNull(work);

		Assert.assertTrue(workService.getWorkContext(work.getId(), employee.getId()).contains(WorkContext.OWNER));
		Assert.assertTrue(workService.getWorkContext(work.getId(), contractor.getId()).contains(WorkContext.INVITED));
		Assert.assertTrue(workService.getWorkContext(work.getId(), ANONYMOUS_USER_ID).contains(WorkContext.UNRELATED));

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));

		workService.acceptWork(contractor.getId(), work.getId());

		Assert.assertTrue(workService.getWorkContext(work.getId(), employee.getId()).contains(WorkContext.OWNER));
		Assert.assertTrue(workService.getWorkContext(work.getId(), contractor.getId()).contains(WorkContext.ACTIVE_RESOURCE));
		Assert.assertTrue(workService.getWorkContext(work.getId(), contractor2.getId()).contains(WorkContext.INVITED_INACTIVE));

	}

	@Test
	public void test_ownerAndResource() throws Exception {

		User employee = newEmployeeWithCashBalance();
		laneService.addUserToCompanyLane1(employee.getId(), employee.getCompany().getId());

		User worker = newContractor();

		Work work = newWork(employee.getId());
		laneService.addUserToCompanyLane2(worker.getId(), employee.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());

		Assert.assertNotNull(work);

		Assert.assertTrue(workService.getWorkContext(work.getId(), employee.getId()).contains(WorkContext.OWNER));
		Assert.assertTrue(workService.getWorkContext(work.getId(), worker.getId()).contains(WorkContext.INVITED));
	}

	@Test
	@Ignore
	public void test_assignedToCompany() throws Exception {

		User employee = newEmployeeWithCashBalance();
		User contractor = newContractor();

		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword("" + RandomUtilities.nextLong());
		User contractor2 = registrationService.registerNewForCompany(userDTO, contractor.getCompany().getId());

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		Assert.assertNotNull(work);

		workService.acceptWork(contractor.getId(), work.getId());

		Assert.assertTrue(workService.getWorkContext(work.getId(), employee.getId()).contains(WorkContext.OWNER));
		Assert.assertTrue(workService.getWorkContext(work.getId(), contractor.getId()).contains(WorkContext.ACTIVE_RESOURCE));
		Assert.assertTrue(workService.getWorkContext(work.getId(), contractor2.getId()).contains(WorkContext.ASSIGNED_COMPANY));

	}

	@Test
	@Ignore
	public void test_cancelledResource() throws Exception {

		User employee = newEmployeeWithCashBalance();
		User contractor = newContractor();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		Assert.assertNotNull(work);

		workService.acceptWork(contractor.getId(), work.getId());
		workService.abandonWork(contractor.getId(), work.getId(), "Abandoned");

		Assert.assertTrue(workService.getWorkContext(work.getId(), employee.getId()).contains(WorkContext.OWNER));
		Assert.assertTrue(workService.getWorkContext(work.getId(), contractor.getId()).contains(WorkContext.CANCELLED_RESOURCE));

	}
}
