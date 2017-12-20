package com.workmarket.service.notification;

import com.workmarket.notification.NotificationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationServiceConfiguration {
	@Bean
	NotificationClient getNotificationClient() {
		return new NotificationClient();
	}
}
