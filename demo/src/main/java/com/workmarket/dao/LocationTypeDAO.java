package com.workmarket.dao;

import java.util.List;

import com.workmarket.domains.model.LocationType;

public interface LocationTypeDAO extends DAOInterface<LocationType>{

	LocationType findLocationTypeById(Long id);
	
	List<LocationType> findAllLocationTypes();

}
