package com.workmarket.service.data;

import com.workmarket.data.DataAccessClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataAccessServiceConfiguration {

  private DataAccessClient dataAccessClient = null;

  @Bean
  DataAccessClient getDataAccessClient() {
    if (dataAccessClient == null) {
      dataAccessClient = new DataAccessClient();
    }
    return dataAccessClient;
  }
}
