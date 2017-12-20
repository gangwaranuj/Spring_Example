package com.workmarket.domains.onboarding.model;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;

/**
 * Created by ianha on 9/15/14
 */
public interface OnboardCompleteValidator {
	boolean validateWeb(Profile profile, Company company, Boolean isLast);
	boolean validateMobile(Profile profile, Company company);
}
