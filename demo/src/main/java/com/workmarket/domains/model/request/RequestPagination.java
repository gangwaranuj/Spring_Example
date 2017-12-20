package com.workmarket.domains.model.request;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class RequestPagination extends AbstractPagination<Request> implements Pagination<Request> {
	
	public enum FILTER_KEYS {}
	public enum SORTS {}
	
	public RequestPagination() {}
	public RequestPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}
