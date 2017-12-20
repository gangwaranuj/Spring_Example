package com.workmarket.dao.option;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.option.Option;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Author: rocio
 */
@Repository
public class OptionDAOImpl extends AbstractDAO<Option> implements OptionDAO {

	@Override
	protected Class<Option> getEntityClass() {
		return Option.class;
	}

	@Override
	public <T extends Option> Option findOptionByNameAndValue(Class<T> clazz, String name, String value, Long entityId) {
		Assert.isAssignable(Option.class, clazz);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz);
		criteria.add(Restrictions.eq("name", name))
				.add(Restrictions.eq("value", value))
				.add(Restrictions.eq("entityId", entityId))
				.setMaxResults(1);

		return (T)criteria.uniqueResult();
	}

	@Override
	public <T extends Option> Option findOptionByName(Class<T> clazz, String name, Long entityId) {
		Assert.isAssignable(Option.class, clazz);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz);
		criteria.add(Restrictions.eq("name", name))
				.add(Restrictions.eq("entityId", entityId))
				.setMaxResults(1);

		return (T)criteria.uniqueResult();
	}
}
