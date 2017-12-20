package com.workmarket.domains.model.certification;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserCertificationAssociationPagination extends AbstractPagination<UserCertificationAssociation> implements Pagination<UserCertificationAssociation> {

	public UserCertificationAssociationPagination() {}
	
	public UserCertificationAssociationPagination(boolean returnAllRows) { super(returnAllRows); }
	
	public enum FILTER_KEYS { VERIFICATION_STATUS, USER_NAME, USER_ID, WITH_ASSETS }

	public enum ASSETS { YES, NO }
	
	public enum SORTS { USER_ID, VERIFICATION_STATUS, CREATED_DATE, USER_FIRST_NAME, USER_LAST_NAME, VENDOR_NAME , ISSUE_DATE, EXPIRATION_DATE, LAST_ACTIVITY_DATE }
}
