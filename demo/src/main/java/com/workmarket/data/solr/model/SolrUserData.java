package com.workmarket.data.solr.model;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.UserStatusType;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SolrUserData extends SolrBaseUserData implements UuidSolrData {

	private static final long serialVersionUID = -3548820783265411791L;
	private static final SolrUserType USER_TYPE = SolrUserType.WORKER;

	private long id;
	private String uuid;
	private SolrCompanyData company;
	private String title;
	private List<String> jobFunctions;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private List<SolrLicenseData> licenses;
	private List<SolrCertificationData> certifications;
	private List<SolrAssessmentData> assessments;
	private List<SolrAssessmentData> invitedAssessments;
	private List<SolrAssessmentData> failedTests;
	private List<SolrAssessmentData> passedAssessments;
	private List<SolrContractData> contracts;
	private List<SolrGroupData> groupData;
	private List<SolrSharedGroupData> sharedGroupData;
	private List<SolrGroupData> groupMember;
	private List<SolrGroupData> groupMemberOverride;
	private List<SolrGroupData> groupPending;
	private List<SolrGroupData> groupPendingOverride;
	private List<SolrGroupData> groupInvited;
	private List<SolrGroupData> groupDeclined;
	private List<Long> industries;
	private int hourlyRate;
	private SolrRatingData rating;
	private List<Integer> verificationIds;
	private List<SolrCompanyLaneData> laneData;
	private String avatarSmallAssetUri;
	private String videoAssetUri;
	private float maxTravelDistance;
	private List<SolrSkillData> skills;
	private List<SolrCompanyUserTag> userTags;
	private DateTime lastDrugTestDate;
	private DateTime lastBackgroundCheckDate;
	private boolean passedBackgroundCheck;
	private boolean passedDrugTest;
	private List<SolrInsuranceType> insurances;
	private List<SolrInsuranceCoverageData> workersCompCoverage;
	private List<SolrInsuranceCoverageData> generalLiabilityCoverage;
	private List<SolrInsuranceCoverageData> errorsAndOmissionsCoverage;
	private List<SolrInsuranceCoverageData> automobileCoverage;
	private List<SolrInsuranceCoverageData> contractorsCoverage;
	private List<SolrInsuranceCoverageData> commercialGeneralLiabilityCoverage;
	private List<SolrInsuranceCoverageData> businessLiabilityCoverage;
	private SolrRecruitingCampaignData recruitingData;
	private GeoPoint point;
	private List<Long> blockedUserIds;
	private List<Long> blockedCompanyIds;
	private int screeningStatus;
	private SolrLinkedInData solrLinkedInData;
	private List<String> toolNames;
	private List<String> orgUnits;
	private List<String> specialtyNames;
	private String timeZoneId;
	private Calendar lastAssignedWorkDate;
	private Integer workCancelledCount = 0;
	private DateTime createdOn;
	private List<String> completedWorkKeywords;
	private Collection<String> appliedGroupsKeywords;
	private List<Long> workInvitedIds;
	private List<Long> workAssignedIds;
	private Map<Long, Integer> paidCompanyAssignmentCounts;
	private boolean mbo;
	private String mboStatus;
	private Integer warpRequisitionId;

	//Quality boost
	private Double averageStarRating = 0.0;
	private Integer repeatClientsCount = 0;
	private Integer blocksCount = 0;
	private Integer lateLabelCount = 0;
	private Integer abandonedLabelCount = 0;
	private Integer cancelledLabelCount = 0;
	private Integer delayedLabelCount = 0;
	private Integer paidAssignmentsCount = 0;
	private Set<Long> workCompletedForCompanies;
	private Double  weightedAverageRating = 0.0;
	private Double  recentWorkingWeeksRatio = 0.0;


	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public static SolrUserType getUserType() {
		return USER_TYPE;
	}

	public SolrCompanyData getCompany() {
		return company;
	}

	public void setCompany(SolrCompanyData company) {
		this.company = company;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getJobFunctions() {
		return jobFunctions;
	}

	public void setJobFunctions(final List<String> jobFunctions) {
		this.jobFunctions = jobFunctions;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<SolrLicenseData> getLicenses() {
		return licenses;
	}

	public void setLicenses(List<SolrLicenseData> licenses) {
		this.licenses = licenses;
	}

	public List<SolrCertificationData> getCertifications() {
		return certifications;
	}

	public void setCertifications(List<SolrCertificationData> certifications) {
		this.certifications = certifications;
	}

	public List<SolrAssessmentData> getAssessments() {
		return assessments;
	}

	public void setAssessments(List<SolrAssessmentData> assessments) {
		this.assessments = assessments;
	}

	public List<SolrAssessmentData> getInvitedAssessments() {
		return invitedAssessments;
	}

	public void setInvitedAssessments(List<SolrAssessmentData> invitedAssessments) {
		this.invitedAssessments = invitedAssessments;
	}

	public List<SolrAssessmentData> getFailedTests() {
		return failedTests;
	}

	public void setFailedTests(List<SolrAssessmentData> failedTests) {
		this.failedTests = failedTests;
	}

	public List<SolrAssessmentData> getPassedAssessments() {
		return passedAssessments;
	}

	public void setPassedAssessments(List<SolrAssessmentData> passedAssessments) {
		this.passedAssessments = passedAssessments;
	}

	public List<SolrContractData> getContracts() {
		return contracts;
	}

	public void setContracts(List<SolrContractData> contracts) {
		this.contracts = contracts;
	}

	public List<SolrGroupData> getGroupData() {
		return groupData;
	}

	public void setGroupData(List<SolrGroupData> groupData) {
		this.groupData = groupData;
	}

	public List<SolrSharedGroupData> getSharedGroupData() { return sharedGroupData; }

	public void setSharedGroupData(List<SolrSharedGroupData> sharedGroupData) { this.sharedGroupData = sharedGroupData; }

	public List<SolrGroupData> getGroupMember() { return groupMember; }

	public void setGroupMember(List<SolrGroupData> groupMember) {
		this.groupMember = groupMember;
	}

	public List<SolrGroupData> getGroupMemberOverride() {
		return groupMemberOverride;
	}

	public void setGroupMemberOverride(List<SolrGroupData> groupMemberOverride) {
		this.groupMemberOverride = groupMemberOverride;
	}

	public List<SolrGroupData> getGroupPending() {
		return groupPending;
	}

	public void setGroupPending(List<SolrGroupData> groupPending) {
		this.groupPending = groupPending;
	}

	public List<SolrGroupData> getGroupPendingOverride() {
		return groupPendingOverride;
	}

	public void setGroupPendingOverride(List<SolrGroupData> groupPendingOverride) { this.groupPendingOverride = groupPendingOverride; }

	public List<SolrGroupData> getGroupInvited() {
		return groupInvited;
	}

	public void setGroupInvited(List<SolrGroupData> groupInvited) {
		this.groupInvited = groupInvited;
	}

	public List<SolrGroupData> getGroupDeclined() {
		return groupDeclined;
	}

	public void setGroupDeclined(List<SolrGroupData> groupDeclined) {
		this.groupDeclined = groupDeclined;
	}

	public List<Long> getIndustries() {
		return industries;
	}

	public void setIndustries(List<Long> industries) {
		this.industries = industries;
	}

	public int getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(int hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public SolrRatingData getRating() {
		return rating;
	}

	public void setRating(SolrRatingData rating) {
		this.rating = rating;
	}

	public List<Integer> getVerificationIds() {
		return verificationIds;
	}

	public void setVerificationIds(List<Integer> verificationIds) {
		this.verificationIds = verificationIds;
	}

	public List<SolrCompanyLaneData> getLaneData() {
		return laneData;
	}

	public void setLaneData(List<SolrCompanyLaneData> laneData) {
		this.laneData = laneData;
	}

	public String getAvatarSmallAssetUri() {
		return avatarSmallAssetUri;
	}

	public void setAvatarSmallAssetUri(String avatarSmallAssetUri) {
		this.avatarSmallAssetUri = avatarSmallAssetUri;
	}

	public String getVideoAssetUri() {
		return videoAssetUri;
	}

	public void setVideoAssetUri(String videoAssetUri) {
		this.videoAssetUri = videoAssetUri;
	}

	public float getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public void setMaxTravelDistance(float maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	public Collection<SolrSkillData> getSkills() {
		return skills;
	}

	public void setSkills(List<SolrSkillData> skills) {
		this.skills = skills;
	}

	public List<SolrCompanyUserTag> getUserTags() {
		return userTags;
	}

	public void setUserTags(List<SolrCompanyUserTag> userTags) {
		this.userTags = userTags;
	}

	public DateTime getLastDrugTestDate() {
		return lastDrugTestDate;
	}

	public void setLastDrugTestDate(DateTime lastDrugTestDate) {
		this.lastDrugTestDate = lastDrugTestDate;
	}

	public DateTime getLastBackgroundCheckDate() {
		return lastBackgroundCheckDate;
	}

	public void setLastBackgroundCheckDate(DateTime lastBackgroundCheckDate) {
		this.lastBackgroundCheckDate = lastBackgroundCheckDate;
	}

	public boolean isPassedBackgroundCheck() {
		return passedBackgroundCheck;
	}

	public void setPassedBackgroundCheck(boolean passedBackgroundCheck) {
		this.passedBackgroundCheck = passedBackgroundCheck;
	}

	public boolean isPassedDrugTest() {
		return passedDrugTest;
	}

	public void setPassedDrugTest(boolean passedDrugTest) {
		this.passedDrugTest = passedDrugTest;
	}

	public List<SolrInsuranceType> getInsurances() {
		return insurances;
	}

	public void setInsurances(List<SolrInsuranceType> insurances) {
		this.insurances = insurances;
	}

	public List<SolrInsuranceCoverageData> getWorkersCompCoverage() {
		return workersCompCoverage;
	}

	public void setWorkersCompCoverage(List<SolrInsuranceCoverageData> workersCompCoverage) {
		this.workersCompCoverage = workersCompCoverage;
	}

	public List<SolrInsuranceCoverageData> getGeneralLiabilityCoverage() {
		return generalLiabilityCoverage;
	}

	public void setGeneralLiabilityCoverage(List<SolrInsuranceCoverageData> generalLiabilityCoverage) {
		this.generalLiabilityCoverage = generalLiabilityCoverage;
	}

	public List<SolrInsuranceCoverageData> getErrorsAndOmissionsCoverage() {
		return errorsAndOmissionsCoverage;
	}

	public void setErrorsAndOmissionsCoverage(List<SolrInsuranceCoverageData> errorsAndOmissionsCoverage) {
		this.errorsAndOmissionsCoverage = errorsAndOmissionsCoverage;
	}

	public List<SolrInsuranceCoverageData> getAutomobileCoverage() {
		return automobileCoverage;
	}

	public void setAutomobileCoverage(List<SolrInsuranceCoverageData> automobileCoverage) {
		this.automobileCoverage = automobileCoverage;
	}

	public List<SolrInsuranceCoverageData> getContractorsCoverage() {
		return contractorsCoverage;
	}

	public void setContractorsCoverage(List<SolrInsuranceCoverageData> contractorsCoverage) {
		this.contractorsCoverage = contractorsCoverage;
	}

	public List<SolrInsuranceCoverageData> getCommercialGeneralLiabilityCoverage() {
		return commercialGeneralLiabilityCoverage;
	}

	public void setCommercialGeneralLiabilityCoverage(List<SolrInsuranceCoverageData> commercialGeneralLiabilityCoverage) {
		this.commercialGeneralLiabilityCoverage = commercialGeneralLiabilityCoverage;
	}

	public List<SolrInsuranceCoverageData> getBusinessLiabilityCoverage() {
		return businessLiabilityCoverage;
	}

	public void setBusinessLiabilityCoverage(List<SolrInsuranceCoverageData> businessLiabilityCoverage) {
		this.businessLiabilityCoverage = businessLiabilityCoverage;
	}

	public void setToolNames(List<String> toolNames) {
		this.toolNames = toolNames;
	}

	public List<String> getToolNames() {
		return this.toolNames;
	}

	public List<String> getOrgUnits() {
		return orgUnits;
	}

	public void setOrgUnits(final List<String> orgUnits) {
		this.orgUnits = orgUnits;
	}

	public void setSpecialtyNames(List<String> specialtyNames) {
		this.specialtyNames = specialtyNames;
	}

	public List<String> getSpecialtyNames() {
		return this.specialtyNames;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	public SolrRecruitingCampaignData getRecruitingData() {
		return recruitingData;
	}

	public void setRecruitingData(SolrRecruitingCampaignData recruitingData) {
		this.recruitingData = recruitingData;
	}

	public SolrLinkedInData getSolrLinkedInData() {
		return solrLinkedInData;
	}

	public void setSolrLinkedInData(SolrLinkedInData solrLinkedInData) {
		this.solrLinkedInData = solrLinkedInData;
	}

	public List<Long> getBlockedUserIds() {
		return blockedUserIds;
	}

	public void setBlockedUserIds(List<Long> blockedUserIds) {
		this.blockedUserIds = blockedUserIds;
	}

	public List<Long> getBlockedCompanyIds() {
		return blockedCompanyIds;
	}

	public void setBlockedCompanyIds(List<Long> blockedCompanyIds) {
		this.blockedCompanyIds = blockedCompanyIds;
	}

	public int getScreeningStatus() {
		return screeningStatus;
	}

	public void setScreeningStatus(int screeningStatus) {
		this.screeningStatus = screeningStatus;
	}

	public boolean isLane4Active() {
		return isEmailConfirmed() && isSharedWorkerRole() && UserStatusType.APPROVED.equals(getUserStatusType()) && ApprovalStatus.APPROVED.ordinal() == getLane3ApprovalStatus();
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public Calendar getLastAssignedWorkDate() {
		return lastAssignedWorkDate;
	}

	public void setLastAssignedWorkDate(Calendar lastAssignedWorkDate) {
		this.lastAssignedWorkDate = lastAssignedWorkDate;
	}

	public Integer getWorkCancelledCount() {
		return workCancelledCount;
	}

	public void setWorkCancelledCount(Integer workCancelledCount) {
		this.workCancelledCount = workCancelledCount;
	}

	public DateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(DateTime createdOn) {
		this.createdOn = createdOn;
	}

	public List<String> getCompletedWorkKeywords() {
		return completedWorkKeywords;
	}

	public void setCompletedWorkKeywords(List<String> completedWorkKeywords) {
		this.completedWorkKeywords = completedWorkKeywords;
	}

	public Collection<String> getAppliedGroupsKeywords() {
		return appliedGroupsKeywords;
	}

	public void setAppliedGroupsKeywords(Collection<String> appliedGroupsKeywords) {
		this.appliedGroupsKeywords = appliedGroupsKeywords;
	}

	public List<Long> getWorkAssignedIds() {
		return workAssignedIds;
	}

	public void setWorkAssignedIds(List<Long> workAssignedIds) {
		this.workAssignedIds = workAssignedIds;
	}

	public List<Long> getWorkInvitedIds() {
		return workInvitedIds;
	}

	public void setWorkInvitedIds(List<Long> workInvitedIds) {
		this.workInvitedIds = workInvitedIds;
	}

	public Integer getAbandonedLabelCount() {
		return abandonedLabelCount;
	}

	public void setAbandonedLabelCount(Integer abandonedLabelCount) {
		this.abandonedLabelCount = abandonedLabelCount;
	}

	public Double getAverageStarRating() {
		return averageStarRating;
	}

	public void setAverageStarRating(Double averageStarRating) {
		this.averageStarRating = averageStarRating;
	}

	public Integer getBlocksCount() {
		return blocksCount;
	}

	public void setBlocksCount(Integer blocksCount) {
		this.blocksCount = blocksCount;
	}

	public Integer getCancelledLabelCount() {
		return cancelledLabelCount;
	}

	public void setCancelledLabelCount(Integer cancelledLabelCount) {
		this.cancelledLabelCount = cancelledLabelCount;
	}

	public Integer getDelayedLabelCount() {
		return delayedLabelCount;
	}

	public void setDelayedLabelCount(Integer delayedLabelCount) {
		this.delayedLabelCount = delayedLabelCount;
	}

	public Integer getLateLabelCount() {
		return lateLabelCount;
	}

	public void setLateLabelCount(Integer lateLabelCount) {
		this.lateLabelCount = lateLabelCount;
	}

	public Integer getPaidAssignmentsCount() {
		return paidAssignmentsCount;
	}

	public void setPaidAssignmentsCount(Integer paidAssignmentsCount) {
		this.paidAssignmentsCount = paidAssignmentsCount;
	}

	public Double getRecentWorkingWeeksRatio() { return recentWorkingWeeksRatio; }

	public void setRecentWorkingWeeksRatio(Double recentWorkingWeeksRatio) {
		this.recentWorkingWeeksRatio = recentWorkingWeeksRatio;
	}

	public Integer getRepeatClientsCount() {
		return repeatClientsCount;
	}

	public void setRepeatClientsCount(Integer repeatClientsCount) {
		this.repeatClientsCount = repeatClientsCount;
	}

	public Map<Long, Integer> getPaidCompanyAssignmentCounts() { return paidCompanyAssignmentCounts; }

	public void setPaidCompanyAssignmentCounts(Map<Long, Integer> paidCompanyAssignmentCounts) { this.paidCompanyAssignmentCounts = paidCompanyAssignmentCounts; }

	public Set<Long> getWorkCompletedForCompanies() {
		if (CollectionUtils.isEmpty(workCompletedForCompanies)) {
			return Sets.newHashSet();
		}
		return workCompletedForCompanies;
	}

	public boolean isMbo() { return mbo; }

	public void setMbo(boolean mbo) { this.mbo = mbo; }

	public String getMboStatus() { return mboStatus; }

	public void setMboStatus(String mboStatus) { this.mboStatus = mboStatus; }

	public Integer getWarpRequisitionId() { return warpRequisitionId; }

	public void setWarpRequisitionId(Integer warpRequisitionId){
		this.warpRequisitionId = warpRequisitionId;
	}

	public Double getWeightedAverageRating() { return weightedAverageRating; }

	public void setWeightedAverageRating(Double weightedAverageRating) {
		this.weightedAverageRating = weightedAverageRating;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SolrUserData)) return false;

		SolrUserData that = (SolrUserData) o;

		if (mbo != that.mbo) return false;
		if (passedBackgroundCheck != that.passedBackgroundCheck) return false;
		if (passedDrugTest != that.passedDrugTest) return false;
		if (screeningStatus != that.screeningStatus) return false;
		if (hourlyRate != that.hourlyRate) return false;
		if (id != that.id) return false;
		if (!uuid.equals(that.getUuid())) return false;
		if (Float.compare(that.maxTravelDistance, maxTravelDistance) != 0) return false;
		if (abandonedLabelCount != null ? !abandonedLabelCount.equals(that.abandonedLabelCount) : that.abandonedLabelCount != null)
			return false;
		if (assessments != null ? !assessments.equals(that.assessments) : that.assessments != null) return false;
		if (avatarSmallAssetUri != null ? !avatarSmallAssetUri.equals(that.avatarSmallAssetUri) : that.avatarSmallAssetUri != null)
			return false;
		if (videoAssetUri != null ? !videoAssetUri.equals(that.videoAssetUri) : that.videoAssetUri != null)
			return false;
		if (averageStarRating != null ? !averageStarRating.equals(that.averageStarRating) : that.averageStarRating != null)
			return false;
		if (blockedCompanyIds != null ? !blockedCompanyIds.equals(that.blockedCompanyIds) : that.blockedCompanyIds != null)
			return false;
		if (blockedUserIds != null ? !blockedUserIds.equals(that.blockedUserIds) : that.blockedUserIds != null)
			return false;
		if (blocksCount != null ? !blocksCount.equals(that.blocksCount) : that.blocksCount != null) return false;
		if (cancelledLabelCount != null ? !cancelledLabelCount.equals(that.cancelledLabelCount) : that.cancelledLabelCount != null)
			return false;
		if (certifications != null ? !certifications.equals(that.certifications) : that.certifications != null)
			return false;
		if (city != null ? !city.equals(that.city) : that.city != null) return false;
		if (company != null ? !company.equals(that.company) : that.company != null) return false;
		if (completedWorkKeywords != null ? !completedWorkKeywords.equals(that.completedWorkKeywords) : that.completedWorkKeywords != null)
			return false;
		if (country != null ? !country.equals(that.country) : that.country != null) return false;
		if (createdOn != null ? !createdOn.equals(that.createdOn) : that.createdOn != null) return false;
		if (delayedLabelCount != null ? !delayedLabelCount.equals(that.delayedLabelCount) : that.delayedLabelCount != null)
			return false;
		if (groupData != null ? !groupData.equals(that.groupData) : that.groupData != null) return false;
		if (industries != null ? !industries.equals(that.industries) : that.industries != null) return false;
		if (insurances != null ? !insurances.equals(that.insurances) : that.insurances != null) return false;
		if (laneData != null ? !laneData.equals(that.laneData) : that.laneData != null) return false;
		if (lastAssignedWorkDate != null ? !lastAssignedWorkDate.equals(that.lastAssignedWorkDate) : that.lastAssignedWorkDate != null)
			return false;
		if (lastBackgroundCheckDate != null ? !lastBackgroundCheckDate.equals(that.lastBackgroundCheckDate) : that.lastBackgroundCheckDate != null)
			return false;
		if (lastDrugTestDate != null ? !lastDrugTestDate.equals(that.lastDrugTestDate) : that.lastDrugTestDate != null)
			return false;
		if (lateLabelCount != null ? !lateLabelCount.equals(that.lateLabelCount) : that.lateLabelCount != null)
			return false;
		if (licenses != null ? !licenses.equals(that.licenses) : that.licenses != null) return false;
		if (paidAssignmentsCount != null ? !paidAssignmentsCount.equals(that.paidAssignmentsCount) : that.paidAssignmentsCount != null)
			return false;
		if (point != null ? !point.equals(that.point) : that.point != null) return false;
		if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) return false;
		if (rating != null ? !rating.equals(that.rating) : that.rating != null) return false;
		if (recruitingData != null ? !recruitingData.equals(that.recruitingData) : that.recruitingData != null)
			return false;
		if (repeatClientsCount != null ? !repeatClientsCount.equals(that.repeatClientsCount) : that.repeatClientsCount != null)
			return false;
		if (skills != null ? !skills.equals(that.skills) : that.skills != null) return false;
		if (solrLinkedInData != null ? !solrLinkedInData.equals(that.solrLinkedInData) : that.solrLinkedInData != null)
			return false;
		if (specialtyNames != null ? !specialtyNames.equals(that.specialtyNames) : that.specialtyNames != null)
			return false;
		if (state != null ? !state.equals(that.state) : that.state != null) return false;
		if (timeZoneId != null ? !timeZoneId.equals(that.timeZoneId) : that.timeZoneId != null) return false;
		if (title != null ? !title.equals(that.title) : that.title != null) return false;
		if (toolNames != null ? !toolNames.equals(that.toolNames) : that.toolNames != null) return false;
		if (orgUnits != null ? !orgUnits.equals(that.orgUnits) : that.orgUnits != null) return false;
		if (userTags != null ? !userTags.equals(that.userTags) : that.userTags != null) return false;
		if (verificationIds != null ? !verificationIds.equals(that.verificationIds) : that.verificationIds != null)
			return false;
		if (workCancelledCount != null ? !workCancelledCount.equals(that.workCancelledCount) : that.workCancelledCount != null)
			return false;
		if (workCompletedForCompanies != null ? !workCompletedForCompanies.equals(that.workCompletedForCompanies) : that.workCompletedForCompanies != null)
			return false;
		if (weightedAverageRating != null ? !weightedAverageRating.equals(that.weightedAverageRating) : that.weightedAverageRating != null)
			return false;
		if (recentWorkingWeeksRatio != null ? !recentWorkingWeeksRatio.equals(that.recentWorkingWeeksRatio) : that.recentWorkingWeeksRatio != null)
			return false;
		if (jobFunctions != null ? !jobFunctions.equals(that.jobFunctions) : that.jobFunctions != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + uuid.hashCode();
		result = 31 * result + (company != null ? company.hashCode() : 0);
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		result = 31 * result + (licenses != null ? licenses.hashCode() : 0);
		result = 31 * result + (certifications != null ? certifications.hashCode() : 0);
		result = 31 * result + (assessments != null ? assessments.hashCode() : 0);
		result = 31 * result + (groupData != null ? groupData.hashCode() : 0);
		result = 31 * result + (industries != null ? industries.hashCode() : 0);
		result = 31 * result + hourlyRate;
		result = 31 * result + (rating != null ? rating.hashCode() : 0);
		result = 31 * result + (verificationIds != null ? verificationIds.hashCode() : 0);
		result = 31 * result + (laneData != null ? laneData.hashCode() : 0);
		result = 31 * result + (avatarSmallAssetUri != null ? avatarSmallAssetUri.hashCode() : 0);
		result = 31 * result + (videoAssetUri != null ? videoAssetUri.hashCode() : 0);
		result = 31 * result + (maxTravelDistance != +0.0f ? Float.floatToIntBits(maxTravelDistance) : 0);
		result = 31 * result + (skills != null ? skills.hashCode() : 0);
		result = 31 * result + (userTags != null ? userTags.hashCode() : 0);
		result = 31 * result + (lastDrugTestDate != null ? lastDrugTestDate.hashCode() : 0);
		result = 31 * result + (lastBackgroundCheckDate != null ? lastBackgroundCheckDate.hashCode() : 0);
		result = 31 * result + (insurances != null ? insurances.hashCode() : 0);
		result = 31 * result + (recruitingData != null ? recruitingData.hashCode() : 0);
		result = 31 * result + (point != null ? point.hashCode() : 0);
		result = 31 * result + (blockedUserIds != null ? blockedUserIds.hashCode() : 0);
		result = 31 * result + (blockedCompanyIds != null ? blockedCompanyIds.hashCode() : 0);
		result = 31 * result + screeningStatus;
		result = 31 * result + (solrLinkedInData != null ? solrLinkedInData.hashCode() : 0);
		result = 31 * result + (toolNames != null ? toolNames.hashCode() : 0);
		result = 31 * result + (orgUnits != null ? orgUnits.hashCode() : 0);
		result = 31 * result + (specialtyNames != null ? specialtyNames.hashCode() : 0);
		result = 31 * result + (timeZoneId != null ? timeZoneId.hashCode() : 0);
		result = 31 * result + (lastAssignedWorkDate != null ? lastAssignedWorkDate.hashCode() : 0);
		result = 31 * result + (workCancelledCount != null ? workCancelledCount.hashCode() : 0);
		result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
		result = 31 * result + (completedWorkKeywords != null ? completedWorkKeywords.hashCode() : 0);
		result = 31 * result + (averageStarRating != null ? averageStarRating.hashCode() : 0);
		result = 31 * result + (repeatClientsCount != null ? repeatClientsCount.hashCode() : 0);
		result = 31 * result + (blocksCount != null ? blocksCount.hashCode() : 0);
		result = 31 * result + (lateLabelCount != null ? lateLabelCount.hashCode() : 0);
		result = 31 * result + (abandonedLabelCount != null ? abandonedLabelCount.hashCode() : 0);
		result = 31 * result + (cancelledLabelCount != null ? cancelledLabelCount.hashCode() : 0);
		result = 31 * result + (delayedLabelCount != null ? delayedLabelCount.hashCode() : 0);
		result = 31 * result + (paidAssignmentsCount != null ? paidAssignmentsCount.hashCode() : 0);
		result = 31 * result + (recentWorkingWeeksRatio != null ? recentWorkingWeeksRatio.hashCode() : 0);
		result = 31 * result + (weightedAverageRating != null ? weightedAverageRating.hashCode() : 0);
		result = 31 * result + (workCompletedForCompanies != null ? workCompletedForCompanies.hashCode() : 0);
		result = 31 * result + (jobFunctions != null ? jobFunctions.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SolrUserData{" +
				"abandonedLabelCount=" + abandonedLabelCount +
				", id=" + id +
				", uuid=" + uuid +
				", company=" + company +
				", title='" + title + '\'' +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", postalCode='" + postalCode + '\'' +
				", country='" + country + '\'' +
				", licenses=" + licenses +
				", certifications=" + certifications +
				", assessments=" + assessments +
				", groupData=" + groupData +
				", industries=" + industries +
				", hourlyRate=" + hourlyRate +
				", rating=" + rating +
				", verificationIds=" + verificationIds +
				", laneData=" + laneData +
				", avatarSmallAssetUri='" + avatarSmallAssetUri + '\'' +
				", videoAssetUri='" + videoAssetUri + '\'' +
				", maxTravelDistance=" + maxTravelDistance +
				", skills=" + skills +
				", userTags=" + userTags +
				", lastDrugTestDate=" + lastDrugTestDate +
				", lastBackgroundCheckDate=" + lastBackgroundCheckDate +
				", insurances=" + insurances +
				", recruitingData=" + recruitingData +
				", point=" + point +
				", blockedUserIds=" + blockedUserIds +
				", blockedCompanyIds=" + blockedCompanyIds +
				", screeningStatus=" + screeningStatus +
				", solrLinkedInData=" + solrLinkedInData +
				", toolNames=" + toolNames +
				", orgUnits=" + orgUnits +
				", specialtyNames=" + specialtyNames +
				", timeZoneId='" + timeZoneId + '\'' +
				", lastAssignedWorkDate=" + lastAssignedWorkDate +
				", workCancelledCount=" + workCancelledCount +
				", createdOn=" + createdOn +
				", completedWorkKeywords=" + completedWorkKeywords +
				", averageStarRating=" + averageStarRating +
				", repeatClientsCount=" + repeatClientsCount +
				", blocksCount=" + blocksCount +
				", lateLabelCount=" + lateLabelCount +
				", cancelledLabelCount=" + cancelledLabelCount +
				", delayedLabelCount=" + delayedLabelCount +
				", paidAssignmentsCount=" + paidAssignmentsCount +
				", workCompletedForCompanies=" + workCompletedForCompanies +
				", mbo=" + mbo +
				", mboStatus=" + mboStatus +
				", passedBackgroundCheck=" + passedBackgroundCheck +
				", passedDrugTest=" + passedDrugTest +
				", warpRequisitionId=" + warpRequisitionId +
				", recentWorkingWeeksRatio=" + recentWorkingWeeksRatio +
				", weightedAverageRating=" + weightedAverageRating +
				", jobFunctions=" + jobFunctions +
				'}';
	}
}
