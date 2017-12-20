package com.workmarket.service.core;

import com.workmarket.collection.CollectionClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectionServiceConfiguration {

  private CollectionClient collectionServiceClient = null;

  @Bean
  CollectionClient getCollectionServiceClient() {
    collectionServiceClient = new CollectionClient();
    return collectionServiceClient;
  }
}
