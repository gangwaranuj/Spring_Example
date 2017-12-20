package com.workmarket.data.solr.repository;

import org.springframework.data.solr.core.query.Field;

public enum UserBoostFields implements Field {

	WORK_COMPLETED_COUNT("workCompletedCount"),
	SATISFACTION_RATE("satisfactionRate"),
	ON_TIME_PERCENTAGE("onTimePercentage"),
	DELIVERABLE_ON_TIME_PERCENTAGE("deliverableOnTimePercentage"),
	AVERAGE_STAR_RATING("averageStarRating"),
	REPEATED_CLIENTS_COUNT("repeatedClientsCount"),
	DISTINCT_COMPANY_BLOCKS_COUNT("blocksCount"),
	LATE_LABEL_COUNT("lateLabelCount"),
	ABANDONED_LABEL_COUNT("abandonedLabelCount"),
	CANCELLED_LABEL_COUNT("cancelledLabelCount"),
	COMPLETED_ON_TIME_LABEL_COUNT("completedOnTimeLabelCount"),
	PAID_ASSIGNMENTS_COUNT("paidAssignmentsCount"),
	PASSED_BACKGROUND_CHECK("passedBackgroundCheck"),
	PASSED_DRUG_TEST("passedDrugTest"),
	SCREEENING_STATUS("screeningStatus"),
	WORK_COMPLETED_FOR_COMPANIES("workCompletedForCompanies"),
	APPLIED_WORK_KEYWORDS("appliedWorkKeywords"),
	APPLIED_GROUPS_KEYWORDS("appliedGroupsKeywords"),
	COMPLETED_WORK_KEYWORDS("completedWorkKeywords"),
	HAS_AVATAR("hasAvatar"),
	LAST_ASSIGNED_WORK_DATE("lastAssignedWorkDate"),
	PAID_COMPANY_COUNT("companyPaidAssignmentCount_"),
	RECENT_WORKING_WEEKS_RATIO("recentWorkingWeeksRatio"),
	WEIGHTED_AVERAGE_RATING("weightedAverageRating");

	private final String fieldName;

	UserBoostFields(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getName() {
		return this.fieldName;
	}
}
