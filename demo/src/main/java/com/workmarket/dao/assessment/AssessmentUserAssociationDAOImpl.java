package com.workmarket.dao.assessment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.assessment.*;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.dto.AssessmentUser;
import com.workmarket.dto.AssessmentUserPagination;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.utility.StringUtilities.equalsAnyIgnoreCase;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class AssessmentUserAssociationDAOImpl extends AbstractDAO<AssessmentUserAssociation> implements AssessmentUserAssociationDAO {
	private static final Log logger = LogFactory.getLog(AssessmentUserAssociationDAOImpl.class);
	private static final String FAST_INVITATION_STATUS_COLUMN = new StringBuilder()
			.append(" IFNULL( IF (association.attempt_status_type_code IN ('graded', 'gradePending'), \n")
			.append(" IF(association.passed_flag = TRUE, 'Passed', 'Failed'), \n")
			.append(" IF(request_join.request_status_type_code IS NOT NULL, ")
			.append(" (SELECT 	CASE request_join.request_status_type_code \n")
			.append(" WHEN 		'accepted' \n")
			.append(" THEN 		'Accepted' \n")
			.append(" ELSE 		'Invited' END \n")
			.append("), 'Not Invited')), 'Not Invited') AS invitationStatus ").toString();
	private static final Map<String, String> assessmentUserStatusWhereClauses;
	private static final Map<String, String> assessmentUserLaneTypeWhereClauses;

	/** The purpose of these maps is to have the where clauses in one single place and be consistent while querying the counts */
	static {
		assessmentUserStatusWhereClauses = Maps.newLinkedHashMap();
		assessmentUserStatusWhereClauses.put(AssessmentUserPagination.COMPLETED, " association.attempt_status_type_code IN ('graded', 'gradePending') ");
		assessmentUserStatusWhereClauses.put(AssessmentUserPagination.PASSED, " association.passed_flag = TRUE ");
		assessmentUserStatusWhereClauses.put(AssessmentUserPagination.FAILED, " association.passed_flag = FALSE ");

		//User doesn't have an invitation and also hasn't completed the assessment
		assessmentUserStatusWhereClauses.put(AssessmentUserPagination.NOT_INVITED,
				" request_join.id IS NULL ");

		//User has an invitation but he hasn't completed the assessment
		assessmentUserStatusWhereClauses.put(AssessmentUserPagination.ACCEPTED,
				" request_join.id IS NOT NULL AND request_join.request_status_type_code = 'accepted' ");
		//User has an invitation but he hasn't completed the assessment
		assessmentUserStatusWhereClauses.put(AssessmentUserPagination.INVITED,
				" request_join.id IS NOT NULL ");

		//LaneType
		assessmentUserLaneTypeWhereClauses = Maps.newLinkedHashMap();
		assessmentUserLaneTypeWhereClauses.put(AssessmentUserPagination.LANE0, " lane.id IS NULL");
		assessmentUserLaneTypeWhereClauses.put(AssessmentUserPagination.LANE1, " lane.lane_type_id = 1");
		assessmentUserLaneTypeWhereClauses.put(AssessmentUserPagination.LANE2, " lane.lane_type_id = 2");
		assessmentUserLaneTypeWhereClauses.put(AssessmentUserPagination.LANE3, " lane.lane_type_id = 3");
	}

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<AssessmentUserAssociation> getEntityClass() {
		return AssessmentUserAssociation.class;
	}

	@SuppressWarnings("unchecked")
	public AssessmentUserAssociation findByUserAndAssessment(Long userId, Long assessmentId) {
		List<AssessmentUserAssociation> results = (List<AssessmentUserAssociation>) getFactory().getCurrentSession().getNamedQuery("assessmentUserAssociation.byUserAndAssessment")
				.setLong("user_id", userId)
				.setLong("assessment_id", assessmentId)
				.list();

		return (results == null || results.size() == 0) ? null : results.get(0);
	}

	public AssessmentUserAssociation findByUserAssessmentAndWork(Long userId, Long assessmentId, Long workId) {
		return (AssessmentUserAssociation) getFactory().getCurrentSession().getNamedQuery("assessmentUserAssociation.byUserAssessmentAndWork")
				.setLong("user_id", userId)
				.setLong("assessment_id", assessmentId)
				.setLong("work_id", workId)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public AssessmentUserAssociationPagination findByUser(Long userId, AssessmentUserAssociationPagination pagination) {
		return findByUsers(ImmutableSet.of(userId), pagination);
	}

	@SuppressWarnings("unchecked")
	public AssessmentUserAssociationPagination findByUsers(Set<Long> userIds, AssessmentUserAssociationPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("assessment", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.rowCount());

		criteria.add(Restrictions.in("user.id", userIds));
		criteria
				.createAlias("assessment", "a")
				.createAlias("a.assessmentStatusType", "s")
				.add(Restrictions.eq("s.code", AssessmentStatusType.ACTIVE));

		count.add(Restrictions.in("user.id", userIds));
		count
				.createAlias("assessment", "a")
				.createAlias("a.assessmentStatusType", "s")
				.add(Restrictions.eq("s.code", AssessmentStatusType.ACTIVE));

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().containsKey(AssessmentUserAssociationPagination.FILTER_KEYS.COMPANY_ID.toString())) {
				Long companyId = Long.valueOf(pagination.getFilters().get(AssessmentUserAssociationPagination.FILTER_KEYS.COMPANY_ID.toString()));

				criteria
						.createAlias("a.company", "c", Criteria.LEFT_JOIN)
						.add(Restrictions.eq("c.id", companyId));
				count
						.createAlias("a.company", "c", Criteria.LEFT_JOIN)
						.add(Restrictions.eq("c.id", companyId));
			}
		}

		pagination.setResults(criteria.list());
		pagination.setRowCount(isNotEmpty(count.list()) ? ((Long) CollectionUtilities.first(count.list())).intValue() : 0);
		return pagination;
	}

	@Override
	public AssessmentUserPagination findAllAssessmentUsers(Long companyId, Long assessmentId, AssessmentUserPagination pagination) {
		SQLBuilder builder = newAssessmentUserSQLBuilder(companyId, assessmentId, pagination);
		return processResultSet(builder, pagination);
	}

	@Override
	public AggregatesDTO countAssessmentUsers(Long companyId, Long assessmentId, AssessmentUserPagination pagination) {
		SQLBuilder builder = newAssessmentUserSQLBuilder(companyId, assessmentId, pagination);
		AggregatesDTO dto = new AggregatesDTO();

		builder.getOrderColumns().clear();
		builder.setStartRow(null);

		builder.getColumns().clear();
		builder.addColumns(" assessment.type, COUNT(user.id) as count ",
				" IFNULL(lane.lane_type_id, 0) as laneType ", FAST_INVITATION_STATUS_COLUMN,
				"association.attempt_status_type_code", "association.passed_flag",
				"request_join.id IS NOT NULL AS isInvited");

		builder.addGroupColumns(" invitationStatus ", " laneType ",
				" association.attempt_status_type_code ", " isInvited ");

		logger.debug(builder.build());
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(builder.build(), builder.getParams());

		String type = "";
		if (!rows.isEmpty()) {
			// type will be the same, so get the first non-null one (the group-by makes some entries null)
			for (Map<String, Object> row : rows) {
				type = (String) row.get("type");
				if (isNotBlank(type))
					break;
			}
		}

		for (Map<String, Object> row : rows) {
			Integer count = ((Long) row.get("count")).intValue();

			switch (((Long) row.get("laneType")).intValue()) {
				case 0:
					//Lane 1
					dto.addToStatusCount(AssessmentUserPagination.LANE0, count);
					break;
				case 1:
					//Lane 1
					dto.addToStatusCount(AssessmentUserPagination.LANE1, count);
					break;
				case 2:
					//Lane 2
					dto.addToStatusCount(AssessmentUserPagination.LANE2, count);
					break;
				case 3:
					//Lane 3
					dto.addToStatusCount(AssessmentUserPagination.LANE3, count);
					break;
			}

			String invitationStatus = (String) row.get("invitationStatus");
			dto.addToStatusCount(invitationStatus, count);

			String attemptStatus = (String) row.get("attempt_status_type_code");
			if (AssessmentType.SURVEY.name().equalsIgnoreCase(type)) {
				if (isNotBlank(attemptStatus) && !AttemptStatusType.INPROGRESS.equalsIgnoreCase(attemptStatus))
					dto.addToStatusCount(AssessmentUserPagination.COMPLETED, count);
			} else {
				// for graded assessments, invitation status value is overwritten by grade status, need to add it manually
				// NOTE: the Number cast is because sometimes it returns a Long, sometimes an Integer!
				if ((equalsAnyIgnoreCase(invitationStatus, AssessmentUserPagination.PASSED, AssessmentUserPagination.FAILED))
						&& ((Number) row.get("isInvited")).intValue() > 0) {
					dto.addToStatusCount(AssessmentUserPagination.INVITED, count);
				}
			}
		}

		// graded complete value
		if (AssessmentType.GRADED.name().equalsIgnoreCase(type))
			dto.setCountForStatus(AssessmentUserPagination.COMPLETED,
					dto.getCountForStatus(AssessmentUserPagination.PASSED) + dto.getCountForStatus(AssessmentUserPagination.FAILED));

		//All
		int total = dto.getCountForStatus(AssessmentUserPagination.COMPLETED) + dto.getCountForStatus(AssessmentUserPagination.NOT_INVITED) +
				dto.getCountForStatus(AssessmentUserPagination.INVITED);
		dto.setCountForStatus(AssessmentUserPagination.ALL, total);

		return dto;
	}

	@Override
	public AssessmentUserPagination findLatestAssessmentUserAttempts(Long assessmentId, AssessmentUserPagination pagination) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns(
				"user.id",
				"user.user_number",
				"user.first_name",
				"user.last_name",
				"company.id AS companyId",
				"company.effective_name AS companyName",
				"assessment.id",
				"association.created_on",
				"association.completed_on",
				"association.graded_on",
				"association.passed_flag",
				"association.score",
				"association.attempt_status_type_code",
				"work.work_number",
				"work.id AS workId",
				"work.title",
				"IF((work.id IS NULL), " +
						"(SELECT MAX(ID) FROM assessment_attempt attempt WHERE attempt.assessment_user_association_id = association.id), " +
						"assessment_attempt.id) AS attemptId"
		)
				.addTable("user")

				.addJoin("INNER JOIN company ON company.id = user.company_id")
				.addJoin("INNER JOIN assessment_user_association association ON (association.user_id = user.id AND association.assessment_id = :assessmentId)")
				.addJoin("INNER JOIN assessment ON  assessment.id = association.assessment_id")
				.addJoin("LEFT JOIN assessment_attempt ON (assessment_attempt.assessment_user_association_id = association.id AND assessment_attempt.work_id IS NOT NULL)")
				.addJoin("LEFT JOIN work ON work.id = assessment_attempt.work_id")

				.addWhereClause("user.user_status_type_code IN ('pending', 'approved')")
				.addParam("assessmentId", assessmentId);

		if (pagination.getSortColumn() != null) {

			if (pagination.getSortColumn().equals(AssessmentUserPagination.SORTS.ATTEMPT_STATUS.toString())) {
				// this double column order by allows for proper sorting with "in progress" which has a null score that should be accounted for
				builder.addOrderBy(AssessmentUserPagination.SORTS.ATTEMPT_STATUS.getColumn(), " IS NULL");
				builder.addOrderBy(AssessmentUserPagination.SORTS.SCORE.getColumn(), pagination.getSortDirection().toString());
			} else {
				// everything else can behave normally
				builder.addOrderBy(AssessmentUserPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
			}

		} else {
			builder.addOrderBy(AssessmentUserPagination.SORTS.USER_LAST_NAME.getColumn(), AssessmentUserPagination.SORT_DIRECTION.ASC.toString());
		}

		if (pagination.hasFilter(AssessmentUserPagination.FILTER_KEYS.USER_NUMBER)) {
			builder.getWhereClauses().add("user.user_number = :userNumber");
			builder.addParam("userNumber", pagination.getFilter(AssessmentUserPagination.FILTER_KEYS.USER_NUMBER));
		}

		if (pagination.hasFilter(AssessmentUserPagination.FILTER_KEYS.ATTEMPT_STATUS)) {
			builder.getWhereClauses().add("association.attempt_status_type_code = :attemptStatus");
			builder.addParam("attemptStatus", pagination.getFilter(AssessmentUserPagination.FILTER_KEYS.ATTEMPT_STATUS));
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		logger.debug(builder.build());

		return processResultSetForLatestAssessmentAttempts(builder, pagination);
	}

	public List<Long> findSurveysCompletedForWork(Long workId, Long userId) {

		SQLBuilder finder = new SQLBuilder();
		finder.setDistinct(true)
				.addColumn("a.id")
				.addTable("assessment a")
				.addJoin("inner join assessment_user_association aua on a.id = aua.assessment_id")
				.addJoin("inner join assessment_attempt aat on aat.assessment_user_association_id=aua.id")
				.addWhereClause(":workid = aat.work_id")
				.addWhereClause(":userid = aua.user_id")
				.addWhereClause(":attemptStatus = aat.attempt_status_type_code")
				.addParam("workid", workId)
				.addParam("userid", userId)
				.addParam("attemptStatus", AttemptStatusType.COMPLETE);

		return jdbcTemplate.queryForList(finder.build(), finder.getParams(), Long.class);

	}

	public List<Long> findSurveysCompletedForWorkOnBehalf(Long workId, Long behalfOfId) {

		SQLBuilder finder = new SQLBuilder();
		finder.setDistinct(true);

		finder.addColumn("a.id")
				.addTable("assessment a")
				.addJoin("inner join assessment_user_association aua on a.id = aua.assessment_id")
				.addJoin("inner join assessment_attempt aat on aat.assessment_user_association_id=aua.id")
				.addJoin("inner join work_assessment_association waa on a.id = waa.assessment_id")
				.addWhereClause(":workid = waa.work_id")
				.addWhereClause(":userid = aat.on_behalf_of")
				.addWhereClause(":attemptStatus = aat.attempt_status_type_code")
				.addParam("workid", workId)
				.addParam("userid", behalfOfId)
				.addParam("attemptStatus", "complete");

		return jdbcTemplate.queryForList(finder.build(), finder.getParams(), Long.class);

	}

	private SQLBuilder newAssessmentUserSQLBuilder(Long companyId, Long assessmentId, AssessmentUserPagination pagination) {
		SQLBuilder builder = new SQLBuilder();

		builder.getColumns().addAll(Arrays.asList("user.id", "user.user_number", "user.first_name", "user.last_name",
				"company.id AS companyId", "company.effective_name AS companyName",
				"address.city", "state.short_name as state", "address.country", "address.postal_code", "address.country",
				"lane.lane_type_id AS laneType", "lane.created_on AS dateAdded", "association.attempt_status_type_code", "association.passed_flag"));

		builder.getColumns().add(FAST_INVITATION_STATUS_COLUMN);

		builder.getTables().add("user");

		builder.addJoin(" INNER 	JOIN company ON company.id = user.company_id ");
		builder.addJoin(" INNER 	JOIN profile ON user.id = profile.user_id ");
		builder.addJoin(" LEFT 	JOIN address ON address.id = profile.address_id ");
		builder.addJoin(" LEFT 	JOIN state ON state.id = address.state ");
		builder.addJoin(" LEFT 	JOIN assessment_user_association association ON (association.user_id = user.id AND association.assessment_id = :assessmentId) ");
		builder.addJoin(" LEFT 	JOIN assessment	ON 	assessment.id = association.assessment_id ");
		builder.addJoin(" LEFT 	JOIN lane_association lane ON (lane.user_id = user.id AND lane.deleted = false AND lane.approval_status IN (1,5) " +
				" AND lane.verification_status = 1 AND lane.company_id = :companyId) ");
		builder.addJoin(" LEFT JOIN  \n" +
				"(SELECT request.id, invitee_user_id, request_status_type_code from request \n" +
				"INNER JOIN request_assessment_invitation ON request_assessment_invitation.id = request.id\n" +
				"WHERE request_assessment_invitation.assessment_id = :assessmentId " +
				"AND request.deleted = FALSE AND request.request_status_type_code IN ('sent','declined','ignored','accepted')) request_join " +
				"ON request_join.invitee_user_id = user.id ");

		builder.getWhereClauses().add(" user.user_status_type_code IN ('pending', 'approved')");
		builder.getWhereClauses().add(" ( user.company_id = :companyId OR lane.id IS NOT NULL) ");

		builder.getParams().addValue("companyId", companyId);
		builder.getParams().addValue("assessmentId", assessmentId);

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(AssessmentUserPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else
			builder.addOrderBy(AssessmentUserPagination.SORTS.USER_ID.getColumn(), WorkSearchDataPagination.SORT_DIRECTION.ASC.toString());

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		Iterator<String> it = pagination.getFilters().keySet().iterator();

		int i = 0;
		while (it.hasNext()) {

			String filter = it.next();

			if (AssessmentUserPagination.FILTER_KEYS.STATUS.equals(AssessmentUserPagination.FILTER_KEYS.valueOf(filter))) {

				if (assessmentUserStatusWhereClauses.containsKey(pagination.getFilters().get(filter))) {
					builder.getWhereClauses().add(assessmentUserStatusWhereClauses.get(pagination.getFilters().get(filter)));
				}

			} else if (AssessmentUserPagination.FILTER_KEYS.LANE_TYPE_ID.equals(AssessmentUserPagination.FILTER_KEYS.valueOf(filter))) {

				if (assessmentUserLaneTypeWhereClauses.containsKey(pagination.getFilters().get(filter))) {
					builder.getWhereClauses().add(assessmentUserLaneTypeWhereClauses.get(pagination.getFilters().get(filter)));
				}

			} else if (AssessmentUserPagination.FILTER_KEYS.KEYWORD.equals(AssessmentUserPagination.FILTER_KEYS.valueOf(filter))) {

				builder.getWhereClauses().add(" (user.first_name like :keyword OR user.last_name like :keyword OR company.effective_name like :keyword OR address.city like :keyword) ");
				builder.getParams().addValue("keyword", StringUtilities.processForLike(pagination.getFilters().get(filter)));

			} else {
				builder.getWhereClauses().add(AssessmentUserPagination.FILTER_KEYS.valueOf(filter).getColumn() + " = :param" + i);
				builder.getParams().addValue("param" + i, pagination.getFilters().get(filter));
			}

			i++;
		}

		return builder;
	}

	private AssessmentUserPagination processResultSet(SQLBuilder builder, AssessmentUserPagination pagination) {
		Assert.notNull(pagination);

		logger.debug("SQL: " + builder.build());

		RowMapper<AssessmentUser> mapper = new RowMapper<AssessmentUser>() {

			@Override
			public AssessmentUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				AssessmentUser row = new AssessmentUser();

				row.setUserId(rs.getLong("user.id"));
				row.setUserNumber(rs.getString("user.user_number"));
				row.setFirstName(rs.getString("user.first_name"));
				row.setLastName(rs.getString("user.last_name"));
				row.setCompanyId(rs.getLong("companyId"));
				row.setCompanyName(rs.getString("companyName"));
				row.setCity(rs.getString("address.city"));
				row.setState(rs.getString("state"));
				row.setCountry(rs.getString("address.country"));
				row.setPostalCode(rs.getString("address.postal_code"));
				row.setLaneType(rs.getLong("laneType"));
				row.setDateAdded(DateUtilities.getCalendarFromDate(rs.getTimestamp("dateAdded")));
				row.setInvitationStatus(rs.getString("invitationStatus"));
				row.setAttemptStatus(rs.getString("association.attempt_status_type_code"));
				row.setPassedFlag(rs.getBoolean("association.passed_flag"));

				return row;
			}
		};

		Integer rowCount = jdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class);
		pagination.setRowCount(rowCount);
		List<AssessmentUser> results = Lists.newArrayList();
		if (rowCount > 0) {
			pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), mapper));
		} else {
			pagination.setResults(results);
		}

		return pagination;
	}

	private AssessmentUserPagination processResultSetForLatestAssessmentAttempts(SQLBuilder builder, AssessmentUserPagination pagination) {
		Assert.notNull(pagination);

		logger.debug("SQL: " + builder.build());

		RowMapper<AssessmentUser> mapper = new RowMapper<AssessmentUser>() {

			@Override
			public AssessmentUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				AssessmentUser row = new AssessmentUser();

				row.setUserId(rs.getLong("user.id"));
				row.setUserNumber(rs.getString("user.user_number"));
				row.setFirstName(rs.getString("user.first_name"));
				row.setLastName(rs.getString("user.last_name"));
				row.setCompanyId(rs.getLong("companyId"));
				row.setCompanyName(rs.getString("companyName"));

				row.setAttemptId(rs.getLong("attemptId"));
				row.setAttemptStatus(rs.getString("association.attempt_status_type_code"));
				row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("association.created_on")));
				row.setCompletedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("association.completed_on")));
				row.setGradedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("association.graded_on")));
				row.setPassedFlag(rs.getBoolean("association.passed_flag"));
				row.setScore(rs.getDouble("score"));

				row.setWorkId(rs.getLong("workId"));
				row.setWorkTitle(rs.getString("work.title"));
				row.setWorkNumber(rs.getString("work.work_number"));
				row.setAssessmentId(rs.getLong("assessment.id"));

				return row;
			}
		};

		pagination.setRowCount(jdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class));
		pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), mapper));

		return pagination;
	}
}
