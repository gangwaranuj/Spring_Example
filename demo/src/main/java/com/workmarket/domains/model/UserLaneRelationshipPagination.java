package com.workmarket.domains.model;

public class UserLaneRelationshipPagination extends AbstractPagination<UserLaneRelationship> implements Pagination<UserLaneRelationship> {

	public UserLaneRelationshipPagination() {
    }

    public UserLaneRelationshipPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS {
	}
	
	public enum SORTS {		
		TOTAL_ASSIGNMENTS
	}
}
