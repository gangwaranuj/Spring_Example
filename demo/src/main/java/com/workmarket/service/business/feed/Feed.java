package com.workmarket.service.business.feed;

import com.google.common.collect.Lists;

import java.util.List;

public class Feed {
	long totalCount = 0;
	long page = 0;
	int pageSize = 0;
	List<FeedItem> results = Lists.newArrayList();
	List<String> errorMessages = Lists.newArrayList();
	double distanceRadiusInMiles = 0;

	public double getDistanceRadiusInMiles() {
		return distanceRadiusInMiles;
	}

	public void setDistanceRadiusInMiles(double distanceRadiusInMiles) {
		this.distanceRadiusInMiles = distanceRadiusInMiles;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public Feed setTotalCount(long totalCount) {
		this.totalCount = totalCount;
		return this;
	}

	public long getPage() {
		return page;
	}

	public Feed setPage(long page) {
		this.page = page;
		return this;
	}

	public int getPageSize() {
		return pageSize;
	}

	public Feed setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public boolean hasMorePages() {
		return page < (totalCount / pageSize);
	}

	public List<FeedItem> getResults() {
		return results;
	}

	public Feed setResults(List<FeedItem> results) {
		this.results = results;
		return this;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public Feed setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
		return this;
	}
}
