package com.workmarket.dao;

public interface DeletableDAOInterface<T> extends DAOInterface<T> {
	void delete(T entity);
}