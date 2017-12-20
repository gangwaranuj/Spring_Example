package com.workmarket.domains.model.notification;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserNotificationPagination extends AbstractPagination<UserNotification> implements Pagination<UserNotification> {
	
	private Boolean includeAvatars = Boolean.FALSE;
	private boolean includeFullCount = true;            // if false, don't run the full count query

	public UserNotificationPagination(){}
	public UserNotificationPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public static UserNotificationPagination newBullhornPagination() {
		UserNotificationPagination pagination = new UserNotificationPagination();
		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.ARCHIVED, Boolean.FALSE);
		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.USER_FILTERS, Boolean.FALSE);
		pagination.setSortColumn(UserNotificationPagination.SORTS.CREATED_ON);
		pagination.setSortDirection(UserNotificationPagination.SORT_DIRECTION.DESC);
		pagination.setResultsLimit(7);
		return pagination;
	}
	
	public enum FILTER_KEYS {
		ARCHIVED,
		NOTIFICATION_TYPE,
		LAST_30_DAYS,
		USER_FILTERS
	}
	
	public enum SORTS {
		DESCRIPTION, 
		CREATED_ON
	}
	
	public Boolean getIncludeAvatars() {
		return includeAvatars;
	}
	
	public void setIncludeAvatars(Boolean includeAvatars) {
		this.includeAvatars = includeAvatars;
	}

	public boolean isIncludeFullCount() {
		return includeFullCount;
	}

	public void setIncludeFullCount(boolean includeFullCount) {
		this.includeFullCount = includeFullCount;
	}
}
