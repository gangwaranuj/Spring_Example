package com.workmarket.data.solr.query;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.service.exception.search.SearchException;

public interface PeopleSearchSolrQueryString {

	void addQueryString(PeopleSearchTransientData data, SearchQuery query) throws SearchException;
}