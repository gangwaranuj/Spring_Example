package com.workmarket.domains.model;

/**
 * Author: rocio
 */
public class EntityIdPagination extends AbstractPagination<Long> implements Pagination<Long> {

	private boolean skipTotalCount;

	public EntityIdPagination() {
	}

	public EntityIdPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public boolean isSkipTotalCount() {
		return skipTotalCount;
	}

	public void setSkipTotalCount(boolean skipTotalCount) {
		this.skipTotalCount = skipTotalCount;
	}
}

