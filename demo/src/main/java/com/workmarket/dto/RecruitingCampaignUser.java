package com.workmarket.dto;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.VerificationStatus;

import java.util.Calendar;

public class RecruitingCampaignUser extends AbstractCustomUserEntity {

	private Calendar registrationDate;
	private Long recruitingCampaignId;
	private String recruitingCampaignTitle;
	private boolean recruitingCampaignActive;
	private Calendar recruitingCampaignDate;
	private Long userGroupId;
	private ApprovalStatus groupApprovalStatus;
	private VerificationStatus groupVerificationStatus;
	private ApprovalStatus laneApprovalStatus;
	private VerificationStatus laneVerificationStatus;
	private boolean emailConfirmed;

	public Calendar getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Calendar registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Long getRecruitingCampaignId() {
		return recruitingCampaignId;
	}

	public void setRecruitingCampaignId(Long recruitingCampaignId) {
		this.recruitingCampaignId = recruitingCampaignId;
	}

	public String getRecruitingCampaignTitle() {
		return recruitingCampaignTitle;
	}

	public void setRecruitingCampaignTitle(String recruitingCampaignTitle) {
		this.recruitingCampaignTitle = recruitingCampaignTitle;
	}

	public boolean isRecruitingCampaignActive() {
		return recruitingCampaignActive;
	}

	public void setRecruitingCampaignActive(boolean recruitingCampaignActive) {
		this.recruitingCampaignActive = recruitingCampaignActive;
	}

	public Calendar getRecruitingCampaignDate() {
		return recruitingCampaignDate;
	}

	public void setRecruitingCampaignDate(Calendar recruitingCampaignDate) {
		this.recruitingCampaignDate = recruitingCampaignDate;
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public ApprovalStatus getGroupApprovalStatus() {
		return groupApprovalStatus;
	}

	public void setGroupApprovalStatus(ApprovalStatus groupApprovalStatus) {
		this.groupApprovalStatus = groupApprovalStatus;
	}

	public void setGroupApprovalStatus(Long groupApprovalStatus) {
		if (groupApprovalStatus != null)
			this.groupApprovalStatus = ApprovalStatus.values()[groupApprovalStatus.intValue()];
	}

	public VerificationStatus getGroupVerificationStatus() {
		return groupVerificationStatus;
	}

	public void setGroupVerificationStatus(VerificationStatus groupVerificationStatus) {
		this.groupVerificationStatus = groupVerificationStatus;
	}

	public void setGroupVerificationStatus(Long groupVerificationStatus) {
		if (groupVerificationStatus != null)
			this.groupVerificationStatus = VerificationStatus.values()[groupVerificationStatus.intValue()];

	}

	public ApprovalStatus getLaneApprovalStatus() {
		return laneApprovalStatus;
	}

	public void setLaneApprovalStatus(ApprovalStatus laneApprovalStatus) {
		this.laneApprovalStatus = laneApprovalStatus;
	}

	public void setLaneApprovalStatus(Long laneApprovalStatus) {
		if (laneApprovalStatus != null)
			this.laneApprovalStatus = ApprovalStatus.values()[laneApprovalStatus.intValue()];
	}

	public VerificationStatus getLaneVerificationStatus() {
		return laneVerificationStatus;
	}

	public void setLaneVerificationStatus(VerificationStatus laneVerificationStatus) {
		this.laneVerificationStatus = laneVerificationStatus;
	}

	public void setLaneVerificationStatus(Long laneVerificationStatus) {
		if (laneVerificationStatus != null)
			this.laneVerificationStatus = VerificationStatus.values()[laneVerificationStatus.intValue()];
	}

	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}

}
