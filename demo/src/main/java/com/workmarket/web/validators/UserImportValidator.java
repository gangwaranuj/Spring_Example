package com.workmarket.web.validators;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.EmailUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("userImportValidator")
public class UserImportValidator implements Validator {

	protected static final Log logger = LogFactory.getLog(UserImportValidator.class);
	private static User.WorkStatus WORK_STATUS;
	private static final String ADMINISTRATOR = "Administrator";

	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private MessageBundleHelper messageBundleHelper;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserImportDTO.class == clazz;
	}

	@Override
	public void validate(Object obj, Errors errors) {
		UserImportDTO input = (UserImportDTO) obj;
		User existingUser = userService.findUserByEmail(input.getEmail());

		if (StringUtils.isBlank(input.getFirstName())) {
			errors.rejectValue("firstName", "firstName", messageBundleHelper.getMessage("users.upload.row_no_firstName"));
		}

		if (StringUtils.isBlank(input.getLastName())) {
			errors.rejectValue("lastName", "lastName", messageBundleHelper.getMessage("users.upload.row_no_lastName"));
		}

		if (StringUtils.isBlank(input.getEmail())) {
			errors.rejectValue("email", "email", messageBundleHelper.getMessage("users.upload.row_no_email"));
		} else if (!EmailUtilities.isValidEmailAddress(input.getEmail())) {
			errors.rejectValue("email", "invalid_email", messageBundleHelper.getMessage("users.upload.row_invalid_email", input.getEmail()));
		} else {
			if (existingUser != null) {
				errors.rejectValue("email", "email_exists", messageBundleHelper.getMessage("users.upload.row_emailExists", input.getEmail()));
			}
		}

		String workPhoneNumber = input.getWorkPhone();
		if (StringUtils.isBlank(workPhoneNumber)) {
			errors.rejectValue("workPhone", "workPhone", messageBundleHelper.getMessage("users.upload.row_no_workPhone"));
		} else if (!StringUtils.isNumericSpace(workPhoneNumber.replaceAll("[-\\(\\)]", StringUtils.EMPTY))) {
			errors.rejectValue("workPhone", "invalid_workPhone", messageBundleHelper.getMessage("users.upload.row_invalid_workPhone", input.getWorkPhone()));
		}

		if (StringUtils.isBlank(input.getRole())) {
			errors.rejectValue("role", "role", messageBundleHelper.getMessage("users.upload.row_no_roles"));
		} else {
			if (StringUtils.containsIgnoreCase(input.getRole(), "admin")) {
				input.setRole(ADMINISTRATOR);
			}
			AclRole aclRole = authenticationService.findSystemRoleByName(StringUtilities.capitalizeFirstLetter(input.getRole().toLowerCase()));
			if (aclRole == null) {
				errors.rejectValue("role", "invalid_role", messageBundleHelper.getMessage("users.upload.row_invalid_roles", input.getRole()));
			}
		}
	}

}

