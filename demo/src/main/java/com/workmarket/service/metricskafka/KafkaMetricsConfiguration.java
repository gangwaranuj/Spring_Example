package com.workmarket.service.metricskafka;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigOrigin;
import com.workmarket.common.configuration.ConfigInfo;
import com.workmarket.common.configuration.ConfigLoader;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaConfiguration;
import com.workmarket.common.metric.Metrics;
import com.workmarket.common.metric.MetricsConfiguration;
import com.workmarket.common.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.workmarket.common.configuration.ConfigLoader.getConfig;

@Configuration
public class KafkaMetricsConfiguration {
    private static final String APPLICATION_NAME = "application";

    private static final Logger logger = LoggerFactory.getLogger(KafkaMetricsConfiguration.class);

    @Autowired
    private Config config;

    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    @Bean
    public MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }

    @PostConstruct
    private void initialize() throws Exception {
        try {
            Preconditions.checkNotNull(config);
            logger.info("Configuring regular metrics reporting");
            Metrics.initializeMetrics(getMetricsConfiguration(), APPLICATION_NAME, "version", Util.getHostNameFromSystem(),
                METRIC_REGISTRY);
            logger.info("Configuring metrics to report Hystrix stuff");
            HystrixPlugins.reset();
            final HystrixCodaHaleMetricsPublisher publisher = new HystrixCodaHaleMetricsPublisher(METRIC_REGISTRY);
            HystrixPlugins.getInstance().registerMetricsPublisher(publisher);
        } catch (final Exception e) {
            logger.error("Encountered error during initialize()", e);
        }
    }

    private MetricsConfiguration getMetricsConfiguration() throws IOException {
        return getConfig(MetricsConfiguration.class, config.getConfig("metrics"));
    }

    private KafkaConfiguration getKafkaConfiguration() throws IOException {
        return getConfig(KafkaConfiguration.class, config.getConfig("app_kafka"));
    }

    /**
     * Wire KafkaClient.
     *
     * @return KafkaClient
     */
    @Bean(name = "AppKafkaClient")
    protected KafkaClient getKafkaClient() throws IOException {
        final KafkaConfiguration kafkaConfiguration = getKafkaConfiguration();
        return new KafkaClient(kafkaConfiguration);
    }

    /**
     * Makes the configuration injectable.
     *
     * @return The loaded configuration.
     */
    @Bean
    @VisibleForTesting
    protected static Config loadConfig() {
        Preconditions.checkNotNull(APPLICATION_NAME,
            "Something has gone wrong.  Your app name should've been loaded when app.conf "
                + "was loaded.  This is a bug somewhere.");
        final Config cfg = ConfigLoader.loadConfiguration(APPLICATION_NAME);
        final String rendered = ConfigLoader.secureRenderConfig(cfg, new Predicate<ConfigInfo>() {
            @Override
            public boolean apply(final ConfigInfo configInfo) {
                final ConfigOrigin origin = configInfo.getOrigin();
                if (origin.resource() != null && origin.resource().contains("-credentials")) {
                    return true;
                }

                if (origin.filename() != null && origin.filename().contains("-credentials")) {
                    return true;
                }

                if (origin.url() != null && origin.url().toString().contains("-credentials")) {
                    return true;
                }

                if (configInfo.getPath().matches("jwt") || configInfo.getPath().matches("jwtConfig")) {
                    return true;
                }

                return false;
            }
        });
        logger.info("Configuration for the service is {}", rendered);
        return cfg;
    }
}
