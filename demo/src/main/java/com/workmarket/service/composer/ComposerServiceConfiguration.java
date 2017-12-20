package com.workmarket.service.composer;

import com.workmarket.core.composer.ComposerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComposerServiceConfiguration {

  private ComposerClient composerClient = null;

  @Bean
  ComposerClient getComposerClient() {
    if (composerClient == null) {
      composerClient = new ComposerClient();
    }
    return composerClient;
  }
}