package com.workmarket.service.infra.business;

import com.workmarket.media.MediaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediaServiceConfiguration {

	private MediaClient mediaClient = null;

	@Bean
	MediaClient getMediaClient() {
		if (mediaClient == null) {
			mediaClient = new MediaClient();
		}
		return mediaClient;
	}

}
