package com.workmarket.dao.clientservice;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.clientservice.ClientServiceAlert;

public interface ClientServiceAlertDAO extends DAOInterface<ClientServiceAlert> {

    ClientServiceAlert findById(Long alertId);
}