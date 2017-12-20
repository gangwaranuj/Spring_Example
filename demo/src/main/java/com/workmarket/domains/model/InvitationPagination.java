package com.workmarket.domains.model;

public class InvitationPagination extends AbstractPagination<Invitation> implements
		Pagination<Invitation> {
	
	public enum FILTER_KEYS {
		   SINCE, BEFORE, USER_TYPE, USER_STATUS
	}
	
	public enum SORTS {
		   FIRST_NAME, LAST_NAME, EMAIL, INVITATION_DATE, LAST_REMINDER_DATE, USER_TYPE, USER_STATUS
	}
}
