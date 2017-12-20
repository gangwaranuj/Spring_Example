package com.workmarket.service.business.dto;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Profile;
import com.workmarket.dto.AddressDTO;
import com.workmarket.utility.BeanUtilities;

import java.math.BigDecimal;
import java.util.List;

public class ProfileDTO extends AddressDTO {

	private static final long serialVersionUID = 8266878216017908655L;

	private Long profileId;
	private String firstName;
	private String lastName;
	private String jobTitle;
	private String email;
	private String workPhone;
	private String workPhoneExtension;
	private String mobilePhone;
	private boolean useCompanyAddress;

	private Long workPhoneInternationalCode;
	private String workPhoneInternationalCallingCodeId;
	private Long mobilePhoneInternationalCode;
	private String mobilePhoneInternationalCallingCodeId;
	private Long smsPhoneInternationalCode;

	private String smsPhone;
	private Boolean smsPhoneVerified;
	private String overview;
	private BigDecimal maxTravelDistance = Constants.DEFAULT_MAX_TRAVEL_DISTANCE;
	private BigDecimal minOnsiteHourlyRate = BigDecimal.valueOf(0.0);
	private BigDecimal minOnsiteWorkPrice = BigDecimal.valueOf(0.0);
	private BigDecimal minOffsiteHourlyRate = BigDecimal.valueOf(0.0);
	private BigDecimal minOffsiteWorkPrice = BigDecimal.valueOf(0.0);
	private String associations;
	private String specialties;
	private Boolean manageWork = Boolean.FALSE;
	private Boolean findWork = Boolean.TRUE;
	private Boolean linkedin = Boolean.FALSE;
	private String sessionId;
	private Long timeZoneId;
	private String timeZoneCode;
	private Long industryId;
	private Long[] industryIds;
	private boolean onboardCompleted;
	private String resumeUrl;
	private List<String> blacklistedPostalCodes;

	public Profile toProfile() {
		Profile profile = new Profile();
		BeanUtilities.copyProperties(profile, this, new String[]{
			"postalCode", "workPhoneInternationalCode", "mobilePhoneInternationalCode", "smsPhoneInternationalCode"
		});
		return profile;
	}

	public boolean isOnboardCompleted() {
		return onboardCompleted;
	}

	public void setOnboardCompleted(boolean onboardCompleted) {
		this.onboardCompleted = onboardCompleted;
	}

	public Long[] getIndustryIds() {
		return industryIds;
	}

	public void setIndustryIds(Long[] industryIds) {
		this.industryIds = industryIds;
	}

	public Long getSmsPhoneInternationalCode() {
		return smsPhoneInternationalCode;
	}

	public void setSmsPhoneInternationalCode(Long smsPhoneInternationalCode) {
		this.smsPhoneInternationalCode = smsPhoneInternationalCode;
	}

	public Long getMobilePhoneInternationalCode() {
		return mobilePhoneInternationalCode;
	}

	public void setMobilePhoneInternationalCode(Long mobilePhoneInternationalCode) {
		this.mobilePhoneInternationalCode = mobilePhoneInternationalCode;
	}

	public String getMobilePhoneInternationalCallingCodeId() {
		return mobilePhoneInternationalCallingCodeId;
	}

	public void setMobilePhoneInternationalCallingCodeId(String mobilePhoneInternationalCallingCodeId) {
		this.mobilePhoneInternationalCallingCodeId = mobilePhoneInternationalCallingCodeId;
	}

	public Long getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public void setWorkPhoneInternationalCode(Long workPhoneInternationalCode) {
		this.workPhoneInternationalCode = workPhoneInternationalCode;
	}

	public Long getProfileId() {
		return profileId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public String getEmail() {
		return email;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public BigDecimal getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public BigDecimal getMinOnsiteHourlyRate() {
		return minOnsiteHourlyRate;
	}

	public BigDecimal getMinOnsiteWorkPrice() {
		return minOnsiteWorkPrice;
	}

	public BigDecimal getMinOffsiteHourlyRate() {
		return minOffsiteHourlyRate;
	}

	public BigDecimal getMinOffsiteWorkPrice() {
		return minOffsiteWorkPrice;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public String getWorkPhoneInternationalCallingCodeId() {
		return workPhoneInternationalCallingCodeId;
	}

	public void setWorkPhoneInternationalCallingCodeId(String workPhoneInternationalCallingCodeId) {
		this.workPhoneInternationalCallingCodeId = workPhoneInternationalCallingCodeId;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public void setMaxTravelDistance(Double maxTravelDistance) {
		setMaxTravelDistance(BigDecimal.valueOf(maxTravelDistance));
	}

	public void setMaxTravelDistance(BigDecimal maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	public void setMinOnsiteHourlyRate(Double minOnsiteHourlyRate) {
		setMinOnsiteHourlyRate(BigDecimal.valueOf(minOnsiteHourlyRate));
	}

	public void setMinOnsiteHourlyRate(BigDecimal minOnsiteHourlyRate) {
		this.minOnsiteHourlyRate = minOnsiteHourlyRate;
	}

	public void setMinOnsiteWorkPrice(Double minOnsiteWorkPrice) {
		setMinOnsiteWorkPrice(BigDecimal.valueOf(minOnsiteWorkPrice));
	}

	public void setMinOnsiteWorkPrice(BigDecimal minOnsiteWorkPrice) {
		this.minOnsiteWorkPrice = minOnsiteWorkPrice;
	}

	public void setMinOffsiteHourlyRate(Double minOffsiteHourlyRate) {
		setMinOffsiteHourlyRate(BigDecimal.valueOf(minOffsiteHourlyRate));
	}

	public void setMinOffsiteHourlyRate(BigDecimal minOffsiteHourlyRate) {
		this.minOffsiteHourlyRate = minOffsiteHourlyRate;
	}

	public void setMinOffsiteWorkPrice(Double minOffsiteWorkPrice) {
		setMinOffsiteWorkPrice(BigDecimal.valueOf(minOffsiteWorkPrice));
	}

	public void setMinOffsiteWorkPrice(BigDecimal minOffsiteWorkPrice) {
		this.minOffsiteWorkPrice = minOffsiteWorkPrice;
	}

	public boolean isUseCompanyAddress() {
		return useCompanyAddress;
	}

	public void setUseCompanyAddress(boolean useCompanyAddress) {
		this.useCompanyAddress = useCompanyAddress;
	}

	public void setSmsPhone(String smsPhone) {
		this.smsPhone = smsPhone;
	}

	public String getSmsPhone() {
		return smsPhone;
	}

	public Boolean getSmsPhoneVerified() {
		return smsPhoneVerified;
	}

	public void setSmsPhoneVerified(Boolean smsPhoneVerified) {
		this.smsPhoneVerified = smsPhoneVerified;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getOverview() {
		return overview;
	}

	public String getAssociations() {
		return associations;
	}

	public void setAssociations(String associations) {
		this.associations = associations;
	}

	public String getSpecialties() {
		return specialties;
	}

	public void setSpecialties(String specialties) {
		this.specialties = specialties;
	}

	public boolean getManageWork() {
		return manageWork;
	}

	public void setManageWork(boolean manageWork) {
		this.manageWork = manageWork;
	}

	public boolean getFindWork() {
		return findWork;
	}

	public void setFindWork(boolean findWork) {
		this.findWork = findWork;
	}

	public boolean getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(boolean linkedin) {
		this.linkedin = linkedin;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(Long timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getTimeZoneCode() {
		return timeZoneCode;
	}

	public void setTimeZoneCode(String timeZoneCode) {
		this.timeZoneCode = timeZoneCode;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}

	public String getResumeUrl() {
		return resumeUrl;
	}

	public List<String> getBlacklistedPostalCodes() {
		return blacklistedPostalCodes;
	}

	public void setBlacklistedPostalCodes(List<String> blacklistedPostalCodes) {
		this.blacklistedPostalCodes = blacklistedPostalCodes;
	}
}
