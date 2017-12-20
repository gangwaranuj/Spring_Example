package com.workmarket.dao.postalcode;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.postalcode.LocationMapping;

/**
 * Author: rocio
 */
public class LocationMappingDAOImpl extends AbstractDAO<LocationMapping> implements LocationMappingDAO {

	@Override
	protected Class<LocationMapping> getEntityClass() {
		return LocationMapping.class;
	}
}
