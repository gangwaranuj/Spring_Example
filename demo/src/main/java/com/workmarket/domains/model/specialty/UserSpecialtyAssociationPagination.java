package com.workmarket.domains.model.specialty;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class UserSpecialtyAssociationPagination extends AbstractPagination<UserSpecialtyAssociation> implements Pagination<UserSpecialtyAssociation> {
	public UserSpecialtyAssociationPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
	}

	public enum SORTS {
	}
}
