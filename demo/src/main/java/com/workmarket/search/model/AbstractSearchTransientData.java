package com.workmarket.search.model;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.search.request.SearchRequest;

public abstract class AbstractSearchTransientData {

	private SearchType searchType;
	private SearchUser currentUser;// the user performing the search
	private SearchRequest originalRequest;
	private GeoPoint point;

	protected AbstractSearchTransientData() {}

	public SearchUser getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(SearchUser currentUser) {
		this.currentUser = currentUser;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public SearchRequest getOriginalRequest() {
		return originalRequest;
	}

	public void setOriginalRequest(SearchRequest originalRequest) {
		this.originalRequest = originalRequest;
	}

	public GeoPoint getGeopoint() {
		return point;
	}

	public void setGeopoint(GeoPoint point) {
		this.point = point;
	}

	public boolean isSetGeopoint() {
		return point != null;
	}
}
