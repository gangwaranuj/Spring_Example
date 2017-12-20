package com.workmarket.api.v2.employer.assignments.config;

import com.workmarket.recurrence.RecurrenceClient;
import com.workmarket.recurrence.RecurrenceClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecurrenceConfig {

	@Bean
	RecurrenceClient recurrenceClient() {
		return new RecurrenceClientImpl();
	}
}
