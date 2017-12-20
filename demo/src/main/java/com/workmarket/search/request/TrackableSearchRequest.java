package com.workmarket.search.request;

import com.workmarket.search.request.user.PeopleSearchRequest;

import java.io.Serializable;

public abstract class TrackableSearchRequest implements Serializable {

	private static final long serialVersionUID = -532357577038380130L;
	public static final String GROUP_REQUEST = "GROUP";
	public static final String ASSIGNMENT_REQUEST = "WORK";

	public abstract String getRequestType();

	private PeopleSearchRequest request;

	protected TrackableSearchRequest() {
	}

	protected TrackableSearchRequest(PeopleSearchRequest request) {
		this.request = request;
	}

	public PeopleSearchRequest getRequest() {
		return request;
	}

	public TrackableSearchRequest setRequest(PeopleSearchRequest request) {
		this.request = request;
		return this;
	}

	public boolean isSetRequest() {
		return this.request != null;
	}
}
