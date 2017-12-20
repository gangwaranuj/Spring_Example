package com.workmarket.service.search.user;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.service.exception.search.SearchException;

/**
 * In charge of taking a people search request and adding the appropriate query
 * and boost factor to the request.
 * 
 */
public interface PeopleSearchSort {

	public void addSortField(SearchQuery query, PeopleSearchTransientData data) throws SearchException;
}
