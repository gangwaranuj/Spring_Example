package com.workmarket.domains.model.skill;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;


public class UserSkillAssociationPagination extends AbstractPagination<UserSkillAssociation> implements Pagination<UserSkillAssociation> {

	public UserSkillAssociationPagination() {
	}

	public UserSkillAssociationPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
	}

	public enum SORTS {
	}
}
