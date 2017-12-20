package com.workmarket.dao;

import com.workmarket.domains.model.DressCode;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public class DressCodeDAOImpl extends AbstractDAO<DressCode> implements DressCodeDAO {

	protected Class<DressCode> getEntityClass() {
		return DressCode.class;
	}
	
	@Override
	public DressCode findDressCodeById(Long id) {
		return (DressCode) getFactory().getCurrentSession().get(DressCode.class, id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DressCode> findAllDressCodes() {
		
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());	
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
