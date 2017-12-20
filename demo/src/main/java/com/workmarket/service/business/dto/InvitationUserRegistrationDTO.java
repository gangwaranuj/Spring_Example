package com.workmarket.service.business.dto;

import com.workmarket.dto.AddressDTO;
import com.workmarket.utility.ArrayUtilities;
import org.apache.commons.beanutils.ConvertUtils;

import java.math.BigDecimal;

public class InvitationUserRegistrationDTO extends AddressDTO {

	private static final long serialVersionUID = -3054142615171691223L;

	private String resumeFilePath;
	private String linkedInProfileId;
	private String currentEmployer;
	private String firstName;
	private String lastName;
	private String workPhone;
	private String workPhoneExtension;
	private String email;
	private String password;
	private String over21;
	private String agree;
	private String country;
	private String workPhoneInternationalCode;
	private String companyName;
	private Boolean operatingAsIndividualFlag = Boolean.FALSE;
	private Long invitationId;
	private Long campaignId;
	private Long[] industryIds;
	private String resumeUploadUUID;
	private Boolean manageWork = Boolean.TRUE;
	private Boolean findWork = Boolean.TRUE;
	private Boolean linkedin = Boolean.FALSE;
	private String sessionId;
	private Long industryId;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String planCode;
	private String networkId;

	private Integer warpRequisitionId;

	public Integer getWarpRequisitionId() {
		return warpRequisitionId;
	}

	public void setWarpRequisitionId(Integer warpRequisitionId) {
		this.warpRequisitionId = warpRequisitionId;
	}

	private boolean notifyUser = true;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public void setWorkPhoneInternationalCode(String workPhoneInternationalCode) {
		this.workPhoneInternationalCode = workPhoneInternationalCode;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public String getResumeFilePath() {
		return resumeFilePath;
	}

	public void setResumeFilePath(String resumeFilePath) {
		this.resumeFilePath = resumeFilePath;
	}

	public String getLinkedInProfileId() {
		return linkedInProfileId;
	}

	public void setLinkedInProfileId(String linkedInProfileId) {
		this.linkedInProfileId = linkedInProfileId;
	}

	public String getCurrentEmployer() {
		return currentEmployer;
	}

	public void setCurrentEmployer(String currentEmployer) {
		this.currentEmployer = currentEmployer;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOver21() {
		return over21;
	}

	public void setOver21(String over21) {
		this.over21 = over21;
	}

	public String getAgree() {
		return agree;
	}

	public void setAgree(String agree) {
		this.agree = agree;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Boolean getOperatingAsIndividualFlag() {
		return operatingAsIndividualFlag;
	}

	public void setOperatingAsIndividualFlag(Boolean operatingAsIndividualFlag) {
		this.operatingAsIndividualFlag = operatingAsIndividualFlag;
	}

	public Long getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(Long invitationId) {
		this.invitationId = invitationId;
	}

	public Long[] getIndustryIds() {
		return industryIds;
	}

	public void setIndustryIds(Long[] industryIds) {
		this.industryIds = industryIds;
	}

	public void setIndustryIds(Integer[] industryIds) {
		setIndustryIds(ArrayUtilities.convertToLongArrays(industryIds));
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public String getResumeUploadUUID() {
		return resumeUploadUUID;
	}

	public void setResumeUploadUUID(String resumeUploadUUID) {
		this.resumeUploadUUID = resumeUploadUUID;
	}

	public Boolean getManageWork() {
		return manageWork;
	}

	public void setManageWork(Boolean manageWork) {
		this.manageWork = manageWork;
	}

	public Boolean getFindWork() {
		return findWork;
	}

	public void setFindWork(Boolean findWork) {
		this.findWork = findWork;
	}

	public Boolean getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = (Boolean) ConvertUtils.convert(linkedin, Boolean.class);
	}

	public void setLinkedin(Boolean linkedin) {
		this.linkedin = linkedin;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getPlanCode() {
		return planCode;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public boolean isNotifyUser() {
		return notifyUser;
	}

	public void setNotifyUser(boolean notifyUser) {
		this.notifyUser = notifyUser;
	}
}
