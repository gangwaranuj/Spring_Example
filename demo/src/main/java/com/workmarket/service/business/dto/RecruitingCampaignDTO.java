package com.workmarket.service.business.dto;

public class RecruitingCampaignDTO {
	private Long recruitingCampaignId;
	private String recruitingVendorCode;
	private Long companyId;
	private String title;
	private String description;
	private String companyOverview;
	private Long companyUserGroupId;
	private Long companyLogoAssetId;
	private boolean privateCampaign;

	public Long getRecruitingCampaignId() {
		return recruitingCampaignId;
	}

	public void setRecruitingCampaignId(Long recruitingCampaignId) {
		this.recruitingCampaignId = recruitingCampaignId;
	}

	public String getRecruitingVendorCode() {
		return recruitingVendorCode;
	}

	public void setRecruitingVendorId(String recruitingVendorCode) {
		this.recruitingVendorCode = recruitingVendorCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompanyOverview() {
		return companyOverview;
	}

	public void setCompanyOverview(String companyOverview) {
		this.companyOverview = companyOverview;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCompanyUserGroupId() {
		return companyUserGroupId;
	}

	public void setCompanyUserGroupId(Long companyUserGroupId) {
		this.companyUserGroupId = companyUserGroupId;
	}

	public Long getCompanyLogoAssetId() {
		return companyLogoAssetId;
	}

	public void setCompanyLogoAssetId(Long companyLogoAssetId) {
		this.companyLogoAssetId = companyLogoAssetId;
	}

	public boolean isPrivateCampaign() {
		return privateCampaign;
	}

	public void setPrivateCampaign(boolean privateCampaign) {
		this.privateCampaign = privateCampaign;
	}
}
