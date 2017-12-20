package com.workmarket.domains.authentication.services;

import com.workmarket.domains.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ExtendedUserDetailsOptionsService {
	enum OPTION {
		COMPANY, PROFILE, TIME_ZONE, POSTAL_CODE, PERSONA_PREFERENCE, AVATARS, BACKGROUND_IMAGE, AUTH, TAX_INFO
	}

	OPTION[] ALL_OPTIONS = {
			OPTION.COMPANY, OPTION.PROFILE, OPTION.TIME_ZONE, OPTION.POSTAL_CODE, OPTION.PERSONA_PREFERENCE,
			OPTION.AVATARS, OPTION.BACKGROUND_IMAGE, OPTION.AUTH, OPTION.TAX_INFO
	};

	UserDetails loadUser(User user) throws UsernameNotFoundException, DataAccessException;

	UserDetails loadUserByEmail(String email, OPTION[] options);
}
