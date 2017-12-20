package com.workmarket.service.business;

import com.codahale.metrics.Meter;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.dao.BlacklistedEmailDAO;
import com.workmarket.dao.InvitationDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.changelog.user.UserChangeLogDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.company.CompanySignUpInfoDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.dao.random.UserRandomIdentifierDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.business.AuthTrialCommon;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.service.web.WebRequestContextProvider;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.workmarket.utility.RandomUtilities.generateAlphaString;
import static com.workmarket.utility.RandomUtilities.nextLong;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistationServiceTest {

	@Mock NotificationService notificationService;
	@Mock UserNotificationService userNotificationService;
	@Mock UserService userService;
	@Mock UserRandomIdentifierDAO randomNumberGenerator;
	@Mock EventRouter eventRouter;
	@Mock CompanyService companyService;
	@Mock ProfileService profileService;
	@Mock DateTimeService dateTimeService;
	@Mock ProfileDAO profileDAO;
	@Mock AuthenticationService authenticationService;
	@Mock SummaryService summaryService;
	@Mock UserDAO userDAO;
	@Mock InvitationDAO invitationDAO;
	@Mock BlacklistedEmailDAO blacklistedEmailDAO;
	@Mock EmailTemplateFactory emailTemplateFactory;
	@Mock CompanySignUpInfoDAO companySignUpInfoDAO;
	@Mock UserChangeLogDAO userChangeLogDAO;
	@Mock UserIndexer userIndexer;
	@Mock AdmissionService admissionService;
	@Mock PlanService planService;
	@Mock CompanyDAO companyDAO;
	@Mock IndustryService industryService;
	@Mock InvariantDataService invariantDataService;
	@Mock UserRoleService userRoleService;
	@Mock AuthTrialCommon authTrialCommon;
	@Mock RequestService requestService;
	@Mock WebRequestContextProvider webRequestContextProvider;
	@InjectMocks RegistrationServiceImpl registrationService;

	@Mock Meter registerMeter;

	@Mock WMMetricRegistryFacade wmMetricRegistryFacade;

	@Mock Company company;
	@Mock User user;

	@Before
	public void setUp() throws Exception {

		when(user.getId()).thenReturn(1L);
		when(userService.findUserByEmail(anyString())).thenReturn(null);
		when(randomNumberGenerator.generateUniqueNumber()).thenReturn("1");
		when(companyService.createCompany(anyString(), anyBoolean(), anyString())).thenReturn(company);
		when(userDAO.get(anyLong())).thenReturn(user);
		when(companyDAO.get(anyLong())).thenReturn(company);
		when(wmMetricRegistryFacade.meter(anyString())).thenReturn(registerMeter);
		when(authenticationService.authorizeUserByAclPermission(anyLong(), anyString())).thenReturn(false);
	}

	/**
	 * Test that notificationService.sendNotification is called
	 *
	 * @throws Exception
	 */
	@Test
	public void registerNew_emailSent() throws Exception {

		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());
		User user = registrationService.registerNew(userDTO, null, "testCompany" + generateAlphaString(10), null, null, true);

		verify(notificationService, times(1)).sendNotification(any(EmailTemplate.class), any(Calendar.class));
	}

	/**
	 * Test that notificationService.sendNotification is not called
	 *
	 * @throws Exception
	 */
	@Test
	public void registerNew_emailNotSent() throws Exception {

		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName("firstname" + generateAlphaString(10));
		userDTO.setLastName("lastname" + generateAlphaString(10));
		userDTO.setPassword("" + nextLong());
		User user = registrationService.registerNew(userDTO, null, "testCompany" + generateAlphaString(10), null, null, true, false);

		verify(notificationService, never()).sendNotification(any(EmailTemplate.class), any(Calendar.class));
	}

	@Test
	public void testUpdateUserStatusToDeleted() {
		final String email = "test@email.com";
		final User user = new User();
		user.setEmail(email);
		user.setUserStatusType(new UserStatusType(UserStatusType.APPROVED));
		when(userDAO.findUser(anyString())).thenReturn(user);

		registrationService.updateUserStatusToDeleted(email);

		verify(userDAO, times(1)).saveOrUpdate(any(User.class));
		verify(authenticationService, times(1))
				.deleteUser(anyString(), eq(email), any(RequestContext.class));
		assertThat(user.getUserStatusType(), equalTo(new UserStatusType(UserStatusType.DELETED)));
	}

	@Test
	public void testRegisterNewForCompany_ForExistingUser() throws Exception {
		final String email = "test@email.com";
		final User existingUser = new User();
		existingUser.setEmail(email);
		when(userDAO.findUser(anyString())).thenReturn(existingUser);
		try {
			registrationService.registerNewForCompany(new UserDTO(), 1L, null, false);
		} catch (Exception e ) {
			assertThat(e.getMessage(), equalTo("E-mail already registered!"));
		}
	}

	@Test
	public void testRegisterNewForCompany_ForDeletedUser() throws Exception {
		final String email = "test@email.com";

		final UserDTO userDTO = new UserDTO();
		userDTO.setEmail(email);
		userDTO.setPassword("password");

		final User deletedUser = new User();
		deletedUser.setEmail(email);
		deletedUser.setId(3L);
		deletedUser.setUserStatusType(new UserStatusType(UserStatusType.DELETED));
		deletedUser.setUuid("test-uuid");

		final User adminUser = new User();
		adminUser.setId(4L);

		when(userDAO.findUser(anyString())).thenReturn(null);
		when(userDAO.findDeletedUsersByEmail(email)).thenReturn(deletedUser);
		when(authenticationService.getCurrentUser()).thenReturn(adminUser);

		final User user = registrationService.registerNewForCompany(userDTO, 1L, new Long[]{1L}, false);

		verify(userDAO, times(1)).saveOrUpdate(deletedUser);
		verify(authenticationService,times(1)).createUser(deletedUser.getUuid(),
				deletedUser.getEmail(),
				userDTO.getPassword(),
				company.getUuid(),
				new UserStatusType(UserStatusType.APPROVED));

		assertThat(user.getId(),equalTo(deletedUser.getId()));
		assertThat(user.getEmail(),equalTo(deletedUser.getEmail()));
		assertThat(user.getUuid(),equalTo(deletedUser.getUuid()));
		assertThat(user.getUserStatusType(),equalTo(new UserStatusType(UserStatusType.APPROVED)));

	}

	@Test
	public void testConfirmAccountWithSuspendedUser() {
		confirmAccountWithSuspendedUser();
	}

	private User confirmAccountWithSuspendedUser() {
		when(userService.findUserById(1234L)).thenReturn(user);
		when(authenticationService.isSuspended(user)).thenReturn(true);
		assertEquals(null, registrationService.confirmAccount(1234L, false));
		return null;
	}

	@Test
	public void testConfirmAccountAnotherUserSameEmail() {
		confirmAccountAnotherUserSameEmail();
	}

	private User confirmAccountAnotherUserSameEmail() {
		User anotherUser = mock(User.class);
		when(userService.findUserById(1234L)).thenReturn(user);
		when(authenticationService.isSuspended(user)).thenReturn(false);
		when(userService.findUserByEmail(anyString())).thenReturn(anotherUser);
		assertEquals(null, registrationService.confirmAccount(1234L, false));
		verify(user).setChangedEmail(null);
		return null;
	}

	@Test
	public void testConfirmAccountIfNotConfirmedYet() {
		confirmAccountIfNotConfirmedYet();
	}

	private User confirmAccountIfNotConfirmedYet() {
		when(userService.findUserById(1234L)).thenReturn(user);
		when(authenticationService.isSuspended(user)).thenReturn(false);
		when(userService.findUserByEmail(anyString())).thenReturn(null);
		when(user.getChangedEmail()).thenReturn(null);
		when(authenticationService.getEmailConfirmed(user)).thenReturn(false);
		when(user.getId()).thenReturn(1234L);

		User result = registrationService.confirmAccount(1234L, false);

		assertEquals(user, result);

		verify(authenticationService).setEmailConfirmed(user, true);
		verify(userNotificationService).onConfirmAccount(user, false);
		verify(authenticationService).refreshSessionForUser(user.getId());

		return result;
	}

	@Test
	public void testConfirmAccountEquivalence() {
		when(userService.findUserById(1234L)).thenReturn(user);

		assertEquals(confirmAccountWithSuspendedUser(),
				registrationService.confirmAccount(1234L));

		assertEquals(confirmAccountAnotherUserSameEmail(),
				registrationService.confirmAccount(1234L));

		assertEquals(confirmAccountIfNotConfirmedYet(),
				registrationService.confirmAccount(1234L));
	}
}
