package com.workmarket.service.infra.security;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.lane.LaneType;

public class LaneContext {
	private LaneType laneType = LaneType.LANE_0;
	private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;

	public LaneContext(LaneType laneType, ApprovalStatus approvalStatus) {
		this.laneType = laneType;
		this.approvalStatus = approvalStatus;
	}
	
	public LaneType getLaneType() {
		return this.laneType;
	}
	public void setLaneType(LaneType laneType) {
		this.laneType = laneType;
	}
	
	public ApprovalStatus getApprovalStatus() {
		return this.approvalStatus;
	}
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	
	public boolean isInWorkerPool() {
		return approvalStatus.isApproved() && (
			laneType.equals(LaneType.LANE_0) ||
			laneType.equals(LaneType.LANE_1) ||
			laneType.equals(LaneType.LANE_2) ||
			laneType.equals(LaneType.LANE_3)
		);
	}
}