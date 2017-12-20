package com.workmarket.search.response.user;

import com.google.common.collect.Maps;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.model.SolrBaseUserData;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.search.SearchError;
import com.workmarket.search.request.user.RecruitingCampaign;
import com.workmarket.search.request.user.Verification;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.RatingStarsHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeopleSearchResult extends SolrBaseUserData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private SolrUserType userType;
	private String uuid;
	private com.workmarket.thrift.core.Name name;
	private String jobTitle;
	private String companyName;
	private String companyStatusType;
	private long companyId;
	private com.workmarket.thrift.core.Address address;
	private RecruitingCampaign recruitingCampaign;
	private LaneType lane;
	private com.workmarket.thrift.core.RatingSummary rating;
	private com.workmarket.thrift.core.RatingSummary companyRating;
	private String smallAvatarAssetUUID;
	private String smallAvatarAssetUri;
	private GeoPoint locationPoint;
	private double distance;
	private List<Verification> verifications;
	private List<String> certifications;
	private List<String> licenses;
	private List<String> companyAssessments;
	private List<String> insurances;
	private List<String> groups;
	private List<String> companyTags;
	private boolean blocked;
	private int rank;
	private String lane2ApprovalStatus;
	private List<SearchError> searchErrors;
	private String lastBackgroundCheckDate;
	private String lastDrugTestDate;
	private Integer workCompletedForSearchCompanyCount;
	private Integer workCancelledCount;
	private Integer workAbandonedCount;
	private String lastAssignedWorkDate;
	private String createdOn;
	private double score;
	private boolean confirmedBankAccount;
	private boolean approvedTIN;
	private String groupMemberStatus;
	private String assessmentStatus;
	private Map<Long, Integer> companyPaidCounts; // maps # of paid assignments by company
	private String mboStatus;
	private boolean mbo;
	private List<Long> groupIds;
	private List<Long> passedAssessmentIds;
	private List<Long> contractIds;
	private String videoAssetUri;
	private List<Long> licenseIds;
	private List<Long> insuranceIds;
	private List<Long> industryIds;
	private List<Long> certificationIds;
	private String country;
	private Map<Long, List<LaneType>> companyLaneTypes;
	private Long companyType;
	private List<String> skillNames;
	private List<String> toolNames;
	private float maxTravelDistance;
	private Eligibility eligibility;

	public PeopleSearchResult() {}

	public long getUserId() {
		return this.userId;
	}

	public PeopleSearchResult setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public SolrUserType getUserType() {
		return userType;
	}

	public PeopleSearchResult setUserType(SolrUserType userType) {
		this.userType = userType;
		return this;
	}

	public String getUuid() {
		return uuid;
	}

	public PeopleSearchResult setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public float getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public void setMaxTravelDistance(float maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	public List<String> getToolNames() {
		return toolNames;
	}

	public void setToolNames(List<String> toolNames) {
		this.toolNames = toolNames;
	}

	public List<String> getSkillNames() {
		return skillNames;
	}

	public void setSkillNames(List<String> skillNames) {
		this.skillNames = skillNames;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public Long getCompanyType() {
		return companyType;
	}

	public void setCompanyType(Long companyType) {
		this.companyType = companyType;
	}

	public Map<Long, List<LaneType>> getCompanyLaneTypes() {
		return companyLaneTypes;
	}

	public void setCompanyLaneTypes(Map<Long, List<LaneType>> companyLaneTypes) {
		this.companyLaneTypes = companyLaneTypes;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<Long> getInsuranceIds() {
		return insuranceIds;
	}

	public void setInsuranceIds(List<Long> insuranceIds) {
		this.insuranceIds = insuranceIds;
	}

	public List<Long> getIndustryIds() {
		return industryIds;
	}

	public void setIndustryIds(List<Long> industryIds) {
		this.industryIds = industryIds;
	}

	public List<Long> getCertificationIds() {
		return certificationIds;
	}

	public void setCertificationIds(List<Long> certificationIds) {
		this.certificationIds = certificationIds;
	}

	public List<Long> getLicenseIds() {
		return licenseIds;
	}

	public void setLicenseIds(List<Long> licenseIds) {
		this.licenseIds = licenseIds;
	}

	public List<Long> getContractIds() {
		return contractIds;
	}

	public void setContractIds(List<Long> contractIds) {
		this.contractIds = contractIds;
	}

	public List<Long> getPassedAssessmentIds() {
		return passedAssessmentIds;
	}

	public void setPassedAssessmentIds(List<Long> passedAssessmentIds) {
		this.passedAssessmentIds = passedAssessmentIds;
	}

	public List<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}

	public Map<Long, Integer> getCompanyPaidCounts() {
		return companyPaidCounts;
	}

	public void setCompanyPaidCounts(Map<Long, Integer> companyPaidCounts) {
		this.companyPaidCounts = companyPaidCounts;
	}

	public void addToCompanyPaidCounts(Long companyId, Integer count) {
		if (this.companyPaidCounts == null) {
			this.companyPaidCounts = Maps.newHashMap();
		}
		this.companyPaidCounts.put(companyId, count);
	}

	public com.workmarket.thrift.core.Name getName() {
		return this.name;
	}

	public PeopleSearchResult setName(com.workmarket.thrift.core.Name name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getJobTitle() {
		return this.jobTitle;
	}

	public PeopleSearchResult setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
		return this;
	}

	public boolean isSetJobTitle() {
		return this.jobTitle != null;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public PeopleSearchResult setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public boolean isSetCompanyName() {
		return this.companyName != null;
	}

	public String getCompanyStatusType() {
		return companyStatusType;
	}

	public void setCompanyStatusType(String companyStatusType) {
		this.companyStatusType = companyStatusType;
	}

	public com.workmarket.thrift.core.Address getAddress() {
		return this.address;
	}

	public PeopleSearchResult setAddress(com.workmarket.thrift.core.Address address) {
		this.address = address;
		return this;
	}

	public boolean isSetAddress() {
		return this.address != null;
	}

	public RecruitingCampaign getRecruitingCampaign() {
		return this.recruitingCampaign;
	}

	public PeopleSearchResult setRecruitingCampaign(RecruitingCampaign recruitingCampaign) {
		this.recruitingCampaign = recruitingCampaign;
		return this;
	}

	public boolean isSetRecruitingCampaign() {
		return this.recruitingCampaign != null;
	}

	public LaneType getLane() {
		return this.lane;
	}

	public PeopleSearchResult setLane(LaneType lane) {
		this.lane = lane;
		return this;
	}

	public boolean isSetLane() {
		return this.lane != null;
	}

	public com.workmarket.thrift.core.RatingSummary getRating() {
		return this.rating;
	}

	public PeopleSearchResult setRating(com.workmarket.thrift.core.RatingSummary rating) {
		this.rating = rating;
		return this;
	}

	public boolean isSetRating() {
		return this.rating != null;
	}

	public com.workmarket.thrift.core.RatingSummary getCompanyRating() {
		return this.companyRating;
	}

	public PeopleSearchResult setCompanyRating(com.workmarket.thrift.core.RatingSummary companyRating) {
		this.companyRating = companyRating;
		return this;
	}

	public boolean isSetCompanyRating() {
		return this.companyRating != null;
	}

	public String getSmallAvatarAssetUUID() {
		return this.smallAvatarAssetUUID;
	}

	public PeopleSearchResult setSmallAvatarAssetUUID(String smallAvatarAssetUUID) {
		this.smallAvatarAssetUUID = smallAvatarAssetUUID;
		return this;
	}

	public boolean isSetSmallAvatarAssetUUID() {
		return this.smallAvatarAssetUUID != null;
	}

	public GeoPoint getLocationPoint() {
		return this.locationPoint;
	}

	public PeopleSearchResult setLocationPoint(GeoPoint locationPoint) {
		this.locationPoint = locationPoint;
		return this;
	}

	public boolean isSetLocationPoint() {
		return this.locationPoint != null;
	}

	public int getVerificationsSize() {
		return (this.verifications == null) ? 0 : this.verifications.size();
	}

	public java.util.Iterator<Verification> getVerificationsIterator() {
		return (this.verifications == null) ? null : this.verifications.iterator();
	}

	public void addToVerifications(Verification elem) {
		if (this.verifications == null) {
			this.verifications = new ArrayList<>();
		}
		this.verifications.add(elem);
	}

	public List<Verification> getVerifications() {
		return this.verifications;
	}

	public PeopleSearchResult setVerifications(List<Verification> verifications) {
		this.verifications = verifications;
		return this;
	}

	public boolean isSetVerifications() {
		return this.verifications != null;
	}

	public int getCertificationsSize() {
		return (this.certifications == null) ? 0 : this.certifications.size();
	}

	public java.util.Iterator<String> getCertificationsIterator() {
		return (this.certifications == null) ? null : this.certifications.iterator();
	}

	public void addToCertifications(String elem) {
		if (this.certifications == null) {
			this.certifications = new ArrayList<>();
		}
		this.certifications.add(elem);
	}

	public void addToPassedAssessments(Long id) {
		if (this.passedAssessmentIds == null) {
			this.passedAssessmentIds = new ArrayList<>();
		}
		this.passedAssessmentIds.add(id);
	}

	public void addToContracts(Long id) {
		if (this.contractIds == null) {
			this.contractIds = new ArrayList<>();
		}
		this.contractIds.add(id);
	}

	public void addToLicenseIds(Long id) {
		if (this.licenseIds == null) {
			this.licenseIds = new ArrayList<>();
		}
		this.licenseIds.add(id);
	}

	public void addToIndustryIds(Long id) {
		if (this.industryIds == null) {
			this.industryIds = new ArrayList<>();
		}
		this.industryIds.add(id);
	}

	public void addToInsuranceIds(Long id) {
		if (this.insuranceIds == null) {
			this.insuranceIds = new ArrayList<>();
		}
		this.insuranceIds.add(id);
	}

	public void addToCertificationIds(Long id) {
		if (this.certificationIds == null) {
			this.certificationIds = new ArrayList<>();
		}
		this.certificationIds.add(id);
	}

	public void addToCompanyLaneTypes(Long companyId, LaneType laneType) {
		if (this.companyLaneTypes == null) {
			this.companyLaneTypes = new HashMap<>();
		}

		if (MapUtils.getObject(this.companyLaneTypes, companyId) == null) {
			this.companyLaneTypes.put(companyId, new ArrayList<LaneType>());
		}

		List<LaneType> types = this.companyLaneTypes.get(companyId);

		types.add(laneType);

		this.companyLaneTypes.put(companyId, types);
	}

	public List<String> getCertifications() {
		return this.certifications;
	}

	public PeopleSearchResult setCertifications(List<String> certifications) {
		this.certifications = certifications;
		return this;
	}

	public boolean isSetCertifications() {
		return this.certifications != null;
	}

	public void addToLicenses(String elem) {
		if (this.licenses == null) {
			this.licenses = new ArrayList<>();
		}
		this.licenses.add(elem);
	}

	public List<String> getLicenses() {
		if (!CollectionUtils.isEmpty(licenses)) Collections.sort(licenses, String.CASE_INSENSITIVE_ORDER);
		return licenses;
	}

	public void setLicenses(List<String> licenses) {
		this.licenses = licenses;
	}

	public int getInsurancesSize() {
		return (this.insurances == null) ? 0 : this.insurances.size();
	}

	public java.util.Iterator<String> getInsurancesIterator() {
		return (this.insurances == null) ? null : this.insurances.iterator();
	}

	public void addToInsurances(String elem) {
		if (this.insurances == null) {
			this.insurances = new ArrayList<>();
		}
		this.insurances.add(elem);
	}

	public List<String> getInsurances() {
		return this.insurances;
	}

	public PeopleSearchResult setInsurances(List<String> insurances) {
		this.insurances = insurances;
		return this;
	}

	public boolean isSetInsurances() {
		return this.insurances != null;
	}

	public int getGroupsSize() {
		return (this.groups == null) ? 0 : this.groups.size();
	}

	public java.util.Iterator<String> getGroupsIterator() {
		return (this.groups == null) ? null : this.groups.iterator();
	}

	public void addToGroups(String elem) {
		if (this.groups == null) {
			this.groups = new ArrayList<>();
		}
		this.groups.add(elem);
	}

	public void addToCompanyAssessments(String elem) {
		if (this.companyAssessments == null) {
			this.companyAssessments = new ArrayList<>();
		}
		this.companyAssessments.add(elem);
	}

	public List<String> getGroups() {
		return this.groups;
	}

	public PeopleSearchResult setGroups(List<String> groups) {
		this.groups = groups;
		return this;
	}

	public boolean isSetGroups() {
		return this.groups != null;
	}

	public boolean isBlocked() {
		return this.blocked;
	}

	public PeopleSearchResult setBlocked(boolean blocked) {
		this.blocked = blocked;
		return this;
	}

	public int getRank() {
		return this.rank;
	}

	public PeopleSearchResult setRank(int rank) {
		this.rank = rank;
		return this;
	}

	public boolean isSetRank() {
		return (rank > 0);
	}

	public String getLane2ApprovalStatus() {
		return this.lane2ApprovalStatus;
	}

	public PeopleSearchResult setLane2ApprovalStatus(String lane2ApprovalStatus) {
		this.lane2ApprovalStatus = lane2ApprovalStatus;
		return this;
	}

	public boolean isSetLane2ApprovalStatus() {
		return this.lane2ApprovalStatus != null;
	}

	public int getSearchErrorsSize() {
		return (this.searchErrors == null) ? 0 : this.searchErrors.size();
	}

	public java.util.Iterator<SearchError> getSearchErrorsIterator() {
		return (this.searchErrors == null) ? null : this.searchErrors.iterator();
	}

	public void addToSearchErrors(SearchError elem) {
		if (this.searchErrors == null) {
			this.searchErrors = new ArrayList<>();
		}
		this.searchErrors.add(elem);
	}

	public List<SearchError> getSearchErrors() {
		return this.searchErrors;
	}

	public PeopleSearchResult setSearchErrors(List<SearchError> searchErrors) {
		this.searchErrors = searchErrors;
		return this;
	}

	public boolean isSetSearchErrors() {
		return this.searchErrors != null;
	}

	public double getDistance() {
		return this.distance;
	}

	public PeopleSearchResult setDistance(double distance) {
		this.distance = distance;
		return this;
	}

	public boolean isSetDistance() {
		return (distance > 0D);
	}

	public String getSmallAvatarAssetUri() {
		return this.smallAvatarAssetUri;
	}

	public PeopleSearchResult setSmallAvatarAssetUri(String smallAvatarAssetUri) {
		this.smallAvatarAssetUri = smallAvatarAssetUri;
		return this;
	}

	public boolean isSetSmallAvatarAssetUri() {
		return this.smallAvatarAssetUri != null;
	}

	public String getVideoAssetUri() {
		return this.videoAssetUri;
	}

	public PeopleSearchResult setVideoAssetUri(String videoAssetUri) {
		this.videoAssetUri = videoAssetUri;
		return this;
	}

	public String getLastBackgroundCheckDate() {
		return lastBackgroundCheckDate;
	}

	public void setLastBackgroundCheckDate(String lastBackgroundCheckDate) {
		this.lastBackgroundCheckDate = lastBackgroundCheckDate;
	}

	public String getLastDrugTestDate() {
		return lastDrugTestDate;
	}

	public void setLastDrugTestDate(String lastDrugTestDate) {
		this.lastDrugTestDate = lastDrugTestDate;
	}

	public boolean isSetLastDrugTestDate() {
		return this.lastDrugTestDate != null;
	}

	public boolean isSetLastBackgroundCheckDate() {
		return this.lastBackgroundCheckDate != null;
	}

	public List<String> getCompanyAssessments() {
		if (!CollectionUtils.isEmpty(companyAssessments))
			Collections.sort(companyAssessments, String.CASE_INSENSITIVE_ORDER);
		return companyAssessments;
	}

	public void setCompanyAssessments(List<String> companyAssessments) {
		this.companyAssessments = companyAssessments;
	}

	public boolean isSetCompanyAssessments() {
		return CollectionUtils.isNotEmpty(companyAssessments);
	}

	public List<String> getCompanyTags() {
		if (!CollectionUtils.isEmpty(companyTags)) Collections.sort(companyTags, String.CASE_INSENSITIVE_ORDER);
		return companyTags;
	}

	public void setCompanyTags(List<String> companyTags) {
		this.companyTags = companyTags;
	}

	public void addToCompanyTags(List<String> elemList) {
		if (this.companyTags == null) {
			this.companyTags = new ArrayList<>();
		}
		this.companyTags.addAll(elemList);
	}

	public Integer getWorkCancelledCount() {
		return workCancelledCount;
	}

	public void setWorkCancelledCount(Integer workCancelledCount) {
		this.workCancelledCount = workCancelledCount;
	}

	public boolean isSetWorkCancelledCount() {
		return this.workCancelledCount != null;
	}

	public Integer getWorkAbandonedCount() {
		return workAbandonedCount;
	}

	public void setWorkAbandonedCount(Integer workAbandonedCount) {
		this.workAbandonedCount = workAbandonedCount;
	}

	public Integer getWorkCompletedForSearchCompanyCount() {
		return workCompletedForSearchCompanyCount;
	}

	public void setWorkCompletedForSearchCompanyCount(Integer workCompletedForSearchCompanyCount) {
		this.workCompletedForSearchCompanyCount = workCompletedForSearchCompanyCount;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getFormattedCreatedOn() {
		if (StringUtils.isBlank(createdOn)) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.format("MM/dd/yyyy", createdOn);
	}

	public String getFormattedLastDrugTestDate() {
		if (StringUtils.isBlank(lastDrugTestDate)) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.format("MM/dd/yyyy", lastDrugTestDate);
	}

	public String getFormattedLastBackgroundCheckDate() {
		if (StringUtils.isBlank(lastBackgroundCheckDate)) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.format("MM/dd/yyyy", lastBackgroundCheckDate);
	}

	public String getFormattedLastAssignedWorkDate() {
		if (StringUtils.isBlank(lastAssignedWorkDate)) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.format("MM/dd/yyyy", lastAssignedWorkDate);
	}

	public String getLastAssignedWorkDate() {
		return lastAssignedWorkDate;
	}

	public void setLastAssignedWorkDate(String lastAssignedWorkDate) {
		this.lastAssignedWorkDate = lastAssignedWorkDate;
	}

	public boolean isApprovedTIN() {
		return approvedTIN;
	}

	public void setApprovedTIN(boolean approvedTIN) {
		this.approvedTIN = approvedTIN;
	}

	public boolean isConfirmedBankAccount() {
		return confirmedBankAccount;
	}

	public void setConfirmedBankAccount(boolean confirmedBankAccount) {
		this.confirmedBankAccount = confirmedBankAccount;
	}

	public String getGroupMemberStatus() {
		return groupMemberStatus;
	}

	public void setGroupMemberStatus(String groupMemberStatus) {
		this.groupMemberStatus = groupMemberStatus;
	}

	public String getAssessmentStatus() {
		return assessmentStatus;
	}

	public void setAssessmentStatus(String assessmentStatus) {
		this.assessmentStatus = assessmentStatus;
	}

	public String getMboStatus() {
		return mboStatus;
	}

	public void setMboStatus(String mboStatus) {
		this.mboStatus = mboStatus;
	}

	public boolean isMbo() {
		return mbo;
	}

	public void setMbo(boolean mbo) {
		this.mbo = mbo;
	}

	public void setEligibility(Eligibility eligibility) { this.eligibility = eligibility; }

	public Eligibility getEligibility() { return this.eligibility; }

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return "PeopleSearchResult{" +
				", userId=" + userId +
				", userNumber='" + getUserNumber() + '\'' +
				", onTimePercentage=" + getOnTimePercentage() +
				", deliverableOnTimePercentage=" + getDeliverableOnTimePercentage() +
				", score=" + score +
				'}';
	}

	public Map<String, Object> toObjectMap(boolean isInstantWorkerPoolEnabled, boolean isInternalUser) {
		Map<String, Object> objectMap = CollectionUtilities.newObjectMap(
				"position", this.getRank(),
				"score", MathUtils.round(this.getScore(), 4),
				"id", this.getUserId(),
				"userType", this.getUserType().name(),
				"userNumber", this.getUserNumber(),
				"first_name", StringUtilities.toPrettyName(this.getName().getFirstName()),
				"last_name", StringUtilities.toPrettyName(this.getName().getLastName()),
				"email", this.getEmail(),
				"avatar_asset_uri", this.getSmallAvatarAssetUri(),
				"job_title", this.getJobTitle(),
				"company_name", this.getCompanyName(),
				"city", StringUtilities.toPrettyName(this.getAddress().getCity()),
				"state", this.getAddress().getState(),
				"postal_code", this.getAddress().getZip(),
				"country", StringUtilities.toPrettyName(com.workmarket.domains.model.postalcode.Country.valueOf(this.getAddress().getCountry()).getName()),
				"latitude", this.getLocationPoint().getLatitude(),
				"longitude", this.getLocationPoint().getLongitude(),
				"lane", this.getLane().getValue(),
				"laneString", this.getLane().getDescription(),
				"lane2_approval_status", this.getLane2ApprovalStatus(),
				"recruiting_campaign_id", (this.getRecruitingCampaign() != null) ? this.getRecruitingCampaign().getId() : null,
				"recruiting_campaign_name", (this.getRecruitingCampaign() != null) ? this.getRecruitingCampaign().getName() : null,
				"rating", this.getSatisfactionRate(),
				"rating_text", RatingStarsHelper.convertRatingValueToText((this.getRating() != null) ? this.getRating().getRating() : 0),
				"rating_count", (this.getRating() != null) ? this.getRating().getNumberOfRatings() : 0,
				"company_rating", (this.getCompanyRating() != null) ? this.getCompanyRating().getRating() : 0,
				"company_rating_text", RatingStarsHelper.convertRatingValueToText((this.getCompanyRating() != null) ? this.getCompanyRating().getRating() : 0),
				"company_rating_count", (this.getCompanyRating() != null) ? this.getCompanyRating().getNumberOfRatings() : 0,
				"background_check", CollectionUtils.isNotEmpty(this.getVerifications()) && this.getVerifications().contains(Verification.BACKGROUND_CHECK),
				"background_check_failed", CollectionUtils.isNotEmpty(this.getVerifications()) && this.getVerifications().contains(Verification.FAILED_BACKGROUND_CHECK),
				"background_check_date", this.getFormattedLastBackgroundCheckDate(),
				"last_assigned_work_date", this.getFormattedLastAssignedWorkDate(),
				"drug_test", CollectionUtils.isNotEmpty(this.getVerifications()) && this.getVerifications().contains(Verification.DRUG_TEST),
				"drug_test_failed", CollectionUtils.isNotEmpty(this.getVerifications()) && this.getVerifications().contains(Verification.FAILED_DRUG_TEST),
				"drug_test_date", this.getFormattedLastDrugTestDate(),
				"ontime_reliability", BigDecimal.valueOf(this.getOnTimePercentage()).movePointRight(2).setScale(2, RoundingMode.HALF_UP),
				"deliverable_on_time_reliability", BigDecimal.valueOf(this.getDeliverableOnTimePercentage()).movePointRight(2).setScale(2, RoundingMode.HALF_UP),
				"blocked", this.isBlocked(),
				"certifications", this.getCertifications(),
				"insurances", this.getInsurances(),
				"licenses", this.getLicenses(),
				"company_assessments", this.getCompanyAssessments(),
				"company_tags", this.getCompanyTags(),
				"groups", this.getGroups(),
				"instant_worker_pool", isInstantWorkerPoolEnabled,
				"distance", this.isSetDistance() ? this.getDistance() : org.apache.commons.lang.StringUtils.EMPTY,
				"work_completed_count", this.getWorkCompletedCount(),
				"abandoned_count", this.getWorkAbandonedCount(),
				"work_completed_company_count", this.getWorkCompletedForSearchCompanyCount(),
				"work_cancelled_count", this.getWorkCancelledCount(),
				"created_on", this.getFormattedCreatedOn(),
				"derivedStatus", this.getGroupMemberStatus(),
				"assessmentStatus", this.getAssessmentStatus(),
				"internal", isInternalUser,
				"mbo", this.isMbo(),
				"mbo_status", this.getMboStatus()
		);

		if (this.getEligibility() != null) {
			objectMap.put("eligibility", this.getEligibility());
		}

		return objectMap;
	}
}
