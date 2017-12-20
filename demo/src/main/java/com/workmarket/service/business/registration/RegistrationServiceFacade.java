package com.workmarket.service.business.registration;

import com.workmarket.domains.model.User;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserDTO;

public interface RegistrationServiceFacade {

	public User registerNew(UserDTO userDTO,
                            Long invitationId,
                            String companyName,
                            AddressDTO addressDTO,
                            ProfileDTO profileDTO,
                            boolean isBuyer) throws Exception;

	public User registerNew(UserDTO userDTO, Long invitationId) throws Exception;

	public User registerUserSimple(InvitationUserRegistrationDTO invitationUserRegistrationDTO, boolean isBuyer) throws Exception;
}
