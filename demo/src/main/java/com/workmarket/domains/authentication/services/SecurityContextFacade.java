package com.workmarket.domains.authentication.services;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.springframework.security.core.context.SecurityContext;

public interface SecurityContextFacade {

	SecurityContext getSecurityContext();

	ExtendedUserDetails getCurrentUser();
}
