package com.workmarket.web.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.EducationHistoryDTO;
import com.workmarket.service.business.dto.EmploymentHistoryDTO;
import com.workmarket.service.business.dto.EsignatureTemplateDTO;
import com.workmarket.service.business.dto.UserAvailabilityDTO;
import com.workmarket.service.business.dto.UserProfileCompletenessDTO;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProfileFacade implements Serializable {

	public static class Documentation {
		public static class Asset {
			private Long id;
			private String uuid;
			private String name;
			private String description;
			private String mimeType;
			private String type;
			private String uri;

			public Asset() {
			}

			public Asset(Long id, String uuid, String name, String mimeType, String uri) {
				this.id = id;
				this.uuid = uuid;
				this.name = name;
				this.mimeType = mimeType;
				this.uri = uri;
			}

			public Long getId() {
				return id;
			}

			public void setId(Long id) {
				this.id = id;
			}

			public String getUuid() {
				return uuid;
			}

			public void setUuid(String uuid) {
				this.uuid = uuid;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getDescription() {
				return description;
			}

			public void setDescription(String description) {
				this.description = description;
			}

			public String getMimeType() {
				return mimeType;
			}

			public void setMimeType(String mimeType) {
				this.mimeType = mimeType;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public String getUri() {
				return uri;
			}

			public void setUri(String uri) {
				this.uri = uri;
			}
		}

		private Long id;
		private Long secondaryId;
		private String name;
		private String description;
		private String notes;
		private String verificationStatus;
		private Date createdOn;
		private List<Asset> assets = Lists.newArrayList();
		private Map<String, String> meta = Maps.newHashMap();

		public Documentation() {
		}

		public Documentation(Long id, String name, String description, String notes) {
			this.setId(id);
			this.setName(name);
			this.setDescription(description);
			this.setNotes(notes);
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getSecondaryId() {
			return secondaryId;
		}

		public void setSecondaryId(Long id) {
			this.secondaryId = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}

		public String getVerificationStatus() {
			return verificationStatus;
		}

		public void setVerficationStatus(String verificationStatus) {
			this.verificationStatus = verificationStatus;
		}

		public Date getCreatedOn() {
			return this.createdOn;
		}

		public void setCreatedOn(Date createdOn) {
			this.createdOn = createdOn;
		}

		public List<Asset> getAssets() {
			return assets;
		}

		public void setAssets(List<Asset> assets) {
			this.assets = assets;
		}

		public Map<String, String> getMeta() {
			return this.meta;
		}

		public void setMeta(Map<String, String> meta) {
			this.meta = meta;
		}
	}

	private String requestContext;
	private Integer laneType = null;
	private String laneTypeApprovalStatus;
	private List<Long> roleIds;
	private List<String> roleNames;

	private Boolean blocked;

	private Long id;
	private String uuid;
	private String userNumber;
	private String firstName;
	private String lastName;
	private String email;
	private String changedEmail;
	private Boolean emailConfirmed;
	private Calendar createdOn;
	private String userStatusType;
	private Boolean lane3Pending;
	private Boolean lane3Active;

	private String jobTitle;
	private String overview;
	private String secondaryEmail;
	private String workPhone;
	private String workPhoneExtension;
	private String workPhoneInternationalCode;
	private String mobilePhone;
	private String mobilePhoneInternationalCode;

	private AddressDTO address;

	private Long companyId;
	private String companyName;
	private String companyOverview;
	private String companyWebsite;
	private String companyNumber;
	private AddressDTO companyAddress;

	private boolean taxEntityExists;
	private Integer paymentTermsDays;

	private String avatarOriginalAssetUri;
	private String avatarSmallAssetUri;
	private String avatarLargeAssetUri;

	private String companyAvatarOriginalAssetUri;
	private String companyAvatarSmallAssetUri;
	private String companyAvatarLargeAssetUri;

	private String backgroundImageUri;

	private UserProfileCompletenessDTO profileCompleteness;
	private List<Documentation> industries;
	private List<Documentation> skills;
	private List<Documentation> tools;
	private List<Documentation> languages;
	private List<Documentation> specialties;
	private List<Documentation> licenses;
	private List<Documentation> certifications;
	private List<Documentation> insurance;
	private List<Documentation> assessments;
	private List<Documentation> resumes;
	private List<Map> publicGroups;
	private List<Map> privateGroups;
	private List<Map> sharedGroups;

	private List<String> privateTags;

	private String backgroundCheckStatus;
	private Boolean backgroundCheckVerified;
	private Date lastBackgroundCheckRequestDate;
	private Date lastBackgroundCheckResponseDate;
	private boolean priorPassedBackgroundCheck;

	private String drugTestStatus;
	private Boolean drugTestVerified;
	private Date lastDrugTestRequestDate;
	private Date lastDrugTestResponseDate;
	private boolean priorPassedDrugTest;

	private Double minOnsiteHourlyRate;
	private Double minOnsiteWorkPrice;
	private Double minOffsiteHourlyRate;
	private Double minOffsiteWorkPrice;

	private List<UserAvailabilityDTO> workingHours;
	private Double maxTravelDistance;

	private Double rating;
	private Integer ratingCount;

	private Boolean linkedInVerified;
	private String linkedInPublicProfileUrl;
	private List<EducationHistoryDTO> linkedInEducation;
	private List<EmploymentHistoryDTO> linkedInPositions;

	private Documentation recruitingCampaign;
	private List<EsignatureTemplateDTO> esignatures;

	private List<String> blacklistedPostalCodes;

	private Map<String, String> meta = Maps.newHashMap();
	private String zipCode;
	private double latitude;
	private double longitude;
	private boolean confirmedBankAccount;

	private boolean verifiedTaxEntity;
	private boolean rejectedTaxEntity;
	private boolean video;
	private boolean photo;
	private String taxEntityCountry;

	private MboProfile mboProfile;
	private Map<String, String> profileMediaTypes;
	private Map<Long, String> groupsAvailableToInvite;
	private Map<Long, Boolean> currentCompanyGroupPermission;

	public void initializeDefaults() {
		blocked = Boolean.FALSE;
		lane3Pending = Boolean.FALSE;
		lane3Active = Boolean.FALSE;
		backgroundCheckVerified = Boolean.FALSE;
		drugTestVerified = Boolean.FALSE;
		priorPassedBackgroundCheck = false;
		priorPassedDrugTest = false;
		blacklistedPostalCodes = Lists.newArrayList();
		roleIds = Lists.newArrayList();
		roleNames = Lists.newArrayList();
		industries = Lists.newArrayList();
		skills = Lists.newArrayList();
		tools = Lists.newArrayList();
		languages = Lists.newArrayList();
		specialties = Lists.newArrayList();
		licenses = Lists.newArrayList();
		certifications = Lists.newArrayList();
		insurance = Lists.newArrayList();
		assessments = Lists.newArrayList();
		resumes = Lists.newArrayList();
		publicGroups = Lists.newArrayList();
		privateGroups = Lists.newArrayList();
		sharedGroups = Lists.newArrayList();
		privateTags = Lists.newArrayList();
		workingHours = Lists.newArrayList();
		linkedInEducation = Lists.newArrayList();
		linkedInPositions = Lists.newArrayList();
		profileMediaTypes = Maps.newHashMap();
		groupsAvailableToInvite = Maps.newHashMap();
		currentCompanyGroupPermission = Maps.newHashMap();
	}

	public String getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public void setWorkPhoneInternationalCode(String workPhoneInternationalCode) {
		this.workPhoneInternationalCode = workPhoneInternationalCode;
	}

	public String getMobilePhoneInternationalCode() {
		return mobilePhoneInternationalCode;
	}

	public void setMobilePhoneInternationalCode(String mobilePhoneInternationalCode) {
		this.mobilePhoneInternationalCode = mobilePhoneInternationalCode;
	}


	public String getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(String requestContext) {
		this.requestContext = requestContext;
	}

	public Integer getLaneType() {
		return laneType;
	}

	public void setLaneType(Integer laneType) {
		this.laneType = laneType;
	}

	public String getLaneTypeApprovalStatus() {
		return this.laneTypeApprovalStatus;
	}

	public void setLaneTypeApprovalStatus(String laneTypeApprovalStatus) {
		this.laneTypeApprovalStatus = laneTypeApprovalStatus;
	}

	public List<Long> getRoleIds() {
		return this.roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public List<String> getRoleNames() {
		return this.roleNames;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	public Boolean getBlocked() {
		return this.blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getChangedEmail() {
		return this.changedEmail;
	}

	public void setChangedEmail(String changedEmail) {
		this.changedEmail = changedEmail;
	}

	public Boolean getEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	public String getUserStatusType() {
		return userStatusType;
	}

	public void setUserStatusType(String userStatusType) {
		this.userStatusType = userStatusType;
	}

	public Boolean getLane3Pending() {
		return this.lane3Pending;
	}

	public void setLane3Pending(Boolean lane3Pending) {
		this.lane3Pending = lane3Pending;
	}

	public Boolean getLane3Active() {
		return this.lane3Active;
	}

	public void setLane3Active(Boolean lane3Active) {
		this.lane3Active = lane3Active;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getOverview() {
		return this.overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public AddressDTO getAddress() {
		return address;
	}

	public void setAddress(AddressDTO address) {
		this.address = address;
	}

	public Long getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyOverview() {
		return this.companyOverview;
	}

	public void setCompanyOverview(String companyOverview) {
		this.companyOverview = companyOverview;
	}

	public String getCompanyWebsite() {
		return this.companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public String getCompanyNumber() { return this.companyNumber; }

	public void setCompanyNumber(String companyNumber) { this.companyNumber = companyNumber; }

	public AddressDTO getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(AddressDTO companyAddress) {
		this.companyAddress = companyAddress;
	}

	public boolean getTaxEntityExists() {
		return this.taxEntityExists;
	}

	public void setTaxEntityExists(boolean taxEntityExists) {
		this.taxEntityExists = taxEntityExists;
	}

	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	public String getAvatarOriginalAssetUri() {
		return avatarOriginalAssetUri;
	}

	public void setAvatarOriginalAssetUri(String avatarOriginalAssetUri) {
		this.avatarOriginalAssetUri = avatarOriginalAssetUri;
	}

	public String getAvatarSmallAssetUri() {
		return avatarSmallAssetUri;
	}

	public void setAvatarSmallAssetUri(String avatarSmallAssetUri) {
		this.avatarSmallAssetUri = avatarSmallAssetUri;
	}

	public String getAvatarLargeAssetUri() {
		return avatarLargeAssetUri;
	}

	public void setAvatarLargeAssetUri(String avatarLargeAssetUri) {
		this.avatarLargeAssetUri = avatarLargeAssetUri;
	}

	public String getCompanyAvatarOriginalAssetUri() {
		return companyAvatarOriginalAssetUri;
	}

	public void setCompanyAvatarOriginalAssetUri(String companyAvatarOriginalAssetUri) {
		this.companyAvatarOriginalAssetUri = companyAvatarOriginalAssetUri;
	}

	public String getCompanyAvatarSmallAssetUri() {
		return companyAvatarSmallAssetUri;
	}

	public void setCompanyAvatarSmallAssetUri(String companyAvatarSmallAssetUri) {
		this.companyAvatarSmallAssetUri = companyAvatarSmallAssetUri;
	}

	public String getCompanyAvatarLargeAssetUri() {
		return companyAvatarLargeAssetUri;
	}

	public void setCompanyAvatarLargeAssetUri(String companyAvatarLargeAssetUri) {
		this.companyAvatarLargeAssetUri = companyAvatarLargeAssetUri;
	}

	public void setBackgroundImageUri(String backgroundImageUri) {
		this.backgroundImageUri = backgroundImageUri;
	}

	public String getBackgroundImageUri() {
		return backgroundImageUri;
	}

	public UserProfileCompletenessDTO getProfileCompleteness() {
		return profileCompleteness;
	}

	public void setProfileCompleteness(
			UserProfileCompletenessDTO profileCompleteness) {
		this.profileCompleteness = profileCompleteness;
	}

	public List<Documentation> getIndustries() {
		return industries;
	}

	public void setIndustries(List<Documentation> industries) {
		this.industries = industries;
	}

	public List<Documentation> getSkills() {
		return skills;
	}

	public void setSkills(List<Documentation> skills) {
		this.skills = skills;
	}

	public List<Documentation> getTools() {
		return tools;
	}

	public void setTools(List<Documentation> tools) {
		this.tools = tools;
	}

	public List<Documentation> getLanguages() {
		return this.languages;
	}

	public void setLanguages(List<Documentation> languages) {
		this.languages = languages;
	}

	public List<Documentation> getSpecialties() {
		return specialties;
	}

	public void setSpecialties(List<Documentation> specialties) {
		this.specialties = specialties;
	}

	public List<Documentation> getLicenses() {
		return licenses;
	}

	public void setLicenses(List<Documentation> licenses) {
		this.licenses = licenses;
	}

	public List<Documentation> getCertifications() {
		return certifications;
	}

	public void setCertifications(List<Documentation> certifications) {
		this.certifications = certifications;
	}

	public List<Documentation> getInsurance() {
		return insurance;
	}

	public void setInsurance(List<Documentation> insurance) {
		this.insurance = insurance;
	}

	public List<Documentation> getAssessments() {
		return assessments;
	}

	public void setAssessments(List<Documentation> assessments) {
		this.assessments = assessments;
	}

	public List<Documentation> getResumes() {
		return resumes;
	}

	public void setResumes(List<Documentation> resumes) {
		this.resumes = resumes;
	}

	public List<Map> getPublicGroups() {
		return publicGroups;
	}

	public void setPublicGroups(List<Map> publicGroups) {
		this.publicGroups = publicGroups;
	}

	public List<Map> getPrivateGroups() {
		return privateGroups;
	}

	public void setPrivateGroups(List<Map> privateGroups) {
		this.privateGroups = privateGroups;
	}

	public List<Map> getSharedGroups() {
		return sharedGroups;
	}

	public void setSharedGroups(List<Map> sharedGroups) {
		this.sharedGroups = sharedGroups;
	}

	public List<String> getPrivateTags() {
		return this.privateTags;
	}

	public void setPrivateTags(List<String> privateTags) {
		this.privateTags = privateTags;
	}

	public String getBackgroundCheckStatus() {
		return backgroundCheckStatus;
	}

	public void setBackgroundCheckStatus(String backgroundCheckStatus) {
		this.backgroundCheckStatus = backgroundCheckStatus;
	}

	public Boolean getBackgroundCheckVerified() {
		return backgroundCheckVerified;
	}

	public void setBackgroundCheckVerified(Boolean backgroundCheckVerified) {
		this.backgroundCheckVerified = backgroundCheckVerified;
	}

	public Date getLastBackgroundCheckRequestDate() {
		return lastBackgroundCheckRequestDate;
	}

	public void setLastBackgroundCheckRequestDate(Date lastBackgroundCheckRequestDate) {
		this.lastBackgroundCheckRequestDate = lastBackgroundCheckRequestDate;
	}

	public Date getLastBackgroundCheckResponseDate() {
		return lastBackgroundCheckResponseDate;
	}

	public void setLastBackgroundCheckResponseDate(Date lastBackgroundCheckResponseDate) {
		this.lastBackgroundCheckResponseDate = lastBackgroundCheckResponseDate;
	}

	public boolean isPriorPassedBackgroundCheck() {
		return priorPassedBackgroundCheck;
	}

	public void setPriorPassedBackgroundCheck(boolean priorPassedBackgroundCheck) {
		this.priorPassedBackgroundCheck = priorPassedBackgroundCheck;
	}

	public String getDrugTestStatus() {
		return drugTestStatus;
	}

	public void setDrugTestStatus(String drugTestStatus) {
		this.drugTestStatus = drugTestStatus;
	}

	public Boolean getDrugTestVerified() {
		return drugTestVerified;
	}

	public void setDrugTestVerified(Boolean drugTestVerified) {
		this.drugTestVerified = drugTestVerified;
	}

	public Date getLastDrugTestRequestDate() {
		return lastDrugTestRequestDate;
	}

	public void setLastDrugTestRequestDate(Date lastDrugTestRequestDate) {
		this.lastDrugTestRequestDate = lastDrugTestRequestDate;
	}

	public Date getLastDrugTestResponseDate() {
		return lastDrugTestResponseDate;
	}

	public void setLastDrugTestResponseDate(Date lastDrugTestResponseDate) {
		this.lastDrugTestResponseDate = lastDrugTestResponseDate;
	}

	public boolean isPriorPassedDrugTest() {
		return priorPassedDrugTest;
	}

	public void setPriorPassedDrugTest(boolean priorPassedDrugTest) {
		this.priorPassedDrugTest = priorPassedDrugTest;
	}

	public Double getMinOnsiteHourlyRate() {
		return this.minOnsiteHourlyRate;
	}

	public void setMinOnsiteHourlyRate(Double minOnsiteHourlyRate) {
		this.minOnsiteHourlyRate = minOnsiteHourlyRate;
	}

	public Double getMinOnsiteWorkPrice() {
		return this.minOnsiteWorkPrice;
	}

	public void setMinOnsiteWorkPrice(Double minOnsiteWorkPrice) {
		this.minOnsiteWorkPrice = minOnsiteWorkPrice;
	}

	public Double getMinOffsiteHourlyRate() {
		return this.minOffsiteHourlyRate;
	}

	public void setMinOffsiteHourlyRate(Double minOffsiteHourlyRate) {
		this.minOffsiteHourlyRate = minOffsiteHourlyRate;
	}

	public Double getMinOffsiteWorkPrice() {
		return this.minOffsiteWorkPrice;
	}

	public void setMinOffsiteWorkPrice(Double minOffsiteWorkPrice) {
		this.minOffsiteWorkPrice = minOffsiteWorkPrice;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Integer getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	public List<UserAvailabilityDTO> getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(List<UserAvailabilityDTO> workingHours) {
		this.workingHours = workingHours;
	}

	public Double getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public void setMaxTravelDistance(Double maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	public Boolean getLinkedInVerified() {
		return linkedInVerified;
	}

	public void setLinkedInVerified(Boolean linkedInVerified) {
		this.linkedInVerified = linkedInVerified;
	}

	public String getLinkedInPublicProfileUrl() {
		return this.linkedInPublicProfileUrl;
	}

	public void setLinkedInPublicProfileUrl(String linkedInPublicProfileUrl) {
		this.linkedInPublicProfileUrl = linkedInPublicProfileUrl;
	}

	public List<EducationHistoryDTO> getLinkedInEducation() {
		return this.linkedInEducation;
	}

	public void setLinkedInEducation(List<EducationHistoryDTO> linkedInEducation) {
		this.linkedInEducation = linkedInEducation;
	}

	public List<EmploymentHistoryDTO> getLinkedInPositions() {
		return this.linkedInPositions;
	}

	public void setLinkedInPositions(List<EmploymentHistoryDTO> linkedInPositions) {
		this.linkedInPositions = linkedInPositions;
	}

	public Documentation getRecruitingCampaign() {
		return this.recruitingCampaign;
	}

	public void setRecruitingCampaign(Documentation recruitingCampaign) {
		this.recruitingCampaign = recruitingCampaign;
	}

	public List<EsignatureTemplateDTO> getEsignatures() {
		return esignatures;
	}

	public void setEsignatures(final List<EsignatureTemplateDTO> esignatures) {
		this.esignatures = esignatures;
	}

	public List<String> getBlacklistedPostalCodes() {
		return blacklistedPostalCodes;
	}

	public void setBlacklistedPostalCodes(List<String> blacklistedPostalCodes) {
		this.blacklistedPostalCodes = blacklistedPostalCodes;
	}

	public Map<Long, Boolean> getCurrentCompanyGroupPermission() {
		return currentCompanyGroupPermission;
	}

	public void setCurrentCompanyGroupPermission(Map<Long, Boolean> currentCompanyGroupPermission) {
		this.currentCompanyGroupPermission = currentCompanyGroupPermission;
	}

	public Map<Long, String> getGroupsAvailableToInvite() {
		return groupsAvailableToInvite;
	}

	public void setGroupsAvailableToInvite(Map<Long, String> groupsAvailableToInvite) {
		this.groupsAvailableToInvite = groupsAvailableToInvite;
	}

	public Map<String, String> getMeta() {
		return this.meta;
	}

	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isConfirmedBankAccount() {
		return confirmedBankAccount;
	}

	public void setConfirmedBankAccount(boolean confirmedBankAccount) {
		this.confirmedBankAccount = confirmedBankAccount;
	}

	public boolean hasPhoto() {
		return photo;
	}

	public void setPhoto(boolean photo) {
		this.photo = photo;
	}

	public boolean hasRejectedTaxEntity() {
		return rejectedTaxEntity;
	}

	public void setRejectedTaxEntity(boolean rejectedTaxEntity) {
		this.rejectedTaxEntity = rejectedTaxEntity;
	}

	public boolean hasVerifiedTaxEntity() {
		return verifiedTaxEntity;
	}

	public void setVerifiedTaxEntity(boolean verifiedTaxEntity) {
		this.verifiedTaxEntity = verifiedTaxEntity;
	}

	public boolean hasVideo() {
		return video;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}

	public MboProfile getMboProfile() {
		return mboProfile;
	}

	public void setMboProfile(MboProfile mboProfile) {
		this.mboProfile = mboProfile;
	}

	public String getMboStatus() {
		if (mboProfile != null) {
			return mboProfile.getStatus();
		}
		return StringUtils.EMPTY;
	}

	public boolean isOwner() {
		return RequestContext.OWNER.getCode().equals(this.requestContext);
	}

	public boolean isSuspended() {
		return UserStatusType.SUSPENDED.equals(this.userStatusType);
	}

	public boolean isDeactivated() {
		return UserStatusType.DEACTIVATED.equals(this.userStatusType);
	}

	public boolean hasWorkerRole() {
		return this.getRoleIds().contains(AclRole.ACL_WORKER);
	}

	public boolean hasSharedWorkerRole() {
		return this.getRoleIds().contains(AclRole.ACL_SHARED_WORKER);
	}

	public boolean isLane4() {
		if (laneType != null) {
			return LaneType.LANE_4.ordinal() == this.getLaneType().intValue();
		}
		return false;
	}

	public String getLaneAccess() {
		return (this.hasSharedWorkerRole()) ? "Lane 3" : (this.hasWorkerRole()) ? "Lane 1" : "Staff";
	}

	public boolean hasGetWorkAccess() {
		return CollectionUtilities.contains(this.getRoleIds(),
				AclRole.ACL_SHARED_WORKER,
				AclRole.ACL_USER
		);
	}

	public String getTaxEntityCountry() {
		return taxEntityCountry;
	}

	public void setTaxEntityCountry(String taxEntityCountry) {
		this.taxEntityCountry = taxEntityCountry;
	}
}
