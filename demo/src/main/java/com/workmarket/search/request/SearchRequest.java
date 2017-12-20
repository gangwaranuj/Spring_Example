package com.workmarket.search.request;

import com.workmarket.search.request.user.NumericFilter;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Set;

public class SearchRequest implements Serializable {

	private static final long serialVersionUID = -7249759717060292406L;

	private long userId; // user doing the search
	public static final String ALL = "all";
	private int startRow = 0;
	private int pageSize = 10;
	private SearchSortDirection sortDirection = SearchSortDirection.DESCENDING;
	private String keyword;
	private String type = ALL;
	private Double latitude;
	private Double longitude;
	private LocationFilter locationFilter;
	private Set<String> countryFilter;
	private NumericFilter satisfactionRateFilter;
	private NumericFilter onTimePercentageFilter;
	private NumericFilter deliverableOnTimePercentageFilter;

	public long getUserId() {
		return userId;
	}

	public SearchRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return userId > 0;
	}

	public int getPageSize() {
		return pageSize;
	}

	public SearchRequest setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public int getStartRow() {
		return startRow;
	}

	public SearchRequest setStartRow(int startRow) {
		this.startRow = startRow;
		return this;
	}

	public SearchSortDirection getSortDirection() {
		return sortDirection;
	}

	public SearchRequest setSortDirection(SearchSortDirection sortDirection) {
		this.sortDirection = sortDirection;
		return this;
	}

	public boolean isSetSortDirection() {
		return this.sortDirection != null;
	}

	public String getKeyword() {
		return keyword;
	}

	public SearchRequest setKeyword(String keyword) {
		this.keyword = keyword;
		return this;
	}

	public String getType() {
		return type;
	}

	public SearchRequest setType(String type) {
		this.type = type;
		return this;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public LocationFilter getLocationFilter() {
		return this.locationFilter;
	}

	public SearchRequest setLocationFilter(LocationFilter locationFilter) {
		this.locationFilter = locationFilter;
		return this;
	}

	public boolean isSetLocationFilter() {
		return this.locationFilter != null;
	}

	public Set<String> getCountryFilter() {
		return this.countryFilter;
	}

	public SearchRequest setCountryFilter(Set<String> countryFilter) {
		this.countryFilter = countryFilter;
		return this;
	}

	public boolean isSetCountryFilter() {
		return CollectionUtils.isNotEmpty(this.countryFilter);
	}

	public boolean isSetStartRow() {
		return (startRow > 0);
	}

	public boolean isSetPageSize() {
		return (pageSize > 0);
	}

	public NumericFilter getSatisfactionRateFilter() {
		return satisfactionRateFilter;
	}

	public SearchRequest setSatisfactionRateFilter(NumericFilter satisfactionRateFilter) {
		this.satisfactionRateFilter = satisfactionRateFilter;
		return this;
	}

	public NumericFilter getOnTimePercentageFilter() {
		return onTimePercentageFilter;
	}

	public SearchRequest setOnTimePercentageFilter(NumericFilter onTimePercentageFilter) {
		this.onTimePercentageFilter = onTimePercentageFilter;
		return this;
	}

	public boolean isSetOnTimePercentageFilter() {
		return this.onTimePercentageFilter != null;
	}

	public NumericFilter getDeliverableOnTimePercentageFilter() {
		return deliverableOnTimePercentageFilter;
	}

	public SearchRequest setDeliverableOnTimePercentageFilter(NumericFilter deliverableOnTimePercentageFilter) {
		this.deliverableOnTimePercentageFilter = deliverableOnTimePercentageFilter;
		return this;
	}

	public boolean isSetDeliverableOnTimePercentageFilter() {
		return this.deliverableOnTimePercentageFilter != null;
	}

	public boolean isSetSatisfactionRateFilter() {
		return this.satisfactionRateFilter != null;
	}
}
