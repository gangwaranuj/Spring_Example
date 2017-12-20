package com.workmarket.service.infra.security;

public enum WorkContext {

	OWNER(1L, "owner", "User is the buyer of the assignment."),
	ASSIGNED_COMPANY(2L, "assignedCompany", "User belongs to the assignment's active resource company."),
	INVITED_COMPANY(3L, "invitedCompany", "User belongs to the assignment's invited resource company."), // TODO: Not sure if we need this.
	INVITED(4L, "invited", "User is an invited resource."),
	ACTIVE_RESOURCE(5L, "active", "User is the assigned resource."),
	INVITED_INACTIVE(6L, "inactive", "User is an invited resource but he isn't the active resource."),
	UNRELATED(7L, "unrelated", "User doesn't have any relationship with the assignment."),
	COMPANY_OWNED(8L, "ownerCompany", "User belongs to the assignment's owner company and is not the owner."),
	CANCELLED_RESOURCE(9L, "cancelledResource", "User was a resource of the assignment and cancelled his participation."),
	DECLINED_RESOURCE(10L, "declinedResource", "User was a resource of the assignment and declined his participation."),
	WORK_MARKET_INTERNAL(11L, "wmInternal", "User who belongs to Wok Market"),
	DISPATCHER(12L, "dispatcher", "User is dispatcher of active resource's company");
	
	private long id;
	private String code;
	private String description;

	WorkContext(long id, String code, String description) {
		this.id = id;
		this.code = code;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

}
