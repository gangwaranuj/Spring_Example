package com.workmarket.service.search;

import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.response.SearchResponse;
import com.workmarket.service.exception.search.SearchException;
import org.apache.solr.client.solrj.response.QueryResponse;

public interface SearchResultParser<T extends SearchResponse> {

	/**
	 * Returns the search response object from the search
	 *
	 * @param hydrateData
	 * @param queryResponse
	 * @return
	 * @throws SearchException
	 */
	 T parseSolrQueryResponse(T searchResponse, AbstractSearchTransientData hydrateData, QueryResponse queryResponse) throws SearchException;

}