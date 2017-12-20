package com.workmarket.web.validators;

import com.google.api.client.util.Lists;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.web.forms.user.ReassignUserForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ReassignValidator {
	@Autowired private UserService userService;
	@Autowired private MessageBundleHelper messageBundleHelper;

	private static final String ADMIN_ROLE = "ACL_ADMIN";
	private static final String SUPERUSER_ROLE = "ROLE_SUPERUSER";

	public List<ConstraintViolation> validate(final ReassignUserForm reassignUserForm, final ExtendedUserDetails actingUser) {

		final List<ConstraintViolation> errors = Lists.newArrayList();

		if (actingUser == null || reassignUserForm == null) {
			errors.add(createConstraintViolation("users.edit.exception"));
			return errors;
		}

		if (StringUtils.isBlank(reassignUserForm.getCurrentOwner())) {
			errors.add(createConstraintViolation("users.edit.exception"));
			return errors;
		}

		if (reassignUserForm.getNewAssessmentsOwner() == null) {
			errors.add(createConstraintViolation("users.reassign.blankAssessmentsOwner"));
			return errors;
		}

		if (reassignUserForm.getNewGroupsOwner() == null) {
			errors.add(createConstraintViolation("users.reassign.blankTalentPoolsOwner"));
			return errors;
		}

		if (reassignUserForm.getNewWorkOwner() == null) {
			errors.add(createConstraintViolation("users.reassign.blankWorkOwner"));
			return errors;
		}

		final User userToDeactivate = userService.findUserByUserNumber(reassignUserForm.getCurrentOwner());

		if (userToDeactivate == null) {
			errors.add(createConstraintViolation("users.edit.exception"));
			return errors;
		}

		final boolean doesActingUserHaveTheRightPermissions =
			actingUser.getCompanyId().equals(userToDeactivate.getCompany().getId()) &&
			actingUser.hasAnyRoles(ADMIN_ROLE, SUPERUSER_ROLE);

		if (!doesActingUserHaveTheRightPermissions) {
			errors.add(createConstraintViolation("users.reassign.authorize"));
		}

		return errors;
	}

	private ConstraintViolation createConstraintViolation(final String errorKey) {
		final String errorMessage = messageBundleHelper.getMessage(errorKey);
		return new ConstraintViolation()
			.setError(errorMessage)
			.setWhy(errorMessage);
	}
}
