package com.workmarket.domains.authentication.features;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Set;

@Component
public class FeatureEvaluatorImpl implements FeatureEvaluator {
	private static final Logger logger = LoggerFactory.getLogger(FeatureEvaluatorImpl.class);

	@Autowired ExtendedUserDetailsOptionsService extendedUserDetailsService;

	private FeatureEvaluatorConfiguration config;

	public void setConfig(FeatureEvaluatorConfiguration config) {
		this.config = config;
	}

	@Override
	public boolean hasFeature(String email, ExtendedUserDetailsOptionsService.OPTION[] options, Object feature) {
		UserDetails userDetails = extendedUserDetailsService.loadUserByEmail(email, options);
		return hasFeature(userDetails, feature);
	}

	@Override
	public boolean hasGlobalFeature(Object feature) {
		Assert.notNull(feature);

		Feature resolvedFeature = resolveFeature(feature);

		return resolvedFeature.isEnabled() && resolvedFeature.getEntitledSegmentKeys().size() == 0;
	}

	@Override
	public boolean hasFeature(Authentication authentication, Object feature) {
		return
			authentication.getPrincipal() instanceof ExtendedUserDetails &&
			hasFeature((ExtendedUserDetails)authentication.getPrincipal(), feature);
	}

	@Override
	public boolean hasFeature(UserDetails user, Object feature) {
		Assert.notNull(feature);

		if (!(user instanceof  ExtendedUserDetails)) {
			return false;
		}

		ExtendedUserDetails exUser = (ExtendedUserDetails) user;
		if (exUser.isMasquerading()) { user = exUser.getMasqueradeUser(); }

		Feature resolvedFeature = resolveFeature(feature);
		boolean isOn  = resolvedFeature.isEnabled();
		Set<String> entitledSegmentKeys = resolvedFeature.getEntitledSegmentKeys();

		if (entitledSegmentKeys.size() == 0) {
			return isOn;
		}

		for (String userProperty : entitledSegmentKeys) {
			try {
				Method method = new PropertyDescriptor(userProperty, user.getClass()).getReadMethod();
				boolean isSet = resolvedFeature.isEnabledFor(userProperty, method.invoke(user));
				if ((isSet && isOn) || (!isSet && !isOn)) return true;
			} catch (Exception e) {
				logger.error("Failed to find " + userProperty + " field on user.", e);
			}
		}
		return false;
	}

	@Override
	public boolean hasFeature(Long companyId, Object feature) {
		Assert.notNull(companyId);
		Assert.notNull(feature);

		Feature resolvedFeature = resolveFeature(feature);
		boolean isOn = resolvedFeature.isEnabled();
		Set<String> entitledSegmentKeys = resolvedFeature.getEntitledSegmentKeys();

		if (entitledSegmentKeys.size() == 0) {
			return isOn;
		}

		boolean isSet = resolvedFeature.isEnabledFor("companyId", companyId);

		return (isOn && isSet) || (!isOn && !isSet);
	}

	private Feature resolveFeature(Object o) {
		Feature f;
		if (config != null) {
			f = this.config.get(o.toString());
			if (f == null) {
				f = new Feature(o.toString());
				f.setEnabled(false);
			}
		} else {
			f = new Feature(o.toString());
			f.setEnabled(false);
		}
		return f;
	}
}
