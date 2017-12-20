package com.workmarket.domains.model;

public enum ApprovalStatus {
	PENDING("Pending"),
	APPROVED("Approved"),
	DECLINED("Declined"),
	NOT_READY("Not ready"),
	OPT_OUT("Opt-Out"),
	PENDING_REMOVAL("Pending removal"),
	REMOVED("Removed");

	@SuppressWarnings("unused")
	private String displayName;

	ApprovalStatus(String displayName) {
		this.displayName = displayName;
	}

	public static ApprovalStatus lookupByCode(Integer statusCode) {
		switch (statusCode) {
		case 0:
			return PENDING;
		case 1:
			return APPROVED;
		case 2:
			return DECLINED;
		case 3:
			return NOT_READY;
		case 4:
			return OPT_OUT;
		case 5:
			return PENDING_REMOVAL;
		case 6:
			return REMOVED;
		default:
			return null;
		}
	}

	public boolean isApproved() {
		return APPROVED.equals(this);
	}

	public boolean isDeclined() {
		return DECLINED.equals(this);
	}

	public boolean isPending() {
		return PENDING.equals(this);
	}

	public int getCode() {
		return this.ordinal();
	}
}
