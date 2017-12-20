package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserUserGroupAssociationPagination extends AbstractPagination<UserUserGroupAssociation> implements Pagination<UserUserGroupAssociation> {
	public enum FILTER_KEYS {
		INVITED
	}
	public enum SORTS {}
}
