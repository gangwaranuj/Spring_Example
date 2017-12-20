package com.workmarket.api.v2.employer.search.common.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class used to wire our Guava module in to Spring's Jackson deserializer.
 */
@Configuration
public class GuavaModuleConfiguration {

	@Bean
	public Module guavaModule() {
		return new GuavaModule();
	}
}
