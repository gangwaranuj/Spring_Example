package com.workmarket.domains.authentication.handlers;

import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.services.PrivacyEvaluator;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

public class CustomMethodSecurityExpressionHandler extends AbstractSecurityExpressionHandler<FilterInvocation> {
	private FeatureEvaluator featureEvaluator;
	private PrivacyEvaluator privacyEvaluator;

	public void setFeatureEvaluator(FeatureEvaluator featureEvaluator) {
		this.featureEvaluator = featureEvaluator;
	}

	public void setPrivacyEvaluator(PrivacyEvaluator privacyEvaluator) {
		this.privacyEvaluator = privacyEvaluator;
	}

	@Override
	protected SecurityExpressionRoot createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
		CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, fi);
		root.setFeatureEvaluator(featureEvaluator);
		root.setPrivacyEvaluator(privacyEvaluator);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(new AuthenticationTrustResolverImpl());
		return root;
	}
}
