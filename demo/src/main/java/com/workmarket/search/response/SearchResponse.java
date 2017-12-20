package com.workmarket.search.response;

import com.workmarket.search.SearchWarning;
import com.workmarket.search.response.user.PeopleSearchResponse;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SearchResponse<T, S extends Enum<S>> implements Serializable, ISearchResponse<T> {
	private static final long serialVersionUID = 1L;

	private List<T> results;
	private Map<Enum<S>, List<FacetResult>> facets;
	private int totalResultCount = 0;
	private List<SearchWarning> warnings;
	private int resultsLimit = 5000;
	private int startRow = 0;
	private long queryTimeMillis = 0;

	protected SearchResponse(List<T> results, Map<Enum<S>, List<FacetResult>> facets) {
		this.results = results;
		this.facets = facets;
	}

	public int getResultsSize() {
		return (this.results == null) ? 0 : this.results.size();
	}

	public void addToResults(T elem) {
		if (getResults() == null) {
			setResults(Collections.synchronizedList(new ArrayList<T>()));
		}
		getResults().add(elem);
	}

	public List<T> getResults() {
		return results;
	}

	public SearchResponse<T, S> setResults(List<T> results) {
		this.results = results;
		return this;
	}

	public boolean isSetResults() {
		return this.results != null;
	}

	public void putToFacets(Enum<S> key, List<FacetResult> val) {
		if (this.facets == null) {
			this.facets = new LinkedHashMap<>();
		}
		this.facets.put(key, val);
	}

	public Map<Enum<S>, List<FacetResult>> getFacets() {
		return this.facets;
	}

	public SearchResponse setFacets(Map<Enum<S>, List<FacetResult>> facets) {
		this.facets = facets;
		return this;
	}

	public boolean isSetFacets() {
		return this.facets != null;
	}

	public int getTotalResultsCount() {
		return totalResultCount;
	}

	public SearchResponse setTotalResultsCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
		return this;
	}

	public List<SearchWarning> getWarnings() {
		return this.warnings;
	}

	public SearchResponse setWarnings(List<SearchWarning> warnings) {
		this.warnings = warnings;
		return this;
	}

	public boolean isSetWarnings() {
		return this.warnings != null;
	}

	public int getResultsLimit() {
		return resultsLimit;
	}

	public void setResultsLimit(int resultsLimit) {
		this.resultsLimit = resultsLimit;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public Integer getNumberOfPages() {
		return getTotalResultsCount() / getResultsLimit() + (getTotalResultsCount() % getResultsLimit() > 0 ? 1 : 0);
	}

	public Integer getCurrentPage() {
		return getStartRow() / getResultsLimit() + (getStartRow() % getResultsLimit() >= 0 ? 1 : 0);
	}

	/**
	 * Gets the queryTimeMillis.
	 *
	 * @return long The queryTimeMillis
	 */
	public long getQueryTimeMillis() {
		return queryTimeMillis;
	}

	/**
	 * Sets the queryTimeMillis.
	 *
	 * @param queryTimeMillis The queryTimeMillis to set
	 */
	public void setQueryTimeMillis(long queryTimeMillis) {
		this.queryTimeMillis = queryTimeMillis;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof PeopleSearchResponse)
			return this.equals((PeopleSearchResponse) that);
		return false;
	}

	private boolean equals(SearchResponse that) {
		if (that == null)
			return false;

		boolean this_present_results = true && this.isSetResults();
		boolean that_present_results = true && that.isSetResults();
		if (this_present_results || that_present_results) {
			if (!(this_present_results && that_present_results))
				return false;
			if (!this.results.equals(that.results))
				return false;
		}

		boolean this_present_facets = true && this.isSetFacets();
		boolean that_present_facets = true && that.isSetFacets();
		if (this_present_facets || that_present_facets) {
			if (!(this_present_facets && that_present_facets))
				return false;
			if (!this.facets.equals(that.facets))
				return false;
		}

		boolean this_present_numResults = true;
		boolean that_present_numResults = true;
		if (this_present_numResults || that_present_numResults) {
			if (!(this_present_numResults && that_present_numResults))
				return false;
			if (this.totalResultCount != that.totalResultCount)
				return false;
		}

		boolean this_present_warnings = true && this.isSetWarnings();
		boolean that_present_warnings = true && that.isSetWarnings();
		if (this_present_warnings || that_present_warnings) {
			if (!(this_present_warnings && that_present_warnings))
				return false;
			if (!this.warnings.equals(that.warnings))
				return false;
		}

		if (this.queryTimeMillis != that.queryTimeMillis) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_results = true && (isSetResults());
		builder.append(present_results);
		if (present_results)
			builder.append(results);

		boolean present_facets = true && (isSetFacets());
		builder.append(present_facets);
		if (present_facets)
			builder.append(facets);

		boolean present_numResults = true;
		builder.append(present_numResults);
		if (present_numResults)
			builder.append(totalResultCount);

		boolean present_warnings = true && (isSetWarnings());
		builder.append(present_warnings);
		if (present_warnings)
			builder.append(warnings);

		return builder.toHashCode();
	}

	@Override public String toString() {
		return "SearchResponse{" +
				", results=" + results +
				", totalResultCount=" + totalResultCount +
				", warnings=" + warnings +
				", resultsLimit=" + resultsLimit +
				", startRow=" + startRow +
			    ", queryTimeMillis=" + queryTimeMillis +
				'}';
	}
}
