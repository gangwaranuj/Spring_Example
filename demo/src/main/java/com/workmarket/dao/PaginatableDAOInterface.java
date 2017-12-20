package com.workmarket.dao;

import java.util.Map;

import org.hibernate.Criteria;

import com.workmarket.domains.model.Pagination;

public interface PaginatableDAOInterface<T> extends DAOInterface<T>{
	
	void applySorts(Pagination<T> pagination, Criteria query, Criteria count);
	
	void applyFilters(Pagination<T> pagination, Criteria query, Criteria count);
	
	void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params);
	
	Pagination<T> paginationQuery(Pagination<T> pagination, Map<String, Object> params);
    
	Pagination<T> paginationQuery(Class<? extends T> klass, Pagination<T> pagination, Map<String, Object> params);
}