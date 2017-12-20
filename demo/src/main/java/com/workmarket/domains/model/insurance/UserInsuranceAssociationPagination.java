package com.workmarket.domains.model.insurance;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserInsuranceAssociationPagination extends AbstractPagination<UserInsuranceAssociation> implements Pagination<UserInsuranceAssociation> {
	
	public UserInsuranceAssociationPagination() { super(false); }
	
	public UserInsuranceAssociationPagination(boolean returnAllRows) { super(returnAllRows); }
	
	public enum FILTER_KEYS { USER_ID, INSURANCE_ID, VERIFICATION_STATUS, USER_NAME }
	
	public enum SORTS { CREATED_DATE,USER_FIRST_NAME, USER_LAST_NAME, VERIFICATION_STATUS, PROVIDER, ISSUE_DATE, EXPIRATION_DATE, LAST_ACTIVITY_DATE }
}

