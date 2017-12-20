package com.workmarket.web.forms.search;

import com.workmarket.search.request.LocationFilter;
import com.workmarket.search.request.user.NumericFilter;

import java.io.Serializable;
import java.util.Set;

public class BaseWorkerSearchForm implements Serializable {

	private static final long serialVersionUID = 3318132190759236743L;

	// Pagination
	private Integer start = 0;
	private Integer limit = 100;
	private String sortby;

	// Filters
	private String keyword;
	private String address;
	private String radius;
	private Set<String> countries;
	private Integer satisfactionRate;
	private Integer onTimePercentage;
	private Integer deliverableOnTimePercentage;

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getSortby() {
		return sortby;
	}

	public void setSortby(String sortby) {
		this.sortby = sortby;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public Set<String> getCountries() {
		return countries;
	}

	public void setCountries(Set<String> countries) {
		this.countries = countries;
	}

	public Integer getSatisfactionRate() {
		return satisfactionRate;
	}

	public void setSatisfactionRate(Integer satisfactionRate) {
		this.satisfactionRate = satisfactionRate;
	}

	public Integer getOnTimePercentage() {
		return onTimePercentage;
	}

	public void setOnTimePercentage(Integer onTimePercentage) {
		this.onTimePercentage = onTimePercentage;
	}

	public Integer getDeliverableOnTimePercentage() {
		return deliverableOnTimePercentage;
	}

	public void setDeliverableOnTimePercentage(Integer deliverableOnTimePercentage) {
		this.deliverableOnTimePercentage = deliverableOnTimePercentage;
	}

	public LocationFilter getLocationFilter() {
		if (address == null) { return null; }

		LocationFilter filter = new LocationFilter();
		filter.setWillingToTravelTo(address);
		if (radius != null) {
			if (radius.equals("any")) {
				filter.setAnywhere(true);
			} else {
				filter.setMaxMileFromResourceToLocation(Integer.valueOf(radius));
			}
		}
		return filter;
	}

	public NumericFilter getSatisfactionRateFilter() {
		if (satisfactionRate == null) { return null; }
		return new NumericFilter()
			.setFrom((float) satisfactionRate / 100)
			.setTo(1);
	}

	public NumericFilter getOnTimePercentageFilter() {
		if (onTimePercentage == null) { return null; }
		return new NumericFilter()
			.setFrom((float) onTimePercentage / 100)
			.setTo(1);
	}

	public NumericFilter getDeliverableOnTimePercentageFilter() {
		if (deliverableOnTimePercentage == null) { return null; }
		return new NumericFilter()
			.setFrom((float) deliverableOnTimePercentage / 100)
			.setTo(1);
	}
}
