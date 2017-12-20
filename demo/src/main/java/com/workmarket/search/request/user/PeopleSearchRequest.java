package com.workmarket.search.request.user;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.SearchRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class PeopleSearchRequest extends SearchRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private long masqueradeUserId;
	private Set<Long> groupFilter;
	private boolean groupMemberFilter;
	private boolean groupOverrideMemberFilter;
	private boolean groupPendingFilter;
	private boolean groupPendingFailedFilter;
	private boolean groupInvitedFilter;
	private boolean groupDeclinedFilter;
	private Set<LaneType> laneFilter;
	private Set<RatingsChoice> ratingsFilter;
	private Set<BackgroundScreeningChoice> backgroundScreeningFilter;
	private Set<Long> testFilter;
	private Set<Long> certificationFilter;
	private Set<String> stateLicenseFilter;
	private Set<Long> industryFilter;
	private Set<CompanyType> companyTypeFilter = Sets.newHashSet();
	private Set<Long> skillFilter;
	private Set<String> skillNamesFilter;
	private Set<Long> insuranceFilter;
	private Set<Long> assessmentFilter; //only passed tests, no surveys
	private Set<Long> invitedAssessmentFilter;
	private Set<Long> notInvitedAssessmentFilter;
	private Set<Long> passedAssessmentFilter;
	private Set<Long> failedTestFilter;
	private Pagination paginationRequest;
	private Set<Long> companyFilter;
	private Set<Long> declinedCompanyFilter;
	private NumericFilter workersCompCoverageFilter;
	private NumericFilter generalLiabilityCoverageFilter;
	private NumericFilter errorsAndOmissionsCoverageFilter;
	private NumericFilter automobileCoverageFilter;
	private NumericFilter contractorsCoverageFilter;
	private NumericFilter businessLiabilityCoverageFilter;
	private NumericFilter commercialGeneralLiabilityCoverageFilter;
	private boolean avatarFilter;
	private boolean videoFilter;
	private boolean exportSearch = false;
	private Long invitedToWorkIdFilter;
	private long currentAssessmentId;
	private Set<AbstractRequirement> requirements;
	private boolean mboFilter;
	private Set<Long> userIds; // filter results for these users
	private Long laneFilterCompanyId;
	private boolean noFacetsFlag;
	private List<Long> networkIds;
	private Set<Long> sharedGroupFilter;
	private boolean disableMarketplace = false;
	private SearchType searchType = SearchType.PEOPLE_SEARCH;
	private Set<SolrUserType> userTypeFilter = Sets.newHashSet(SolrUserType.WORKER);

	public PeopleSearchRequest() {
	}

	public Set<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(Set<Long> userIds) {
		this.userIds = userIds;
	}

	public void addToUserIds(Long userId) {
		if (this.userIds == null) {
			this.userIds = new HashSet<>();
		}

		this.userIds.add(userId);
	}

	public Long getLaneFilterCompanyId() {
		return laneFilterCompanyId;
	}

	public void setLaneFilterCompanyId(Long laneFilterCompanyId) {
		this.laneFilterCompanyId = laneFilterCompanyId;
	}

	public Set<AbstractRequirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(Set<AbstractRequirement> requirements) {
		this.requirements = requirements;
	}

	public void addToRequirements(AbstractRequirement requirement) {
		if (this.requirements == null) {
			this.requirements = new HashSet<>();
		}
		this.requirements.add(requirement);
	}

	public PeopleSearchRequest setUserId(long userId) {
		super.setUserId(userId);
		return this;
	}

	// For CSV export email
	public long getMasqueradeUserId() {
		return masqueradeUserId;
	}

	public PeopleSearchRequest setMasqueradeUserId(long masqueradeUserId) {
		this.masqueradeUserId = masqueradeUserId;
		return this;
	}

	public boolean isSetUserId() {
		return (getUserId() > 0L);
	}

	public boolean isSetKeyword() {
		return this.getKeyword() != null;
	}

	public int getGroupFilterSize() {
		return (this.groupFilter == null) ? 0 : this.groupFilter.size();
	}

	public java.util.Iterator<Long> getGroupFilterIterator() {
		return (this.groupFilter == null) ? null : this.groupFilter.iterator();
	}

	public void addToGroupFilter(long elem) {
		if (this.groupFilter == null) {
			this.groupFilter = new HashSet<>();
		}
		this.groupFilter.add(elem);
	}

	public Set<Long> getGroupFilter() {
		return this.groupFilter;
	}

	public PeopleSearchRequest setGroupFilter(Set<Long> groupFilter) {
		this.groupFilter = groupFilter;
		return this;
	}

	public boolean isSetGroupFilter() {
		return this.groupFilter != null;
	}

	public boolean isGroupMemberFilter() {
		return groupMemberFilter;
	}

	public PeopleSearchRequest setGroupMemberFilter(boolean groupMemberFilter) {
		this.groupMemberFilter = groupMemberFilter;
		return this;
	}

	public boolean isGroupOverrideMemberFilter() {
		return groupOverrideMemberFilter;
	}

	public PeopleSearchRequest setGroupOverrideMemberFilter(boolean groupOverrideMemberFilter) {
		this.groupOverrideMemberFilter = groupOverrideMemberFilter;
		return this;
	}

	public boolean isGroupPendingFilter() {
		return groupPendingFilter;
	}

	public PeopleSearchRequest setGroupPendingFilter(boolean groupPendingFilter) {
		this.groupPendingFilter = groupPendingFilter;
		return this;
	}

	public boolean isGroupPendingFailedFilter() {
		return groupPendingFailedFilter;
	}

	public PeopleSearchRequest setGroupPendingFailedFilter(boolean groupPendingFailedFilter) {
		this.groupPendingFailedFilter = groupPendingFailedFilter;
		return this;
	}

	public boolean isGroupInvitedFilter() {
		return groupInvitedFilter;
	}

	public PeopleSearchRequest setGroupInvitedFilter(boolean groupInvitedFilter) {
		this.groupInvitedFilter = groupInvitedFilter;
		return this;
	}

	public boolean isGroupDeclinedFilter() {
		return groupDeclinedFilter;
	}

	public PeopleSearchRequest setGroupDeclinedFilter(boolean groupDeclinedFilter) {
		this.groupDeclinedFilter = groupDeclinedFilter;
		return this;
	}

	public int getLaneFilterSize() {
		return (this.laneFilter == null) ? 0 : this.laneFilter.size();
	}

	public java.util.Iterator<LaneType> getLaneFilterIterator() {
		return (this.laneFilter == null) ? null : this.laneFilter.iterator();
	}

	public void addToLaneFilter(LaneType elem) {
		if (this.laneFilter == null) {
			this.laneFilter = new HashSet<>();
		}
		this.laneFilter.add(elem);
	}

	public Set<LaneType> getLaneFilter() {
		return this.laneFilter;
	}

	public PeopleSearchRequest setLaneFilter(Set<LaneType> laneFilter) {
		this.laneFilter = laneFilter;
		return this;
	}

	public boolean isSetLaneFilter() {
		return this.laneFilter != null;
	}

	public int getRatingsFilterSize() {
		return (this.ratingsFilter == null) ? 0 : this.ratingsFilter.size();
	}

	public java.util.Iterator<RatingsChoice> getRatingsFilterIterator() {
		return (this.ratingsFilter == null) ? null : this.ratingsFilter.iterator();
	}

	public void addToRatingsFilter(RatingsChoice elem) {
		if (this.ratingsFilter == null) {
			this.ratingsFilter = new HashSet<>();
		}
		this.ratingsFilter.add(elem);
	}

	public Set<RatingsChoice> getRatingsFilter() {
		return this.ratingsFilter;
	}

	public PeopleSearchRequest setRatingsFilter(Set<RatingsChoice> ratingsFilter) {
		this.ratingsFilter = ratingsFilter;
		return this;
	}

	public boolean isSetRatingsFilter() {
		return this.ratingsFilter != null;
	}

	public int getBackgroundScreeningFilterSize() {
		return (this.backgroundScreeningFilter == null) ? 0 : this.backgroundScreeningFilter.size();
	}

	public java.util.Iterator<BackgroundScreeningChoice> getBackgroundScreeningFilterIterator() {
		return (this.backgroundScreeningFilter == null) ? null : this.backgroundScreeningFilter.iterator();
	}

	public void addToBackgroundScreeningFilter(BackgroundScreeningChoice elem) {
		if (this.backgroundScreeningFilter == null) {
			this.backgroundScreeningFilter = new HashSet<>();
		}
		this.backgroundScreeningFilter.add(elem);
	}

	public Set<BackgroundScreeningChoice> getBackgroundScreeningFilter() {
		return this.backgroundScreeningFilter;
	}

	public PeopleSearchRequest setBackgroundScreeningFilter(Set<BackgroundScreeningChoice> backgroundScreeningFilter) {
		this.backgroundScreeningFilter = backgroundScreeningFilter;
		return this;
	}

	public boolean isSetBackgroundScreeningFilter() {
		return this.backgroundScreeningFilter != null;
	}

	public int getTestFilterSize() {
		return (this.testFilter == null) ? 0 : this.testFilter.size();
	}

	public java.util.Iterator<Long> getTestFilterIterator() {
		return (this.testFilter == null) ? null : this.testFilter.iterator();
	}

	public void addToTestFilter(long elem) {
		if (this.testFilter == null) {
			this.testFilter = new HashSet<>();
		}
		this.testFilter.add(elem);
	}

	public Set<Long> getTestFilter() {
		return this.testFilter;
	}

	public PeopleSearchRequest setTestFilter(Set<Long> testFilter) {
		this.testFilter = testFilter;
		return this;
	}

	public boolean isSetTestFilter() {
		return this.testFilter != null;
	}

	public int getCertificationFilterSize() {
		return (this.certificationFilter == null) ? 0 : this.certificationFilter.size();
	}

	public java.util.Iterator<Long> getCertificationFilterIterator() {
		return (this.certificationFilter == null) ? null : this.certificationFilter.iterator();
	}

	public void addToCertificationFilter(long elem) {
		if (this.certificationFilter == null) {
			this.certificationFilter = new HashSet<>();
		}
		this.certificationFilter.add(elem);
	}

	public Set<Long> getCertificationFilter() {
		return this.certificationFilter;
	}

	public PeopleSearchRequest setCertificationFilter(Set<Long> certificationFilter) {
		this.certificationFilter = certificationFilter;
		return this;
	}

	public boolean isSetCertificationFilter() {
		return this.certificationFilter != null;
	}

	public int getStateLicenseFilterSize() {
		return (this.stateLicenseFilter == null) ? 0 : this.stateLicenseFilter.size();
	}

	public java.util.Iterator<String> getStateLicenseFilterIterator() {
		return (this.stateLicenseFilter == null) ? null : this.stateLicenseFilter.iterator();
	}

	public void addToStateLicenseFilter(String elem) {
		if (this.stateLicenseFilter == null) {
			this.stateLicenseFilter = new HashSet<>();
		}
		this.stateLicenseFilter.add(elem);
	}

	public Set<String> getStateLicenseFilter() {
		return this.stateLicenseFilter;
	}

	public PeopleSearchRequest setStateLicenseFilter(Set<String> stateLicenseFilter) {
		this.stateLicenseFilter = stateLicenseFilter;
		return this;
	}

	public boolean isSetStateLicenseFilter() {
		return this.stateLicenseFilter != null;
	}

	public int getIndustryFilterSize() {
		return (this.industryFilter == null) ? 0 : this.industryFilter.size();
	}

	public java.util.Iterator<Long> getIndustryFilterIterator() {
		return (this.industryFilter == null) ? null : this.industryFilter.iterator();
	}

	public void addToIndustryFilter(long elem) {
		if (this.industryFilter == null) {
			this.industryFilter = new HashSet<>();
		}
		this.industryFilter.add(elem);
	}

	public Set<Long> getIndustryFilter() {
		return this.industryFilter;
	}

	public PeopleSearchRequest setIndustryFilter(Set<Long> industryFilter) {
		this.industryFilter = industryFilter;
		return this;
	}

	public boolean isSetIndustryFilter() {
		return this.industryFilter != null;
	}

	public int getCompanyTypeFilterSize() {
		return (this.companyTypeFilter == null) ? 0 : this.companyTypeFilter.size();
	}

	public java.util.Iterator<CompanyType> getCompanyTypeFilterIterator() {
		return (this.companyTypeFilter == null) ? null : this.companyTypeFilter.iterator();
	}

	public void addToCompanyTypeFilter(CompanyType elem) {
		if (this.companyTypeFilter == null) {
			this.companyTypeFilter = new HashSet<>();
		}
		this.companyTypeFilter.add(elem);
	}

	public Set<CompanyType> getCompanyTypeFilter() {
		return this.companyTypeFilter;
	}

	public PeopleSearchRequest setCompanyTypeFilter(Set<CompanyType> companyTypeFilter) {
		this.companyTypeFilter = companyTypeFilter;
		return this;
	}

	public boolean isSetCompanyTypeFilter() {
		return this.companyTypeFilter != null;
	}

	public int getSkillFilterSize() {
		return (this.skillFilter == null) ? 0 : this.skillFilter.size();
	}

	public java.util.Iterator<Long> getSkillFilterIterator() {
		return (this.skillFilter == null) ? null : this.skillFilter.iterator();
	}

	public void addToSkillFilter(long elem) {
		if (this.skillFilter == null) {
			this.skillFilter = new HashSet<>();
		}
		this.skillFilter.add(elem);
	}

	public Set<Long> getSkillFilter() {
		return this.skillFilter;
	}

	public PeopleSearchRequest setSkillFilter(Set<Long> skillFilter) {
		this.skillFilter = skillFilter;
		return this;
	}

	public boolean isSetSkillFilter() {
		return this.skillFilter != null;
	}

	public int getInsuranceFilterSize() {
		return (this.insuranceFilter == null) ? 0 : this.insuranceFilter.size();
	}

	public java.util.Iterator<Long> getInsuranceFilterIterator() {
		return (this.insuranceFilter == null) ? null : this.insuranceFilter.iterator();
	}

	public void addToInsuranceFilter(long elem) {
		if (this.insuranceFilter == null) {
			this.insuranceFilter = new HashSet<>();
		}
		this.insuranceFilter.add(elem);
	}

	public Set<Long> getInsuranceFilter() {
		return this.insuranceFilter;
	}

	public PeopleSearchRequest setInsuranceFilter(Set<Long> insuranceFilter) {
		this.insuranceFilter = insuranceFilter;
		return this;
	}

	public void addToSkillNamesFilter(String skill) {
		if (this.skillNamesFilter == null) {
			this.skillNamesFilter = new HashSet<>();
		}
		this.skillNamesFilter.add(skill);
	}

	public Set<String> getSkillNamesFilter() {
		return skillNamesFilter;
	}

	public PeopleSearchRequest setSkillNamesFilter(Set<String> skillNamesFilter) {
		this.skillNamesFilter = skillNamesFilter;
		return this;
	}

	public boolean isSetInsuranceFilter() {
		return this.insuranceFilter != null;
	}

	public java.util.Iterator<Long> getAssessmentFilterIterator() {
		return (this.assessmentFilter == null) ? null : this.assessmentFilter.iterator();
	}

	public void addToAssessmentFilter(long elem) {
		if (this.assessmentFilter == null) {
			this.assessmentFilter = new HashSet<>();
		}
		this.assessmentFilter.add(elem);
	}

	public Set<Long> getAssessmentFilter() {
		return this.assessmentFilter;
	}

	public int getAssessmentFilterSize() {
		return (this.assessmentFilter == null) ? 0 : this.assessmentFilter.size();
	}

	public PeopleSearchRequest setAssessmentFilter(Set<Long> assessmentFilter) {
		this.assessmentFilter = assessmentFilter;
		return this;
	}

	public boolean isSetAssessmentFilter() {
		return this.assessmentFilter != null;
	}

	public Set<Long> getInvitedAssessmentFilter() {
		return invitedAssessmentFilter;
	}

	public PeopleSearchRequest setInvitedAssessmentFilter(Set<Long> invitedAssessmentFilter) {
		this.invitedAssessmentFilter = invitedAssessmentFilter;
		return this;
	}

	public boolean isSetInvitedAssessmentFilter() {
		return this.invitedAssessmentFilter != null;
	}

	public Set<Long> getNotInvitedAssessmentFilter() {
		return notInvitedAssessmentFilter;
	}

	public PeopleSearchRequest setNotInvitedAssessmentFilter(Set<Long> notInvitedAssessmentFilter) {
		this.notInvitedAssessmentFilter = notInvitedAssessmentFilter;
		return this;
	}

	public boolean isSetNotInvitedAssessmentFilter() {
		return this.notInvitedAssessmentFilter != null;
	}

	public Set<Long> getPassedAssessmentFilter() {
		return passedAssessmentFilter;
	}

	public PeopleSearchRequest setPassedAssessmentFilter(Set<Long> passedAssessmentFilter) {
		this.passedAssessmentFilter = passedAssessmentFilter;
		return this;
	}

	public boolean isSetPassedAssessmentFilter() {
		return this.passedAssessmentFilter != null;
	}

	public Set<Long> getFailedTestFilter() {
		return failedTestFilter;
	}

	public PeopleSearchRequest setFailedTestFilter(Set<Long> failedTestFilter) {
		this.failedTestFilter = failedTestFilter;
		return this;
	}

	public boolean isSetFailedTestFilter() {
		return this.failedTestFilter != null;
	}

	public Pagination getPaginationRequest() {
		return this.paginationRequest;
	}

	public PeopleSearchRequest setPaginationRequest(Pagination paginationRequest) {
		this.paginationRequest = paginationRequest;
		return this;
	}

	public boolean isSetPaginationRequest() {
		return this.paginationRequest != null;
	}

	public PeopleSearchRequest setCountryFilter(Set<String> countryFilter) {
		super.setCountryFilter(countryFilter);
		return this;
	}

	public PeopleSearchRequest setSatisfactionRateFilter(NumericFilter satisfactionRateFilter) {
		super.setSatisfactionRateFilter(satisfactionRateFilter);
		return this;
	}

	public PeopleSearchRequest setOnTimePercentageFilter(NumericFilter onTimePercentageFilter) {
		super.setOnTimePercentageFilter(onTimePercentageFilter);
		return this;
	}

	public PeopleSearchRequest setDeliverableOnTimePercentageFilter(NumericFilter deliverableOnTimePercentageFilter) {
		super.setDeliverableOnTimePercentageFilter(deliverableOnTimePercentageFilter);
		return this;
	}

	public NumericFilter getWorkersCompCoverageFilter() {
		return workersCompCoverageFilter;
	}

	public PeopleSearchRequest setWorkersCompCoverageFilter(NumericFilter workersCompCoverageFilter) {
		this.workersCompCoverageFilter = workersCompCoverageFilter;
		return this;
	}

	public NumericFilter getGeneralLiabilityCoverageFilter() {
		return generalLiabilityCoverageFilter;
	}

	public PeopleSearchRequest setGeneralLiabilityCoverageFilter(NumericFilter generalLiabilityCoverageFilter) {
		this.generalLiabilityCoverageFilter = generalLiabilityCoverageFilter;
		return this;
	}

	public NumericFilter getErrorsAndOmissionsCoverageFilter() {
		return errorsAndOmissionsCoverageFilter;
	}

	public PeopleSearchRequest setErrorsAndOmissionsCoverageFilter(NumericFilter errorsAndOmissionsCoverageFilter) {
		this.errorsAndOmissionsCoverageFilter = errorsAndOmissionsCoverageFilter;
		return this;
	}

	public NumericFilter getAutomobileCoverageFilter() {
		return automobileCoverageFilter;
	}

	public PeopleSearchRequest setAutomobileCoverageFilter(NumericFilter automobileCoverageFilter) {
		this.automobileCoverageFilter = automobileCoverageFilter;
		return this;
	}

	public NumericFilter getContractorsCoverageFilter() {
		return contractorsCoverageFilter;
	}

	public PeopleSearchRequest setContractorsCoverageFilter(NumericFilter contractorsCoverageFilter) {
		this.contractorsCoverageFilter = contractorsCoverageFilter;
		return this;
	}

	public NumericFilter getBusinessLiabilityCoverageFilter() {
		return businessLiabilityCoverageFilter;
	}

	public PeopleSearchRequest setBusinessLiabilityCoverageFilter(NumericFilter businessLiabilityCoverageFilter) {
		this.businessLiabilityCoverageFilter = businessLiabilityCoverageFilter;
		return this;
	}

	public NumericFilter getCommercialGeneralLiabilityCoverageFilter() {
		return commercialGeneralLiabilityCoverageFilter;
	}

	public PeopleSearchRequest setCommercialGeneralLiabilityCoverageFilter(NumericFilter commercialGeneralLiabilityCoverageFilter) {
		this.commercialGeneralLiabilityCoverageFilter = commercialGeneralLiabilityCoverageFilter;
		return this;
	}

	public boolean isAvatarFilter() {
		return avatarFilter;
	}

	public PeopleSearchRequest setAvatarFilter(boolean avatarFilter) {
		this.avatarFilter = avatarFilter;
		return this;
	}

	public boolean isVideoFilter() {
		return videoFilter;
	}

	public PeopleSearchRequest setVideoFilter(boolean videoFilter) {
		this.videoFilter = videoFilter;
		return this;
	}

	public Set<Long> getDeclinedCompanyFilter() {
		return declinedCompanyFilter;
	}

	public PeopleSearchRequest setDeclinedCompanyFilter(Set<Long> declinedCompanyFilter) {
		this.declinedCompanyFilter = declinedCompanyFilter;
		return this;
	}

	public Set<Long> getCompanyFilter() {
		return companyFilter;
	}

	public PeopleSearchRequest setCompanyFilter(Set<Long> companyFilter) {
		this.companyFilter = companyFilter;
		return this;
	}

	public boolean isSetCompanyFilter() {
		return this.companyFilter != null;
	}

	public boolean isExportSearch() {
		return exportSearch;
	}

	public PeopleSearchRequest setExportSearch(boolean exportSearch) {
		this.exportSearch = exportSearch;
		return this;
	}

	public Long getInvitedToWorkIdFilter() {
		return invitedToWorkIdFilter;
	}

	public void setInvitedToWorkIdFilter(Long invitedToWorkIdFilter) {
		this.invitedToWorkIdFilter = invitedToWorkIdFilter;
	}

	public boolean isSetInvitedToWorkIdFilter() {
		return this.invitedToWorkIdFilter != null;
	}

	public long getCurrentAssessmentId() {
		return this.currentAssessmentId;
	}

	public PeopleSearchRequest setCurrentAssessmentId(long currentAssessmentId) {
		this.currentAssessmentId = currentAssessmentId;
		return this;
	}

	public boolean isSetCurrentAssessmentId() {
		return (currentAssessmentId > 0L);
	}

	public boolean isMboFilter() {
		return mboFilter;
	}

	public PeopleSearchRequest setMboFilter(boolean mboFilter) {
		this.mboFilter = mboFilter;
		return this;
	}

	public boolean isNoFacetsFlag() {
		return noFacetsFlag;
	}

	public PeopleSearchRequest setNoFacetsFlag(boolean noFacetsFlag) {
		this.noFacetsFlag = noFacetsFlag;
		return this;
	}

	public List<Long> getNetworkIds() {
		return networkIds;
	}

	public void setNetworkIds(List<Long> networkIds) {
		this.networkIds = networkIds;
	}

	public boolean isSetNetworkIds() {
		return isNotEmpty(networkIds);
	}

	public Set<Long> getSharedGroupFilter() {
		return sharedGroupFilter;
	}

	public PeopleSearchRequest setSharedGroupFilter(Set<Long> sharedGroupFilter) {
		this.sharedGroupFilter = sharedGroupFilter;
		return this;
	}

	public boolean isSetSharedGroupFilter() {
		return isNotEmpty(sharedGroupFilter);
	}

	public boolean isDisableMarketplace() {
		return disableMarketplace;
	}

	public PeopleSearchRequest setDisableMarketplace(boolean disableMarketplace) {
		this.disableMarketplace = disableMarketplace;
		return this;
	}

	public PeopleSearchRequest buildFilterFromRoutingStrategy(AbstractRoutingStrategy routingStrategy) {
		Assert.isTrue(GroupRoutingStrategy.GROUP_ROUTING_STRATEGY.equals(routingStrategy.getType()));
		Assert.notNull(((GroupRoutingStrategy) routingStrategy).getUserGroups());

		this.groupFilter = ((GroupRoutingStrategy) routingStrategy).getUserGroups();
		this.setPaginationRequest(new Pagination()
				.setCursorPosition(0)
				.setPageSize(Constants.GROUP_SEND_RESOURCES_LIMIT));
		return this;
	}


	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public Set<SolrUserType> getUserTypeFilter() {
		return userTypeFilter;
	}

	public PeopleSearchRequest setUserTypeFilter(final Set<SolrUserType> userTypeFilter) {
		this.userTypeFilter = userTypeFilter;
		return this;
	}

	public boolean isSetUserTypeFilter() {
		return this.userTypeFilter != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof PeopleSearchRequest)
			return this.equals((PeopleSearchRequest) that);
		return false;
	}

	private boolean equals(PeopleSearchRequest that) {
		if (that == null)
			return false;

		if (this.getUserId() != that.getUserId()) {
			return false;
		}

		if ((this.isSetKeyword() != that.isSetKeyword()) ||
			(this.isSetKeyword() && !this.getKeyword().equals(that.getKeyword()))) {
			return false;
		}

		if ((this.isSetLocationFilter() != that.isSetLocationFilter()) ||
			(this.isSetLocationFilter() && !this.getLocationFilter().equals(that.getLocationFilter()))) {
			return false;
		}

		if ((this.isSetGroupFilter() != that.isSetGroupFilter()) ||
			(this.isSetGroupFilter() && !this.groupFilter.equals(that.groupFilter))) {
			return false;
		}

		if ((this.isSetLaneFilter() != that.isSetLaneFilter()) ||
			(this.isSetLaneFilter() && !this.laneFilter.equals(that.laneFilter))) {
			return false;
		}

		if ((this.isSetRatingsFilter() != that.isSetRatingsFilter()) ||
			(this.isSetRatingsFilter() && !this.ratingsFilter.equals(that.ratingsFilter))) {
			return false;
		}

		if ((this.isSetBackgroundScreeningFilter() != that.isSetBackgroundScreeningFilter()) ||
			(this.isSetBackgroundScreeningFilter() && !this.backgroundScreeningFilter.equals(that.backgroundScreeningFilter))) {
			return false;
		}

		if ((this.isSetTestFilter() != that.isSetTestFilter()) ||
			(this.isSetTestFilter() && !this.testFilter.equals(that.testFilter))) {
			return false;
		}

		if ((this.isSetCertificationFilter() != that.isSetCertificationFilter()) ||
			(this.isSetCertificationFilter() && !this.certificationFilter.equals(that.certificationFilter))) {
			return false;
		}

		if ((this.isSetStateLicenseFilter() != that.isSetStateLicenseFilter()) ||
			(this.isSetStateLicenseFilter() && !this.stateLicenseFilter.equals(that.stateLicenseFilter))) {
			return false;
		}

		if ((this.isSetIndustryFilter() != that.isSetIndustryFilter()) ||
			(this.isSetIndustryFilter() && !this.industryFilter.equals(that.industryFilter))) {
			return false;
		}

		if ((this.isSetCompanyTypeFilter() != that.isSetCompanyTypeFilter()) ||
			(this.isSetCompanyTypeFilter() && !this.companyFilter.equals(that.companyFilter))) {
			return false;
		}

		if ((this.isSetSkillFilter() != that.isSetSkillFilter()) ||
			(this.isSetSkillFilter() && !this.skillFilter.equals(that.skillFilter))) {
			return false;
		}

		if ((this.isSetInsuranceFilter() != that.isSetInsuranceFilter()) ||
			(this.isSetInsuranceFilter() && !this.insuranceFilter.equals(that.insuranceFilter))) {
			return false;
		}

		if ((this.isSetAssessmentFilter() != that.isSetAssessmentFilter()) ||
			(this.isSetAssessmentFilter() && !this.assessmentFilter.equals(that.assessmentFilter))) {
			return false;
		}

		if ((this.isSetPaginationRequest() != that.isSetPaginationRequest()) ||
			(this.isSetPaginationRequest() && !this.paginationRequest.equals(that.paginationRequest))) {
			return false;
		}

		if ((this.isSetCountryFilter() != that.isSetCountryFilter()) ||
			(this.isSetCountryFilter() && !this.getCountryFilter().equals(that.getCountryFilter()))) {
			return false;
		}

		if ((this.isSetNetworkIds() != that.isSetNetworkIds()) ||
			(this.isSetNetworkIds() && !this.networkIds.equals(that.networkIds))) {
			return false;
		}

		if ((this.isSetUserTypeFilter() != that.isSetUserTypeFilter()) ||
			(this.isSetUserTypeFilter() && !this.userTypeFilter.equals(that.userTypeFilter))) {
			return false;
		}

		if (this.searchType != that.searchType) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userId = true;
		builder.append(present_userId);
		builder.append(getUserId());

		builder.append(isSetKeyword());
		if (isSetKeyword())
			builder.append(getKeyword());

		builder.append(isSetLocationFilter());
		if (isSetLocationFilter())
			builder.append(getLocationFilter());

		builder.append(isSetGroupFilter());
		if (isSetGroupFilter())
			builder.append(groupFilter);

		builder.append(isSetLaneFilter());
		if (isSetLaneFilter())
			builder.append(laneFilter);

		builder.append(isSetRatingsFilter());
		if (isSetRatingsFilter())
			builder.append(ratingsFilter);

		builder.append(isSetBackgroundScreeningFilter());
		if (isSetBackgroundScreeningFilter())
			builder.append(backgroundScreeningFilter);

		builder.append(isSetTestFilter());
		if (isSetTestFilter())
			builder.append(testFilter);

		builder.append(isSetCertificationFilter());
		if (isSetCertificationFilter())
			builder.append(certificationFilter);

		builder.append(isSetStateLicenseFilter());
		if (isSetStateLicenseFilter())
			builder.append(stateLicenseFilter);

		builder.append(isSetIndustryFilter());
		if (isSetIndustryFilter())
			builder.append(industryFilter);

		builder.append(isSetCompanyTypeFilter());
		if (isSetCompanyTypeFilter())
			builder.append(companyTypeFilter);

		builder.append(isSetSkillFilter());
		if (isSetSkillFilter())
			builder.append(skillFilter);

		builder.append(isSetInsuranceFilter());
		if (isSetInsuranceFilter())
			builder.append(insuranceFilter);

		builder.append(isSetAssessmentFilter());
		if (isSetAssessmentFilter())
			builder.append(assessmentFilter);

		builder.append(isSetPaginationRequest());
		if (isSetPaginationRequest())
			builder.append(paginationRequest);

		builder.append(isSetCountryFilter());
		if (isSetCountryFilter())
			builder.append(getCountryFilter());

		builder.append(isSetNetworkIds());
		if (isSetNetworkIds()) {
			builder.append(networkIds);
		}

		builder.append(isSetUserTypeFilter());
		if (isSetUserTypeFilter())
			builder.append(userTypeFilter);

		if (searchType != null) {
			builder.append(searchType.name());
		}

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		return "PeopleSearchRequest{" +
				"assessmentFilter=" + assessmentFilter +
				", userId=" + getUserId() +
				", keyword='" + getKeyword() + '\'' +
				", location=" + getLocationFilter() +
				", groupFilter=" + groupFilter +
				", laneFilter=" + laneFilter +
				", ratingsFilter=" + ratingsFilter +
				", backgroundScreeningFilter=" + backgroundScreeningFilter +
				", testFilter=" + testFilter +
				", certificationFilter=" + certificationFilter +
				", stateLicenseFilter=" + stateLicenseFilter +
				", industryFilter=" + industryFilter +
				", companyTypeFilter=" + companyTypeFilter +
				", userTypeFilter=" + userTypeFilter +
				", skillFilter=" + skillFilter +
				", insuranceFilter=" + insuranceFilter +
				", paginationRequest=" + paginationRequest +
				", countryFilter=" + getCountryFilter() +
				", onTimePercentageFilter=" + getOnTimePercentageFilter() +
				", deliverableOnTimePercentageFilter=" + getDeliverableOnTimePercentageFilter() +
				", currentAssessmentId=" + currentAssessmentId +
				", networkIds=" + networkIds +
				", mbo=" + mboFilter +
			    ", searchType=" + searchType +
				'}';
	}
}
