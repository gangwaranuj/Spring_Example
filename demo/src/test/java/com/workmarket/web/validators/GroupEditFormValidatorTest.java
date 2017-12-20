package com.workmarket.web.validators;

import com.workmarket.BaseUnitTest;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.web.forms.groups.manage.GroupAddEditForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class GroupEditFormValidatorTest extends BaseUnitTest {

	@Mock GroupAddEditFormUserGroupExistsValidator groupAddEditFormUserGroupExistsValidator;
	@Mock UserGroupService userGroupService;
	@InjectMocks GroupEditFormValidator validator;

	private static long GROUP_ID = 1L;
	private static String GROUP_NAME = "group name";
	private static String NEW_GROUP_NAME = "new group name";
	private static String OBJECT_NAME = "groupAddEditForm";

	private Errors errors;
	private GroupAddEditForm form;
	private UserGroup userGroup;

	@Before
	public void setup() {
		form = mock(GroupAddEditForm.class);
		when(form.getId()).thenReturn(GROUP_ID);

		errors = new BindException(form, OBJECT_NAME);

		userGroup = mock(UserGroup.class);
		when(userGroup.getName()).thenReturn(GROUP_NAME);

		when(userGroupService.findGroupById(anyLong())).thenReturn(userGroup);
	}

	@Test
	public void validate_differentFormGroupNameAndGroupName_validationHappens() {
		when(form.getName()).thenReturn(NEW_GROUP_NAME);

		validator.validate(form, errors);

		verify(groupAddEditFormUserGroupExistsValidator, times(1)).validate(any(GroupAddEditForm.class), any(Errors.class));
	}

	@Test
	public void validate_sameFormGroupNameAndGroupName_validationDoesntHappens() {
		when(form.getName()).thenReturn(GROUP_NAME);

		validator.validate(form, errors);

		verify(groupAddEditFormUserGroupExistsValidator, never()).validate(any(GroupAddEditForm.class), any(Errors.class));
	}
}
