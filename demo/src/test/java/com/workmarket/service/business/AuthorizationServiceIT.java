package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AuthorizationServiceIT extends BaseServiceIT {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private AuthorizationService authorizationService;

	@Test
	public void getContext_OWNER() throws Exception {
		User employee = newEmployeeWithCashBalance();
		authenticationService.setCurrentUser(employee);
		Assert.assertEquals(RequestContext.OWNER, authorizationService.getRequestContext(employee.getId()));
	}

	@Test
	public void getContext_PUBLIC() throws Exception {
		User employee = newEmployeeWithCashBalance();
		User contractor = newContractorIndependentlane4Ready();
		authenticationService.setCurrentUser(employee);
		Assert.assertEquals(RequestContext.OWNER, authorizationService.getRequestContext(employee.getId()));
		Assert.assertEquals(RequestContext.PUBLIC, authorizationService.getRequestContext(contractor.getId()));
	}

	@Test
	public void getContext_ADMIN() throws Exception {
		User employeeAdmin = newEmployeeWithCashBalance();
		User employee = newCompanyEmployee(employeeAdmin.getCompany().getId());
		authenticationService.setCurrentUser(employeeAdmin);
		authenticationService.assignAclRolesToUser(employeeAdmin.getId(), new Long[]{AclRole.ACL_ADMIN});
		Assert.assertEquals(RequestContext.ADMIN, authorizationService.getRequestContext(employee.getId()));
	}
}

