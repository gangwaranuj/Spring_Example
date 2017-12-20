package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="work_resource_status_type")
@Table(name="work_resource_status_type")
public class WorkResourceStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String OPEN = "open";
	public static final String ACCEPTED = "accepted";
	public static final String DECLINED = "declined";
	public static final String ACTIVE = "active";
	public static final String CANCELLED = "cancelled";
	public static final String UNASSIGNED = "unassigned"; // Unassigned by buyer
	@Deprecated
	public static final String DELEGATED = "delegated";
	public static final String ASSIGNMENT_CANCELLED = "workCancel";

	public static WorkResourceStatusType OPEN_STATUS, ACCEPTED_STATUS, DECLINED_STATUS, CANCELLED_STATUS, UNASSIGNED_STATUS, DELEGATED_STATUS, ACTIVE_STATUS, ASSIGNMENT_CANCELLED_STATUS;

	static {
		WorkResourceStatusType.OPEN_STATUS = new WorkResourceStatusType(OPEN);
		WorkResourceStatusType.ACCEPTED_STATUS = new WorkResourceStatusType(ACCEPTED);
		WorkResourceStatusType.DECLINED_STATUS = new WorkResourceStatusType(DECLINED);
		WorkResourceStatusType.CANCELLED_STATUS = new WorkResourceStatusType(CANCELLED);
		WorkResourceStatusType.UNASSIGNED_STATUS = new WorkResourceStatusType(UNASSIGNED);
		WorkResourceStatusType.DELEGATED_STATUS = new WorkResourceStatusType(DELEGATED);
		WorkResourceStatusType.ACTIVE_STATUS = new WorkResourceStatusType(ACTIVE);
		WorkResourceStatusType.ASSIGNMENT_CANCELLED_STATUS = new WorkResourceStatusType(ASSIGNMENT_CANCELLED);
	}
	public WorkResourceStatusType() {
	}

	public WorkResourceStatusType(String code) {
		super(code);
	}
}
