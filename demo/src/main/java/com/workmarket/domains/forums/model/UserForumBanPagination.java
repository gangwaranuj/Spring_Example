package com.workmarket.domains.forums.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

import java.util.List;
import java.util.Map;

public class UserForumBanPagination extends AbstractPagination<UserForumBan> implements Pagination<UserForumBan> {

	public static final List<String> COLUMNS =  ImmutableList.of("User", "Reason", "Date Banned", "Actions");

	public static final Map<Integer,String> SORTABLE_COLUMNS = ImmutableMap.of(
		UserForumBanPagination.COLUMNS.indexOf("User"), "user.firstName",
		UserForumBanPagination.COLUMNS.indexOf("Reason"), "reason",
		UserForumBanPagination.COLUMNS.indexOf("Date Banned"), "createdOn"
	);

	public enum FILTER_KEYS { NAME_REASON }

	public UserForumBanPagination() {}

	public UserForumBanPagination(boolean returnAllRows) {
		super(returnAllRows);
	}


}
