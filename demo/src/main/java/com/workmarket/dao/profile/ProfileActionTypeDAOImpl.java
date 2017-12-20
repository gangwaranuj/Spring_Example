package com.workmarket.dao.profile;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ProfileActionType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileActionTypeDAOImpl extends AbstractDAO<ProfileActionType> implements ProfileActionTypeDAO {
	
	protected Class<ProfileActionType> getEntityClass() {
		return ProfileActionType.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProfileActionType> findAll() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.addOrder(Order.desc("weight"))
			.addOrder(Order.asc("description"));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProfileActionType> findIn(String[] types) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.in("code", types))
			.addOrder(Order.desc("weight"))
			.addOrder(Order.asc("description"))
			.list();
	}

}
