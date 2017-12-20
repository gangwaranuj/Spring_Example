package com.workmarket.dao;

import com.workmarket.domains.model.crm.ClientLocation;

public interface ClientLocationDAO extends DAOInterface<ClientLocation> {
	ClientLocation findLocationById(Long clientLocationId);
}
