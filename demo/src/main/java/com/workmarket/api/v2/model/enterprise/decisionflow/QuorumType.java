package com.workmarket.api.v2.model.enterprise.decisionflow;

import io.swagger.annotations.ApiModel;

@ApiModel("QuorumType")
public enum QuorumType {

	UNANIMOUS("Unanimous");

	private String name;

	QuorumType(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
