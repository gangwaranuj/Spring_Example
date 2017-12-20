package com.workmarket.service.business;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.acl.PermissionDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.authentication.features.Feature;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.features.FeatureEvaluatorConfiguration;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.exception.authentication.InvalidGoogleRecaptchResponseException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.FeatureToggleService;
import com.workmarket.test.IntegrationTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AuthenticationServiceIT extends BaseServiceIT {

	@Autowired private RegistrationService registrationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;
	@Autowired private LaneService laneService;
	@Autowired private PermissionDAO permissionDAO;
	@Autowired private FeatureToggleService featureToggleService;
	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private FeatureEvaluatorConfiguration featureEvaluatorConfiguration;
	@Autowired private UserRoleService userRoleService;

	private static String PASSWORD = "password";
	private static String WRONG_PASSWORD = "wrong_password";
	private static String DUMMY_IP = "127.0.0.1";
	private static String DUMMY_SESSION = "asced-t23rs-wehdsds";
	private static String DUMMY_RECAPTCHATOKEN = "recaptchaToken";
	private static String RECAPTCHA_FEATURE = "recaptcha";

	@Before
	public void setup() throws Exception {
		toggleRecaptchaFeatureToggle(true);
	}

	@Test
	public void test_authentication_with_recaptcha() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);

		for (int i=0; i<Constants.GOOGLE_RECAPTCHA_ENABLED_ON_FAILED_ATTEMPTS; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}

		assertNotNull(authenticationService.auth(user.getEmail(), PASSWORD, DUMMY_IP, DUMMY_SESSION, DUMMY_RECAPTCHATOKEN, false));
	}

	@Test
	public void test_authentication_without_recaptcha() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);

		toggleRecaptchaFeatureToggle(false);
		assertNotNull(authenticationService.auth(user.getEmail(), PASSWORD));
	}

	@Test
	public void test_authenticationFails() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);

		assertNull(authenticationService.auth(user.getEmail(), WRONG_PASSWORD));
	}

	@Test
	public void test_userAccountLockWithoutSuccessfulLogins() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);

		for (int i=0; i<Constants.MAX_FAILED_LOGIN_ATTEMPTS; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}

		assertTrue(authenticationService.isLocked(user));
	}

	@Test
	public void test_userAccountLockAfterSuccessfulLogins() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);
		assertNotNull(authenticationService.auth(user.getEmail(), PASSWORD));

		for (int i=0; i<Constants.MAX_FAILED_LOGIN_ATTEMPTS; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}

		assertTrue(authenticationService.isLocked(user));
	}

	@Test
	public void test_recaptchaEnabledOnUserWithFeatureToggleOn() throws Exception {
		toggleRecaptchaFeatureToggle(true);
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);
		assertNotNull(authenticationService.auth(user.getEmail(), PASSWORD));
		for (int i=0; i<Constants.GOOGLE_RECAPTCHA_ENABLED_ON_FAILED_ATTEMPTS; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}

		assertTrue(authenticationService.isRecaptchaEnabledOnUser(user));
	}

	@Test
	public void test_recaptchaDisableGloballyWhenFeatureToggleOffOrAbsent() throws Exception {
		toggleRecaptchaFeatureToggle(false);
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);
		assertNotNull(authenticationService.auth(user.getEmail(), PASSWORD));

		for (int i=0; i<Constants.GOOGLE_RECAPTCHA_ENABLED_ON_FAILED_ATTEMPTS; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}

		assertFalse(authenticationService.isRecaptchaEnabledOnUser(user));
	}

	@Test
	@ExpectedException(InvalidGoogleRecaptchResponseException.class)
	public void test_authenticationFailsOnEmptyRecaptchaResponse() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);
		assertNotNull(authenticationService.auth(user.getEmail(), PASSWORD));

		for (int i=0; i<1; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}

		authenticationService.auth(user.getEmail(), PASSWORD, DUMMY_IP, DUMMY_SESSION, "", false);
	}

	@Test
	public void test_unconfirmedUserPasswordSetAfterAccountIsLocked() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);

		for (int i=0; i<Constants.MAX_FAILED_LOGIN_ATTEMPTS; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}
		assertTrue(authenticationService.isLocked(user));

		userService.unlockUser(user.getId());
		user = userService.getUser(user.getId());
		assertFalse(authenticationService.isActive(user));
	}

	@Test
	public void test_confirmedUserPasswordSetAfterAccountIsLocked() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);
		Long userId = user.getId();
		registrationService.confirmAccount(userId);

		for (int i=0; i<Constants.MAX_FAILED_LOGIN_ATTEMPTS; i++) {
			authenticationService.auth(user.getEmail(), WRONG_PASSWORD);
		}
		assertTrue(authenticationService.isLocked(user));

		userService.unlockUser(userId);
		user = userService.getUser(userId);
		assertTrue(authenticationService.isActive(user));
	}

	@Test
	public void test_authenticationFailsForDeletedUser() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);

		userService.deleteUser(user.getId());

		assertNull(authenticationService.auth(user.getEmail(), PASSWORD));
	}

	@Test
	public void test_authenticationFailsForDeactivatedUser() throws Exception {
		UserDTO dto = newContractorDTO();
		dto.setPassword(PASSWORD);
		User user = registrationService.registerNew(dto, null);
		Long userId = user.getId();
		userService.deactivateUser(userId, userId, userId, userId);

		assertNotNull(authenticationService.auth(user.getEmail(), PASSWORD));
	}

	@Test
	public void test_findPermissionByCode() throws Exception {

		Permission addFunds = authenticationService.findPermissionByCode("addFunds");
		assertNotNull(addFunds);
	}

	@Test
	public void test_verifyUserAclRoles() throws Exception {
		authenticationService.removeAclRoleFromUser(ANONYMOUS_USER_ID, AclRole.ACL_USER);
		authenticationService.assignAclRolesToUser(ANONYMOUS_USER_ID, new Long[]{
				AclRole.ACL_ADMIN, AclRole.ACL_MANAGER,
		});

		assertTrue(authenticationService.userHasAclRole(ANONYMOUS_USER_ID, AclRole.ACL_ADMIN));
		assertTrue(authenticationService.userHasAclRole(ANONYMOUS_USER_ID, AclRole.ACL_MANAGER));
		assertFalse(authenticationService.userHasAclRole(ANONYMOUS_USER_ID, AclRole.ACL_USER));

		assertTrue(authenticationService.userHasAclRoles(
				ANONYMOUS_USER_ID,
				new Long[]{AclRole.ACL_ADMIN, AclRole.ACL_MANAGER, AclRole.ACL_USER},
				false
		));

		assertFalse(authenticationService.userHasAclRoles(
				ANONYMOUS_USER_ID,
				new Long[]{AclRole.ACL_ADMIN, AclRole.ACL_MANAGER, AclRole.ACL_USER},
				true
		));
	}

	@Test
	public void test_authorizeUserForWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		User user = newContractor();

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2009-06-02T09:00:00Z");
		workDTO.setIndustryId(Constants.WM_TIME_INDUSTRY_ID);

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);
		assertNotNull(work);
		authenticationService.setCurrentUser(user);
		assertFalse(authenticationService.authorizeUserForWork(work.getId()));

		laneService.addUserToCompanyLane2(user.getId(), employee.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), user.getId());

		assertTrue(authenticationService.authorizeUserForWork(work.getId()));
	}

	@Test
	public void test_currentUser() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		authenticationService.setCurrentUser(employee);

		assertEquals(employee.getId(), authenticationService.getCurrentUser().getId());
	}

	@Test
	public void test_masqueradeUser() throws Exception {
		User internal = newInternalUser();

		User contractor = newContractorIndependentLane4ReadyWithCashBalance();

		authenticationService.setCurrentUser(internal);

		authenticationService.startMasquerade(internal.getId(), contractor.getId());

		assertTrue(authenticationService.isMasquerading());

		assertEquals(internal.getId(), authenticationService.getMasqueradeUser().getId());

		assertEquals(contractor.getId(), authenticationService.getCurrentUser().getId());
	}

	@Test
	public void test_findAllUsersByPermissionAndCompany() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Set<User> userList = authenticationService.findAllUsersByPermissionAndCompany(employee.getCompany().getId(), Permission.ADD_FUNDS);

		assertTrue(userList.size() == 1);

	}

	@Test
	public void test_findAllDispatchersByCompany() throws Exception {
		User me = newFirstEmployee();
		newCompanyEmployeeDispatcherConfirmed(me.getCompany().getId());
		Set<User> userList = authenticationService.findAllUsersByPermissionAndCompany(me.getCompany().getId(), Permission.ACCEPT_WORK_AS_DISPATCHER);

		assertTrue(userList.size() == 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_findAllUsersByACLRoleAndCompany() throws Exception {
		User employee = newEmployeeWithCashBalance();
		List<User> userList = authenticationService.findAllUsersByACLRoleAndCompany(employee.getCompany().getId(), AclRole.ACL_ADMIN);

		assertTrue(userList.size() == 1);

		authenticationService.removeAclRoleFromUser(employee.getId(), AclRole.ACL_ADMIN);
	}

	@Test
	public void test_findAllAssignedPermissionsByUser() throws Exception {
		authenticationService.removeAclRoleFromUser(ANONYMOUS_USER_ID, AclRole.ACL_WORKER);
		assertTrue(authenticationService.findAllAssignedPermissionsByUser(ANONYMOUS_USER_ID).size() > 2);
		Permission permission = permissionDAO.findPermissionByUserAndPermissionCode(ANONYMOUS_USER_ID, Permission.ACCEPT_WORK_FROM_MYCOMPANY);
		assertNull(permission);

		permission = permissionDAO.findPermissionByUserAndPermissionCode(ANONYMOUS_USER_ID, Permission.APPROVE_ASSIGNMENTS);
		assertTrue(permission.getCode().equals(Permission.APPROVE_ASSIGNMENTS));
	}

	@Test
	public void test_customAccessSettings() throws Exception {
		User employee = newWMEmployee();

		boolean flag = userRoleService.hasPermissionsForCustomAuth(employee.getId(), Permission.EDIT_PRICING_AUTH);
		assertTrue(flag);

		authenticationService.setCustomAccess(false, false, false, false, false, false, employee.getId());
		flag = userRoleService.hasPermissionsForCustomAuth(employee.getId(), Permission.COUNTEROFFER_AUTH);

		assertFalse(flag);

		authenticationService.setCustomAccess(false, false, true, false, false, false, employee.getId());
		flag = userRoleService.hasPermissionsForCustomAuth(employee.getId(), Permission.COUNTEROFFER_AUTH);

		assertTrue(flag);
	}

	@Test
	public void test_paymentCenterAndEmailsAccess() throws Exception {
		User employee = newWMEmployee();

		boolean flag = authenticationService.hasPaymentCenterAndEmailsAccess(employee.getId(), Boolean.TRUE);
		assertTrue("should have paymentcenter and emails access", flag);

		authenticationService.setCustomAccess(false, false, true, false, false, false, employee.getId());

		flag = authenticationService.hasPaymentCenterAndEmailsAccess(employee.getId(), Boolean.FALSE);
		assertFalse(flag);
	}

	@Test
	public void test_manageBankAndFundsAccess() throws Exception {
		User employee = newWMEmployee();

		boolean flag = authenticationService.hasManageBankAndFundsAccess(employee.getId(), Boolean.TRUE);
		assertTrue("should have manage bank and funds access", flag);

		authenticationService.setCustomAccess(false, false, true, false, false, false, employee.getId());
		flag = authenticationService.hasManageBankAndFundsAccess(employee.getId(), Boolean.FALSE);
		assertFalse(flag);
	}

	private void toggleRecaptchaFeatureToggle(boolean enable) throws Exception {
		featureEvaluatorConfiguration.reload();
		Feature feature = featureEvaluatorConfiguration.get(RECAPTCHA_FEATURE);

		if (feature == null) {
			featureToggleService.addFeature(RECAPTCHA_FEATURE, enable);
			return;
		}

		featureToggleService.updateFeature(RECAPTCHA_FEATURE, enable);
		featureEvaluatorConfiguration.reload();
	}
}
