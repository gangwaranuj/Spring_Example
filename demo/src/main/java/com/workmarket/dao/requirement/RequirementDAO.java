package com.workmarket.dao.requirement;

import com.workmarket.dao.DAOInterface;

public interface RequirementDAO<T> extends DAOInterface<T> {
	T getOrInitializeBy(Object... objects);
	void saveOrUpdate(T entity);
}
