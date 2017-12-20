package com.workmarket.domains.model.changelog.user;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserChangeLogPagination extends AbstractPagination<UserChangeLog> implements Pagination<UserChangeLog> {

	public UserChangeLogPagination() {
	}

	public UserChangeLogPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		MASQUERADED
	}

	public enum SORTS {
		ID,
		TYPE,
		USER_LAST_NAME,
		CREATED_ON,
		ACTOR_LAST_NAME,
		MASQUERADE_ACTOR_LAST_NAME,
		OLD_VALUE,
		NEW_VALUE;
	}
}
