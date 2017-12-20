package com.workmarket.data.solr.configuration;

import com.google.common.collect.ImmutableList;
import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.data.solr.repository.UserSearchableFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BoostConfiguration {

	public static final List<UserBoostFields> BOOST_FIELDS = ImmutableList.of(
			UserBoostFields.PAID_ASSIGNMENTS_COUNT,
			UserBoostFields.ON_TIME_PERCENTAGE,
			UserBoostFields.SATISFACTION_RATE,
			UserBoostFields.CANCELLED_LABEL_COUNT,
			UserBoostFields.LATE_LABEL_COUNT,
			UserBoostFields.AVERAGE_STAR_RATING,
			UserBoostFields.COMPLETED_ON_TIME_LABEL_COUNT,
			UserBoostFields.ABANDONED_LABEL_COUNT,
			UserBoostFields.REPEATED_CLIENTS_COUNT,
			UserBoostFields.PASSED_BACKGROUND_CHECK,
			UserBoostFields.PASSED_DRUG_TEST,
			UserBoostFields.WORK_COMPLETED_FOR_COMPANIES,
			UserBoostFields.DISTINCT_COMPANY_BLOCKS_COUNT,
			UserBoostFields.HAS_AVATAR,
			UserBoostFields.LAST_ASSIGNED_WORK_DATE);

	private static final List<UserSearchableFields> SKILL_BOOST_FIELD_LIST = ImmutableList.of(UserSearchableFields.SKILL_NAMES, UserSearchableFields.CERTIFICATION_NAMES,
			UserSearchableFields.CERTIFICATION_VENDORS, UserSearchableFields.LICENSE_NAMES);

	@Value("${people.search.boost.distance}")
	private Double distanceBoost;
	@Value("${people.search.boost.profilePicture}")
	private Double profilePictureBoost;
	@Value("${people.search.boost.completedWork}")
	private Double workCompletedBoost;
	@Value("${people.search.boost.onTimePercentage}")
	private Double onTimePercentageBoost;
	@Value("${people.search.boost.lateLabelCount}")
	private Double lateLabelCountBoost;
	@Value("${people.search.boost.cancelledLabelCount}")
	private Double cancelledLabelCountBoost;
	@Value("${people.search.boost.abandonedLabelCount}")
	private Double abandonedLabelCountBoost;
	@Value("${people.search.boost.completedOnTimeLabelCount}")
	private Double completedOnTimeLabelCountBoost;
	@Value("${people.search.boost.blocksCount}")
	private Double blocksCountBoost;
	@Value("${people.search.boost.repeatedClients}")
	private Double repeatedClientsBoost;
	@Value("${people.search.boost.paidAssignments}")
	private Double paidAssignmentsBoost;
	@Value("${people.search.boost.satisfactionRate}")
	private Double satisfactionRate;
	@Value("${people.search.boost.averageStarRating}")
	private Double averageStarRatingBoost;
	@Value("${people.search.boost.backgroundCheck}")
	private Double passedBackgroundCheckBoost;
	@Value("${people.search.boost.screeningStatus}")
	private Double screeningStatusBoost;
	@Value("${people.search.boost.drugTest}")
	private Double passedDrugTestBoost;
	@Value("${people.search.boost.completedWorkForCompanies}")
	private Double completedWorkForCompaniesBoost;
	@Value("${people.search.boost.skillsAssignment}")
	private Double skillsAssignmentBoost;
	@Value("${people.search.boost.lastAssignedWorkDate}")
	private Double lastAssignedWorkDateBoost;

	public static List<UserBoostFields> getBoostFields() {
		return BOOST_FIELDS;
	}

	public Double getDistanceBoost() {
		return distanceBoost;
	}

	public Double getWorkCompletedBoost() {
		return workCompletedBoost;
	}

	public Double getSatisfactionRate() {
		return satisfactionRate;
	}

	public Double getProfilePictureBoost() {
		return profilePictureBoost;
	}

	public Double getOnTimePercentageBoost() {
		return onTimePercentageBoost;
	}

	public Double getAbandonedLabelCountBoost() {
		return abandonedLabelCountBoost;
	}

	public Double getCancelledLabelCountBoost() {
		return cancelledLabelCountBoost;
	}

	public Double getCompletedOnTimeLabelCountBoost() {
		return completedOnTimeLabelCountBoost;
	}

	public Double getLateLabelCountBoost() {
		return lateLabelCountBoost;
	}

	public Double getAverageStarRatingBoost() {
		return averageStarRatingBoost;
	}

	public Double getBlocksCountBoost() {
		return blocksCountBoost;
	}

	public Double getPaidAssignmentsBoost() {
		return paidAssignmentsBoost;
	}

	public Double getBackgroundCheckBoost() {
		return passedBackgroundCheckBoost;
	}

	public Double getDrugTestBoost() {
		return passedDrugTestBoost;
	}

	public Double getScreeningStatusBoost() {
		return screeningStatusBoost;
	}

	public Double getRepeatedClientsBoost() {
		return repeatedClientsBoost;
	}

	public Double getCompletedWorkForCompaniesBoost() {
		return completedWorkForCompaniesBoost;
	}

	public Double getSkillsAssignmentBoost() {
		return skillsAssignmentBoost;
	}

	public void setSkillsAssignmentBoost(Double skillsAssignmentBoost) {
		this.skillsAssignmentBoost = skillsAssignmentBoost;
	}

	public static List<UserSearchableFields> getSkillBoostFieldList() {
		return SKILL_BOOST_FIELD_LIST;
	}

	public Double getLastAssignedWorkDateBoost() {
		return lastAssignedWorkDateBoost;
	}
}
