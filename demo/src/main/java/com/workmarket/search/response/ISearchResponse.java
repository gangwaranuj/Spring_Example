package com.workmarket.search.response;

import java.util.List;

public interface ISearchResponse<T> {

	List<T> getResults();

	SearchResponse setResults(List<T> results);
}
