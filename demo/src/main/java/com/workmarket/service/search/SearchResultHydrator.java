package com.workmarket.service.search;

import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.response.SearchResponse;

public interface SearchResultHydrator<T extends SearchResponse> {

	T hydrateSearchResult(T response, AbstractSearchTransientData hydrateData);

}