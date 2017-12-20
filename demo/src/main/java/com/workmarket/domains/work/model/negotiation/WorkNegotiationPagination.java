package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkNegotiationPagination extends AbstractPagination<AbstractWorkNegotiation> implements Pagination<AbstractWorkNegotiation> {
	public enum FILTER_KEYS {
		APPROVAL_STATUS,
		EXPIRED
	}
	public enum SORTS {}
	
	public WorkNegotiationPagination() {}
	public WorkNegotiationPagination(Boolean returnAllRows) {
		super(returnAllRows);
	}
}
