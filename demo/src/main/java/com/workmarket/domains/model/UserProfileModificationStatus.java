package com.workmarket.domains.model;

public enum UserProfileModificationStatus {

	PENDING_APPROVAL("Pending Approval"),    
    APPROVED("Approved"),
    REJECTED("Rejected");
    
	@SuppressWarnings("unused")
	private String displayName;

	UserProfileModificationStatus(String displayName) {
        this.displayName = displayName;
    }
    
}

