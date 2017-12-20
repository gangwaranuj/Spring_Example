package com.workmarket.service.business.event.search;

import com.workmarket.search.request.SearchRequest;
import com.workmarket.search.response.FacetResult;
import com.workmarket.service.business.event.Event;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SearchRequestEvent<T extends SearchRequest, V extends Enum<V>> extends Event {

	private static final long serialVersionUID = -6255231413991017692L;

	private long companyId;
	private String workNumber;
	private T searchRequest;
	private final Map<Enum<V>, List<FacetResult>> facets;
	private int totalResultsCount = 0;

	public SearchRequestEvent(T searchRequest, Map<Enum<V>, List<FacetResult>> facets, int totalResultsCount, long companyId) {
		this.facets = Collections.unmodifiableMap(facets);
		this.searchRequest = searchRequest;
		this.totalResultsCount = totalResultsCount;
		this.companyId = companyId;
	}

	public Map<Enum<V>, List<FacetResult>> getFacets() {
		return facets;
	}

	public T getSearchRequest() {
		return searchRequest;
	}

	public int getTotalResultsCount() {
		return totalResultsCount;
	}

	public long getCompanyId() {
		return companyId;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	@Override
	public String toString() {
		return "SearchRequestEvent{" +
				"totalResultsCount=" + totalResultsCount +
				", searchRequest=" + searchRequest +
				", facets=" + facets +
				'}';
	}
}
