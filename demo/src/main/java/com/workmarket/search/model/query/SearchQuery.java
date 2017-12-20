package com.workmarket.search.model.query;

import com.workmarket.search.SearchWarning;
import com.workmarket.search.request.SearchRequest;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

public class SearchQuery<T extends SearchRequest> extends SolrQuery {
	private static final long serialVersionUID = 6407815758621311392L;
	private List<SearchWarning> searchWarnings;

	protected final T request;

	public SearchQuery(T request) {
		super();
		this.request = request;
	}

	public List<SearchWarning> getSearchWarnings() {
		return searchWarnings;
	}

	public void setSearchWarnings(List<SearchWarning> searchWarnings) {
		this.searchWarnings = searchWarnings;
	}

	public T getRequest() {
		return request;
	}

	@Override
	public String toString() {
		return "SearchQuery{" +
				"searchWarnings=" + searchWarnings +
				'}';
	}
}
