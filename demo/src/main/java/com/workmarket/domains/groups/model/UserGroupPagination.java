package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserGroupPagination extends AbstractPagination<UserGroup> implements Pagination<UserGroup> {
	public enum FILTER_KEYS {
		IS_ACTIVE
	}

	public enum SORTS {}

	public UserGroupPagination() {}
	public UserGroupPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}
