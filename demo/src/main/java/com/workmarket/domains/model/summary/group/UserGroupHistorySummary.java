package com.workmarket.domains.model.summary.group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.summary.HistorySummaryEntity;

@Entity(name = "userGroupHistorySummary")
@Table(name = "user_group_association_history_summary")
public class UserGroupHistorySummary extends HistorySummaryEntity {

	private static final long serialVersionUID = 3052432019532178984L;
	
	public static final String APPROVED_USER_GROUP_ASSOCIATION_STATUS = "approved";
	public static final String DELETED_USER_GROUP_ASSOCIATION_STATUS = "deleted";
	public static final String PENDING_USER_GROUP_ASSOCIATION_STATUS = "pending";
	public static final String DECLINED_USER_GROUP_ASSOCIATION_STATUS = "declined";

	private Long groupId;
	private Long userId;
	private Long userCompanyId;
	private Long groupCompanyId;
	private Long groupIndustryId;
	private String userGroupAssociationStatusTypeCode;
	private Integer approvalStatus;
	private Integer verificationStatus;
	private Double requirementsFitScore;
	private boolean overrideMember;

	@Column(name = "group_id", nullable = false, length = 11)
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Column(name = "user_id", nullable = false, length = 11)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "user_company_id", nullable = false, length = 11)
	public Long getUserCompanyId() {
		return userCompanyId;
	}

	public void setUserCompanyId(Long userCompanyId) {
		this.userCompanyId = userCompanyId;
	}

	@Column(name = "group_company_id", nullable = false, length = 11)
	public Long getGroupCompanyId() {
		return groupCompanyId;
	}

	public void setGroupCompanyId(Long groupCompanyId) {
		this.groupCompanyId = groupCompanyId;
	}

	@Column(name = "group_industry_id")
	public Long getGroupIndustryId() {
		return groupIndustryId;
	}

	public void setGroupIndustryId(Long groupIndustryId) {
		this.groupIndustryId = groupIndustryId;
	}

	@Column(name = "user_group_association_status_type_code", nullable = false, length = 15)
	public String getUserGroupAssociationStatusTypeCode() {
		return userGroupAssociationStatusTypeCode;
	}

	public void setUserGroupAssociationStatusTypeCode(String userGroupAssociationStatusTypeCode) {
		this.userGroupAssociationStatusTypeCode = userGroupAssociationStatusTypeCode;
	}

	@Column(name = "approval_status", nullable = false)
	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	@Column(name = "verification_status", nullable = false)
	public Integer getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(Integer verificationStatus) {
		this.verificationStatus = verificationStatus;
	}

	@Column(name = "requirements_fit_score", nullable = false)
	public Double getRequirementsFitScore() {
		return requirementsFitScore;
	}

	public void setRequirementsFitScore(Double requirementsFitScore) {
		this.requirementsFitScore = requirementsFitScore;
	}

	@Column(name = "override_member", nullable = false)
	public boolean isOverrideMember() {
		return overrideMember;
	}

	public void setOverrideMember(boolean overrideMember) {
		this.overrideMember = overrideMember;
	}

}