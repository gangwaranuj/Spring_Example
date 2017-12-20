package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.CreateUserDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.web.forms.user.ReassignUserForm;

public interface UserSettingService {
	UserDTO create(CreateUserDTO builder) throws Exception;
	UserDTO update(String userNumber, UserDTO builder) throws Exception;
	UserDTO get(String userNumber) throws Exception;
	UserDTO deactivate(ReassignUserForm reassignUserForm) throws ValidationException;
}
