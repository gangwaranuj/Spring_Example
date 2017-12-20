package com.workmarket.dao;

import org.springframework.util.Assert;

public abstract class DeletableAbstractDAO<T> extends AbstractDAO<T> {
	public void delete(T entity) {
		Assert.notNull(entity);
		getFactory().getCurrentSession().delete(entity);
	}
}
