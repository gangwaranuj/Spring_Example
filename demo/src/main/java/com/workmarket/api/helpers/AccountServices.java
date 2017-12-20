package com.workmarket.api.helpers;

import com.workmarket.domains.model.User;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.UserDTO;

/**
 * Serves as a facade facing web controllers, called on to perform backend
 * system functions, delegating tasks to lower level backend controllers. This facade's
 * focus is on user account related operations, such as new user registration, profile updates,
 * security access updates, etc.
 */
public interface AccountServices {

	public User registerNewUser(UserDTO userDTO, AddressDTO profileAddress, boolean sendEmail, boolean onboardCompleted,
		boolean autoConfirmAccount) throws Exception;

}
