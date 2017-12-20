package com.workmarket.domains.groups.dao;

import com.google.common.base.Joiner;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import com.workmarket.domains.groups.model.GroupMembershipPagination;
import com.workmarket.domains.groups.model.GroupMembershipRowMapper;
import com.workmarket.domains.model.request.Request;
import com.workmarket.domains.model.request.RequestStatusType;
import com.workmarket.service.business.dto.GroupMembershipDTO;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;


@SuppressWarnings("deprecation")
@Repository
public class GroupMembershipDAOImpl extends AbstractDAO<Request> implements GroupMembershipDAO {

	private static final Log logger = LogFactory.getLog(GroupMembershipDAOImpl.class);

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Request> getEntityClass() {
		return Request.class;
	}

	private final static String MEMBER_PASSED = StringUtils.quote(GroupMemberRequestType.MEMBER_PASSED.toString());
	private final static String MEMBER_OVERRIDE = StringUtils.quote(GroupMemberRequestType.MEMBER_OVERRIDE.toString());
	private final static String PENDING_FAILED = StringUtils.quote(GroupMemberRequestType.PENDING_FAILED.toString());
	private final static String PENDING_PASSED = StringUtils.quote(GroupMemberRequestType.PENDING_PASSED.toString());
	private final static String DECLINED = StringUtils.quote(GroupMemberRequestType.DECLINED.toString());
	private final static String INVITED = StringUtils.quote(GroupMemberRequestType.INVITED.toString());

	private final static String[] SELECT_FIELDS = new String[]{
			"DISTINCT user.id AS userId", "user.user_number AS userNumber",
			"user.first_name AS firstName", "user.last_name AS lastName",
			"(SELECT AVG(rating.value) FROM rating WHERE rating_shared_flag = true AND rated_user_id = user.id) AS starRating",
			"company.name AS companyName", "address.city", "address.country",
			"address.postal_code AS postalCode", "state.short_name as state",
			"IFNULL(address.latitude, postal_code.latitude) AS latitude",
			"IFNULL(address.longitude, postal_code.longitude) AS longitude",
			"IFNULL(lane.lane_type_id, IF(company.id = :companyId, 0, 4)) AS laneType"
	};

	private final static String GROUP_MEMBERSHIP_STATUS_COLUMN =
			"IF(user_user_group_association.approval_status = 1 AND user_user_group_association.override_member = false," + MEMBER_PASSED + ", \n" +
					"IF(user_user_group_association.approval_status = 1 AND user_user_group_association.override_member = true," + MEMBER_OVERRIDE + ", \n" +
					"IF(user_user_group_association.approval_status = 0 AND user_user_group_association.verification_status = 2," + PENDING_FAILED + ", \n" +
					"IF(user_user_group_association.approval_status = 0 AND user_user_group_association.verification_status IN (0,1)," + PENDING_PASSED + ", \n" +
					"IF(user_user_group_association.approval_status = 2, " + DECLINED + ", " + INVITED + "))))) as status ";

	@Override
	public GroupMembershipPagination findGroupMembersByUserGroupId(Long groupId, GroupMemberRequestType requestType, GroupMembershipPagination pagination, Long companyId) {
		Assert.notNull(groupId);
		Assert.notNull(pagination);
		Assert.notNull(requestType);

		List<GroupMembershipDTO> groupMembershipResults = new ArrayList<>();
		SQLBuilder sqlBuilder = newCountGroupMembersSQLBuilder(groupId, requestType, companyId);
		pagination.setRowCount(jdbcTemplate.queryForObject(sqlBuilder.buildCount("DISTINCT user.id"), sqlBuilder.getParams(), Integer.class));

		if (pagination.getRowCount() > 0) {
			sqlBuilder = newGroupMembersSQLBuilder(groupId, requestType, pagination, companyId);
			groupMembershipResults = jdbcTemplate.query(sqlBuilder.build(), sqlBuilder.getParams(), new GroupMembershipRowMapper());
		}
		pagination.setResults(groupMembershipResults);
		return pagination;
	}

	@Override
	public Integer countGroupMembersByUserGroupId(Long groupId, Long companyId, GroupMemberRequestType memberType) {
		SQLBuilder sqlBuilder = newCountGroupMembersSQLBuilder(groupId, memberType, companyId);
		logger.debug(sqlBuilder.buildCount("DISTINCT user.id"));
		return jdbcTemplate.queryForObject(sqlBuilder.buildCount("DISTINCT user.id"), sqlBuilder.getParams(), Integer.class);
	}

	@Override
	public Map<Long, String> getDerivedStatusesByGroupIdAndUserIds(final long groupId, final Set<Long> userIds) {
		Assert.notEmpty(userIds);

		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder.addTable("user")
				.addJoin("LEFT JOIN user_user_group_association ON user_user_group_association.user_id = user.id"
						+ " AND user_user_group_association.user_group_id = " + groupId
						+ " AND user_user_group_association.deleted = 0")
				.addJoin("LEFT JOIN user_group ON user_group.id = user_user_group_association.user_group_id ")
				.addColumn(GROUP_MEMBERSHIP_STATUS_COLUMN)
				.addColumn("user.id")
				.addWhereClause("user.id IN " + "(" + Joiner.on(",").join(userIds) + ")");

		List<Map<Long, String>> results = jdbcTemplate.query(sqlBuilder.build(),
				new RowMapper() {
					public Map<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
						try {
							Map<Long, String> row = new HashMap<>();
							row.put(rs.getLong("id"), rs.getString("status"));
							return row;
						} catch (Exception e) {
							logger.error(e.getMessage());
							throw new SQLException(e);
						}
					}
				});

		//flatten List of maps into Map
		Map<Long, String> statusMap = newHashMapWithExpectedSize(results.size());
		for (Map<Long, String> row : results) {
			for (Entry<Long, String> keyVal : row.entrySet()) {
				statusMap.put(keyVal.getKey(), keyVal.getValue());
			}
		}
		return statusMap;
	}

	@Override
	public int countPendingMembershipsByCompany(Long companyId) {
		SQLBuilder sql = new SQLBuilder()
				.addColumn("COUNT(m.id)")
				.addTable("user_user_group_association m")
				.addJoin("INNER JOIN user_group g ON g.id = m.user_group_id")
				.addWhereClause("m.approval_status", "=", "approvalStatus", ApprovalStatus.PENDING)
				.addWhereClause("g.company_id", "=", "companyId", companyId)
				.addWhereClause("g.deleted", "=", "deleted", 0)
				.addWhereClause("m.deleted", "=", "deleted", 0);
		return jdbcTemplate.queryForObject(sql.build(), sql.getParams(), Integer.class);
	}

	private SQLBuilder newGroupMembersSQLBuilder(Long groupId, GroupMemberRequestType requestType, GroupMembershipPagination pagination, Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder()
				.addColumns(SELECT_FIELDS)
				.addTable("user")
				.addJoin("INNER JOIN company ON user.company_id = company.id ")
				.addJoin("INNER JOIN profile ON profile.user_id = user.id ");

		if (GroupMemberRequestType.INVITED.equals(requestType)) {
			sqlBuilder.addJoin("INNER JOIN request ON user.id = request.invitee_user_id")
					.addJoin("INNER JOIN request_group_invitation ON request.id = request_group_invitation.id")
					.addWhereClause("request_group_invitation.user_group_id = :groupId")
					.addWhereClause("request_group_invitation.invitation_type = 'NEW' ")
					.addWhereClause("request.deleted = false")
					.addWhereClause("request.invitee_user_id = user.id")
					.addWhereClause(String.format("request.request_status_type_code = '%s'", RequestStatusType.SENT))
					.addColumns("NULL AS verificationStatus", "NULL AS approvalStatus");
		} else {
			sqlBuilder.addColumns("user_user_group_association.verification_status AS verificationStatus",
					"user_user_group_association.approval_status AS approvalStatus")
					.addJoin("LEFT JOIN user_user_group_association ON user_user_group_association.user_id = user.id " +
							" AND user_user_group_association.user_group_id = :groupId AND user_user_group_association.deleted = false ")
					.addJoin("LEFT	JOIN user_group ON user_group.id = user_user_group_association.user_group_id ");
		}

		sqlBuilder.addJoin("LEFT JOIN address ON profile.address_id = address.id ")
				.addJoin("LEFT JOIN state ON state.id = address.state ")
				.addJoin("LEFT JOIN postal_code ON postal_code.id = profile.postal_code_id ")
				.addJoin("LEFT JOIN lane_association lane ON lane.company_id = :companyId " +
						" AND lane.deleted = false AND lane.approval_status IN (1,5) AND lane.verification_status = 1 " +
						" AND lane.user_id = user.id ")
				.addParam("groupId", groupId)
				.addParam("companyId", companyId);

		applyFilters(sqlBuilder, requestType);

		if (pagination.getSortColumn() != null) {
			sqlBuilder.addOrderBy(GroupMembershipPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		}

		sqlBuilder.addLimitClause(pagination.getStartRow(), pagination.getResultsLimit(), pagination.isLimitMaxRows());

		logger.debug(sqlBuilder.build());
		return sqlBuilder;
	}

	private SQLBuilder newCountGroupMembersSQLBuilder(Long groupId, GroupMemberRequestType requestType, Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder();

		sqlBuilder.addTable("user")
				.addParam("groupId", groupId)
				.addParam("companyId", companyId);

		if (requestType.equals(GroupMemberRequestType.INVITED)) {
			sqlBuilder.addJoin("INNER JOIN request ON user.id = request.invitee_user_id")
					.addJoin("INNER JOIN request_group_invitation ON request.id = request_group_invitation.id")
					.addWhereClause("request_group_invitation.user_group_id = :groupId")
					.addWhereClause("request_group_invitation.invitation_type = 'NEW' ")
					.addWhereClause("request.invitee_user_id = user.id")
					.addWhereClause("request.deleted = false")
					.addWhereClause(String.format("request.request_status_type_code = '%s'", RequestStatusType.SENT));
		} else {
			sqlBuilder.addJoin("LEFT JOIN user_user_group_association ON user_user_group_association.user_id = user.id " +
					" AND user_user_group_association.user_group_id = :groupId AND user_user_group_association.deleted = false ")
					.addJoin("LEFT 	JOIN user_group ON user_group.id = user_user_group_association.user_group_id ");
		}

		applyFilters(sqlBuilder, requestType);
		return sqlBuilder;
	}

	private void applyFilters(SQLBuilder sqlBuilder, GroupMemberRequestType requestType) {
		switch (requestType) {
			case MEMBER: {
				String memberStatus =
						"IF(user_user_group_association.approval_status = :approvalStatus AND user_user_group_association.override_member = false," + MEMBER_PASSED + ", \n" +
								"IF(user_user_group_association.approval_status = :approvalStatus AND user_user_group_association.override_member = true," + MEMBER_OVERRIDE + ",'')) as status";

				sqlBuilder.addColumn(memberStatus)
						.addWhereClause("user_user_group_association.approval_status = :approvalStatus")
						.addParam("approvalStatus", ApprovalStatus.APPROVED.ordinal());
			}
			break;
			case MEMBER_PASSED: {
				sqlBuilder.addColumn(MEMBER_PASSED + " AS status")
						.addWhereClause("user_user_group_association.approval_status = :approvalStatus")
						.addWhereClause("user_user_group_association.override_member = false")
						.addParam("approvalStatus", ApprovalStatus.APPROVED.ordinal());
			}
			break;
			case MEMBER_OVERRIDE: {
				sqlBuilder.addColumn(MEMBER_OVERRIDE + " AS status")
						.addWhereClause("user_user_group_association.approval_status = :approvalStatus")
						.addWhereClause("user_user_group_association.override_member = true")
						.addParam("approvalStatus", ApprovalStatus.APPROVED.ordinal());
			}
			break;
			case PENDING: {
				String pendingStatus =
						"IF(user_user_group_association.approval_status = :approvalStatus AND user_user_group_association.verification_status = 2," + PENDING_FAILED + ", \n" +
								"IF(user_user_group_association.approval_status = :approvalStatus AND user_user_group_association.verification_status IN (0,1)," + PENDING_PASSED + ",'')) AS status";

				sqlBuilder.addColumn(pendingStatus)
						.addWhereClause("user_user_group_association.approval_status = :approvalStatus")
						.addParam("approvalStatus", ApprovalStatus.PENDING.ordinal());
			}
			break;
			case PENDING_PASSED: {
				sqlBuilder.addColumn(PENDING_PASSED + " AS status")
						.addWhereClause("user_user_group_association.approval_status = :approvalStatus")
						.addWhereClause("user_user_group_association.verification_status IN (0,1)")
						.addParam("approvalStatus", ApprovalStatus.PENDING.ordinal());
			}
			break;
			case PENDING_FAILED: {
				sqlBuilder.addColumn(PENDING_FAILED + " AS status")
						.addWhereClause("user_user_group_association.approval_status = :approvalStatus")
						.addWhereClause("user_user_group_association.verification_status = :verificationStatus")
						.addParam("approvalStatus", ApprovalStatus.PENDING.ordinal())
						.addParam("verificationStatus", VerificationStatus.FAILED.ordinal());
			}
			break;
			case DECLINED: {
				sqlBuilder.addColumn(DECLINED + " AS status")
						.addWhereClause("user_user_group_association.approval_status = :approvalStatus")
						.addParam("approvalStatus", ApprovalStatus.DECLINED.ordinal());
			}
			break;
			case INVITED: {
				sqlBuilder.addColumn(INVITED + " AS status")
						// user association doesn't exist
						.addWhereClause("NOT EXISTS(SELECT id FROM user_user_group_association WHERE user_group_id = :groupId AND deleted = false AND user_id = user.id)");
			}
			break;
			default: {
				sqlBuilder.addColumn(GROUP_MEMBERSHIP_STATUS_COLUMN)
						.addWhereClause("user_group.id = :groupId");
			}
		}
	}

}
