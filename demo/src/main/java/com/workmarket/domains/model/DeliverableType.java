package com.workmarket.domains.model;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * User: alexsilva Date: 11/24/14 Time: 11:06 AM
 */
public enum DeliverableType {

	PHOTOS("photos", "Photos"),
	SIGN_OFF("sign_off", "Sign Off"),
	OTHER("other", "Other");

	private final String type;
	private final String description;
	private static final Map<String, Map<String, String>> mapping;
	static {
		Map<String, Map<String, String>> mMap = new HashMap<>();
		for (DeliverableType d : DeliverableType.values()) {
			mMap.put(d.type, ImmutableMap.of("type", d.type, "description", d.description));
		}
		mapping = ImmutableMap.copyOf(mMap);
	}

	DeliverableType(String type, String description) {
		this.type = type;
		this.description = description;
	}

	public String type() {
		return type;
	}

	public String description() {
		return description;
	}

	public static Map<String, Map<String, String>> getMapping() {
		return mapping;
	}
}
