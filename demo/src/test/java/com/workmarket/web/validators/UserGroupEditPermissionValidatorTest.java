package com.workmarket.web.validators;

import com.workmarket.BaseUnitTest;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.service.business.UserGroupService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class UserGroupEditPermissionValidatorTest extends BaseUnitTest {

	@Mock SecurityContextFacade securityContextFacade;
	@Mock UserGroupService userGroupService;
	@InjectMocks UserGroupEditPermissionValidator validator;

	private static long COMPANY_ID = 1L;
	private static long OTHER_COMPANY_ID = 2L;
	private static String OBJECT_NAME = "userGroup";

	private Company company = mock(Company.class);
	private Errors errors;
	private UserGroup userGroup = mock(UserGroup.class);

	@Before
	public void setup() {
		errors = new BindException(userGroup, OBJECT_NAME);

		ExtendedUserDetails extendedUserDetails = mock(ExtendedUserDetails.class);
		when(extendedUserDetails.getCompanyId()).thenReturn(COMPANY_ID);
		when(securityContextFacade.getCurrentUser()).thenReturn(extendedUserDetails);
	}

	@Test
	public void validate_canEditGroup_noErrors() {
		when(userGroup.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(COMPANY_ID);
		validator.validate(userGroup, errors);

		assertFalse(errors.hasErrors());
	}

	@Test
	public void validate_nullGroup_hasError() {
		validator.validate(null, errors);

		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_nullGroupCompany_hasError() {
		when(userGroup.getCompany()).thenReturn(null);

		validator.validate(userGroup, errors);

		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_differentUserCompanyAndGroupCompany_hasError() {
		when(userGroup.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(OTHER_COMPANY_ID);
		validator.validate(userGroup, errors);

		validator.validate(userGroup, errors);

		assertTrue(errors.hasErrors());
	}
}
