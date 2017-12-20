package com.workmarket.domains.work.service.resource;

public enum WorkAuthorizationResponse {
	SUCCEEDED("succeeded"),
	INSUFFICIENT_FUNDS("insufficient_funds"),
	INSUFFICIENT_BUDGET("insufficient_budget"),
	INSUFFICIENT_SPEND_LIMIT("insufficient_spend_limit"),
	PAYMENT_TERMS_AP_CREDIT_LIMIT("payment_terms_ap_credit_limit"),
	INTERNAL_PRICING("internal_pricing"),
	ALREADY_ADDED("already_added"),
	LANE0_NOT_ALLOWED("lane0_not_allowed"),
	INVALID_USER("invalid_user"),
	INTERNAL_ERROR("internal_error"),
	MAX_RESOURCES_EXCEEDED("max_resources_exceeded"),
	BLOCKED_RESOURCE("blocked_resource"),
	COMPANY_LOCKED("company_locked"),
	EMAIL_UNCONFIRMED("email_unconfirmed"),
	INVALID_COUNTRY("invalid_country"),
	INVALID_INDUSTRY_FOR_RESOURCE("invalid_industry_for_resource"),
	ALREADY_INVITED_TO_WORK("already_added"),
	DISABLED_WORKER_POOL("no_lane_user"),
	INVALID_SPEND_LIMIT("invalid_spend_limit"),
	INVALID_WORK("invalid_work"),
	// add new ones above this comment
	// the ones below were not in the map these descriptions were factored out of
	// no messages.properties entry for these, use generic message
	FAILED("assign_exception"),
	MAX_VENDORS_EXCEEDED("assign_exception"),
	INVALID_BUNDLE_STATE("assign_exception"),
	INSUFFICIENT_WORK_BUNDLE_AUTHORIZED_BUDGET("assign_exception"),
	UNKNOWN("assign_exception");

	private String key;

	WorkAuthorizationResponse(String key) {
		this.key = key;
	}

	public boolean success() {
		return this.equals(SUCCEEDED);
	}

	public boolean fail() {
		return !success();
	}

	public String getKey() {
		return key;
	}

	public String getMessagePropertyKey() {
		return String.format("search.cart.push.assignment.%s", key);
	}

}
