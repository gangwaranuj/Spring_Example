package com.workmarket.domains.search.solr;

import com.typesafe.config.Config;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.workmarket.common.configuration.ConfigLoader.getConfig;

@Configuration
public class KafkaSolrConfiguration {

    @Autowired
    private Config config;

    private KafkaConfiguration getKafkaConfiguration() throws IOException {
        return getConfig(KafkaConfiguration.class, config.getConfig("solr_kafka"));
    }

    /**
     * Wire KafkaClient.
     *
     * @return KafkaClient
     */
    @Bean(name = "SolrKafkaClient")
    protected KafkaClient getKafkaClient() throws IOException {
        final KafkaConfiguration kafkaConfiguration = getKafkaConfiguration();
        return new KafkaClient(kafkaConfiguration);
    }
}
