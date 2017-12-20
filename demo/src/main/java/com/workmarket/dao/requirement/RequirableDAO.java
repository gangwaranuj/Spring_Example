package com.workmarket.dao.requirement;

import com.workmarket.dao.DAOInterface;

public interface RequirableDAO<T> extends DAOInterface<T> {
	T findBy(Object... objects);
}
