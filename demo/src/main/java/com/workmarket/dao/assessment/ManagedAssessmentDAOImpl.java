package com.workmarket.dao.assessment;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination.SORT_DIRECTION;
import com.workmarket.domains.model.Sort;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.AssessmentUserAssociation;
import com.workmarket.domains.model.assessment.AttemptStatusType;
import com.workmarket.domains.model.assessment.ManagedAssessment;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Repository
public class ManagedAssessmentDAOImpl extends AbstractDAO<AssessmentUserAssociation> implements ManagedAssessmentDAO {
	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<AssessmentUserAssociation> getEntityClass() {
		return AssessmentUserAssociation.class;
	}

	private static final class ManagedAssessmentMapper implements RowMapper<ManagedAssessment> {

		@Override
		public ManagedAssessment mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManagedAssessment row = new ManagedAssessment();

			row.setAssessmentId(rs.getLong("assessment.id"));
			row.setAssessmentName(rs.getString("assessment.name"));
			row.setDescription(rs.getString("assessment.description"));
			row.setApproximateMinutesDuration(rs.getInt("approximate_duration_minutes"));
			row.setPassingScore(rs.getDouble("passing_score"));
			row.setCompanyId(rs.getLong("companyId"));
			row.setCompanyName(rs.getString("companyName"));
			row.setCompletedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("association.completed_on")));
			row.setAttemptStatusTypeCode(rs.getString("association.attempt_status_type_code"));

			if (StringUtils.isNotBlank(row.getAttemptStatusTypeCode()) &&
					(row.getAttemptStatusTypeCode().equals(AttemptStatusType.GRADED) ||
						row.getAttemptStatusTypeCode().equals(AttemptStatusType.GRADE_PENDING))
				) {
				row.setCompleted(true);
			}

			row.setPassed(rs.getBoolean("association.passed_flag"));
			row.setScore(rs.getDouble("association.score"));
			row.setReattemptAllowed(rs.getBoolean("reattempt_allowed_flag"));
			row.setInvitationStatus(rs.getString("invitationStatus"));
			row.setInvitationDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("invitationDate")));
			row.setPassedCount(rs.getLong("passedCount"));
			row.setGradePendingCount(rs.getLong("gradePendingCount"));
			row.setStatus(rs.getString("assessment.assessment_status_type_code"));
			row.setProgress(rs.getDouble("progress"));
			row.setFeatured(rs.getInt("assessment.featured_flag"));
			row.setIndustryId(rs.getLong("assessment.industry_id"));

			return row;
		}
	}

	@Override
	public ManagedAssessmentPagination findAssessmentsForUser(
		Long userCompanyId,
		Long userId,
		Long profileId,
		List<Long> exclusiveCompanyIds,
		List<Long> excludeCompanyIds,
		ManagedAssessmentPagination pagination
	) {
		return
			processAssessmentsResultSet(
				assessmentsSQLBuilder(userCompanyId, userId, profileId, exclusiveCompanyIds, excludeCompanyIds, pagination),
				pagination
			);
	}

	private ManagedAssessmentPagination processAssessmentsResultSet(SQLBuilder builder, ManagedAssessmentPagination pagination) {
		Assert.notNull(pagination);

		Integer rowCount = jdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class);
		pagination.setRowCount(rowCount);
		List<ManagedAssessment> results = Lists.newArrayList();
		if (rowCount > 0) {
			pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), new ManagedAssessmentMapper()));
		} else {
			pagination.setResults(results);
		}

		return pagination;
	}

	private SQLBuilder assessmentsSQLBuilder(
		Long companyId,
		Long userId,
		Long profileId,
		List<Long> exclusiveCompanyIds,
		List<Long> excludeCompanyIds,
		ManagedAssessmentPagination pagination
	) {
		boolean includeStatistics = pagination.getRequestInformation().contains(ManagedAssessmentPagination.REQUEST_INFO.STATISTICS);
		boolean includeProgress = pagination.getRequestInformation().contains(ManagedAssessmentPagination.REQUEST_INFO.PROGRESS);

		SQLBuilder builder = new SQLBuilder();

		builder.addColumns(
			new String[]{
				"assessment.id", "assessment.name", "assessment.passing_score",
				"assessment.description", "assessment.featured_flag","assessment.industry_id",
				"assessment.approximate_duration_minutes", "assessment.created_on",
				"company.id AS companyId", "company.effective_name AS companyName",
				"association.completed_on", "association.attempt_status_type_code", "association.passed_flag",
				"association.score", "association.reattempt_allowed_flag, assessment.assessment_status_type_code"
			})

				.addColumn(
					"(SELECT request.request_status_type_code "
						+ " FROM request_assessment_invitation "
						+ " INNER JOIN request ON request.id = request_assessment_invitation.id "
						+ " WHERE request.invitee_user_id = :userId "
						+ " AND request_assessment_invitation.assessment_id = assessment.id "
						+ " ORDER BY request.request_date DESC LIMIT 1 ) AS invitationStatus ")
						//TODO(sgomez) : This is not ordering by group invitations
				.addColumn(
					"(SELECT MAX(request.request_date)  "
						+ " FROM request_assessment_invitation "
						+ " INNER JOIN request ON request.id = request_assessment_invitation.id  "
						+ " WHERE request.invitee_user_id = :userId  "
						+ " AND request_assessment_invitation.assessment_id = assessment.id) AS invitationDate ")
				// TODO(sgomez) :  We need summary tables here
				.addColumn("IF(1="
					+ includeStatistics
					+ ",(SELECT COUNT( distinct (passed_association.user_id)) FROM assessment_user_association passed_association"
					+ " WHERE assessment.id = passed_association.assessment_id AND passed_association.passed_flag = true),0) as passedCount")
				.addColumn("IF(1="
					+ includeStatistics
					+ ",(SELECT COUNT( distinct (grade_pending_association.user_id)) FROM assessment_user_association grade_pending_association"
					+ " WHERE assessment.id = grade_pending_association.assessment_id AND association.attempt_status_type_code = '"
					+ AttemptStatusType.GRADE_PENDING
					+ "'),0) as gradePendingCount")
				.addColumn("IF(1="
					+ includeProgress
					+ " AND association.attempt_status_type_code = '" + AttemptStatusType.INPROGRESS + "',"
					+ " (SELECT (count(distinct response.assessment_item_id) /"
						+ "(SELECT count(item.assessment_id) FROM assessment_item item"
							+ " WHERE item.graded AND item.assessment_id = assessment.id LIMIT 1))"
						+ " FROM assessment_attempt attempt"
							+ " INNER JOIN assessment_attempt_response response ON (response.assessment_attempt_id = attempt.id)"
						+ " WHERE attempt.assessment_user_association_id = association.id), 0) AS progress")
				.addTable("assessment")
				.addJoin(" INNER JOIN company ON	company.id = assessment.company_id ")
				.addJoin(" LEFT JOIN assessment_user_association association " +
						" ON (assessment.id = association.assessment_id AND association.user_id = :userId) ")

				.addParam("userId", userId)
				.addParam("profileId", profileId)
				.addParam("companyId", companyId);
		if (!exclusiveCompanyIds.isEmpty()) {
			builder.addWhereClause(" assessment.company_id IN (" + StringUtils.join(exclusiveCompanyIds, ",") + ")");
		} else {
			// Unless a list of exclusive companies is provided, we always want to
			// filter out all companies that do not have the MARKETPLACE feature
			//   TODO[Jim]: perhaps there is a more elegant way to identify these companies via Velvet Rope
			builder.addWhereClause(" assessment.company_id IN (select distinct value from admission where key_name = 'companyId' and venue = 'MARKETPLACE')");
		}
		if (!excludeCompanyIds.isEmpty()) {
			builder.addWhereClause(" assessment.company_id NOT IN (" + StringUtils.join(excludeCompanyIds, ",") + ")");
		}
		applyFilters(builder, pagination);

		// Ability to add more than 1 sort column
		if (!pagination.getSorts().isEmpty()) {
			for (Sort sort : pagination.getSorts()) {
				builder.addOrderBy(sort.getSortColumn(), sort.getSortDirection().toString());
			}
		}
		// Otherwise get the regular sort column
		else if (pagination.getSortColumn() != null) {
			builder.addOrderBy(pagination.getSortColumn(), pagination.getSortDirection().toString());
		} else {
			// Default sort column
			builder.addOrderBy(
				ManagedAssessmentPagination.SORTS.NAME.getSort(SORT_DIRECTION.ASC).getSortColumn(),
				SORT_DIRECTION.ASC.toString()
			);
		}

		builder.addLimitClause(pagination.getStartRow(), pagination.getResultsLimit(), pagination.isLimitMaxRows());

		return builder;
	}

	private static SQLBuilder applyFilters(
		SQLBuilder builder, ManagedAssessmentPagination pagination, String... excludeFilters
	) {
		applyActivityFilters(builder, pagination.getActivityFilter());
		applyAttemptFilters(builder, pagination.getAttemptFilter());
		applyInvitationFilters(builder, pagination.getInvitationFilter());
		applyOwnerFilters(builder, pagination.getOwnerFilter());
		applyTakeabilityFilters(builder, pagination.getTakeabilityFilter());
		applyTypeFilters(builder, pagination.getTypeFilter());
		applyPrivacyFilters(builder, pagination.getPrivacyFilter());
		applyIdFilters(builder, pagination.getIdFilter());

		Iterator<String> it = pagination.getFilters().keySet().iterator();

		List<String> exclusions = new ArrayList<>();
		if (excludeFilters != null) {
			exclusions = Arrays.asList(excludeFilters);
		}

		int i = 0;
		while (it.hasNext()) {

			String param = "param" + i;
			String filter = it.next();
			if (exclusions.contains(filter))
				continue;

			ManagedAssessmentPagination.FILTER_KEYS filterKey = ManagedAssessmentPagination.FILTER_KEYS.valueOf(filter);
			String filterValue = pagination.getFilters().get(filter);
				if (filterKey.getExpectedClass().equals(Boolean.class)) {
					builder.addWhereClause(filterKey.getColumn(), filterKey.getOperator(), param, BooleanUtils.toBoolean(filterValue));
				} else if (filterKey.getExpectedClass().equals(Integer.class)) {
					builder.addWhereClause(filterKey.getColumn(), filterKey.getOperator(), param, Integer.parseInt(filterValue));
				} else if (filterKey.getExpectedClass().equals(Long.class)) {
					builder.addWhereClause(filterKey.getColumn(), filterKey.getOperator(), param, Long.parseLong(filterValue));
				} else {
					builder.addWhereClause(filterKey.getColumn(), filterKey.getOperator(), param, filterValue);
				}
			i++;
		}

		return builder;
	}

	private static SQLBuilder applyTypeFilters(SQLBuilder builder, ManagedAssessmentPagination.TYPE_FILTER_KEYS typeFilter) {
		if (typeFilter == null) {
			return builder;
		}
		return
			builder.addWhereClause(
				ManagedAssessmentPagination.TYPE_FILTER_KEYS.getColumn(),
				ManagedAssessmentPagination.TYPE_FILTER_KEYS.getOperator(),
				typeFilter.toString(),
				typeFilter.getValue()
			);
	}

	private static SQLBuilder applyPrivacyFilters(SQLBuilder builder, ManagedAssessmentPagination.PRIVACY_FILTER_KEYS privacyFilter){
		if (privacyFilter == null) {
			return builder;
		}
		return
			builder.addWhereClause(
				ManagedAssessmentPagination.PRIVACY_FILTER_KEYS.getColumn(),
				ManagedAssessmentPagination.PRIVACY_FILTER_KEYS.getOperator(),
				privacyFilter.toString(), privacyFilter.getValue()
			);
	}

	private static SQLBuilder applyAttemptFilters(SQLBuilder builder, ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS attemptFilter) {
		if (attemptFilter == null)
			return builder;
		/* Failed filter has a special meaning: it means you also have never passed */
		if (attemptFilter.equals(ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS.FAILED)) {
			builder.addWhereClause(
				" NOT EXISTS (SELECT aua.id FROM assessment_user_association aua"
				+ " WHERE aua.user_id = :userId AND aua.assessment_id = assessment.id  AND aua.attempt_status_type_code = '"
				+ AttemptStatusType.GRADED + "' AND aua.passed_flag = true)"
			);
		}

		builder.addWhereClause(
			ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS.getColumn(),
			ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS.getOperator(),
			attemptFilter.toString(),
			attemptFilter.getValue()
		);
		/* This is because PASSED and FAILED are not a real statuses */
		if (attemptFilter.equals(
			ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS.PASSED) ||
			attemptFilter.equals(ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS.FAILED)) {
			builder.addWhereClause(
				" association.passed_flag = "
					+ attemptFilter.equals(ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS.PASSED)
			);
		}
		return builder;
	}

	private static SQLBuilder applyTakeabilityFilters(
		SQLBuilder builder, ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS takeabilityFilter
	) {
		if (takeabilityFilter == null)
			return builder;
		if (takeabilityFilter.equals(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.TAKEN)) {
			builder.addWhereClause(" association.attempt_status_type_code IN ('" + AttemptStatusType.GRADED + "','" + AttemptStatusType.GRADE_PENDING + "') ");
		} else if (takeabilityFilter.equals(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.NOT_TAKEN)) {
			builder.addWhereClause(" association.id IS NULL");
		} else if (takeabilityFilter.equals(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.TAKEABLE)) {
			builder
				.addWhereClause(
					" NOT EXISTS (" +
					"    SELECT aua.id FROM assessment_user_association aua " +
					"    WHERE aua.user_id = :userId AND aua.assessment_id = assessment.id " +
					"    AND aua.attempt_status_type_code = '" + AttemptStatusType.GRADED + "' AND passed_flag = true" +
					" ) "
				)
				.addWhereClause(
					" (" +
					"   association.id IS NULL OR " +
					"    (" +
					"      association.attempt_status_type_code = '" + AttemptStatusType.GRADED + "' " +
					"      AND association.passed_flag = false AND association.reattempt_allowed_flag = true " +
					"      AND (assessment.retakes_allowed IS NULL OR assessment.retakes_allowed > (SELECT COUNT(*) FROM assessment_user_association WHERE assessment_id = assessment.id AND user_id = :userId))" +
					"    ) " +
					" ) "
				);
		} else if (takeabilityFilter.equals(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.RETAKEABLE)) {
			builder.addWhereClause(
				"(association.attempt_status_type_code = '" + AttemptStatusType.GRADED + "' AND association.passed_flag = false AND association.reattempt_allowed_flag = true AND"
					+ " (assessment.retakes_allowed IS NULL OR assessment.retakes_allowed > (SELECT COUNT(*) FROM assessment_user_association "
					+ " WHERE assessment_id = assessment.id AND user_id = :userId)))"
			);
		}
		return builder;
	}

	private static SQLBuilder applyOwnerFilters(SQLBuilder builder, ManagedAssessmentPagination.OWNER_FILTER_KEYS ownerFilter) {
		if (ownerFilter == null) {
			return builder;
		}
		return builder.addWhereClause(ownerFilter.getColumn(), ManagedAssessmentPagination.OWNER_FILTER_KEYS.getOperator(), ownerFilter.toString(), ownerFilter.getValue());
	}

	private static SQLBuilder applyInvitationFilters(SQLBuilder builder, ManagedAssessmentPagination.INVITATION_FILTER_KEYS invitationFilter) {
		if (invitationFilter == null)
			return builder;
		if (invitationFilter.equals(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.DIRECTLY_INVITED)) {
			builder.addWhereClause(
				"EXISTS (SELECT rai.id FROM request_assessment_invitation rai"
				+ " INNER JOIN request r ON r.id = rai.id"
				+ " WHERE r.invitee_user_id = :userId "
				+ " AND rai.assessment_id = assessment.id"
				+ " AND r.request_date > :monthsAgo"
				+ " AND r.deleted = false)"
			);
		} else if (invitationFilter.equals(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.GROUP_INVITED)) {
			builder.addWhereClause(
				"EXISTS (SELECT tr.id from request_group_invitation rgi"
				+ " INNER JOIN request r ON r.id = rgi.id "
				+ " INNER JOIN user_group ug ON ug.id = rgi.user_group_id"
				+ " INNER JOIN user_group_requirement_set_association ugrs ON ugrs.user_group_id = ug.id"
				+ " INNER JOIN requirement_set rs ON rs.id = ugrs.requirement_set_id"
				+ " INNER JOIN requirement ON requirement.requirement_set_id = rs.id"
				+ " INNER JOIN test_requirement tr ON tr.id = requirement.id"
				+ " WHERE r.invitee_user_id = :userId"
				+ " AND tr.test_id = assessment.id"
				+ " AND r.request_status_type_code = 'sent'"
				+ " AND r.request_date > :monthsAgo"
				+ " AND r.deleted = false)"
			);
		} else if (invitationFilter.equals(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.DIRECTLY_OR_GROUP_INVITED)) {
			builder.addWhereClause(
				"(EXISTS (SELECT rai.id FROM request_assessment_invitation rai"
					+ " INNER JOIN request r ON r.id = rai.id"
					+ " WHERE r.invitee_user_id = :userId "
					+ " AND rai.assessment_id = assessment.id) "
				+ " OR EXISTS (SELECT tr.id from request_group_invitation rgi"
					+ " INNER JOIN request r ON r.id = rgi.id "
					+ " INNER JOIN user_group ug ON ug.id = rgi.user_group_id"
					+ " INNER JOIN user_group_requirement_set_association ugrs ON ugrs.user_group_id = ug.id"
					+ " INNER JOIN requirement_set rs ON rs.id = ugrs.requirement_set_id"
					+ " INNER JOIN requirement ON requirement.requirement_set_id = rs.id"
					+ " INNER JOIN test_requirement tr ON tr.id = requirement.id"
					+ " WHERE r.invitee_user_id = :userId"
					+ " AND tr.test_id = assessment.id"
					+ " AND r.request_status_type_code = 'sent'"
					+ " AND r.request_date > :monthsAgo"
					+ " AND r.deleted = false)"
				+ ")"
			);
		} else if (invitationFilter.equals(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.INDUSTRY_INVITED)){
			builder.addWhereClause("EXISTS (SELECT industry_id FROM profile_industry_association WHERE industry_id = assessment.industry_id AND profile_id=:profileId)");
		}
		return builder.addParam("monthsAgo", DateUtilities.getMidnightNMonthsAgo(ManagedAssessmentPagination.INVITATION_FILTER_KEYS.getMonthsago()));
	}

	private static SQLBuilder applyActivityFilters(SQLBuilder builder, ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS activityFilter) {
		if (activityFilter == null) {
			builder.addWhereClause(
				ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.getColumn(),
				SQLOperator.NOT_EQUALS,
				"activityStatusType",
				AssessmentStatusType.REMOVED
			);
			return builder;
		}
		return
			builder.addWhereClause(
				ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.getColumn(),
				ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.getOperator(),
				activityFilter.toString(), activityFilter.getValue()
			);
	}

	private static SQLBuilder applyIdFilters(SQLBuilder builder, List<Long> idsFilter) {
		if (CollectionUtilities.isEmpty(idsFilter)) {
			return builder;
		}
		return builder
			.addWhereClause("assessment.id IN (:assessmentIds)")
			.addParam("assessmentIds", idsFilter);
	}
}
