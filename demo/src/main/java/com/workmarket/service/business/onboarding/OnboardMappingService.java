package com.workmarket.service.business.onboarding;

import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;

import java.util.Map;

/**
 * Created by ianha on 6/2/14
 */
public interface OnboardMappingService {
	Map<String, Object> mapProfile(String fieldsAsCommaSeparated, Profile profile, Company company, RequestContext context);
}
