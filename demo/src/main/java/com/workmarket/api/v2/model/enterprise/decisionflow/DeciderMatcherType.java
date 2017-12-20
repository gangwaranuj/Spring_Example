package com.workmarket.api.v2.model.enterprise.decisionflow;

import io.swagger.annotations.ApiModel;

@ApiModel("DeciderMatcherType")
public enum DeciderMatcherType {

	INDIVIDUAL_USER("Individual user");

	private String name;

	DeciderMatcherType(final String name) {
		this.name = name;
	}

}
