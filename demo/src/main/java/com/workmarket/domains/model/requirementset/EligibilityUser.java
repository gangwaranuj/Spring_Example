package com.workmarket.domains.model.requirementset;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.request.user.Verification;
import com.workmarket.search.response.user.PeopleSearchResult;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EligibilityUser {
	private final User user;
	private PeopleSearchResult peopleSearchResult;

	public EligibilityUser(User user, PeopleSearchResult peopleSearchResult) {
		this.user = user;
		this.peopleSearchResult = peopleSearchResult;
	}

	public Long getId() {
		return user.getId();
	}

	public String getUuid() {
		return user.getUuid();
	}

	@Deprecated
	public DateTime getLastBackgroundCheckDate() {
		return peopleSearchResult != null && peopleSearchResult.getLastBackgroundCheckDate() != null ? new DateTime(peopleSearchResult.getLastBackgroundCheckDate()) : null;
	}

	@Deprecated
	public boolean getPassedBackgroundCheck() {
		return peopleSearchResult != null &&  peopleSearchResult.getVerifications() != null && peopleSearchResult.getVerifications().contains(Verification.BACKGROUND_CHECK);
	}

	@Deprecated
	public List<Long> getCertificationIds() {
		if (peopleSearchResult == null || peopleSearchResult.getCertificationIds() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getCertificationIds();
	}

	@Deprecated
	public String getCountry() {
		return peopleSearchResult == null ? null : peopleSearchResult.getCountry();
	}

	@Deprecated
	public DateTime getLastDrugTestDate() {
		return peopleSearchResult == null || peopleSearchResult.getLastDrugTestDate() == null ? null : new DateTime(peopleSearchResult.getLastDrugTestDate());
	}

	@Deprecated
	public List<Long> getIndustries() {
		if (peopleSearchResult == null || peopleSearchResult.getIndustryIds() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getIndustryIds();
	}

	@Deprecated
	public List<Long> getInsuranceIds() {
		if (peopleSearchResult == null || peopleSearchResult.getInsuranceIds() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getInsuranceIds();
	}

	@Deprecated
	public List<Long> getLicenseIds() {
		if (peopleSearchResult == null || peopleSearchResult.getLicenseIds() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getLicenseIds();
	}

	@Deprecated
	public Double getSatisfactionRate() {
		return peopleSearchResult == null ? null : peopleSearchResult.getSatisfactionRate();
	}

	@Deprecated
	public List<LaneType> getLaneTypesForCompany(Long companyId) {
		if (peopleSearchResult == null || peopleSearchResult.getCompanyLaneTypes() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getCompanyLaneTypes().get(companyId);
	}

	@Deprecated
	public List<Long> getPassedAssessmentIds() {
		if (peopleSearchResult == null || peopleSearchResult.getPassedAssessmentIds() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getPassedAssessmentIds();
	}

	@Deprecated
	public boolean hasProfileVideo() {
		return peopleSearchResult != null && peopleSearchResult.getVideoAssetUri() != null && !peopleSearchResult.getVideoAssetUri().isEmpty();
	}

	@Deprecated
	public boolean hasProfilePicture() {
		return peopleSearchResult != null && peopleSearchResult.getSmallAvatarAssetUri() != null && !peopleSearchResult.getSmallAvatarAssetUri().isEmpty();
	}

	@Deprecated
	public List<Long> getContractIds() {
		if (peopleSearchResult == null || peopleSearchResult.getContractIds() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getContractIds();
	}

	@Deprecated
	public List<Long> getGroupIds() {
		if (peopleSearchResult == null || peopleSearchResult.getGroupIds() == null) {
			return Lists.newArrayList();
		}

		return peopleSearchResult.getGroupIds();
	}

	@Deprecated
	public Map<Long, Integer> getPaidCompanyAssignmentCounts() {
		return peopleSearchResult != null ? peopleSearchResult.getCompanyPaidCounts() : new HashMap<Long, Integer>();
	}

	@Deprecated
	public int getTotalPaidAssignmentsCount() {
		return peopleSearchResult != null ? peopleSearchResult.getWorkCompletedCount() : 0;
	}

	@Deprecated
	public double getOnTimePercentage() {
		return peopleSearchResult != null ? peopleSearchResult.getOnTimePercentage() : 0.0;
	}

	@Deprecated
	public double getDeliverableOnTimePercentage() {
		return peopleSearchResult != null ? peopleSearchResult.getDeliverableOnTimePercentage() : 0.0;
	}

	@Deprecated
	public int getAbandonedCount() {
		return peopleSearchResult != null ? peopleSearchResult.getWorkAbandonedCount() : 0;
	}

	@Deprecated
	public int getCancelledCount() {
		return peopleSearchResult != null ? peopleSearchResult.getWorkCancelledCount() : 0;
	}

	public Company getCompany() {
		return user.getCompany();
	}

	public String getEmail() {
		return user.getEmail();
	}
}
