package com.workmarket.domains.work.service.part;

import com.workmarket.shipment.client.ShipmentClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PartServiceConfiguration {
	@Bean
	ShipmentClient getShipmentServiceClient() {
		return new ShipmentClient();
	}
}
