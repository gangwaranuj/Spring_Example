package com.workmarket.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.LocationType;

@Repository
public class LocationTypeDAOImpl extends AbstractDAO<LocationType> implements LocationTypeDAO {

	protected Class<LocationType> getEntityClass() {
		return LocationType.class;
	}
	
	@Override
	public LocationType findLocationTypeById(Long id) {
		return (LocationType) getFactory().getCurrentSession().get(LocationType.class, id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LocationType> findAllLocationTypes() {
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());	
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
