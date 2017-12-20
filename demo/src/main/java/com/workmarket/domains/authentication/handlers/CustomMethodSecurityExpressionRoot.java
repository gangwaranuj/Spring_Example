package com.workmarket.domains.authentication.handlers;

import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.services.PrivacyEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

public class CustomMethodSecurityExpressionRoot extends WebSecurityExpressionRoot {
	private FeatureEvaluator featureEvaluator;
	private PrivacyEvaluator privacyEvaluator;

	public void setFeatureEvaluator(FeatureEvaluator featureEvaluator) {
		this.featureEvaluator = featureEvaluator;
	}

	public void setPrivacyEvaluator(PrivacyEvaluator privacyEvaluator) {
		this.privacyEvaluator = privacyEvaluator;
	}

	public CustomMethodSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
		super(a, fi);
	}

	public boolean hasFeature(String feature) {
		return featureEvaluator.hasFeature(getAuthentication(), feature);
	}

	public boolean hasFeature(Long companyId, String feature) {
		return featureEvaluator.hasFeature(companyId, feature);
	}

	public boolean isProtected(String profileProperty) {
		return privacyEvaluator.isProtected(getAuthentication(), profileProperty);
	}
}
