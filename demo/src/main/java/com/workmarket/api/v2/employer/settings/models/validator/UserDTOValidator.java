package com.workmarket.api.v2.employer.settings.models.validator;

import com.workmarket.api.v2.employer.settings.models.PermissionSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.RoleSettingsDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.utility.EmailUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;


@Component
public class UserDTOValidator implements Validator {

	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;

	@Override
	public boolean supports(final Class<?> clazz) {
		return UserDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		UserDTO userDTO = (UserDTO) target;
		User existingUser = userService.findUserByEmail(userDTO.getEmail());

		ValidationUtils.rejectIfEmpty(errors, "firstName", "user.validation.firstNameRequired");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "user.validation.lastNameRequired");

		if (StringUtils.isEmpty(userDTO.getEmail())) {
			ValidationUtils.rejectIfEmpty(errors, "email", "user.validation.emailRequired");
		} else if (!EmailUtilities.isValidEmailAddress(userDTO.getEmail())) {
			errors.rejectValue("email", "user.validation.emailInvalid");
		} else if (existingUser != null && !existingUser.getId().equals(userDTO.getId())) {
			errors.rejectValue("email", "user.validation.emailExists");
		}

		if (userDTO.getSpendLimit() == null || (userDTO.getSpendLimit().compareTo(BigDecimal.ZERO) < 0)) {
			errors.rejectValue("spendLimit", "user.validation.spendLimit");
		}

		String workPhoneNumber = userDTO.getWorkPhone();
		if (StringUtils.isBlank(workPhoneNumber)) {
			errors.rejectValue("workPhone", "user.validation.workPhoneRequired");
		} else if (!StringUtils.isNumericSpace(workPhoneNumber.replaceAll("[-\\(\\)]", StringUtils.EMPTY))) {
			errors.rejectValue("workPhone", "user.validation.workPhoneInvalid");
		}

		if (userDTO.getIndustryId() == null) {
			errors.rejectValue("industryId", "user.validation.industryRequired");
		}

		RoleSettingsDTO roleSettings = userDTO.getRoleSettings();
		if (roleSettings.isEmployeeWorker()) {
			if (roleSettings.isAdmin() ||
				roleSettings.isManager() ||
				roleSettings.isController() ||
				roleSettings.isUser() ||
				roleSettings.isViewOnly() ||
				roleSettings.isStaff() ||
				roleSettings.isDeputy() ||
				roleSettings.isDispatcher()) {
				errors.rejectValue("roleSettings", "user.validation.employeeWorkerNoOtherRolesAllowed");
			}

			PermissionSettingsDTO permissionSettings = userDTO.getPermissionSettings();
			if (permissionSettings.isPaymentAccessible() ||
				permissionSettings.isPaymentAccessible() ||
				permissionSettings.isCounterOfferAccessible() ||
				permissionSettings.isPricingEditable() ||
				permissionSettings.isProjectAccessible() ||
				permissionSettings.isWorkApprovalAllowed()
			) {
				errors.rejectValue("permissionSettings", "user.validation.employeeWorkerNoCustomPermissionsAllowed");
			}
		} else {
			if (!roleSettings.isAdmin() &&
				!roleSettings.isManager() &&
				!roleSettings.isController() &&
				!roleSettings.isUser() &&
				!roleSettings.isViewOnly() &&
				!roleSettings.isStaff() &&
				!roleSettings.isDeputy() &&
				!roleSettings.isDispatcher()) {
				errors.rejectValue("roleSettings", "user.validation.notWorkerRolesRequired");
			}
		}

		if (userDTO.getUserNumber() != null) {
			User user = userService.findUserByUserNumber(userDTO.getUserNumber());
			Company company = companyService.findById(user.getCompany().getId());
			if (userService.isLastAdmin(user.getUserNumber())) {
				errors.rejectValue("roleSettings", "user.validation.cannotRemoveAdministrator", new Object[]{company.getName()}, null);
			}
		}
	}
}
