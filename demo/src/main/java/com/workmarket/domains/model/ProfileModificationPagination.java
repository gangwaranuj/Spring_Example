package com.workmarket.domains.model;

public class ProfileModificationPagination extends AbstractPagination<UserProfileModification> implements Pagination<UserProfileModification> {
	
	public enum FILTER_KEYS { NAME }
	public enum SORTS {MODIFIED_DATE, NAME, COMPANY_NAME }
	
}

