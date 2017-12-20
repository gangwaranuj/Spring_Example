package com.workmarket.domains.model;

import com.google.common.collect.ImmutableList;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity(name = "user_status_type")
@Table(name = "user_status_type")
public class UserStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String PENDING = "pending";
	public static final String DELETED = "deleted";
	public static final String APPROVED = "approved";
	public static final String SUSPENDED = "suspended";
	public static final String DEACTIVATED = "deactivate";
	public static final String HOLD = "hold";
	public static final String LOCKED = "locked";

	public static final UserStatusType APPROVED_STATUS = new UserStatusType(APPROVED);
	public static final UserStatusType PENDING_STATUS = new UserStatusType(PENDING);
	public static final UserStatusType LOCKED_STATUS = new UserStatusType(LOCKED);

	public static final List<String> ACTIVE_USER_STATUS_TYPES = ImmutableList.of(
		PENDING,
		APPROVED,
		LOCKED);

	public static final List<String> INACTIVE_USER_STATUS_TYPES = ImmutableList.of(
		DEACTIVATED,
		DELETED,
		HOLD,
		SUSPENDED);

	public UserStatusType() {
		super();
	}

	public UserStatusType(String code) {
		super(code);
	}

	@Transient
	public boolean isLocked() {
		return UserStatusType.LOCKED.equals(getCode());
	}

	@Transient
	public boolean isPending() {
		return UserStatusType.PENDING.equals(getCode());
	}

	@Transient
	public boolean isDeleted() {
		return UserStatusType.DELETED.equals(getCode());
	}

	@Transient
	public boolean isSuspended() {
		return UserStatusType.SUSPENDED.equals(getCode());
	}

	@Transient
	public boolean isDeactivated() {
		return UserStatusType.DEACTIVATED.equals(getCode());
	}

	@Transient
	public boolean isHold() {
		return UserStatusType.HOLD.equals(getCode());
	}

	@Transient
	public boolean isApproved() {
		return UserStatusType.APPROVED.equals(getCode());
	}

	@Transient
	public boolean isInactiveStatus() {
		return INACTIVE_USER_STATUS_TYPES.contains(getCode());
	}
}