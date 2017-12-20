package com.workmarket.domains.authentication.features;

import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface FeatureEvaluator {
	boolean hasFeature(Authentication authentication, Object feature);

	boolean hasFeature(Long companyId, Object feature);

	boolean hasFeature(UserDetails user, Object feature);

	boolean hasFeature(String email, ExtendedUserDetailsOptionsService.OPTION[] options, Object feature);

	boolean hasGlobalFeature(Object feature);
}
