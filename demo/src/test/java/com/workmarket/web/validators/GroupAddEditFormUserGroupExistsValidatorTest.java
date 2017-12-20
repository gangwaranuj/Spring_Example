package com.workmarket.web.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.BaseUnitTest;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.web.forms.groups.manage.GroupAddEditForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class GroupAddEditFormUserGroupExistsValidatorTest extends BaseUnitTest {

	@Mock SecurityContextFacade securityContextFacade;
	@Mock UserGroupService userGroupService;
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock FeatureEntitlementService featureEntitlementService;
	@Mock OrgStructureService orgStructureService;
	@InjectMocks GroupAddEditFormUserGroupExistsValidator validator;

	private static long USER_ID = 1L, COMPANY_ID = 2L;
	private static String GROUP_NAME = "group name";
	private static String OBJECT_NAME = "groupAddEditForm";
	private static String NAME_FIELD_NAME = "name";
	private static String ORG_UNITS_FIELD = "orgUnitUuids";
	private static String ORG_UNIT_ONE_UUID = "orgUnitOneUuid";
	private static String ORG_UNIT_ONE_NAME = "orgUnitOneName";
	private static String ORG_UNIT_TWO_UUID = "orgUnitTwoUuid";

	private static final OrgUnitPath ORG_UNIT_PATH = OrgUnitPath.newBuilder()
		.setUuid(ORG_UNIT_ONE_UUID)
		.setName(ORG_UNIT_ONE_NAME)
		.build();
	private static final OrgUnitDTO ORG_UNIT_DTO = new OrgUnitDTO.Builder(ORG_UNIT_PATH).build();
	private static final List<String> ORG_UNIT_UUIDS = ImmutableList.of(ORG_UNIT_DTO.getUuid());

	private Errors errors;
	private GroupAddEditForm form;

	@Before
	public void setup() {
		form = mock(GroupAddEditForm.class);
		when(form.getName()).thenReturn(GROUP_NAME);
		when(form.getOrgUnitUuids()).thenReturn(Lists.newArrayList(ORG_UNIT_ONE_UUID));

		errors = new BindException(form, OBJECT_NAME);

		ExtendedUserDetails extendedUserDetails = mock(ExtendedUserDetails.class);
		when(extendedUserDetails.getId()).thenReturn(USER_ID);
		when(extendedUserDetails.getCompanyId()).thenReturn(COMPANY_ID);
		when(securityContextFacade.getCurrentUser()).thenReturn(extendedUserDetails);
		when(featureEntitlementService.hasFeatureToggle(anyLong(), anyString())).thenReturn(true);
		when(orgStructureService.getSubtreePathOrgUnitUuidsForCurrentOrgMode(anyLong(), anyLong())).thenReturn(ORG_UNIT_UUIDS);
	}

	@Test
	public void validate_noFormName_noNameError() {
		when(form.getName()).thenReturn("");

		validator.validate(form, errors);

		assertFalse(errors.hasFieldErrors(NAME_FIELD_NAME));
	}

	@Test
	public void validate_uniqueGroupName_noNameError() {
		when(userGroupService.findCompanyUserGroupByName(anyLong(), anyString())).thenReturn(null);

		validator.validate(form, errors);

		assertFalse(errors.hasFieldErrors(NAME_FIELD_NAME));
	}

	@Test
	public void validate_duplicateGroupName_nameError() {
		UserGroup userGroup = mock(UserGroup.class);
		when(userGroupService.findCompanyUserGroupByName(anyLong(), anyString())).thenReturn(userGroup);

		validator.validate(form, errors);

		assertTrue(errors.hasFieldErrors(NAME_FIELD_NAME));
	}

	@Test
	public void validate_userDoesNotBelongToSelectedOrgUnit_orgUnitsError() {
		when(form.getOrgUnitUuids()).thenReturn(Lists.newArrayList(ORG_UNIT_TWO_UUID));

		validator.validate(form, errors);

		assertTrue(errors.hasFieldErrors(ORG_UNITS_FIELD));
	}

	@Test
	public void validate_userDoesBelongToSelectedOrgUnit_orgUnitsError() {
		validator.validate(form, errors);

		assertFalse(errors.hasFieldErrors(ORG_UNITS_FIELD));
	}

	@Test
	public void validate_userBelongsToEmptyListOrgUnits_orgUnitsError() {
		when(orgStructureService.getSubtreePathOrgUnitUuidsForCurrentOrgMode(anyLong(), anyLong())).thenReturn(Lists.<String>newArrayList());

		validator.validate(form, errors);

		assertTrue(errors.hasFieldErrors(ORG_UNITS_FIELD));
	}

	@Test
	public void validate_userSelectedNoOrgUnits_orgUnitsError() {
		when(form.getOrgUnitUuids()).thenReturn(Lists.<String>newArrayList());

		validator.validate(form, errors);

		assertTrue(errors.hasFieldErrors(ORG_UNITS_FIELD));
	}

	// TODO: Org-Structures, add test for form.getOrgUnitUuids() == null
}
