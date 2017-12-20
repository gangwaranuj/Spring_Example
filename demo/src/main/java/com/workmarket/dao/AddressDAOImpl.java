package com.workmarket.dao;

import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.Address;

@Repository
public class AddressDAOImpl extends AbstractDAO<Address> implements AddressDAO {
	protected Class<Address> getEntityClass() {
		return Address.class;
	}

}
