package com.workmarket.service.infra.kafka;

import com.typesafe.config.Config;
import com.workmarket.service.infra.kafka.config.KafkaConsumerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.workmarket.common.configuration.ConfigLoader.getConfig;

@Configuration
public class KafkaConsumerForIndexingConfiguration {
	private static final Log logger = LogFactory.getLog(KafkaConsumerForIndexingConfiguration.class);

	private static final String INDEX_CONSUMER_CONFIG = "indexing_kafka";

	@Autowired
	private Config config;

	public KafkaConsumerConfig getKafkaConsumerConfig() {
		KafkaConsumerConfig kafkaConsumerConfig = null;

		try {
			kafkaConsumerConfig = getConfig(KafkaConsumerConfig.class, config.getConfig(INDEX_CONSUMER_CONFIG));
		} catch (IOException ioException) {
			logger.error("Failed to read KafkaConsumerConfig: " + INDEX_CONSUMER_CONFIG, ioException);
		}

		return kafkaConsumerConfig;
	}

}
