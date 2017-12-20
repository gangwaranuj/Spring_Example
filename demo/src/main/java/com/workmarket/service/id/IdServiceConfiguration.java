package com.workmarket.service.id;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.id.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class IdServiceConfiguration {
	@Autowired private MetricRegistry metricRegistry;
	private IdGenerator generator = null;

	@Bean
	IdGenerator getIdGenerator() throws IOException {
		if (generator == null) {
			generator = new IdGenerator(metricRegistry);
		}
		return generator;
	}
}
