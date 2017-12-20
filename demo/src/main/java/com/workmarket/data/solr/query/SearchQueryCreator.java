package com.workmarket.data.solr.query;

import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.service.exception.search.SearchException;

public interface SearchQueryCreator<T extends AbstractSearchTransientData> {

	SearchQuery createSearchQuery(T data) throws SearchException;

	<S extends SearchRequest> SearchQuery createSearchQuery(T data, S request) throws SearchException;
}