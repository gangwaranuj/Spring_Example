package com.workmarket.data.solr.model;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.data.solr.repository.UserSearchableFields;

public class SolrCompanyLaneData {
	private long companyId;
	private String companyUuid;
	private ApprovalStatus approvalStatus;
	private LaneType laneType;
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public LaneType getLaneType() {
		return laneType;
	}
	public void setLaneType(LaneType laneType) {
		this.laneType = laneType;
	}

	public String getCompanyUuid() {
		return companyUuid;
	}

	public void setCompanyUuid(final String companyUuid) {
		this.companyUuid = companyUuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((approvalStatus == null) ? 0 : approvalStatus.hashCode());
		result = prime * result + (int) (companyId ^ (companyId >>> 32));
		result = prime * result
				+ ((laneType == null) ? 0 : laneType.hashCode());
		result = prime * result + ((companyUuid == null) ? 0 : companyUuid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrCompanyLaneData other = (SolrCompanyLaneData) obj;
		if (approvalStatus != other.approvalStatus)
			return false;
		if (companyId != other.companyId)
			return false;
		if (laneType != other.laneType)
			return false;
		if (!companyUuid.equals(other.companyUuid))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SolrCompanyLaneData [companyId=" + companyId
				+ ", approvalStatus=" + approvalStatus + ", laneType="
				+ laneType + "]";
	}
	
	public int laneNumber() {
		switch(laneType) {
		case LANE_0:
			return 0;
		case LANE_1:
			return 1;
		case LANE_2:
			return 2;
		case LANE_3:
			return 3;
		case LANE_4:
			return 4;
		default:
			return -100000;
		}
	}
	
	public String laneName() {
		switch(laneType) {
		case LANE_0:
			return "lane0";
		case LANE_1:
			return "lane1";
		case LANE_2:
			return "lane2";
		case LANE_3:
			return "lane3";
		case LANE_4:
			return "lane4";
		default:
			return "mysteriosoLane";
		}
	}
	
	public UserSearchableFields companyLaneField() {
		switch(laneType) {
			case LANE_0:
				return UserSearchableFields.LANE0_COMPANY_IDS;
			case LANE_1:
				return UserSearchableFields.LANE1_COMPANY_IDS;
			case LANE_2:
				return UserSearchableFields.LANE2_COMPANY_IDS;
			case LANE_3:
				return UserSearchableFields.LANE3_COMPANY_IDS;
			case LANE_4:
			default:
				return null;
		}
	}

	public UserSearchableFields companyLaneUuidField() {
		switch(laneType) {
			case LANE_0:
				return UserSearchableFields.LANE0_COMPANY_UUIDS;
			case LANE_1:
				return UserSearchableFields.LANE1_COMPANY_UUIDS;
			case LANE_2:
				return UserSearchableFields.LANE2_COMPANY_UUIDS;
			case LANE_3:
				return UserSearchableFields.LANE3_COMPANY_UUIDS;
			case LANE_4:
			default:
				return null;
		}
	}
	
}

