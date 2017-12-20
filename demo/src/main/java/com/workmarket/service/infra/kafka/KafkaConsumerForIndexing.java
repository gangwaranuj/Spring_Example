package com.workmarket.service.infra.kafka;

import com.workmarket.service.infra.kafka.config.KafkaConsumerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class KafkaConsumerForIndexing implements SmartLifecycle {
	private static final Log logger = LogFactory.getLog(KafkaConsumerForIndexing.class);

	@Autowired
	private KafkaConsumerForIndexingConfiguration kafkaConsumerForIndexingConfiguration;
	@Autowired
	private KafkaConsumerListener kafkaConsumerListener;

	private ExecutorService executorService;
	private boolean started = false;

	@Override
	public void start() {
		final KafkaConsumerConfig kafkaConsumerConfig =
				kafkaConsumerForIndexingConfiguration.getKafkaConsumerConfig();

		if (kafkaConsumerConfig == null || !kafkaConsumerConfig.isEnabled()) {
			logger.info("Kafka Consumer For Indexing: DISABLED");
			return;
		}

		executorService = Executors.newSingleThreadExecutor();
		executorService.submit(kafkaConsumerListener);

		logger.info("Kafka Consumer For Indexing: ENABLED");
		this.started = true;
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isRunning() {
		return started;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
	}

	@Override
	public int getPhase() {
		return 0;
	}
}
