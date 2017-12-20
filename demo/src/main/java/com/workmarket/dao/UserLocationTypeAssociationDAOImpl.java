package com.workmarket.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.UserLocationTypeAssociation;

@Repository
public class UserLocationTypeAssociationDAOImpl extends AbstractDAO<UserLocationTypeAssociation> implements UserLocationTypeAssociationDAO  {

	protected Class<UserLocationTypeAssociation> getEntityClass() {
		return UserLocationTypeAssociation.class;
	}
	
	@Override
	public  UserLocationTypeAssociation findByUserAndLocationType(Long userId, Long locationTypeId) {
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("user.id", userId))
			.add(Restrictions.eq("locationType.id", locationTypeId));
		
		return (UserLocationTypeAssociation) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LocationType> findActiveLocationTypesByUserId(Long userId) {
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())	
			.setFetchMode("locationType", FetchMode.JOIN)
			.add(Restrictions.eq("user.id", userId))
			.add(Restrictions.eq("deleted", Boolean.FALSE))
			.setProjection(Projections.property("locationType"));
		
		return (List<LocationType>) criteria.list();
	}
}
