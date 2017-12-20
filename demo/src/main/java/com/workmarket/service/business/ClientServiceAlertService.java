package com.workmarket.service.business;

import com.workmarket.domains.model.clientservice.ClientServiceAlert;

public interface ClientServiceAlertService {
	void saveOrUpdate(ClientServiceAlert alert);

	ClientServiceAlert findById(Long alertId);
}
