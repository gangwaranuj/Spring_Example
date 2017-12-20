package com.workmarket.domains.model.license;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserLicenseAssociationPagination extends AbstractPagination<UserLicenseAssociation> implements Pagination<UserLicenseAssociation> {

	public UserLicenseAssociationPagination() {}

	public UserLicenseAssociationPagination(boolean returnAllRows) { super(returnAllRows); }

	public enum FILTER_KEYS { VERIFICATION_STATUS, USER_NAME }

	public enum SORTS { VERIFICATION_STATUS, CREATED_DATE, STATE, USER_FIRST_NAME, USER_LAST_NAME, LICENSE_NAME, LICENSE_NUMBER, LAST_ACTIVITY_DATE }
}
