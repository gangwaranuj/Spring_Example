package com.workmarket.dao;

import java.util.List;

import com.workmarket.domains.model.OrderedLookupEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.LookupEntity;

@Repository
public class LookupEntityDAOImpl extends AbstractDAO<LookupEntity> implements LookupEntityDAO {
	
	protected Class<LookupEntity> getEntityClass() {
		return LookupEntity.class;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends LookupEntity> List<T> findLookupEntities(Class<T> clazz) {
		Criteria c = getFactory().getCurrentSession().createCriteria(clazz);
		if (OrderedLookupEntity.class.isAssignableFrom(clazz))
			c.addOrder(Order.asc("order"));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends LookupEntity> T findByCode(Class<? extends LookupEntity> clazz, String code) {
		return (T) getFactory().getCurrentSession().createCriteria(clazz)
			.add(Restrictions.eq("code", code))
			.setMaxResults(1)
			.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends LookupEntity> T findByCodeWithDefault(Class<? extends LookupEntity> clazz, String code, T defaultResult) {
		T result = findByCode(clazz, code);
		if (result == null) {
			return defaultResult;
		}
		return result;
	}
}
