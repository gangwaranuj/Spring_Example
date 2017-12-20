package com.workmarket.service.infra.kafka.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicConfig {
	private final String name;

	public TopicConfig(@JsonProperty("name") final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}