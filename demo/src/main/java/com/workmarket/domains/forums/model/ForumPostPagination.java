package com.workmarket.domains.forums.model;


import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ForumPostPagination extends AbstractPagination<ForumPost> implements Pagination<ForumPost> {
	private static final int FORUM_MAX_ROWS = 1000; //may need to be increased if necessary

	public ForumPostPagination() {}

	public ForumPostPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	@Override
	public Integer getResultsLimit() {
		return super.isLimitMaxRows() ? super.getResultsLimit() : FORUM_MAX_ROWS;
	}
}
