package com.workmarket.domains.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum VerificationStatus {
	PENDING("Pending"),
	VERIFIED("Verified"),
	FAILED("Failed"),
	ON_HOLD("Hold"),
	PENDING_INFORMATION("Pending Information"),
	UNVERIFIED("Unverified");

	public static List<VerificationStatus> UNVERIFIED_STATUSES = ImmutableList.of(
			PENDING,
			ON_HOLD,
			PENDING_INFORMATION);

	private String displayName;

	VerificationStatus(String displayName) {
		this.displayName = displayName;
	}

	public boolean isPending() {
		return VerificationStatus.PENDING.equals(this);
	}

	public boolean isVerified() {
		return VerificationStatus.VERIFIED.equals(this);
	}

	public boolean isFailed() {
		return VerificationStatus.FAILED.equals(this);
	}

	public boolean isOnHold() {
		return VerificationStatus.ON_HOLD.equals(this);
	}

	public int getCode() {
		return this.ordinal();
	}
}
