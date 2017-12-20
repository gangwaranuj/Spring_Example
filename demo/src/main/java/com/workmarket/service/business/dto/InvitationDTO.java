package com.workmarket.service.business.dto;

import com.workmarket.domains.model.InvitationType;

public class InvitationDTO {

	private String firstName;
	private String lastName;
	private String email;
	private String relationshipTypeCode;
	private String message;
	private String companyOverview;
	private String showCompanyLogo;
	private String showCompanyDescription;
	private InvitationType invitationType;
	private Long companyLogoAssetId;
	private long inviterUserId;
	private long invitingCompanyId;
	private Long recruitingCampaignId;

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getRelationshipTypeCode() {
		return relationshipTypeCode;
	}

	public String getMessage() {
		return message;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRelationshipTypeCode(String relationshipTypeCode) {
		this.relationshipTypeCode = relationshipTypeCode;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCompanyOverview() {
		return companyOverview;
	}

	public void setCompanyOverview(String companyOverview) {
		this.companyOverview = companyOverview;
	}

	public String getShowCompanyLogo() {
		return showCompanyLogo;
	}

	public void setShowCompanyLogo(String showCompanyLogo) {
		this.showCompanyLogo = showCompanyLogo;
	}

	public String getShowCompanyDescription() {
		return showCompanyDescription;
	}

	public void setShowCompanyDescription(String showCompanyDescription) {
		this.showCompanyDescription = showCompanyDescription;
	}

	public InvitationType getInvitationType() {
		return invitationType;
	}

	public void setInvitationType(InvitationType invitationType) {
		this.invitationType = invitationType;
	}

	public Long getCompanyLogoAssetId() {
		return companyLogoAssetId;
	}

	public void setCompanyLogoAssetId(Long companyLogoAssetId) {
		this.companyLogoAssetId = companyLogoAssetId;
	}

	public long getInvitingCompanyId() {
		return invitingCompanyId;
	}

	public void setInvitingCompanyId(long invitingCompanyId) {
		this.invitingCompanyId = invitingCompanyId;
	}

	public Long getRecruitingCampaignId() {
		return recruitingCampaignId;
	}

	public void setRecruitingCampaignId(Long recruitingCampaignId) {
		this.recruitingCampaignId = recruitingCampaignId;
	}

	public long getInviterUserId() {
		return inviterUserId;
	}

	public void setInviterUserId(long inviterUserId) {
		this.inviterUserId = inviterUserId;
	}

}
