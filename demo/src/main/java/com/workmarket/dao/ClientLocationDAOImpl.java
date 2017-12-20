package com.workmarket.dao;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.crm.ClientLocation;

@Repository
public class ClientLocationDAOImpl extends AbstractDAO<ClientLocation> implements ClientLocationDAO {

	@Override
	protected Class<ClientLocation> getEntityClass() {
		return ClientLocation.class;
	}

	@Override
	public ClientLocation findLocationById(Long clientLocationId) {
		return (ClientLocation) getFactory().getCurrentSession().createCriteria(ClientLocation.class)
				.add(Restrictions.eq("id", clientLocationId))
				.uniqueResult();
	}
}
