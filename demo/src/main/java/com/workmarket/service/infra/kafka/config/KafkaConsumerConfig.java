package com.workmarket.service.infra.kafka.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.util.List;

public class KafkaConsumerConfig {
	private final boolean enabled;
	private final boolean autoCommit;
	private final String bootstrapServers;
	private final String consumerGroup;
	private final Integer sessionTimeoutMs;
	private final Integer autoCommitIntervalMs;
	private final String keyDeserializer;
	private final String valueDeserializer;
	private final List<TopicConfig> topics;

	public KafkaConsumerConfig(
			@JsonProperty("enabled") Boolean enabled,
			@JsonProperty("autoCommit") Boolean autoCommit,
			@JsonProperty("bootstrapServers") String bootstrapServers,
			@JsonProperty("consumerGroup") String consumerGroup,
			@JsonProperty("sessionTimeoutMs") Integer sessionTimeoutMs,
			@JsonProperty("autoCommitIntervalMs") Integer autoCommitIntervalMs,
			@JsonProperty("keyDeserializer") String keyDeserializer,
			@JsonProperty("valueDeserializer") String valueDeserializer,
			@JsonProperty("topics") List<TopicConfig> topics) {
		this.enabled = enabled == null ? false : enabled.booleanValue();
		this.autoCommit = autoCommit == null ? false : autoCommit.booleanValue();
		Preconditions.checkState(!this.enabled || bootstrapServers != null, "If kafka.enabled is true, bootstrapServers " +
				"must not be null");
		this.bootstrapServers = bootstrapServers;
		this.consumerGroup = consumerGroup;
		this.sessionTimeoutMs = sessionTimeoutMs;
		this.autoCommitIntervalMs = autoCommitIntervalMs;
		this.keyDeserializer = keyDeserializer;
		this.valueDeserializer = valueDeserializer;
		this.topics = topics;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	public Integer getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public Integer getAutoCommitIntervalMs() {
		return autoCommitIntervalMs;
	}

	public String getKeyDeserializer() {
		return keyDeserializer;
	}

	public String getValueDeserializer() {
		return valueDeserializer;
	}

	public List<TopicConfig> getTopics() {
		return topics;
	}
}
