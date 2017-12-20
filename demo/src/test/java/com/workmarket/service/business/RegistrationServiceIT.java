package com.workmarket.service.business;

import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.network.Network;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.workmarket.utility.RandomUtilities.generateAlphaString;
import static com.workmarket.utility.RandomUtilities.nextLong;
import static org.junit.Assert.*;

/**
 * Created by nick on 9/13/13 3:56 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RegistrationServiceIT extends BaseServiceIT {

	@Test
	public void registerNew_BuyerRegistration_SellerDisabled() throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());
		User user = registrationService.registerNew(userDTO, null, "testCompany" + generateAlphaString(10), null, null, true);
		assertNotNull(user);

		Profile profile = profileService.findProfile(user.getId());
		assertNotNull(profile);
		assertTrue(profile.getManageWork());
		assertFalse(profile.getFindWork());
	}

	@Test
	public void registerNew_SellerRegistration_BuyerDisabled() throws Exception {
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());
		User user = registrationService.registerNew(userDTO, null, "testCompany" + generateAlphaString(10), null, null, false);
		assertNotNull(user);

		Profile profile = profileService.findProfile(user.getId());
		assertNotNull(profile);
		assertFalse(profile.getManageWork());
		assertTrue(profile.getFindWork());
	}

	@Test
	public void registerNewEmployee_StaffWithNoWorkerRoles_SellerDisabled() throws Exception {
		User firstEmployee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());

		User user = registrationService.registerNewForCompany(
				userDTO, firstEmployee.getCompany().getId(), new Long[] {ACL_ROLE_USER}
		);
		assertNotNull(user);

		Profile profile = profileService.findProfile(user.getId());
		assertNotNull(profile);
		assertTrue(profile.getManageWork());
		assertFalse(profile.getFindWork());
	}

	@Test
	public void registerNewEmployee_StaffWithWorkerRole_SellerEnabled() throws Exception {
		User firstEmployee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());

		User user = registrationService.registerNewForCompany(
				userDTO, firstEmployee.getCompany().getId(), new Long[] {ACL_ROLE_USER, ACL_ROLE_WORKER}
		);
		assertNotNull(user);

		Profile profile = profileService.findProfile(user.getId());
		assertNotNull(profile);
		assertTrue(profile.getManageWork());
		assertTrue(profile.getFindWork());
	}

	@Test
	public void registerNewEmployee_StaffWithSharedWorkerRole_SellerEnabled() throws Exception {
		User firstEmployee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());

		User user = registrationService.registerNewForCompany(
				userDTO, firstEmployee.getCompany().getId(), new Long[] {ACL_ROLE_USER, ACL_ROLE_SHARED_WORKER}
		);
		assertNotNull(user);

		Profile profile = profileService.findProfile(user.getId());
		assertNotNull(profile);
		assertTrue(profile.getManageWork());
		assertTrue(profile.getFindWork());
	}

	@Test
	public void confirmAndApproveAccount_UnconfirmedUser_Success() throws Exception {
		User user = newUnconfirmedContractor();

		assertFalse(user.isEmailConfirmed());

		user = registrationService.confirmAndApproveAccount(user.getId());

		assertTrue(user.isEmailConfirmed());
		assertTrue(authenticationService.isApproved(user));
	}

	@Test
	public void confirmAndApproveAccount_SuspendedUser_NoChange() throws Exception {
		User user = newContractor();
		userService.suspendUser(user.getId(), true);

		user = userService.getUser(user.getId());
		assertTrue(user.isEmailConfirmed());

		User confirmedUser = registrationService.confirmAndApproveAccount(user.getId());

		assertNull(confirmedUser);

		user = userService.getUser(user.getId());
		assertTrue(authenticationService.isSuspended(user));
	}

	@Test
	public void confirmAndApproveAccount_ActiveUser_NoChange() throws Exception {
		User user = newContractor();

		assertTrue(authenticationService.isActive(user));

		user = registrationService.confirmAndApproveAccount(user.getId());

		assertTrue(authenticationService.isActive(user));
	}

	@Test
	public void registerNew_BuyerRegistration_isInNetwork() throws Exception {
		Network newNetwork = createNetwork();
		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());
		userDTO.setNetworkId(String.valueOf(newNetwork.getId()));

		User user = registrationService.registerNew(userDTO, null, "testCompany" + generateAlphaString(10), null, null, true);
		assertNotNull(user);
		assertFalse(networkService.isCompanyInNetwork(user.getCompany().getId(), Long.parseLong(userDTO.getNetworkId())));
	}

	@Test
	public void createNewWorkerFromMinimalFieldSet() throws Exception {
		final String userName = "contractorUser" + nextLong();
		final CreateNewWorkerRequest dto = CreateNewWorkerRequest.builder()
			.setEmail(userName + "@workmarket.com")
			.setPassword("" + nextLong())
			.build();

		final User user = registrationService.registerNew(dto);

		assertNotNull(user);
		assertEquals(userName + "@workmarket.com", user.getEmail());
	}

	@Test
	public void createNewWorkerWithSecondayEmail() throws Exception {
		final String userName = "WorkerUser" + nextLong();
		final CreateNewWorkerRequest dto = CreateNewWorkerRequest.builder()
			.setSecondaryEmail(userName + "+secondary@workmarket.com")
			.setEmail(userName + "@workmarket.com")
			.setPassword("" + nextLong())
			.build();

		final User user = registrationService.registerNew(dto);

		assertNotNull(user);
		assertEquals(userName + "@workmarket.com", user.getEmail());
		assertEquals(userName + "+secondary@workmarket.com", user.getSecondaryEmail());
	}
}
