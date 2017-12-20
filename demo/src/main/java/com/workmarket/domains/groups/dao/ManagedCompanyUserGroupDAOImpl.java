package com.workmarket.domains.groups.dao;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.request.RequestStatusType;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class ManagedCompanyUserGroupDAOImpl implements ManagedCompanyUserGroupDAO {

	private static final Log logger = LogFactory.getLog(ManagedCompanyUserGroupDAOImpl.class);

	/**
	 * All these queries are a match of the SOLR query running for Search.
	 * The intention is to replace the SearchService call on these pages:
	 *
	 * /groups/view
	 * /groups/view/pending
	 * /groups/invitations
	 */

	@Qualifier("jdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Qualifier("readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	/** Common columns to be retrieved by the 3 different searches */
	private static final String[] COMMON_COLUMNS = new String[] {
			"ug.id", "ug.name", "ug.description",
			"ug.deleted", "ug.auto_generated", "ug.industry_id",
			"ug.requires_approval", "ug.uuid",
			"ug.open_membership", "ug.active_flag", "ug.searchable",
			"ug.creator_id", "ug.created_on",
			"c.id AS companyId", "c.name AS companyName",
			"c.operating_as_individual_flag",
			"u.first_name", "u.last_name", "u.user_number", "u.id"};

	private static final String[] OWNER_COLUMN = new String[] {
		"(IF (c.id = :companyId, 1,0) AND TRUE = :isAdminOrManager) AS is_owner"};

	private static final String[] NULL_OWNER_COLUMN = new String[] {
		"NULL AS is_owner"
	};

	private static final String[] NULL_USER_ASSOCIATION_QUERY_COLUMNS = new String[] {
			"NULL AS date_approved",
			"NULL AS date_applied",
			"NULL AS date_invited",
			"NULL AS is_member"
	};

	private static final String SHARED_GROUPS_WHERE_CLAUSE =
			"EXISTS (\n" +
					"  SELECT g.id\n" +
					"  FROM user_group g\n" +
					"  INNER JOIN user_group_network_association gna ON gna.user_group_id = g.id AND g.deleted = false\n" +
					"  INNER JOIN company_network_association cna ON cna.network_id = gna.network_id\n" +
					"  WHERE ug.id = g.id \n" +
					"  AND cna.company_id = :companyId \n" +
					"  AND cna.deleted = false\n" +
					"  AND cna.active = true\n" +
					"  And gna.deleted = false\n" +
					"  AND gna.active = true  \n" +
					"  AND g.deleted = false\n" +
					"  AND g.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'\n" +

					"  UNION\n" +

					"  SELECT user_group.id" +
					"  FROM user_group\n" +
					"  WHERE user_group.company_id = :companyId" +
					"  AND ug.id = user_group.id" +
					"  AND user_group.deleted = false\n" +
					"  AND user_group.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'\n" +
					")";

	/** Columns to be retrieved by the queries that uses the user_user_group_association table */
	private static final String[] USER_ASSOCIATION_QUERY_COLUMNS = new String[] {
			"association.date_approved", "association.date_applied", "association.date_invited", "IFNULL(association.approval_status, 0) AS is_member" };

	private static final String MEMBER_COUNT = "(SELECT count(1) FROM user_user_group_association uuga \n" +
			" INNER  JOIN user u ON u.id = uuga.user_id \n" +
			" WHERE (uuga.approval_status = 1 OR uuga.override_member = 1) AND uuga.user_group_id = ug.id AND u.user_status_type_code = '" + UserStatusType.APPROVED + "' AND uuga.deleted = false ) AS memberCount ";

	private static final String PENDING_APPLICANT_COUNT = "(SELECT count(1) FROM user_user_group_association uuga \n" +
			" INNER  JOIN user u ON u.id = uuga.user_id \n" +
			" WHERE  uuga.verification_status IN (0,1,2) AND uuga.approval_status = 0 AND uuga.user_group_id = ug.id AND u.user_status_type_code = '" + UserStatusType.APPROVED + "' AND uuga.deleted = false ) AS pendingApplicantCount ";

	private static final String INVITED_APPLICANT_COUNT = "(SELECT count(1) FROM request_group_invitation rgi \n" +
			" INNER JOIN request r on r.id = rgi.id \n" +
			" WHERE rgi.user_group_id = ug.id AND r.deleted = false AND r.request_status_type_code = '" + RequestStatusType.SENT + "') AS invitedApplicantCount";

	@Override
	public ManagedCompanyUserGroupRowPagination findMyCompanyGroups(Long companyId, Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		SQLBuilder builder = buildCompanyUserGroupSql(companyId, userId, pagination);

		builder
			.addColumns(NULL_USER_ASSOCIATION_QUERY_COLUMNS)
			.addColumns(COMMON_COLUMNS)
			.addColumns(OWNER_COLUMN)
			.addColumns(MEMBER_COUNT)
			.addColumns(PENDING_APPLICANT_COUNT)
			.addColumns(INVITED_APPLICANT_COUNT)
			// The group is my company's group
			.addWhereClause("c.id = :companyId")
			.addWhereClause("ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'");

		return getResults(builder, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findSharedAndOwnedGroups(
			final Long companyId,
			final Long userId,
			final Boolean orgEnabled,
			final Map<String, OrgUnitDTO> orgUnitsToFilterBy,
			final ManagedCompanyUserGroupRowPagination pagination) {
		final SQLBuilder builder = buildCompanyUserGroupSql(companyId, userId, pagination)
				.addColumns(NULL_USER_ASSOCIATION_QUERY_COLUMNS)
				.addColumns(COMMON_COLUMNS)
				.addColumns(OWNER_COLUMN)
				.addColumns(MEMBER_COUNT)
				.addColumns(PENDING_APPLICANT_COUNT)
				.addColumns(INVITED_APPLICANT_COUNT)
				.addColumn("EXISTS(SELECT a.id FROM user_group_network_association a WHERE a.user_group_id = ug.id AND a.deleted = false AND a.active = true) isShared")
				.addWhereClause(SHARED_GROUPS_WHERE_CLAUSE)
				.addWhereClause("ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'");

		if (orgEnabled) {
			if (MapUtils.isEmpty(orgUnitsToFilterBy)) {
				return pagination;
			}

			builder.addJoin("INNER JOIN user_group_org_unit_association ugoa ON ug.uuid = ugoa.user_group_uuid")
					.addGroupColumns("ug.id")
					.addGroupColumns("ugoa.org_unit_uuid")
					.addColumn("ugoa.org_unit_uuid")
					.addWhereClause("ugoa.org_unit_uuid IN (:orgUnitUuids)")
					.addWhereClause("ugoa.deleted = 0")
					.addParam("orgUnitUuids", orgUnitsToFilterBy.keySet())
					.setPageSize(null); // TODO: fix pagination - talent pools are 1:n to rows in query result
			return getResultsWithOrgUnits(builder, pagination, sharedAndOwnedWithOrgUnitsExtractor(orgUnitsToFilterBy));
		}
		return getResults(builder, pagination, sharedAndOwnedMapper);
	}

	@Override
	public List<ManagedCompanyUserGroupRow> findSharedAndOwnedGroups(Long companyId) {
		String sql =
				"SELECT id, name, company_id, industry_id\n" +
				"FROM (\n" +
					"SELECT g.id, g.name, g.company_id, g.industry_id\n" +
					"FROM company_network_association cna\n" +
					"INNER JOIN user_group_network_association gna ON cna.network_id = gna.network_id\n" +
					"AND cna.company_id = :companyId\n" +
					"AND cna.deleted = false\n" +
					"AND cna.active = true\n" +
					"AND gna.deleted = false\n" +
					"AND gna.active = true\n" +
					"INNER JOIN user_group g ON gna.user_group_id = g.id \n" +
					"AND g.deleted = false\n" +
					"AND g.active_flag = true\n" +
					"WHERE g.company_id != :companyId\n" +
					"AND g.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'\n" +

					"UNION \n" +

					"SELECT g.id, g.name, g.company_id, g.industry_id\n" +
					"FROM user_group g\n" +
					"WHERE g.deleted = false \n" +
					"AND g.active_flag = true\n" +
					"AND g.company_id = :companyId\n" +
					"AND g.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'\n" +
					")res\n" +
				"ORDER BY name ASC";

		SqlParameterSource params = new MapSqlParameterSource().addValue("companyId", companyId);

		RowMapper<ManagedCompanyUserGroupRow> mapper = new RowMapper<ManagedCompanyUserGroupRow>() {
			@Override
			public ManagedCompanyUserGroupRow mapRow(ResultSet resultSet, int i) throws SQLException {
				ManagedCompanyUserGroupRow row = new ManagedCompanyUserGroupRow();
				row.setGroupId(resultSet.getLong("id"));
				row.setName(resultSet.getString("name"));
				row.setCompanyId(resultSet.getLong("company_id"));
				row.setIndustry(resultSet.getLong("industry_id"));
				return row;
			}
		};

		return jdbcTemplate.query(sql, params, mapper);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findMyGroupMemberships(Long companyId, Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		SQLBuilder builder = buildCompanyUserGroupSql(companyId, userId, pagination);

		builder
			.addColumns("ug.id", "ug.name", "ug.description", "ug.open_membership", "ug.active_flag", "c.id")
			.addJoin("INNER JOIN user_user_group_association association " +
					" ON ug.id = association.user_group_id " +
					" AND user_id = :userId " +
					" AND association.approval_status = :approvalStatus " +
					" AND association.deleted = FALSE ")
			.addParam("approvalStatus", ApprovalStatus.APPROVED.getCode())
			.addWhereClause("ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'");

		// unless includePrivateGroups is set to TRUE, only return public groups
		if (!pagination.isShowPrivateGroups()) {
			builder.addWhereClause("ug.open_membership = TRUE");
		}

		RowMapper<ManagedCompanyUserGroupRow> mapper = openMembershipMapper;
		if (pagination.isShowSharedGroups()) {
			builder
				.addColumn("EXISTS(SELECT a.id FROM user_group_network_association a WHERE a.user_group_id = ug.id AND a.deleted = false AND a.active = true) isShared")
				.addWhereClause(SHARED_GROUPS_WHERE_CLAUSE)
				.addParam("companyId", pagination.getCurrentUserCompanyId());
			mapper = openMembershipIsSharedMapper;
		}

		return getResults(builder, pagination, mapper);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findMyGroupMembershipsAndApplications(Long companyId, Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		SQLBuilder builder = buildCompanyUserGroupSql(companyId, userId, pagination);

		builder
			.addColumns(USER_ASSOCIATION_QUERY_COLUMNS)
			.addColumns(COMMON_COLUMNS)
			.addColumns(OWNER_COLUMN)
			.addColumns(MEMBER_COUNT)
			.addColumns(PENDING_APPLICANT_COUNT)
			.addColumns(INVITED_APPLICANT_COUNT)
			.addJoin("INNER JOIN user_user_group_association association ON ug.id = association.user_group_id")

			.addWhereClause("association.user_id = :userId")
			.addWhereClause("association.approval_status IN (0,1)")
			.addWhereClause("association.deleted = FALSE")
			.addWhereClause("ug.active_flag = TRUE")
			.addWhereClause("ug.open_membership = TRUE")
			.addWhereClause("ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'");

		return getResults(builder, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findVendorGroupMembershipsAndApplications(
		Long companyId,
		Long userId,
		Set<Long> groupIds,
		ManagedCompanyUserGroupRowPagination pagination
	) {
		SQLBuilder builder = buildCompanyUserGroupSql(companyId, userId, pagination);

		builder
			.addColumns(NULL_USER_ASSOCIATION_QUERY_COLUMNS)
			.addColumns(NULL_OWNER_COLUMN)
			.addColumns(COMMON_COLUMNS)
			.addColumns(MEMBER_COUNT)
			.addColumns(PENDING_APPLICANT_COUNT)
			.addColumns(INVITED_APPLICANT_COUNT)
			.addParam("groupIds", groupIds)

			.addWhereClause("ug.id IN (:groupIds)")
			.addWhereClause("ug.active_flag = TRUE")
			.addWhereClause("ug.open_membership = TRUE")
			.addWhereClause("ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'");

		return getResults(builder, pagination);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findCompanyGroupsActiveOpenMembershipByCompanyIdAndOrgUnits(
			long companyId,
			ManagedCompanyUserGroupRowPagination pagination,
			List<String> orgUnitsToFilterBy,
			boolean hasOrgStructureFeatureToggle) {

		final SQLBuilder builder = new SQLBuilder();

		builder
				.addColumns("ug.id, ug.name, ug.description, ug.open_membership, ug.active_flag", "c.id")
				.addTable("user_group ug")
				.addJoin("INNER JOIN company c ON c.id = ug.company_id")
				.addWhereClause("c.id = :companyId")
				.addWhereClause("ug.active_flag = TRUE")
				.addWhereClause("ug.deleted = FALSE")
				.addParam("companyId", companyId);

		if (hasOrgStructureFeatureToggle) {
			if (CollectionUtils.isEmpty(orgUnitsToFilterBy)) {
				return pagination;
			}
			builder
					.addJoin("INNER JOIN user_group_org_unit_association ugoua ON ug.uuid = ugoua.user_group_uuid")
					.addWhereClause("ugoua.org_unit_uuid IN (:orgUnitUuids)")
					.addParam("orgUnitUuids", orgUnitsToFilterBy);
		}

		builder.addOrderBy(ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME.getColumn(), ManagedCompanyUserGroupRowPagination.SORT_DIRECTION.ASC.toString());

		return getResults(builder, pagination, openMembershipMapper);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findCompanyGroupsOpenMembership(
			Long companyId,
			ManagedCompanyUserGroupRowPagination pagination) {

		final SQLBuilder builder = new SQLBuilder();

		builder
				.addColumns("ug.id, ug.name, ug.description, ug.open_membership, ug.active_flag", "c.id")
				.addTable("user_group ug")
				.addJoin("INNER JOIN company c ON c.id = ug.company_id")
				.addParam("companyId", companyId)
				.addWhereClause("c.id = :companyId")
				.addWhereClause("ug.deleted = FALSE");

		builder.addOrderBy(ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME.getColumn(), ManagedCompanyUserGroupRowPagination.SORT_DIRECTION.ASC.toString());

		return getResults(builder, pagination, openMembershipMapper);
	}

	@Override
	public ManagedCompanyUserGroupRowPagination findCompanyGroupsActiveOpenMembershipByGroupIds(
			Set<Long> groupIds,
			ManagedCompanyUserGroupRowPagination pagination) {

		final SQLBuilder builder = new SQLBuilder();

		builder
			.addColumns("ug.id, ug.name, ug.open_membership, c.name, c.id")
			.addTable("user_group ug")
			.addJoin("INNER JOIN company c ON c.id = ug.company_id")
			.addWhereClause("ug.id IN (:groupIds)")
			.addWhereClause("ug.deleted = FALSE")
			.addWhereClause("ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'")
				.addParam("groupIds", groupIds);

		builder.addOrderBy(ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME.getColumn(), ManagedCompanyUserGroupRowPagination.SORT_DIRECTION.ASC.toString());

		return getResults(builder, pagination, openMembershipCompanyNameMapper);
	}

	/**
	 * Builds some common sql
	 */
	private SQLBuilder buildCompanyUserGroupSql(Long companyId, Long userId, ManagedCompanyUserGroupRowPagination pagination) {
		SQLBuilder builder = new SQLBuilder();

		builder
			.addTable("user_group ug")
			.addJoin("INNER JOIN user u  ON u.id = ug.owner_id ")
			.addJoin("INNER JOIN company c ON c.id = ug.company_id")
			.addParam("companyId", companyId)
			.addParam("userId", userId)
			.addParam("isAdminOrManager", pagination.isCurrentUserAnAdmin())
			.addWhereClause("ug.deleted = FALSE")
			.addWhereClause("ug.name <> '" + Constants.MY_COMPANY_FOLLOWERS + "'");

		if (pagination.isShowOnlyActiveGroups()) {
			builder.addWhereClause("ug.active_flag = true");
		}

		if (pagination.getFilters() != null) {
			int i = 0;
			for (Map.Entry<String, String> entry : pagination.getFilters().entrySet()) {
				String filter = entry.getKey();
				builder
					.addWhereClause(ManagedCompanyUserGroupRowPagination.FILTER_KEYS.valueOf(filter).getColumn() + " =  :param" + i)
					.addParam("param" + i, entry.getValue());
				i++;
			}
		}

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(ManagedCompanyUserGroupRowPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addOrderBy(ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME.getColumn(), ManagedCompanyUserGroupRowPagination.SORT_DIRECTION.ASC.toString());
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		return builder;
	}

	private ManagedCompanyUserGroupRowPagination getResults(SQLBuilder builder, ManagedCompanyUserGroupRowPagination pagination) {
		return getResults(builder, pagination, mapper);
	}

	private ManagedCompanyUserGroupRowPagination getResults(SQLBuilder builder, ManagedCompanyUserGroupRowPagination pagination, RowMapper<ManagedCompanyUserGroupRow> mapper) {
		String query = builder.build();

		final List<ManagedCompanyUserGroupRow> rows = readOnlyJdbcTemplate.query(query, builder.getParams(), mapper);
		pagination.setResults(rows);
		pagination.setRowCount(rows.size());

		return pagination;
	}

	private ManagedCompanyUserGroupRowPagination getResultsWithOrgUnits(
			final SQLBuilder builder,
			final ManagedCompanyUserGroupRowPagination pagination,
			final ResultSetExtractor<List<ManagedCompanyUserGroupRow>> rse) {
		final String query = builder.build();

		final List<ManagedCompanyUserGroupRow> rows = readOnlyJdbcTemplate.query(query, builder.getParams(), rse);
		pagination.setResults(rows);
		pagination.setRowCount(rows.size());

		return pagination;
	}

	private final RowMapper<ManagedCompanyUserGroupRow> mapper = new RowMapper<ManagedCompanyUserGroupRow>() {
		@Override
		public ManagedCompanyUserGroupRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManagedCompanyUserGroupRow row = new ManagedCompanyUserGroupRow();

			row.setGroupId(rs.getLong("ug.id"));
			row.setName(rs.getString("name"));
			row.setDescription(rs.getString("description"));
			row.setDeleted(rs.getBoolean("ug.deleted"));
			row.setAutoGenerated(rs.getBoolean("ug.auto_generated"));
			row.setRequiresApproval(rs.getBoolean("ug.requires_approval"));
			row.setOpenMembership(rs.getBoolean("ug.open_membership"));
			row.setActiveFlag(rs.getBoolean("ug.active_flag"));
			row.setSearchable(rs.getBoolean("ug.searchable"));
			row.setCreatorId(rs.getLong("ug.creator_id"));
			row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getDate("ug.created_on")));
			row.setCompanyId(rs.getLong("companyId"));
			row.setCompanyName(rs.getString("companyName"));
			row.setDateApproved(DateUtilities.getCalendarFromDate(rs.getDate("date_approved")));
			row.setDateApplied(DateUtilities.getCalendarFromDate(rs.getDate("date_applied")));
			row.setDateInvited(DateUtilities.getCalendarFromDate(rs.getDate("date_invited")));
			row.setOwnerFullName(StringUtilities.fullName(rs.getString("u.first_name"), rs.getString("u.last_name")));
			row.setOwnerUserNumber(rs.getString("u.user_number"));
			row.setOwnerId(rs.getString("u.id"));
			row.setMember(rs.getBoolean("is_member"));
			row.setOwner(rs.getBoolean("is_owner"));
			row.setMemberCount(rs.getInt("memberCount"));
			row.setPendingApplicantCount(rs.getInt("pendingApplicantCount"));
			row.setInvitedApplicantCount(rs.getInt("invitedApplicantCount"));
			row.setIndustry(rs.getLong("industry_id"));
			row.setUuid(rs.getString("uuid"));

			return row;
		}
	};

	private final RowMapper<ManagedCompanyUserGroupRow> sharedAndOwnedMapper = new RowMapper<ManagedCompanyUserGroupRow>() {
		@Override
		public ManagedCompanyUserGroupRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManagedCompanyUserGroupRow row = new ManagedCompanyUserGroupRow();

			row.setGroupId(rs.getLong("ug.id"));
			row.setName(rs.getString("name"));
			row.setDescription(rs.getString("description"));
			row.setOpenMembership(rs.getBoolean("ug.open_membership"));
			row.setActiveFlag(rs.getBoolean("ug.active_flag"));
			row.setCompanyId(rs.getLong("companyId"));
			row.setCompanyName(rs.getString("companyName"));
			row.setOwnerFullName(StringUtilities.fullName(rs.getString("u.first_name"), rs.getString("u.last_name")));
			row.setOwnerUserNumber(rs.getString("u.user_number"));
			row.setOwnerId(rs.getString("u.id"));
			row.setMember(rs.getBoolean("is_member"));
			row.setOwner(rs.getBoolean("is_owner"));
			row.setMemberCount(rs.getInt("memberCount"));
			row.setAutoGenerated(rs.getBoolean("ug.auto_generated"));
			row.setPendingApplicantCount(rs.getInt("pendingApplicantCount"));
			row.setInvitedApplicantCount(rs.getInt("invitedApplicantCount"));
			row.setSearchable(rs.getBoolean("ug.searchable"));
			row.setIsShared(rs.getBoolean("isShared"));
			row.setIndustry(rs.getLong("industry_id"));
			row.setRequiresApproval(rs.getBoolean("ug.requires_approval"));
			row.setUuid(rs.getString("ug.uuid"));

			return row;
		}
	};

	private ResultSetExtractor<List<ManagedCompanyUserGroupRow>> sharedAndOwnedWithOrgUnitsExtractor(final Map<String, OrgUnitDTO> uuidToOrgUnitDTOMap) {
		return new ResultSetExtractor<List<ManagedCompanyUserGroupRow>>() {
			@Override
			public List<ManagedCompanyUserGroupRow> extractData(final ResultSet rs) throws SQLException, DataAccessException {
				final Map<Long, ManagedCompanyUserGroupRow> idToRowMap = new LinkedHashMap<>();
				while (rs.next()) {
					final Long groupId = rs.getLong("ug.id");
					final String orgUnitUuid = rs.getString("ugoa.org_unit_uuid");
					final OrgUnitDTO orgUnit = uuidToOrgUnitDTOMap.get(orgUnitUuid);
					if (idToRowMap.get(groupId) != null && StringUtils.isNotBlank(orgUnitUuid)) {
						if (orgUnit != null) {
							final List<OrgUnitDTO> orgUnits = addToSortedOrgUnitList(idToRowMap.get(groupId).getOrgUnits(), orgUnit);
							final ManagedCompanyUserGroupRow row = idToRowMap.get(groupId);
							row.setOrgUnits(orgUnits);
							idToRowMap.put(groupId, row);
						}
						continue;
					}
					final ManagedCompanyUserGroupRow row = new ManagedCompanyUserGroupRow();
					row.setGroupId(groupId);
					row.setName(rs.getString("name"));
					row.setDescription(rs.getString("description"));
					row.setOpenMembership(rs.getBoolean("ug.open_membership"));
					row.setActiveFlag(rs.getBoolean("ug.active_flag"));
					row.setCompanyId(rs.getLong("companyId"));
					row.setCompanyName(rs.getString("companyName"));
					row.setOwnerFullName(StringUtilities.fullName(rs.getString("u.first_name"), rs.getString("u.last_name")));
					row.setOwnerUserNumber(rs.getString("u.user_number"));
					row.setOwnerId(rs.getString("u.id"));
					row.setMember(rs.getBoolean("is_member"));
					row.setOwner(rs.getBoolean("is_owner"));
					row.setMemberCount(rs.getInt("memberCount"));
					row.setAutoGenerated(rs.getBoolean("ug.auto_generated"));
					row.setPendingApplicantCount(rs.getInt("pendingApplicantCount"));
					row.setInvitedApplicantCount(rs.getInt("invitedApplicantCount"));
					row.setSearchable(rs.getBoolean("ug.searchable"));
					row.setIsShared(rs.getBoolean("isShared"));
					row.setIndustry(rs.getLong("industry_id"));
					row.setRequiresApproval(rs.getBoolean("ug.requires_approval"));
					row.setUuid(rs.getString("ug.uuid"));
					final List<OrgUnitDTO> orgUnitList = new ArrayList<>();
					if (orgUnit != null) {
						orgUnitList.add(orgUnit);
					}
					row.setOrgUnits(orgUnitList);

					idToRowMap.put(groupId, row);
				}

				return ImmutableList.copyOf(idToRowMap.values());
			}
		};
	}

	private List<OrgUnitDTO> addToSortedOrgUnitList(
			final List<OrgUnitDTO> orgUnits,
			final OrgUnitDTO orgUnitToAdd) {
		final List<OrgUnitDTO> sorted = new ArrayList<>(orgUnits);
		sorted.add(orgUnitToAdd);
		Collections.sort(sorted, new Comparator<OrgUnitDTO>() {
			@Override
			public int compare(final OrgUnitDTO org1, final OrgUnitDTO org2) {
				return org1.getName().compareTo(org2.getName());
			}
		});
		return ImmutableList.copyOf(sorted);
	}


	private final RowMapper<ManagedCompanyUserGroupRow> openMembershipIsSharedMapper = new RowMapper<ManagedCompanyUserGroupRow>() {
		@Override
		public ManagedCompanyUserGroupRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManagedCompanyUserGroupRow row = new ManagedCompanyUserGroupRow();

			row.setGroupId(rs.getLong("ug.id"));
			row.setName(rs.getString("name"));
			row.setOpenMembership(rs.getBoolean("ug.open_membership"));
			row.setCompanyId(rs.getLong("c.id"));
			row.setIsShared(rs.getBoolean("isShared"));

			return row;
		}
	};

	private final RowMapper<ManagedCompanyUserGroupRow> openMembershipMapper = new RowMapper<ManagedCompanyUserGroupRow>() {
		@Override
		public ManagedCompanyUserGroupRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManagedCompanyUserGroupRow row = new ManagedCompanyUserGroupRow();

			row.setGroupId(rs.getLong("ug.id"));
			row.setName(rs.getString("name"));
			row.setDescription(rs.getString("description"));
			row.setOpenMembership(rs.getBoolean("ug.open_membership"));
			row.setActiveFlag(rs.getBoolean("ug.active_flag"));
			row.setCompanyId(rs.getLong("c.id"));

			return row;
		}
	};

	private final RowMapper<ManagedCompanyUserGroupRow> openMembershipCompanyNameMapper = new RowMapper<ManagedCompanyUserGroupRow>() {
		@Override
		public ManagedCompanyUserGroupRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManagedCompanyUserGroupRow row = new ManagedCompanyUserGroupRow();

			row.setGroupId(rs.getLong("ug.id"));
			row.setName(rs.getString("ug.name"));
			row.setOpenMembership(rs.getBoolean("ug.open_membership"));
			row.setCompanyId(rs.getLong("c.id"));
			row.setCompanyName(rs.getString("c.name"));
			return row;
		}
	};

}
