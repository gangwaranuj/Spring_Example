package com.workmarket.service.infra.audit;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.service.business.*;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AuditInterceptorIT extends BaseServiceIT {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;
	@Autowired private ContractService contractService;
	@Autowired private WorkService workService;
	@Autowired private ProfileService profileService;

	@Before
	public void before() {}

	@After
	public void after() {}

	@Test
	@Transactional
	public void test_findWork() throws Exception {
		workService.findWork(1L);
	}

	@Test
	public void test_onFlushDirty() throws Exception {
		User currentUser = userService.findUserById(1L);
		authenticationService.setCurrentUser(currentUser);

		Contract contract = contractService.findContractById(9L);

		Long originalCreatorId = contract.getCreatorId();
		Calendar originalCreatedOn = contract.getCreatedOn();

		Calendar modifiedOn = contract.getModifiedOn();
		contract.setName("Contract name " + RandomUtilities.nextLong());
		contractService.saveOrUpdateContract(contract);
		Assert.assertTrue(contract.getModifiedOn().getTimeInMillis() > modifiedOn.getTimeInMillis());
		Assert.assertTrue(contract.getModifierId() == 1L);
		Assert.assertTrue(contract.getCreatedOn().getTimeInMillis() == originalCreatedOn.getTimeInMillis());
		Assert.assertEquals(contract.getCreatorId(), originalCreatorId);

		currentUser = userService.findUserById(2L);
		authenticationService.setCurrentUser(currentUser);
		contract = contractService.findContractById(9L);
		modifiedOn = contract.getModifiedOn();
		contract.setName("Contract name " + RandomUtilities.nextLong());
		contractService.saveOrUpdateContract(contract);
		Assert.assertTrue(contract.getModifiedOn().getTimeInMillis() > modifiedOn.getTimeInMillis());
		Assert.assertTrue(contract.getModifierId() == 2L);
		Assert.assertTrue(contract.getCreatedOn().getTimeInMillis() == originalCreatedOn.getTimeInMillis());
		Assert.assertEquals(contract.getCreatorId(), originalCreatorId);
	}

	@Test
	public void test_onSave() throws Exception {
		User currentUser = userService.findUserById(1L);
		authenticationService.setCurrentUser(currentUser);

		Contract contract = new Contract();
		contract.setCompany(profileService.findCompany(1L));
		contract.setName("Contract name " + RandomUtilities.nextLong());
		contractService.saveOrUpdateContract(contract);

		Assert.assertNotNull(contract.getCreatedOn());
		Assert.assertNotNull(contract.getModifiedOn());
		Assert.assertNotNull(contract.getCreatorId() == 1L);
		Assert.assertNotNull(contract.getModifierId() == 1L);
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public UserService getUserService() {
		return userService;
	}

	public ContractService getContractService() {
		return contractService;
	}

	public WorkService getWorkService() {
		return workService;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public void setWorkService(WorkService workService) {
		this.workService = workService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}
}
