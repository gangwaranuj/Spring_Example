package com.workmarket.data.solr.repository;

import org.springframework.data.solr.core.query.Field;

public enum GroupSearchableFields implements Field {

	NETWORK_IDS("networkIds");

	private final String fieldName;

	private GroupSearchableFields(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getName() {
		return this.fieldName;
	}
}
