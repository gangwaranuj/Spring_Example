package com.workmarket.domains.forums.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

import java.util.List;

public class ForumFlaggedPostPagination extends AbstractPagination<FlaggedPostStatistics> implements Pagination<FlaggedPostStatistics> {

	public static final List<String> COLUMNS = ImmutableList.of("Post", "Post Creator", "First Reported", "# of Flags", "Actions");

	public static final ImmutableMap<Integer,String> SORTABLE_COLUMNS = ImmutableMap.of(
			ForumFlaggedPostPagination.COLUMNS.indexOf("First Reported"), "createdOn",
			ForumFlaggedPostPagination.COLUMNS.indexOf("# of Flags"), "count");

	public ForumFlaggedPostPagination() {}

	public ForumFlaggedPostPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

}
