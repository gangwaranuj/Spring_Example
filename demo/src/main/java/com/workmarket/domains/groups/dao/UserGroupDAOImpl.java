package com.workmarket.domains.groups.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupHydrateData;
import com.workmarket.domains.groups.model.UserGroupLastRoutedDTO;
import com.workmarket.domains.groups.model.UserGroupLastRoutedRowMapper;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.groups.model.UserGroupThroughputRowMapper;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.id.IdGenerator;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Repository
public class UserGroupDAOImpl extends PaginationAbstractDAO<UserGroup> implements UserGroupDAO {

	private static final Logger logger = LoggerFactory.getLogger(UserGroupDAOImpl.class);

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Qualifier("readOnlyJdbcTemplate") @Autowired private NamedParameterJdbcTemplate readOnlyJdbcTemplate;
	@Autowired private IdGenerator idGenerator;

	@Override
	protected Class<UserGroup> getEntityClass() {
		return UserGroup.class;
	}

	@Override
	public List<Long> findGroupIdsByOwner(final Long ownerId) {
		Assert.notNull(ownerId);
		String sql = "select id from user_group where owner_id = :ownerId";
		return jdbcTemplate.queryForList(sql, ImmutableMap.of("ownerId", ownerId), Long.class);
	}

	@Override
	public Long findUserGroupIdByUuid(final String uuid) {
		Assert.notNull(uuid);
		String sql = "select id from user_group where uuid = :uuid";
		return jdbcTemplate.queryForObject(sql, ImmutableMap.of("uuid", uuid), Long.class);
	}

	@Override
	public String findUserGroupUuidById(final Long groupId) {
		Assert.notNull(groupId);
		String sql = "select uuid from user_group where id = :groupId";
		return jdbcTemplate.queryForObject(sql, ImmutableMap.of("groupId", groupId), String.class);
	}

	@Override
	public List<Long> getUserGroupIdsWithAgreement(final Long agreementId) {
		Assert.notNull(agreementId);
		SQLBuilder builder = groupIdsWithAgreement();
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("agreementId", agreementId), Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllUserGroupIds() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.property("id"))
			.add(Restrictions.eq("deleted", false));

		return criteria.list();
	}

	@Override
	public List<Long> findDueForValidationUserGroupIds() {
		String sql =
			"SELECT DISTINCT ugesr.user_group_id \n" +
				"FROM user_group_evaluation_scheduled_run ugesr \n" +
				"JOIN user_group on ugesr.user_group_id = user_group.id \n" +
				"JOIN scheduled_run on ugesr.scheduled_run_id = scheduled_run.id \n" +
				"WHERE scheduled_run.next_run <= current_date() \n" +
				"AND scheduled_run.completed_on is null \n" +
				"AND scheduled_run.deleted = 0 \n" +
				"AND user_group.active_flag = 1 \n" +
				"AND user_group.deleted = 0";

		return readOnlyJdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public UserPagination findAllUsersByVerificationStatusAndApprovalStatus(Long groupId, VerificationStatus verificationStatus,
																			ApprovalStatus approvalStatus, UserPagination pagination) {
		Assert.notNull(groupId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(User.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(User.class);
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("company", FetchMode.JOIN);
		criteria.setFetchMode("profile", FetchMode.JOIN);
		criteria.setFetchMode("profile.address", FetchMode.JOIN);

		criteria.createAlias("userGroupAssociations", "uga");
		count.createAlias("userGroupAssociations", "uga");

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("lastName"));
		}

		criteria.add(Restrictions.eq("uga.userGroup.id", groupId));
		if (verificationStatus != null)
			criteria.add(Restrictions.eq("uga.verificationStatus", verificationStatus));
		if (approvalStatus != null)
			criteria.add(Restrictions.eq("uga.approvalStatus", approvalStatus));

		count.add(Restrictions.eq("uga.userGroup.id", groupId));
		if (verificationStatus != null)
			count.add(Restrictions.eq("uga.verificationStatus", verificationStatus));
		if (approvalStatus != null)
			count.add(Restrictions.eq("uga.approvalStatus", approvalStatus));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public Integer countAllActiveGroupMembers(Long groupId) {
		SQLBuilder builder = activeGroupMemberSQL(groupId);
		return readOnlyJdbcTemplate.queryForObject(builder.buildCount("u.id"), builder.getParams(), Integer.class);
	}

	@Override
	public List<Long> getAllActiveGroupMemberIds(Long groupId) {
		SQLBuilder builder = activeGroupMemberSQL(groupId).addColumn("u.id");
		return readOnlyJdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	private SQLBuilder activeGroupMemberSQL(Long groupId) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("user_user_group_association ug")
			.addJoin("INNER JOIN user u ON ug.user_id = u.id")
			.addWhereClause("ug.user_group_id = :groupId")
			.addWhereClause("ug.approval_status = :approvalStatus")
			.addWhereClause("ug.deleted = 0")
			.addWhereClause("u.user_status_type_code = :status")
			.addParam("groupId", groupId)
			.addParam("status", UserStatusType.APPROVED)
			.addParam("approvalStatus", ApprovalStatus.APPROVED.ordinal());
		return builder;
	}

	private SQLBuilder groupIdsWithAgreement() {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("user_group ug")
			.addColumn("distinct(ug.id)")
			.addJoin("LEFT JOIN user_group_requirement_set_association ugrs on ugrs.user_group_id = ug.id")
			.addJoin("LEFT JOIN requirement_set rs on rs.id = ugrs.requirement_set_id")
			.addJoin("LEFT JOIN requirement r on r.requirement_set_id = rs.id")
			.addJoin("LEFT JOIN agreement_requirement ar on ar.id = r.id")
			.addWhereClause("ug.deleted = 0")
			.addWhereClause("ar.agreement_id = :agreementId");
		return builder;
	}

	@Override
	public Integer countAllActiveGroupMembersByCompanyId(Long companyId) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("user_user_group_association ug")
			.addJoin("INNER JOIN user_group g ON ug.user_group_id = g.id")
			.addWhereClause("ug.approval_status = :approvalStatus")
			.addWhereClause("ug.deleted = 0")
			.addWhereClause("g.active_flag = 1")
			.addWhereClause("g.deleted = 0")
			.addWhereClause("g.company_id = :companyId")
			.addParam("companyId", companyId)
			.addParam("approvalStatus", ApprovalStatus.APPROVED.ordinal());

		return readOnlyJdbcTemplate.queryForObject(builder.buildCount("ug.id"), builder.getParams(), Integer.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SuggestionDTO> suggest(String prefix, String property, Long companyId) {
		return getFactory()
			.getCurrentSession()
			.createCriteria(UserGroup.class)
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("activeFlag", true))
			.add(Restrictions.eq("deleted", false))
			.add(Restrictions.ne("name", Constants.MY_COMPANY_FOLLOWERS))
			.add(Restrictions.ilike(property, prefix, MatchMode.START))
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
			.setMaxResults(10)
			.setProjection(
					Projections.projectionList().add(Projections.property("id"), "id").add(Projections.property(property), "value"))
			.setResultTransformer(Transformers.aliasToBean(SuggestionDTO.class)).list();
	}

	@Override
	public int updateGroupOwner(final Long newOwnerId, final List<Long> groupIds) {
		Assert.notNull(newOwnerId);

		if (CollectionUtils.isEmpty(groupIds)) return 0;

		String sql = "update user_group set owner_id = :newOwnerId where id in (:groupIds)";

		return jdbcTemplate.update(sql, ImmutableMap.of("newOwnerId", newOwnerId, "groupIds", groupIds));
	}

	@Override
	public int deactivateGroupIds(List<Long> groupIds) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return 0;
		}

		String sql =
			"UPDATE user_group \n" +
				"SET active_flag = 0 \n" +
				"WHERE user_group.id in (:groupIds) ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("groupIds", groupIds);

		return jdbcTemplate.update(sql, params);
	}

	@Override
	public UserGroup findUserGroupById(Long userGroupId) {
		Assert.notNull(userGroupId);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("company", FetchMode.JOIN)
			.setFetchMode("owner", FetchMode.JOIN)
			.add(Restrictions.eq("id", userGroupId));
		return (UserGroup) criteria.uniqueResult();
	}

	@Override
	public List<UserGroup> findUserGroupsByIds(List<Long> userGroupIds) {
		if (CollectionUtils.isEmpty(userGroupIds)) {
			return Collections.emptyList();
		}

		return (List<UserGroup>) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("company", FetchMode.JOIN)
			.setFetchMode("owner", FetchMode.JOIN)
			.add(Restrictions.in("id", userGroupIds))
			.list();
	}


	@Override
	public UserGroup findUserGroupByName(Long companyId, String name) {
		Assert.notNull(companyId);
		Assert.hasText(name);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());

		criteria.add(Restrictions.eq("name", name))
			.add(Restrictions.eq("company.id", companyId));

		return (UserGroup) criteria.uniqueResult();

	}

	@Override
	public UserGroup findGroupByIdNoAssociations(Long groupId) {
		return (UserGroup) getFactory().getCurrentSession().get(getEntityClass(), groupId);
	}

	@Override
	public UserGroupPagination findAllUserGroupsByCompanyId(final Long companyId, UserGroupPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);

		return (UserGroupPagination) super.paginationQuery(pagination, ImmutableMap.<String, Object>of("company.id", companyId, "deleted", false));
	}

	@SuppressWarnings("unchecked")
	public List<UserGroup> findAllUserGroupsByCompanyIdAndUserIsMember(Long companyId, Long userId) {
		return (List<UserGroup>) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("userAssociations", FetchMode.JOIN)
			.createAlias("userAssociations", "associations")
			.setFetchMode("associations.user", FetchMode.JOIN)
			.createAlias("associations.user", "u")
			.add(Restrictions.eq("u.id", userId))
			.add(Restrictions.eq("company.id", companyId)).list();
	}

	@Override
	public List<UserUserGroupAssociation> findAllGroupAssociationByCompanyIdAndUser(Long companyId, Long userId) {
		return (List<UserUserGroupAssociation>) getFactory().getCurrentSession().createCriteria(UserUserGroupAssociation.class)
			.setFetchMode("userGroup", FetchMode.JOIN)
			.createAlias("userGroup", "userGroup")
			.setFetchMode("user", FetchMode.JOIN)
			.createAlias("user", "user")
			.add(Restrictions.eq("user.id", userId))
			.add(Restrictions.eq("userGroup.company.id", companyId)).list();
	}

	@SuppressWarnings("unchecked")
	public List<UserGroup> findAllUserGroupsByUserIsMember(Long userId) {
		return (List<UserGroup>) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("userAssociations", FetchMode.JOIN)
			.createAlias("userAssociations", "associations")
			.setFetchMode("associations.user", FetchMode.JOIN)
			.createAlias("associations.user", "u")
			.add(Restrictions.eq("u.id", userId)).list();
	}

	@Override
	public List<UserGroupLastRoutedDTO> findAllWithNewLastRoutedSinceLastUpdate() {
		SQLBuilder sqlBuilder = new SQLBuilder();

		// Returns all user groups with a newly routed work since the last user group
		// summary update
		String[] selectFields = new String[]{
			"grsa.`user_group_id`",
			"MAX(rs.`routed_on`) AS `routed_on`"
		};
		sqlBuilder.addColumns(selectFields)
			.addTable("routing_strategy rs")
			.addJoin("INNER JOIN group_routing_strategy_association grsa on grsa.`routing_strategy_id` = rs.`id`")
			.addJoin("INNER JOIN user_group_summary ugs on ugs.`user_group_id` = grsa.`user_group_id`")
			.addWhereClause("rs.`routed_on` > ugs.`last_routed_on`")
			.addGroupColumns("grsa.`user_group_id`");

		return readOnlyJdbcTemplate.query(sqlBuilder.build(), new MapSqlParameterSource(), new UserGroupLastRoutedRowMapper());
	}

	@Override
	public List<UserGroupThroughputDTO> calculateThroughputSinceLastUpdate() {
		SQLBuilder sqlBuilderOuter = new SQLBuilder();
		SQLBuilder sqlBuilderInner = new SQLBuilder();

		// Returns the sum of all work price for all work that was successfully routed to a user group since the
		// last time the user_group_summary table was updated.
		// NOTE: We aren't joining to the 'work_resource' table as we only care that the work was completed and paid,
		// not by whom.
		String[] selectInnerFields = new String[] {
			"whs.work_id",
			"grsa.user_group_id",
			"COALESCE(last_update_on, '2010-01-01') as lastUpdateOn",
			"ugs.total_throughput"
		};
		sqlBuilderInner.addColumns(selectInnerFields)
			.addTable("work_status_transition whs")
			.addJoin("INNER JOIN time_dimension td ON td.id = whs.date_id")
			.addJoin("INNER JOIN routing_strategy rs ON rs.work_id = whs.work_id")
			.addJoin("INNER JOIN group_routing_strategy_association grsa ON grsa.routing_strategy_id = rs.id")
			.addJoin("LEFT  JOIN user_group_summary ugs ON ugs.user_group_id = grsa.user_group_id")
			.addWhereClause("whs.work_status_type_code = 'paid'")
			.addWhereClause("rs.delivery_status_type_code = 'sent'")
			.addWhereClause("td.date >= COALESCE(last_update_on, '2010-01-01')");

		String[] selectOuterFields = new String[] {
			"COALESCE(SUM(whs.work_price),0) + COALESCE(total_throughput,0) AS newThroughput",
			"distinctData.user_group_id AS userGroupId",
			"distinctData.lastUpdateOn AS fromDate"
		};
		sqlBuilderOuter.addColumns(selectOuterFields)
			.addTable("work_history_summary whs")
			.addJoin("INNER JOIN (" + sqlBuilderInner.build() + ") distinctData ON distinctData.work_id = whs.work_id")
			.addWhereClause("whs.work_status_type_code = 'paid'")
			.addGroupColumns("user_group_id");

		return readOnlyJdbcTemplate.query(sqlBuilderOuter.build(), new MapSqlParameterSource(), new UserGroupThroughputRowMapper());
	}

	public void applySorts(Pagination<UserGroup> pagination, Criteria query, Criteria count) {
		String sort = "name";

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			query.addOrder(Order.desc(sort));
		} else {
			query.addOrder(Order.asc(sort));
		}
	}

	public void applyFilters(Pagination<UserGroup> pagination, Criteria criteria, Criteria count) {
		if (pagination.getFilters() == null) return;

		if (pagination.hasFilter(UserGroupPagination.FILTER_KEYS.IS_ACTIVE)) {
			Boolean isActive = Boolean.parseBoolean(pagination.getFilter(UserGroupPagination.FILTER_KEYS.IS_ACTIVE));
			criteria.add(Restrictions.eq("activeFlag", isActive));
		}
	}

	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
		query.setFetchMode("company", FetchMode.JOIN);
		for (Map.Entry<String, Object> e : params.entrySet()) {
			query.add(Restrictions.eq(e.getKey(), e.getValue()));
			count.add(Restrictions.eq(e.getKey(), e.getValue()));
		}

		query.add(Restrictions.ne("name", Constants.MY_COMPANY_FOLLOWERS));
		count.add(Restrictions.ne("name", Constants.MY_COMPANY_FOLLOWERS));
	}

	@Override
	public Integer countCompanyUserGroups(Long companyId) {

		return ((Long) getFactory().getCurrentSession()
			.createQuery("select count(g) from userGroup g where g.company.id = :companyId and g.deleted = false and g.name <> :myFollowers")
			.setParameter("companyId", companyId)
			.setParameter("myFollowers", Constants.MY_COMPANY_FOLLOWERS)
			.uniqueResult()).intValue();
	}

	@Override
	public Set<User> findUsersFromCompanyUserGroups(List<UserGroup> listCompanyUserGroups) {
		Set<User> allUsers = new HashSet<>();
		for (UserGroup comp : listCompanyUserGroups) {
			allUsers.addAll(comp.getCompany().getUsers());
		}
		return allUsers;

	}

	@Override
	@SuppressWarnings("unchecked")
	public Pagination<UserGroup> paginationQuery(Pagination<UserGroup> pagination, Map<String, Object> params) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit());

		DetachedCriteria subcriteria = DetachedCriteria.forClass(getEntityClass())
			.setProjection(Projections.distinct(Projections.property("id")));

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.rowCount());

		buildWhereClause((Criteria) subcriteria, count, params);
		applySorts(pagination, (Criteria) subcriteria, count);
		applyFilters(pagination, (Criteria) subcriteria, count);

		criteria.add(Subqueries.propertyIn("id", subcriteria));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;

	}

	@Override
	public Map<Long, UserGroupHydrateData> findAllCompanyUserGroupHydrateData() {
		Query q = getFactory().getCurrentSession().createQuery(
			"select g.id, g.name, g.company.id, g.openMembership from userGroup g where g.deleted = false and g.name <> '" +
					Constants.MY_COMPANY_FOLLOWERS + "'");
		@SuppressWarnings("unchecked")
		List<Object> results = q.list();
		Map<Long, UserGroupHydrateData> returnVal = newHashMapWithExpectedSize(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			UserGroupHydrateData hydrateData = new UserGroupHydrateData();
			hydrateData.setGroupId((Long) row[0]);
			hydrateData.setGroupName((String) row[1]);
			hydrateData.setCompanyId((Long) row[2]);
			hydrateData.setOpenMembership((Boolean) row[3]);
			returnVal.put((Long) row[0], hydrateData);
		}
		return returnVal;
	}

	@Override
	public Map<Long, UserGroupHydrateData> findAllCompanyUserGroupHydrateDataByGroupIds(Collection<Long> groupIds) {
		if (groupIds == null || groupIds.size() == 0) {
			return emptyMap();
		}
		Query q = getFactory().getCurrentSession().createQuery(
			"select g.id, g.name, g.company.id, g.openMembership from userGroup g where g.deleted = false and g.id in (:groupIds)");
		q.setParameterList("groupIds", groupIds);
		@SuppressWarnings("unchecked")
		List<Object> results = q.list();
		Map<Long, UserGroupHydrateData> returnVal = newHashMapWithExpectedSize(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			UserGroupHydrateData hydrateData = new UserGroupHydrateData();
			hydrateData.setGroupId((Long) row[0]);
			hydrateData.setGroupName((String) row[1]);
			hydrateData.setCompanyId((Long) row[2]);
			hydrateData.setOpenMembership((Boolean) row[3]);
			returnVal.put((Long) row[0], hydrateData);
		}
		return returnVal;
	}

	@Override
	public List<Long> getGroupIdsNeverRoutedToOlderThan(Date date) {
		if (date == null) {
			return Collections.emptyList();
		}

		String sql =
			"SELECT user_group.id \n" +
				"FROM user_group \n" +
				"LEFT JOIN group_routing_strategy_association association on user_group.id = association.user_group_id \n" +
				"WHERE user_group.modified_on < :modifiedDate \n" +
				"AND user_group.active_flag = 1 \n" +
				"AND user_group.auto_generated = 0 \n" +
				"AND user_group.open_membership = 1 \n" +
				"AND user_group.searchable = 1 \n" +
				"AND user_group.deleted = 0 \n" +
				"AND association.modified_on is null ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("modifiedDate", date);

		return jdbcTemplate.queryForList(sql, params, Long.class);
	}

	@Override
	public List<Long> getGroupIdsNotRoutedToSince(Date notRoutedToSince) {
		if (notRoutedToSince == null) {
			return Collections.emptyList();
		}

		String sql =
			"SELECT user_group_id \n" +
				"FROM group_routing_strategy_association association \n" +
				"JOIN user_group on association.user_group_id = user_group.id \n" +
				"WHERE user_group.modified_on < :modifiedDate \n" +
				"AND user_group.active_flag = 1 \n" +
				"AND user_group.auto_generated = 0 \n" +
				"AND user_group.open_membership = 1 \n" +
				"AND user_group.searchable = 1 \n" +
				"AND user_group.deleted = 0 \n" +
				"GROUP BY user_group_id \n" +
				"HAVING max(association.modified_on) < :notToRoutedSince ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("modifiedDate", notRoutedToSince);
		params.addValue("notToRoutedSince", notRoutedToSince);

		return jdbcTemplate.queryForList(sql, params, Long.class);
	}

	@Override
	public List<Long> getGroupIdsInvitedToSince(final Date date) {
		if (date == null) {
			return Collections.emptyList();
		}

		final String sql =
			"SELECT user_group_id \n" +
				"FROM user_user_group_association association \n" +
				"JOIN user_group ON association.user_group_id = user_group.id \n" +
				"WHERE user_group.active_flag = 1 \n" +
				"AND user_group.open_membership = 1 \n" +
				"AND user_group.searchable = 1 \n" +
				"AND user_group.deleted = 0 \n" +
				"GROUP BY user_group_id \n" +
				"HAVING max(association.date_invited) > :invitedDate ";

		final MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("invitedDate", date);

		return jdbcTemplate.queryForList(sql, params, Long.class);
	}

	@Override
	public boolean userCanViewSharedGroup(Long userGroupId, Long userId) {
		SQLBuilder builder = new SQLBuilder();

		builder
			.addColumn("cna.company_id")
			.addTable("company_network_association cna")
			.addJoin("INNER JOIN user u ON u.company_id = cna.company_id AND cna.active = TRUE AND cna.deleted = FALSE")
			.addJoin("INNER JOIN network n ON n.id = cna.network_id")
			.addJoin("INNER JOIN user_group_network_association ugna ON ugna.network_id = n.id AND ugna.active = TRUE AND ugna.deleted = FALSE")
			.addWhereClause("ugna.user_group_id = :userGroupId")
			.addWhereClause("u.id = :userId")
			.addParam("userGroupId", userGroupId)
			.addParam("userId", userId);

		List<Long> companyIds = jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);

		return CollectionUtils.isNotEmpty(companyIds);
	}

	@Override
	public boolean isGroupShared(Long groupId) {
		SQLBuilder builder = new SQLBuilder();
		builder
			.addColumn("count(*)")
			.addTable("user_group_network_association ugna")
			.addWhereClause("ugna.deleted = FALSE")
			.addWhereClause("ugna.active = TRUE")
			.addWhereClause("ugna.user_group_id = :groupId")
			.addParam("groupId", groupId);

		Long count = jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Long.class);

		return count != null && count > 0;
	}

	@Override
	public Integer getMaxGroupId() {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("MAX(user_group.id) as groupId")
			.addTable("user_group");
		try {
			return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Long.class).intValue();
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public List<Long> findGroupIdsBetween(long fromId, long toId) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("id")
			.addTable("user_group")
			.addWhereClause("user_group.id BETWEEN :fromId AND :toId")
			.addParam("fromId", fromId)
			.addParam("toId", toId);
		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public int countGroupsCreatedSince(long companyId, Calendar fromDate) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("COUNT(id) count")
			.addTable("user_group")
			.addWhereClause("user_group.company_id = :companyId")
			.addWhereClause("user_group.created_on >= :fromDate ")
			.addParam("companyId", companyId)
			.addParam("fromDate", fromDate);
		return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Integer.class);
	}

	@Override
	public List<Long> findUserGroupsRequiredInsuranceIds(long groupId) {
		SQLBuilder builder = findRequirementIdsBuilder()
			.addColumn("distinct(ir.insurance_id)")
			.addJoin("JOIN insurance_requirement ir on ir.id = r.id");
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("groupId", groupId), Long.class);
	}

	@Override
	public List<Long> findUserGroupsRequiredCertificationIds(long groupId) {
		SQLBuilder builder = findRequirementIdsBuilder()
			.addColumn("distinct(cr.certification_id)")
			.addJoin("JOIN certification_requirement cr on cr.id = r.id");
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("groupId", groupId), Long.class);
	}

	@Override
	public List<Long> findUserGroupsRequiredLicenseIds(long groupId) {
		SQLBuilder builder = findRequirementIdsBuilder()
			.addColumn("distinct(lr.license_id)")
			.addJoin("JOIN license_requirement lr on lr.id = r.id");
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("groupId", groupId), Long.class);
	}

	@Override
	public List<Long> findUserGroupsRequiredAgreementIds(long groupId) {
		SQLBuilder builder = findRequirementIdsBuilder()
			.addColumn("distinct(ar.agreement_id)")
			.addJoin("JOIN agreement_requirement ar on ar.id = r.id");
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("groupId", groupId), Long.class);
	}

	@Override
	public List<Long> findUserGroupsRequiredIndustryIds(long groupId) {
		 SQLBuilder builder = findRequirementIdsBuilder()
			 .addColumn("distinct(ir.industry_id)")
			 .addJoin("JOIN industry_requirement ir on ir.id = r.id");
		return jdbcTemplate.queryForList(builder.build(), ImmutableMap.of("groupId", groupId), Long.class);
	}

	@Override
	public Map<String, Long> findUserGroupUuidIdPairsByUuids(final Collection<String> uuids) {
		final Map<String, Long> uuidIdPairs = Maps.newHashMap();

		if (isEmpty(uuids)) {
			return uuidIdPairs;
		}

		final SQLBuilder builder = new SQLBuilder()
			.addColumns("id", "uuid")
			.addTable("user_group")
			.addWhereClause("uuid IN (:uuids)");

		try {
			List<Map<String, Object>> rows =
				readOnlyJdbcTemplate.queryForList(builder.build(), ImmutableMap.of("uuids", uuids));
			for (Map<String, Object> row : rows) {
				final String uuid = (String) row.get("uuid");
				final Long id = ((Integer) row.get("id")).longValue();
				uuidIdPairs.put(uuid, id);
			}
		} catch (Exception e) {
			logger.error("failed to fetch uuids and ids: ", e);
		}
		return uuidIdPairs;
	}

	@Override
	public void saveOrUpdate(UserGroup userGroup) {
		setUuidIfEmpty(userGroup);
		super.saveOrUpdate(userGroup);
	}

	@Override
	public void persist(UserGroup userGroup) {
		setUuidIfEmpty(userGroup);
		super.persist(userGroup);
	}

	@Override
	public void saveAll(Collection<UserGroup> userGroups) {
		for (final UserGroup userGroup : userGroups) {
			setUuidIfEmpty(userGroup);
		}
		super.saveAll(userGroups);
	}

	private void setUuidIfEmpty(UserGroup userGroup) {
		if (userGroup.getUuid() == null) {
			userGroup.setUuid(idGenerator.next().toBlocking().singleOrDefault(UUID.randomUUID().toString()));
		}
	}

	private SQLBuilder findRequirementIdsBuilder() {
		return new SQLBuilder()
			.addTable("user_group ug")
			.addJoin("LEFT JOIN user_group_requirement_set_association ugrs on ugrs.user_group_id = ug.id")
			.addJoin("LEFT JOIN requirement_set rs on rs.id = ugrs.requirement_set_id")
			.addJoin("LEFT JOIN requirement r on r.requirement_set_id = rs.id")
			.addWhereClause("ug.deleted = false")
			.addWhereClause("ug.active_flag = true")
			.addWhereClause("ug.id = :groupId");
	}

	@Override
	public Integer countUserGroupMemberships(Long userId) {

		SQLBuilder builder = new SQLBuilder();
		builder.addTable("user_user_group_association ug")
			.addJoin("INNER JOIN user_group g ON ug.user_group_id = g.id")
			.addWhereClause("ug.approval_status IN (0, 1)")
			.addWhereClause("ug.deleted = FALSE")
			.addWhereClause("g.active_flag = TRUE")
			.addWhereClause("g.open_membership = TRUE")
			.addWhereClause("g.deleted = FALSE")
			.addWhereClause("ug.user_id = :userId")
			.addParam("userId", userId);
		return readOnlyJdbcTemplate.queryForObject(builder.buildCount("ug.id"), builder.getParams(), Integer.class);
	}
}
