package com.workmarket.domains.authentication.features;

/**
 * Author: rocio
 */
public enum FeatureKey {

	BUYER_VENUE_BOUNCER("buyer_venue_bouncer");

	private String name;

	private FeatureKey(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
