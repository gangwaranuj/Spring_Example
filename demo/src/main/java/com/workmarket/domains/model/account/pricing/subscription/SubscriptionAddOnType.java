package com.workmarket.domains.model.account.pricing.subscription;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity(name = "subscriptionAddOnType")
@Table(name = "subscription_add_on_type")
public class SubscriptionAddOnType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String ANALYTICAL_SUPPORT = "analytical";
	public static final String CUSTOM_PORTAL = "custPortal";
	public static final String GROUP_BUILDING = "groupBuild";
	public static final String INTEGRATION_EXPENSES = "integraExp";
	public static final String ONSITE_TRAINING = "onsiteTrng";
	public static final String PREMIUM_CONSULTING = "premiumCon";
	public static final String PREMIUM_SUPPORT = "premiumSup";
	public static final String REMOTE_TRAINING = "remoteTrng";
	public static final String RESOURCE_RECRUITING = "resRecruit";


	public static final List<String> addOnTypeCodes = ImmutableList.of(
			ANALYTICAL_SUPPORT,
			CUSTOM_PORTAL,
			GROUP_BUILDING,
			INTEGRATION_EXPENSES,
			ONSITE_TRAINING,
			PREMIUM_CONSULTING,
			PREMIUM_SUPPORT,
			REMOTE_TRAINING,
			RESOURCE_RECRUITING);


	public SubscriptionAddOnType() {
	}

	public SubscriptionAddOnType(String code) {
		super(code);
	}
}
