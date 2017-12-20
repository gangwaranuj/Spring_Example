package com.workmarket.domains.model.changelog.profile;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ProfileChangeLogPagination extends AbstractPagination<ProfileChangeLog> implements Pagination<ProfileChangeLog> {

	public ProfileChangeLogPagination() {
	}

	public ProfileChangeLogPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		MASQUERADED
	}

	public enum SORTS {
		ID,
		USER_ID,
		TYPE,
		USER_LAST_NAME,
		CREATED_ON,
		ACTOR_LAST_NAME,
		MASQUERADE_ACTOR_LAST_NAME,
		OLD_VALUE,
		NEW_VALUE;
	}
}
