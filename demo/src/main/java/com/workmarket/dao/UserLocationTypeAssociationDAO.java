package com.workmarket.dao;

import java.util.List;

import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.UserLocationTypeAssociation;

public interface UserLocationTypeAssociationDAO extends DAOInterface<UserLocationTypeAssociation>{
	
	public UserLocationTypeAssociation findByUserAndLocationType(Long userId, Long locationTypeId);

	public List<LocationType> findActiveLocationTypesByUserId(Long userId);
	
}
