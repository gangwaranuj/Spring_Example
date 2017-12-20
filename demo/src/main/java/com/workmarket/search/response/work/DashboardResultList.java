package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardResultList implements Serializable {
	private static final long serialVersionUID = 1L;

	private long lastUpdated;
	private List<DashboardResult> results;
	private int pageNumber;
	private int totalNumberOfPages;
	private int totalResults;
	private List<String> resultIds;

	public DashboardResultList() {
	}

	public long getLastUpdated() {
		return this.lastUpdated;
	}

	public DashboardResultList setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
		return this;
	}

	public boolean isSetLastUpdated() {
		return (lastUpdated > 0L);
	}

	public int getResultsSize() {
		return (this.results == null) ? 0 : this.results.size();
	}

	public java.util.Iterator<DashboardResult> getResultsIterator() {
		return (this.results == null) ? null : this.results.iterator();
	}

	public void addToResults(DashboardResult elem) {
		if (this.results == null) {
			this.results = new ArrayList<>();
		}
		this.results.add(elem);
	}

	public List<DashboardResult> getResults() {
		return this.results;
	}

	public DashboardResultList setResults(List<DashboardResult> results) {
		this.results = results;
		return this;
	}

	public boolean isSetResults() {
		return this.results != null;
	}

	public int getPageNumber() {
		return this.pageNumber;
	}

	public DashboardResultList setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}

	public boolean isSetPageNumber() {
		return (pageNumber > 0);
	}

	public int getTotalNumberOfPages() {
		return this.totalNumberOfPages;
	}

	public DashboardResultList setTotalNumberOfPages(int totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
		return this;
	}

	public boolean isSetTotalNumberOfPages() {
		return (totalNumberOfPages > 0);
	}

	public int getTotalResults() {
		return this.totalResults;
	}

	public DashboardResultList setTotalResults(int totalResults) {
		this.totalResults = totalResults;
		return this;
	}

	public boolean isSetTotalResults() {
		return (totalResults > 0);
	}

	public int getResultIdsSize() {
		return (this.resultIds == null) ? 0 : this.resultIds.size();
	}

	public java.util.Iterator<String> getResultIdsIterator() {
		return (this.resultIds == null) ? null : this.resultIds.iterator();
	}

	public void addToResultIds(String elem) {
		if (this.resultIds == null) {
			this.resultIds = new ArrayList<>();
		}
		this.resultIds.add(elem);
	}

	public List<String> getResultIds() {
		return this.resultIds;
	}

	public DashboardResultList setResultIds(List<String> resultIds) {
		this.resultIds = resultIds;
		return this;
	}

	public boolean isSetResultIds() {
		return this.resultIds != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardResultList)
			return this.equals((DashboardResultList) that);
		return false;
	}

	private boolean equals(DashboardResultList that) {
		if (that == null)
			return false;

		boolean this_present_lastUpdated = true && this.isSetLastUpdated();
		boolean that_present_lastUpdated = true && that.isSetLastUpdated();
		if (this_present_lastUpdated || that_present_lastUpdated) {
			if (!(this_present_lastUpdated && that_present_lastUpdated))
				return false;
			if (this.lastUpdated != that.lastUpdated)
				return false;
		}

		boolean this_present_results = true && this.isSetResults();
		boolean that_present_results = true && that.isSetResults();
		if (this_present_results || that_present_results) {
			if (!(this_present_results && that_present_results))
				return false;
			if (!this.results.equals(that.results))
				return false;
		}

		boolean this_present_pageNumber = true && this.isSetPageNumber();
		boolean that_present_pageNumber = true && that.isSetPageNumber();
		if (this_present_pageNumber || that_present_pageNumber) {
			if (!(this_present_pageNumber && that_present_pageNumber))
				return false;
			if (this.pageNumber != that.pageNumber)
				return false;
		}

		boolean this_present_totalNumberOfPages = true && this.isSetTotalNumberOfPages();
		boolean that_present_totalNumberOfPages = true && that.isSetTotalNumberOfPages();
		if (this_present_totalNumberOfPages || that_present_totalNumberOfPages) {
			if (!(this_present_totalNumberOfPages && that_present_totalNumberOfPages))
				return false;
			if (this.totalNumberOfPages != that.totalNumberOfPages)
				return false;
		}

		boolean this_present_totalResults = true && this.isSetTotalResults();
		boolean that_present_totalResults = true && that.isSetTotalResults();
		if (this_present_totalResults || that_present_totalResults) {
			if (!(this_present_totalResults && that_present_totalResults))
				return false;
			if (this.totalResults != that.totalResults)
				return false;
		}

		boolean this_present_resultIds = true && this.isSetResultIds();
		boolean that_present_resultIds = true && that.isSetResultIds();
		if (this_present_resultIds || that_present_resultIds) {
			if (!(this_present_resultIds && that_present_resultIds))
				return false;
			if (!this.resultIds.equals(that.resultIds))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_lastUpdated = true && (isSetLastUpdated());
		builder.append(present_lastUpdated);
		if (present_lastUpdated)
			builder.append(lastUpdated);

		boolean present_results = true && (isSetResults());
		builder.append(present_results);
		if (present_results)
			builder.append(results);

		boolean present_pageNumber = true && (isSetPageNumber());
		builder.append(present_pageNumber);
		if (present_pageNumber)
			builder.append(pageNumber);

		boolean present_totalNumberOfPages = true && (isSetTotalNumberOfPages());
		builder.append(present_totalNumberOfPages);
		if (present_totalNumberOfPages)
			builder.append(totalNumberOfPages);

		boolean present_totalResults = true && (isSetTotalResults());
		builder.append(present_totalResults);
		if (present_totalResults)
			builder.append(totalResults);

		boolean present_resultIds = true && (isSetResultIds());
		builder.append(present_resultIds);
		if (present_resultIds)
			builder.append(resultIds);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashbaordResultList(");
		boolean first = true;

		if (isSetLastUpdated()) {
			sb.append("lastUpdated:");
			sb.append(this.lastUpdated);
			first = false;
		}
		if (isSetResults()) {
			if (!first) sb.append(", ");
			sb.append("results:");
			if (this.results == null) {
				sb.append("null");
			} else {
				sb.append(this.results);
			}
			first = false;
		}
		if (isSetPageNumber()) {
			if (!first) sb.append(", ");
			sb.append("pageNumber:");
			sb.append(this.pageNumber);
			first = false;
		}
		if (isSetTotalNumberOfPages()) {
			if (!first) sb.append(", ");
			sb.append("totalNumberOfPages:");
			sb.append(this.totalNumberOfPages);
			first = false;
		}
		if (isSetTotalResults()) {
			if (!first) sb.append(", ");
			sb.append("totalResults:");
			sb.append(this.totalResults);
			first = false;
		}
		if (isSetResultIds()) {
			if (!first) sb.append(", ");
			sb.append("resultIds:");
			if (this.resultIds == null) {
				sb.append("null");
			} else {
				sb.append(this.resultIds);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

