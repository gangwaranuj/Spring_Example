package com.workmarket.web.validators;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.web.forms.user.ReassignUserForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReassignValidatorTest {

	private static final Long ACTING_USER_COMPANY_ID = 1L, USER_TO_DEACTIVATE_COMPANY_ID = 1L, REASSIGN_USER = 2L;
	private static final String USER_TO_DEACTIVATE_USER_NUMBER = "123";
	@Mock UserService userService;
	@Mock MessageBundleHelper messageBundleHelper;
	@InjectMocks ReassignValidator reassignValidator;
	private ExtendedUserDetails actingUser;
	private User userToDeactivate;
	private ReassignUserForm reassignUserForm;
	private Company userToDeactivateCompany;
	private MessageBundle messageBundle;

	@Before
	public void setUp() {
		actingUser = mock(ExtendedUserDetails.class);
		userToDeactivate = mock(User.class);
		reassignUserForm = mock(ReassignUserForm.class);
		userToDeactivateCompany = mock(Company.class);
		messageBundle = mock(MessageBundle.class);

		when(reassignUserForm.getCurrentOwner()).thenReturn(USER_TO_DEACTIVATE_USER_NUMBER);
		when(reassignUserForm.getNewAssessmentsOwner()).thenReturn(REASSIGN_USER);
		when(reassignUserForm.getNewGroupsOwner()).thenReturn(REASSIGN_USER);
		when(reassignUserForm.getNewWorkOwner()).thenReturn(REASSIGN_USER);

		when(messageBundleHelper.newBundle()).thenReturn(messageBundle);

		when(userToDeactivateCompany.getId()).thenReturn(USER_TO_DEACTIVATE_COMPANY_ID);
		when(userToDeactivate.getCompany()).thenReturn(userToDeactivateCompany);

		when(userService.findUserByUserNumber(anyString())).thenReturn(userToDeactivate);

		when(actingUser.getCompanyId()).thenReturn(ACTING_USER_COMPANY_ID);
		when(actingUser.hasAnyRoles(anyString(), anyString())).thenReturn(true);
	}

	@Test
	public void validate_validReassignRequst_NoErrorsAdded() {
		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertTrue(CollectionUtils.isEmpty(errors));
	}

	@Test
	public void validate_reassignFormIsNull_errorsAdded() {
		final List<ConstraintViolation> errors = reassignValidator.validate(null, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_currentOwnerInReassignFormIsNull_errorsAdded() {
		when(reassignUserForm.getCurrentOwner()).thenReturn(null);

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_assessmentsOwnerInReassignFormIsNull_errorsAdded() {
		when(reassignUserForm.getNewAssessmentsOwner()).thenReturn(null);

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_groupsOwnerInReassignFormIsNull_errorsAdded() {
		when(reassignUserForm.getNewGroupsOwner()).thenReturn(null);

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_workOwnerInReassignFormIsNull_errorsAdded() {
		when(reassignUserForm.getNewWorkOwner()).thenReturn(null);

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_actingUserIsNull_errorsAdded() {
		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, null);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_userToDeactivateIsNull_errorsAdded() {
		when(userService.findUserByUserNumber(anyString())).thenReturn(null);

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_userToDeactivateAndActingUserAreFromTwoDifferentCompanies_errorsAdded() {
		when(actingUser.getCompanyId()).thenReturn(ACTING_USER_COMPANY_ID + 1);

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}

	@Test
	public void validate_actingUserDoesNotHaveTheRightRoles_errorsAdded() {
		when(actingUser.hasAnyRoles(anyString(), anyString())).thenReturn(false);

		final List<ConstraintViolation> errors = reassignValidator.validate(reassignUserForm, actingUser);

		assertEquals("Expect one error found", 1, errors.size());
	}
}
