package com.workmarket.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.UserProfileModification;
import com.workmarket.domains.model.UserProfileModificationStatus;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.user.CompanyUser;
import com.workmarket.domains.model.user.CompanyUserPagination;
import com.workmarket.domains.model.user.CompanyUserStats;
import com.workmarket.domains.model.user.RecentUser;
import com.workmarket.domains.model.user.RecentUserPagination;
import com.workmarket.dto.CompanyResource;
import com.workmarket.dto.CompanyResourcePagination;
import com.workmarket.dto.RecruitingCampaignUser;
import com.workmarket.dto.RecruitingCampaignUserPagination;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.search.model.SearchUser;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserIdentityDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import static com.workmarket.utility.ConcatenableIlikeCriterion.ilike;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Repository
public class UserDAOImpl extends PaginationAbstractDAO<User> implements UserDAO {

	private static final Log logger = LogFactory.getLog(UserDAOImpl.class);
	private static final int LANE_1_KEY = 1;
	private static final int LANE_2_KEY = 2;
	private static final int LANE_3_KEY = 3;

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	private static class CompanyResourceMapper implements RowMapper<CompanyResource> {

		@Override
		public CompanyResource mapRow(ResultSet rs, int rowNum) throws SQLException {
			CompanyResource resource = new CompanyResource();

			resource.setId(rs.getLong("id"));
			resource.setUserNumber(rs.getString("u.user_number"));
			resource.setFirstName(rs.getString("first_name"));
			resource.setLastName(rs.getString("last_name"));
			resource.setLaneType(rs.getInt("laneType"));
			/* employee */
			resource.setRolesString(rs.getString("roles"));
			resource.setLastLogin(DateUtilities.getCalendarFromDate(rs.getTimestamp("lastLogin")));
			/* contractor */
			resource.setCompanyId(rs.getLong("companyId"));
			resource.setCompanyName(rs.getString("companyName"));
			resource.setYTDWork(rs.getInt("work"));
			resource.setYTDPayments(rs.getDouble("payments"));

			return resource;
		}
	}

	final RowMapper<UserDTO> USER_DTO_ROW_MAPPER = new RowMapper<UserDTO>() {
		public UserDTO mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final UserDTO userDTO = new UserDTO();
			userDTO.setId(rs.getLong("id"));
			userDTO.setCompanyId(rs.getLong("company_id"));
			userDTO.setFirstName(rs.getString("first_name"));
			userDTO.setLastName(rs.getString("last_name"));
			userDTO.setUuid(rs.getString("uuid"));
			return userDTO;
		}
	};

	@Override
	public void saveOrUpdate(final User entity) {
		setUuidIfEmpty(entity);
		super.saveOrUpdate(entity);
	}

	@Override
	public User findDeletedUsersByEmail(final String email) {
		final Query query = getFactory().getCurrentSession().getNamedQuery("user.findDeletedUserByEmail");
		query.setString("email", email);
		return (User) query.uniqueResult();
	}

	private void setUuidIfEmpty(final User entity) {
		if (entity.getUuid() == null) {
			entity.setUuid(UUID.randomUUID().toString());
		}
	}

	@Override
	public void persist(final User t) {
		setUuidIfEmpty(t);
		super.persist(t);
	}

	@Override
	public void saveAll(final Collection<User> entities) {
		for (final User user : entities) {
			setUuidIfEmpty(user);
		}
		super.saveAll(entities);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<User> findAllByUserIds(Collection<Long> userIds) {
		if (CollectionUtilities.isEmpty(userIds)) {
			return Collections.emptyList();
		}
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("id", userIds));

		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<User> findAllWithProfileAndCompanyByUserIds(Collection<Long> userIds) {
		if (CollectionUtilities.isEmpty(userIds)) {
			return Collections.emptyList();
		}
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("profile", FetchMode.JOIN)
				.setFetchMode("company", FetchMode.JOIN)
				.add(Restrictions.in("id", userIds));

		return criteria.list();
	}

   @Override
    @SuppressWarnings("unchecked")
    public Collection<User> findAllWithCompanyByUserIds(Collection<Long> userIds) {
        if (CollectionUtilities.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
                .setFetchMode("company", FetchMode.JOIN)
                .add(Restrictions.in("id", userIds));

        return criteria.list();
    }

	@Override
	@SuppressWarnings("unchecked")
	public Set<User> findAllByUserNumbers(Collection<String> userNumbers) {
		if (CollectionUtilities.isEmpty(userNumbers)) {
			return Sets.newLinkedHashSet();
		}
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("profile", FetchMode.JOIN)
				.add(Restrictions.in("userNumber", userNumbers));

		return Sets.<User>newLinkedHashSet(criteria.list());
	}

	@Override
	public User findUserById(Long id) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("company", FetchMode.JOIN)
				.setFetchMode("userRoleAssociations", FetchMode.JOIN)
				.setFetchMode("roles", FetchMode.JOIN)
				.add(Restrictions.eq("id", id));

		return (User) criteria.uniqueResult();
	}

	@Override
	public Long findUserId(String userNumber) {
		Query query = getFactory().getCurrentSession().createQuery("Select id from user where userNumber = :userNumber");
		query.setParameter("userNumber", userNumber);

		return (Long) query.uniqueResult();
	}

	@Override
	public String findUserUuidById(final Long id) {
		final Query query = getFactory().getCurrentSession().createQuery("Select uuid from user where id = :id");
		query.setParameter("id", id);

		return (String) query.uniqueResult();
	}

	@Override
	public Long findUserIdByUuid(String userUuid) {
		Query query = getFactory().getCurrentSession().createQuery("Select id from user where uuid = :userUuid");
		query.setParameter("userUuid", userUuid);

		return (Long) query.uniqueResult();
	}

	@Override
	public Long findUserIdByEmail(String email) {
		return (Long) getFactory().getCurrentSession().createQuery("select id from user where email = :email")
				.setParameter("email", email)
				.uniqueResult();
	}

	@Override
	public User findUserByEmail(String email) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(User.class)
				.setFetchMode("company", FetchMode.JOIN)
				.add(Restrictions.eq("email", email));

		return (User) criteria.uniqueResult();
	}

	@Override
	public User findCreatorByAssetId(Long assetId) {
		return findUserByItemId(assetId, "asset", "creator_id");
	}

	@Override
	public User findModifierByAssetId(Long assetId) {
		return findUserByItemId(assetId, "asset", "modifier_id");
	}

	@Override
	public User findCreatorByWorkLabelAssociationId(Long workLabelId) {
		return findUserByItemId(workLabelId, "work_sub_status_type_association", "creator_id");
	}

	@Override
	public User findModifierByWorkLabelAssociationId(Long workLabelId) {
		return findUserByItemId(workLabelId, "work_sub_status_type_association", "modifier_id");
	}

	@Override
	public User findCreatorByWorkNegotiationId(Long workNegotiationId) {
		return findUserByItemId(workNegotiationId, "work_negotiation", "creator_id");
	}

	private User findUserByItemId(Long id, String itemTableName, String userIdType) {
		SQLBuilder builder = new SQLBuilder();
		builder
				.addColumn("u.*")
				.addTable("user u")
				.addJoin(String.format("INNER JOIN %s item ON u.id = item.%s", itemTableName, userIdType))
				.addWhereClause("item.id = :id")
				.addParam("id", id);

		return (User) jdbcTemplate.queryForObject(builder.build(), builder.getParams(), new BasicUserRowMapper());
	}


	@Override
	public String findUserNumber(Long id) {
		return (String) getFactory().getCurrentSession().createQuery("select userNumber from user where id = :id").setParameter("id", id)
				.uniqueResult();
	}

	@Override
	public User findUserByUserNumber(String userNumber, boolean initialize) {
		return findUserByUserNumber(userNumber, initialize, false);
	}

	@Override
	public User findUserByUserNumber(String userNumber, boolean initialize, boolean nullable) {

		if (nullable && StringUtils.isEmpty(userNumber)) {
			return null;
		}
		Assert.hasText(userNumber);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("userNumber", userNumber)).setMaxResults(1);

		if (initialize) {
			criteria
				.setFetchMode("avatarOriginal", FetchMode.JOIN).setFetchMode("avatarSmall", FetchMode.JOIN)
				.setFetchMode("avatarLarge", FetchMode.JOIN).setFetchMode("avatarOriginalOldValue", FetchMode.JOIN)
				.setFetchMode("avatarSmallOldValue", FetchMode.JOIN).setFetchMode("avatarLargeOldValue", FetchMode.JOIN);
		}

		User user = (User) criteria.uniqueResult();
		if (user != null && initialize) {
			Hibernate.initialize(user.getUserRoleAssociations());
			Hibernate.initialize(user.getRoles()); // deprecated but ok for now
		}

		return user;
	}

	@Override
	public Set<Long> findAllUserIdsByUserNumbers(Collection<String> userNumbers) {
		return convertIdentifiers(userNumbers, "userNumber", "id");
	}

	@Override
	public Set<String> findAllUserNumbersByUserIds(Collection<Long> userIds) {
		return convertIdentifiers(userIds, "id", "userNumber");
	}

	private <K,V> Set<K> convertIdentifiers(Collection<V> values, String fromField, String toField) {
		Assert.notEmpty(values);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in(fromField, values)).setProjection(Projections.property(toField));

		@SuppressWarnings("unchecked")
		List<K> users = criteria.list();
		logger.debug(String.format("%d users found in findAllUserNumbersByUserIds", users.size()));
		if (CollectionUtilities.isEmpty(users)) {
			return Collections.emptySet();
		}

		return Sets.newHashSet(users);
	}

	@Override
	public Map<String, String> findAllUserNamesByUserNumbers(Collection<String> userIds) {
		Assert.notEmpty(userIds);

		Criteria criteria = getFactory()
				.getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.in("userNumber", userIds))
				.setProjection(
						Projections.projectionList().add(Projections.property("userNumber")).add(Projections.property("firstName"))
								.add(Projections.property("lastName")));

		@SuppressWarnings("unchecked")
		List<Object[]> users = criteria.list();
		if (CollectionUtilities.isEmpty(users)) {
			return Maps.newHashMap();
		}
		Map<String, String> returnVal = Maps.newHashMapWithExpectedSize(userIds.size());
		for (Object[] userData : users) {
			String userNumber = (String) userData[0];
			String firstName = (String) userData[1];
			String lastName = (String) userData[2];
			returnVal.put(userNumber, firstName + " " + lastName);
		}

		return returnVal;
	}

	@Override
	@SuppressWarnings("JpaQueryApiInspection")
	public User findUser(String email) {

		Query query = getFactory().getCurrentSession().getNamedQuery("user.findbyemail");
		query.setString("email", email);

		return (User) query.uniqueResult();
	}

	@Override
	@SuppressWarnings("JpaQueryApiInspection")
	public User findCreatorByAssetIdInHibernateTransaction(Long assetId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("user.findByAssetCreatorId");

		query.setLong("assetId", assetId);

		return (User) query.uniqueResult();
	}

	@Override
	public User getUser(Long id) {
		return (User) getFactory().getCurrentSession().get(User.class, id);
	}

	@Override
	public SearchUser getSearchUser(Long id) {
		final SQLBuilder sqlBuilder = new SQLBuilder()
			.addColumn("u.id, u.company_id, p.id as profile_id, c.company_number as company_number")
			.addTable("user u")
			.addJoin("INNER JOIN profile p on u.id = p.user_id")
			.addJoin("INNER JOIN company c on u.company_id = c.id")
			.addWhereClause("u.id", SQLOperator.EQUALS, "userId", id);

		return jdbcTemplate.queryForObject(sqlBuilder.build(), sqlBuilder.getParams(), new RowMapper<SearchUser>() {
			@Override
			public SearchUser mapRow(ResultSet resultSet, int i) throws SQLException {
				SearchUser user = new SearchUser();
				user.setId(resultSet.getLong("id"));
				user.setProfileId(resultSet.getLong("profile_id"));
				user.setCompanyId(resultSet.getLong("company_id"));
				user.setCompanyNumber(resultSet.getString("company_number"));

				return user;
			}
		});
	}

	@Override
	public User getUserWithRoles(Long id) {
		User user = findUserById(id);
		Hibernate.initialize(user.getUserRoleAssociations());
		return user;
	}

	@Override
	@SuppressWarnings({"unchecked", "JpaQueryApiInspection"})
	public List<User> findInternalUsers() {

		Query query = getFactory().getCurrentSession().getNamedQuery("user.findallinternal");
		return query.list();
	}

	@Override
	@SuppressWarnings({"unchecked", "JpaQueryApiInspection"})
	public Set<User> findActiveInternalUsers() {

		Query query = getFactory().getCurrentSession().getNamedQuery("user.findAllWorkmarket");
		return new LinkedHashSet(query.list());
	}


	@Override
	@SuppressWarnings({"unchecked", "JpaQueryApiInspection"})
	public Set<User> findActiveInternalContractors() {

		Query query = getFactory().getCurrentSession().getNamedQuery("user.findAllInternalContractors");
		return new LinkedHashSet(query.list());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, String> findAllUsersByCompanyId(long companyId) {
		Map<Long, String> users = Maps.newLinkedHashMap();
		String sql = "SELECT u.id userId, u.first_name, u.last_name FROM user u WHERE u.company_id = :companyId ORDER BY last_name";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);
		List<Map<String, Object>> userInfo = jdbcTemplate.queryForList(sql, params);

		for (Map<String, Object> row : userInfo) {
			Long userId = ((Integer) row.get("userId")).longValue();
			users.put(userId, StringUtilities.fullName((String) row.get("first_name"), (String) row.get("last_name")));
		}
		return users;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findSoleProprietors(Collection<Long> companyIds) {
		if (CollectionUtils.isEmpty(companyIds)) {
			return Collections.emptyList();
		}

		return (List<User>) getFactory().getCurrentSession()
			.createQuery("SELECT user " +
				"FROM user as user " +
				"JOIN user.company as company " +
				"WHERE company.id in (:companyIds) " +
				"AND company.operatingAsIndividualFlag = true")
			.setParameterList("companyIds", companyIds)
			.list();
	}

	@Override
	public Map<Long, String> findAllActiveUsersByCompanyId(long companyId) {
		Map<Long, String> users = Maps.newLinkedHashMap();
		String sql = "SELECT u.id userId, u.first_name, u.last_name FROM user u WHERE u.company_id = :companyId AND u.user_status_type_code = :active ORDER BY last_name";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);
		params.addValue("active", UserStatusType.APPROVED);
		List<Map<String, Object>> userInfo = jdbcTemplate.queryForList(sql, params);

		for (Map<String, Object> row : userInfo) {
			Long userId = ((Integer) row.get("userId")).longValue();
			users.put(userId, StringUtilities.fullName((String) row.get("first_name"), (String) row.get("last_name")));
		}
		return users;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findAllUsersByCompanyIdAndStatus(Long companyId, List<String> userStatusTypeCodes) {
		Assert.notNull(companyId);

		if (CollectionUtils.isEmpty(userStatusTypeCodes)) return Collections.emptyList();

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass()).add(Restrictions.eq("company.id", companyId));

		if (userStatusTypeCodes.size() == 1)
			criteria.add(Restrictions.eq("userStatusType.code", userStatusTypeCodes.get(0))).addOrder(Order.asc("lastName"));
		else
			criteria.add(Restrictions.in("userStatusType.code", userStatusTypeCodes.toArray())).addOrder(Order.asc("lastName"));

		return criteria.list();
	}

	@Override
	public List<User> findApprovedLane3UsersByCompanyId(Long companyId) {
		Assert.notNull(companyId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("lane3ApprovalStatus", ApprovalStatus.APPROVED))
			.addOrder(Order.asc("lastName"));

		return criteria.list();
	}

	@Override
	public UserPagination findAllPendingLane33Users(UserPagination pagination) {
		return (UserPagination) super.paginationQuery(pagination, ImmutableMap.<String, Object>of("lane3ApprovalStatus", ApprovalStatus.PENDING));
	}

	@Override
	public void updateLane3ApprovalStatus(Long userId, ApprovalStatus approvalStatus) {
		Assert.notNull(userId);
		Assert.notNull(approvalStatus);

		User user = getUser(userId);

		Assert.notNull(user);

		user.setLane3ApprovalStatus(approvalStatus);
	}

	@Override
	public Integer countAllPendingUsers() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("lane3ApprovalStatus", ApprovalStatus.PENDING)).setProjection(Projections.rowCount());

		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	public Integer countAllProfilesPendingApproval() {

		DetachedCriteria profileModifications = DetachedCriteria.forClass(UserProfileModification.class)
				.setProjection(Projections.distinct(Projections.property("user.id")))
				.add(Restrictions.eq("userProfileModificationStatus", UserProfileModificationStatus.PENDING_APPROVAL));

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass()).setProjection(Projections.rowCount())
				.add(Subqueries.propertyIn("id", profileModifications));

		return ((Long) count.uniqueResult()).intValue();
	}

	private static final Map<String, String> sortFields = new ImmutableMap.Builder<String, String>()
			.put(CompanyResourcePagination.SORTS.RESOURCE_LASTNAME.toString(), "u.last_name")
			.put(CompanyResourcePagination.SORTS.RESOURCE_COMPANY_NAME.toString(), "companyName")
			.put(CompanyResourcePagination.SORTS.LAST_LOGIN.toString(), "lastLogin")
			.put(CompanyResourcePagination.SORTS.YTD_WORK.toString(), "work")
			.put(CompanyResourcePagination.SORTS.YTD_PAYMENTS.toString(), "payments")
			.put(CompanyResourcePagination.SORTS.ROLES.toString(), "roles")
			.put(CompanyResourcePagination.SORTS.LANE.toString(), "laneType")
			.build();

	@Override
	public RecruitingCampaignUserPagination findAllRecruitingCampaignUsers(RecruitingCampaignUserPagination pagination)  {

		StringBuilder sql = new StringBuilder();
		StringBuilder sqlOrder = new StringBuilder(" ORDER BY");
		// @formatter:off
		sql.append("Select u.id AS userId, u.user_number, u.first_name, u.email_confirmed, \n" +
				"	u.last_name, u.created_on, c.id as companyId, c.name as companyName, rc.id as campaignId, rc.title, rc.active, rc.created_on AS campaignCreatedOn, \n" +
				" 	ug.id AS groupId, association.approval_status AS groupApprovalStatus, association.verification_status as groupVerificationStatus, \n" +
				" 	address.line1, address.line2, address.city, state.short_name as state, address.postal_code, address.country, lane.lane_type_id , \n" +
				" 	lane.approval_status AS laneApprovalStatus, lane.verification_status AS laneVerificationStatus \n" +
				" from 		user u \n" +
				" inner 	join company c \n" +
				" on 		c.id = u.company_id \n" +
				" inner 	join profile \n" +
				" on 		profile.user_id = u.id \n" +
				" inner 	join 	recruiting_campaign rc \n" +
				" on 		rc.id = u.recruiting_campaign_id \n" +
				" inner 	join lane_association lane \n" +
				" on  		lane.user_id = u.id \n" +
				" and 		lane.company_id = rc.company_id \n" +
				" left 		join address \n" +
				" on 		address.id = profile.address_id \n" +
				" left 		join state \n" +
				" on 		address.state = state.id \n" +
				" left 		join user_group ug \n" +
				" on 		ug.id = rc.company_user_group_id \n" +
				" left 		join user_user_group_association association \n" +
				" on  		(association.user_id = u.id \n" +
				" and 		association.user_group_id = ug.id \n" +
				" and 		association.deleted = 0 AND association.approval_status <> 2) \n" +
				" where 	lane.deleted = 0 AND lane.approval_status <> 2");
		// @formatter:on

		Map<String, Object> params = Maps.newHashMap();
		String filterValue;

		if (pagination.getFilters() != null) {
			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.CAMPAIGN_DATE_FROM)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.CAMPAIGN_DATE_FROM);
				sql.append(" AND rc.created_on >= :campaignDateFrom ");
				params.put("campaignDateFrom", DateUtilities.getCalendarFromISO8601(filterValue));
			}

			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.CAMPAIGN_DATE_TO)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.CAMPAIGN_DATE_TO);
				sql.append(" AND rc.created_on <= :campaignDateTo ");
				params.put("campaignDateTo", DateUtilities.getCalendarFromISO8601(filterValue));
			}

			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.REGISTRATION_DATE_FROM)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.REGISTRATION_DATE_FROM);
				sql.append(" AND u.created_on >= :userDateFrom ");
				params.put("userDateFrom", DateUtilities.getCalendarFromISO8601(filterValue));
			}

			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.REGISTRATION_DATE_TO)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.REGISTRATION_DATE_TO);
				sql.append(" AND u.created_on <= :userDateTo ");
				params.put("userDateTo", DateUtilities.getCalendarFromISO8601(filterValue));
			}

			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.CAMPAIGN_ID)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.CAMPAIGN_ID);
				sql.append(" AND rc.id = :campaignId");
				params.put("campaignId", Long.parseLong(filterValue));
			}

			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.ZIP_CODE)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.ZIP_CODE);
				sql.append(" AND address.postal_code like :postal_code");
				params.put("postal_code", StringUtilities.processForLike(filterValue));
			}

			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.STATE)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.STATE);
				sql.append(" AND state.short_name like :state");
				params.put("state", StringUtilities.processForLike(filterValue));
			}

			if (pagination.hasFilter(RecruitingCampaignUserPagination.FILTER_KEYS.GROUP_APPROVAL_STATUS)) {
				filterValue = pagination.getFilter(RecruitingCampaignUserPagination.FILTER_KEYS.GROUP_APPROVAL_STATUS);
				sql.append(" AND association.approval_status = :approvalStatus");
				params.put("approvalStatus", filterValue);
			}
		}

		String sortColumn = " u.id";
		String sortOrder = " DESC";
		if (pagination.getSortColumn() != null) {

			if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.CAMPAIGN_LAUNCH_DATE.toString())) {
				sortColumn = "rc.id";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.CAMPAIGN_TITLE.toString())) {
				sortColumn = "rc.title";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.CITY.toString())) {
				sortColumn = "address.city";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.FIRST_NAME.toString())) {
				sortColumn = "u.first_name";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.LAST_NAME.toString())) {
				sortColumn = "u.last_name";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.REGISTRATION_DATE.toString())) {
				sortColumn = "u.created_on";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.STATE.toString())) {
				sortColumn = "state.short_name";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.ZIP_CODE.toString())) {
				sortColumn = "address.postal_code";
			} else if (pagination.getSortColumn().equals(RecruitingCampaignUserPagination.SORTS.GROUP_APPROVAL_STATUS.toString())) {
				sortColumn = "association.approval_status ";
			}

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				sortOrder = " DESC";
			} else {
				sortOrder = " ASC";
			}
		}

		sqlOrder.append(sortColumn).append(sortOrder);

		RowMapper<RecruitingCampaignUser> mapper = new RowMapper<RecruitingCampaignUser>() {
			@Override
			public RecruitingCampaignUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				RecruitingCampaignUser resource = new RecruitingCampaignUser();

				resource.setUserId(rs.getLong("userId"));
				resource.setUserNumber(rs.getString("user_number"));
				resource.setEmailConfirmed(rs.getBoolean("u.email_confirmed"));
				resource.setFirstName(rs.getString("first_name"));
				resource.setLastName(rs.getString("last_name"));
				resource.setCompanyId(rs.getLong("companyId"));
				resource.setCompanyName(rs.getString("companyName"));
				resource.setRegistrationDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("u.created_on")));
				resource.setRecruitingCampaignId(rs.getLong("campaignId"));
				resource.setRecruitingCampaignActive(rs.getBoolean("active"));
				resource.setRecruitingCampaignDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("campaignCreatedOn")));
				resource.setRecruitingCampaignTitle(rs.getString("title"));
				resource.setUserGroupId(rs.getLong("groupId"));

				if (rs.getObject("groupApprovalStatus") != null)
					resource.setGroupApprovalStatus(rs.getLong("groupApprovalStatus"));
				if (rs.getObject("groupVerificationStatus") != null)
					resource.setGroupVerificationStatus(rs.getLong("groupVerificationStatus"));

				resource.setAddress1(rs.getString("line1"));
				resource.setAddress2(rs.getString("line2"));
				resource.setCity(rs.getString("city"));
				resource.setPostalCode(rs.getString("postal_code"));
				resource.setState(rs.getString("state"));
				resource.setCountry(rs.getString("country"));

				if (rs.getObject("laneApprovalStatus") != null)
					resource.setLaneApprovalStatus(rs.getLong("laneApprovalStatus"));
				if (rs.getObject("laneVerificationStatus") != null)
					resource.setLaneVerificationStatus(rs.getLong("laneVerificationStatus"));
				if (rs.getObject("lane_type_id") != null)
					resource.setLaneType(rs.getLong("lane_type_id"));

				return resource;
			}
		};
		Integer limit = (pagination.getResultsLimit() == null ? Pagination.MAX_ROWS : pagination.getResultsLimit());

		Integer count = jdbcTemplate.queryForObject("SELECT count(1) FROM ( " + sql.toString() + " ) data", params, Integer.class);

		pagination.setRowCount(count);
		pagination.setResults(jdbcTemplate.query(sql.append(sqlOrder).append(" LIMIT ").append(pagination.getStartRow()).append(", ").append(limit)
				.toString(), params, mapper));

		return pagination;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findByPhoneNumber(String phoneNumber) {
		return getFactory().getCurrentSession()
				.createQuery("from user u where (u.profile.workPhone = :phone_number or u.profile.mobilePhone = :phone_number)")
				.setParameter("phone_number", phoneNumber).list();
	}

	@Override
	public List<UserDTO> findUserDTOsOfAllActiveEmployees(final Long companyId, final boolean includeApiUsers) {
		Assert.notNull(companyId, "Company id is required");

		final SQLBuilder builder = new SQLBuilder()
				.addColumns("id", "company_id", "first_name", "last_name", "uuid")
				.addTable("user")
				.addWhereClause("company_id = :companyId")
				.addWhereClause("user_status_type_code = :userStatusTypeCode")
				.addParam("companyId", companyId)
				.addParam("userStatusTypeCode", UserStatusType.APPROVED);

		if (includeApiUsers) {
			builder.addWhereClause("api_enabled = true");
		}

		return jdbcTemplate.query(builder.build(), builder.getParams(), USER_DTO_ROW_MAPPER);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllActiveEmployees(Long companyId) {
		Assert.notNull(companyId, "Company id is required");

		return HibernateUtilities.listAndCast(getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("userStatusType.code", UserStatusType.APPROVED)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserPagination findAllActiveEmployees(Long companyId, UserPagination pagination) {
		Assert.notNull(companyId, "Company id is required");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId);

		Long rowCount = HibernateUtilities.getRowCount(count);

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findByAclPermissionCode(Long companyId, String permissionCode) {
		Assert.notNull(companyId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("userRoleAssociations", FetchMode.JOIN).createAlias("userRoleAssociations", "ra")
				.setFetchMode("ra.role", FetchMode.JOIN).createAlias("ra.role", "role")
				.setFetchMode("role.permissionAssociations", FetchMode.JOIN).createAlias("role.permissionAssociations", "permAssociation")
				.setFetchMode("permAssociation.permission", FetchMode.JOIN).createAlias("permAssociation.permission", "permission")
				.add(Restrictions.eq("ra.deleted", Boolean.FALSE)).add(Restrictions.eq("permission.code", permissionCode))
				.add(Restrictions.eq("company.id", companyId)).add(Restrictions.ne("userStatusType.code", UserStatusType.DELETED))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllUsersByACLRoleAndCompany(Long companyId, Long aclRoleId) {
		Assert.notNull(companyId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("userRoleAssociations", FetchMode.JOIN).createAlias("userRoleAssociations", "ra")
				.setFetchMode("ra.role", FetchMode.JOIN).createAlias("ra.role", "role").add(Restrictions.eq("ra.deleted", Boolean.FALSE))
				.add(Restrictions.eq("role.id", aclRoleId)).add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("userStatusType.code", UserStatusType.APPROVED))
				.add(Restrictions.eq("emailConfirmed", Boolean.TRUE))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> suggest(String prefix, Long companyId, boolean internalOnly, boolean externalOnly) {
		Criteria criteria =
				getFactory().getCurrentSession().createCriteria(User.class);
		criteria.
				setFetchMode("company", FetchMode.JOIN).
				setFetchMode("profile", FetchMode.JOIN).
				setFetchMode("profile.address", FetchMode.JOIN).
				add(Restrictions.eq("emailConfirmed", Boolean.TRUE));

		if (internalOnly) {
			criteria.add(
					Restrictions.and(Restrictions.eq("company.id", companyId), Restrictions.eq("userStatusType.code", UserStatusType.APPROVED)));
		} else if (externalOnly) {
			criteria.add(
					Restrictions.and(Restrictions.ne("company.id", companyId), Restrictions.eq("lane3ApprovalStatus", ApprovalStatus.APPROVED)));

		} else {
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.eq("company.id", companyId), Restrictions.eq("userStatusType.code", UserStatusType.APPROVED)),
					Restrictions.eq("lane3ApprovalStatus", ApprovalStatus.APPROVED)
			));
		}

		criteria.add(ilike(prefix, MatchMode.START, "firstName", "lastName")).
				setMaxResults(Constants.MAX_RESOURCES_SUGGESTION_RESULTS_FOR_ASSIGNMENT).
				addOrder(Order.asc("firstName")).
				addOrder(Order.asc("lastName"));

		return criteria.list();
	}

	@Override
	public List<UserSuggestionDTO> suggestWorkers(
		final String pattern,
		final Long companyId,
		final boolean internalOnly,
		final boolean externalOnly,
		final boolean hasMarketplace) {

		List<Integer> lanes = Lists.newArrayList();
		if (internalOnly) {
			lanes.add(LANE_1_KEY); // Employees
		} else if (externalOnly) {
			lanes.add(LANE_2_KEY); // Contractors
			if (hasMarketplace) {
				lanes.add(LANE_3_KEY); // Third-party
			}
		} else {
			// All lanes
			lanes.add(LANE_1_KEY); // Employees
			lanes.add(LANE_2_KEY); // Contractors
			if (hasMarketplace) {
				lanes.add(LANE_3_KEY); // Third-party
			}
		}

		StringBuilder whereClause = new StringBuilder().append("(lane.lane_type_id in (:lanes) AND lane.company_id = :companyId)");

		if (lanes.contains(LANE_3_KEY)) {
			whereClause
				.insert(0, "(")
				.append(" OR (u.lane3_approval_status = 1 AND role.acl_role_id = 7))");
		}

		final SQLBuilder builder = new SQLBuilder()
			.addColumns(
				"u.id AS id",
				"u.email AS email",
				"u.user_number AS userNumber",
				"c.effective_name AS companyName",
				"CONCAT(u.first_name, ' ', u.last_name) AS value",
				"CONCAT(a.city, ', ', s.short_name, ', ', s.country_id) AS cityStateCountry"
			)
			.addTable("user u")
			.addJoin("INNER JOIN company c ON u.company_id = c.id")
			.addJoin("INNER JOIN profile p ON u.id = p.user_id")
			.addJoin("INNER JOIN address a ON a.id = p.address_id")
			.addJoin("INNER JOIN state s ON s.id = a.state")
			.addJoin("LEFT JOIN lane_association lane ON (lane.user_id = u.id AND lane.deleted = false)")
			.addJoin("LEFT JOIN user_acl_role role ON (u.id = role.user_id AND role.deleted = false)")
			.addWhereClause("u.email_confirmed = 'Y'")
			.addWhereClause("u.user_status_type_code in ('approved', 'pending')")
			.addParam("companyId", companyId)
			.addWhereClause("CONCAT(u.first_name, ' ', u.last_name) LIKE :pattern")
			.addParam("pattern", StringUtilities.processForLike(pattern.toLowerCase()))
			.addAscOrderBy("u.first_name")
			.addAscOrderBy("u.last_name")
			.setStartRow(0)
			.setPageSize(Constants.MAX_RESOURCES_SUGGESTION_RESULTS_FOR_ASSIGNMENT)
			.setDistinct(true)
			.addWhereClause(whereClause.toString())
			.addParam("lanes", lanes);

		RowMapper<UserSuggestionDTO> mapper = new RowMapper<UserSuggestionDTO>() {
			@Override
			public UserSuggestionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserSuggestionDTO user = new UserSuggestionDTO();
				user.setId(rs.getLong("id"));
				user.setUserNumber(rs.getString("userNumber"));
				user.setCompanyName(rs.getString("companyName"));
				user.setValue(rs.getString("value"));
				user.setEmail(rs.getString("email"));
				user.setCityStateCountry(rs.getString("cityStateCountry"));
				return user;
			}
		};
		return jdbcTemplate.query(builder.build(), builder.getParams(), mapper);
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<UserSuggestionDTO> suggest(String prefix) {
		SQLBuilder builder = new SQLBuilder()
				.addColumns(
						"u.id AS id",
						"u.email AS email",
						"CONCAT(u.firstName, ' ', u.lastName) AS value",
						"c.name as companyName"
				)
				.addTable("user u")
				.addJoin("INNER JOIN u.company AS c")
				.addWhereClause("CONCAT(u.firstName, ' ', u.lastName) LIKE :prefix")
				.addAscOrderBy("u.firstName")
				.addAscOrderBy("u.lastName");

		Query query = getFactory().getCurrentSession().createQuery(builder.build());
		query.setParameter("prefix", StringUtilities.processForLike(prefix.toLowerCase()));
		query.setMaxResults(10);

		return query.setResultTransformer(Transformers.aliasToBean(UserSuggestionDTO.class)).list();
	}

	@Override
	public UserPagination findAllUsersByUserStatusType(UserPagination pagination, final UserStatusType userStatusType) {

		return (UserPagination) super.paginationQuery(pagination, ImmutableMap.<String, Object>of("userStatusType.code", userStatusType.getCode()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserPagination findAllActiveAdminUsers(Long companyId, UserPagination pagination) {
		Assert.notNull(companyId, "Invalid company id");
		Assert.notNull(pagination, "Invalid pagination");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(User.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(User.class);

		criteria.createAlias("userRoleAssociations", "userRoleAssociations");
		criteria.createAlias("userRoleAssociations.role", "role");

		count.createAlias("userRoleAssociations", "userRoleAssociations");
		count.createAlias("userRoleAssociations.role", "role");

		// base restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId, "role.id", AclRole.ACL_ADMIN);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId, "role.id", AclRole.ACL_ADMIN);

		criteria.add(Restrictions.in("userStatusType.code", new String[]{UserStatusType.PENDING, UserStatusType.APPROVED}));
		count.add(Restrictions.in("userStatusType.code", new String[]{UserStatusType.PENDING, UserStatusType.APPROVED}));

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		// applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public RecentUserPagination findAllRecentUsers(RecentUserPagination pagination) {
		Assert.notNull(pagination, "Invalid pagination");
		// @formatter:off
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns(
				new String[]{"u.id", "u.user_number", "u.first_name", "u.last_name", "u.email", "u.created_on", "p.work_phone",
						"p.mobile_phone", "c.effective_name as company_name", "c.id as company_id"})
				.addColumn(" IF(EXISTS(SELECT id FROM lane_association l WHERE l.user_id = u.id AND l.lane_type_id = 1), true, false) as lane1")
				.addColumn(" IF(EXISTS(SELECT id FROM lane_association l WHERE l.user_id = u.id AND l.lane_type_id = 2), true, false) as lane2")
				.addColumn(" IF(EXISTS(SELECT id FROM lane_association l WHERE l.user_id = u.id AND l.lane_type_id = 3), true, false) as lane3")
				.addColumn(" IF(u.lane3_approval_status = 1, true, false) as lane4")
				.addTable(" user u ");
		// @formatter:on

		Calendar since = null;

		if (pagination.getFilters() != null) {
			if (pagination.hasFilter(RecentUserPagination.FILTER_KEYS.USER_NAME)) {
				String name = pagination.getFilter(RecentUserPagination.FILTER_KEYS.USER_NAME);
				// @formatter:off
				String subSelectSql = "SELECT user.id FROM user \n" +
						// Taking advantage of the index merge optimization
						" WHERE 	last_name like :name OR first_name like :name \n" +
						" UNION \n" +
						" SELECT 	user.id from user \n" +
						" WHERE 	email like :name \n" +
						" UNION \n" +
						" SELECT 	user.id from user \n" +
						" INNER 	JOIN company c \n" +
						" ON 		c.id = company_id  \n" +
						" WHERE 	c.effective_name like :name \n";
				// @formatter:on

				if (name.length() >= 3) {
					builder.addJoin(" INNER JOIN ( " + subSelectSql + " ) ids ON ids.id = u.id").addParam("name",
							StringUtilities.processForLike(name));
				}
			}

			if (pagination.hasFilter(RecentUserPagination.FILTER_KEYS.LAST_30_DAYS)) {
				since = DateUtilities.subtractTime(DateUtilities.getMidnightToday(), 30, Constants.DAY);
			} else if (pagination.hasFilter(RecentUserPagination.FILTER_KEYS.LAST_60_DAYS)) {
				since = DateUtilities.subtractTime(DateUtilities.getMidnightToday(), 60, Constants.DAY);
			} else if (pagination.hasFilter(RecentUserPagination.FILTER_KEYS.LAST_90_DAYS)) {
				since = DateUtilities.subtractTime(DateUtilities.getMidnightToday(), 90, Constants.DAY);
			} else if (pagination.hasFilter(RecentUserPagination.FILTER_KEYS.LAST_120_DAYS)) {
				since = DateUtilities.subtractTime(DateUtilities.getMidnightToday(), 120, Constants.DAY);
			} else if (pagination.hasFilter(RecentUserPagination.FILTER_KEYS.LAST_365_DAYS)) {
				since = DateUtilities.subtractTime(DateUtilities.getMidnightToday(), 365, Constants.DAY);
			} else if (pagination.hasFilter(RecentUserPagination.FILTER_KEYS.YTD)) {
				since = DateUtilities.getMidnightYTD();
			}
		}

		builder.addJoin(" INNER JOIN profile p ON u.id = p.user_id ")
				.addJoin(" INNER JOIN company c ON u.company_id = c.id ");

		if (since != null) {
			builder.addWhereClause(" u.created_on >= :since")
					.addParam("since", since);
		}


		if (pagination.getSortColumn() == null)
			pagination.setSortColumn(RecentUserPagination.SORTS.FIRST_NAME.toString());

		String sortColumn = "u.first_name";
		if (pagination.getSortColumn().equals(RecentUserPagination.SORTS.FIRST_NAME.toString())) {
			sortColumn = "u.first_name";
		} else if (pagination.getSortColumn().equals(RecentUserPagination.SORTS.COMPANY_NAME.toString())) {
			sortColumn = "c.effective_name";
		} else if (pagination.getSortColumn().equals(RecentUserPagination.SORTS.REGISTRATION_DATE.toString())) {
			sortColumn = "u.created_on";
		} else if (pagination.getSortColumn().equals(RecentUserPagination.SORTS.LAST_NAME.toString())) {
			sortColumn = "u.last_name";
		} else if (pagination.getSortColumn().equals(RecentUserPagination.SORTS.EMAIL.toString())) {
			sortColumn = "u.email";
		} else if (pagination.getSortColumn().equals(RecentUserPagination.SORTS.WORK_PHONE.toString())) {
			sortColumn = "p.work_phone";
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.ASC)) {
			builder.addOrderBy(sortColumn, "ASC");
		} else {
			builder.addOrderBy(sortColumn, "DESC");
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		RowMapper<RecentUser> mapper = new RowMapper<RecentUser>() {
			@Override
			public RecentUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				RecentUser user = new RecentUser();

				user.setId(rs.getLong("id"));
				user.setUserNumber(rs.getString("user_number"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setEmail(rs.getString("email"));
				user.setWorkPhone(rs.getString("work_phone"));
				user.setMobilePhone(rs.getString("mobile_phone"));
				user.setCompanyId(rs.getLong("company_id"));
				user.setCompanyName(rs.getString("company_name"));
				user.setRegisteredOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("created_on")));
				user.setLane1Flag(rs.getBoolean("lane1"));
				user.setLane2Flag(rs.getBoolean("lane2"));
				user.setLane3Flag(rs.getBoolean("lane3"));
				user.setLane4Flag(rs.getBoolean("lane4"));

				return user;
			}
		};

		logger.debug(builder.build());
		pagination.setRowCount(jdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class));
		pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), mapper));

		return pagination;
	}

	@Override
	public CompanyResourcePagination findAllEmployeesByCompanyId(Long companyId, CompanyResourcePagination pagination) {

		SQLBuilder sqlBuilderEmployees = new SQLBuilder();
		SQLBuilder sqlCount = new SQLBuilder();

		sqlBuilderEmployees.addColumn(" u.id, u.user_number, u.first_name, u.last_name,  GROUP_CONCAT(role.name SEPARATOR ', ') AS roles ")
				.addColumn(" u.company_id AS companyId, '' AS companyName, 0 AS work, 0 AS payments, 1 AS laneType ")
				.addColumn(" (SELECT  MAX(logged_on)  FROM    login_info login WHERE user_id = u.id ) as lastLogin ");

		sqlBuilderEmployees.addTable(" user u ");
		sqlCount.addTable(" user u ");

		sqlBuilderEmployees.addJoin(" INNER   JOIN user_acl_role uar ON u.id = uar.user_id AND uar.deleted = 0 ").addJoin(
				" INNER   JOIN  acl_role role ON role.id = uar.acl_role_id ");

		sqlBuilderEmployees.addWhereClause(" u.email_confirmed = 'Y' ")
				.addWhereClause(" u.user_status_type_code IN ('pending', 'approved') ").addWhereClause(" u.company_id = :companyId ");

		sqlBuilderEmployees.addParam("companyId", companyId);

		if (pagination.getFilters() != null) {
			if (pagination.getFilter(CompanyResourcePagination.FILTER_KEYS.KEYWORDS) != null) {
				String keyword = pagination.getFilter(CompanyResourcePagination.FILTER_KEYS.KEYWORDS);

				sqlBuilderEmployees.addWhereClause(" (u.first_name like :keyword OR u.last_name like :keyword)").addParam("keyword",
						StringUtilities.processForLike(keyword));
			}
		}

		/** Apply same where clause to the count */
		sqlCount.getWhereClauses().addAll(sqlBuilderEmployees.getWhereClauses());
		sqlCount.setParams(sqlBuilderEmployees.getParams());

		/** Group columns */
		sqlBuilderEmployees.getGroupColumns().add(" u.id, u.first_name, u.last_name");

		/** Sorts */
		if (pagination.getSortColumn() != null) {
			sqlBuilderEmployees.addOrderBy(sortFields.containsKey(pagination.getSortColumn()) ? sortFields.get(pagination.getSortColumn())
					: sortFields.get(CompanyResourcePagination.SORTS.RESOURCE_LASTNAME.toString()), pagination.getSortDirection()
					.toString());

		} else {
			sqlBuilderEmployees.addOrderBy(" u.last_name, u.first_name ", " ASC");
		}

		/** pagination */
		sqlBuilderEmployees.setStartRow(pagination.getStartRow());
		sqlBuilderEmployees.setPageSize(pagination.getResultsLimit());

		pagination
				.setResults(jdbcTemplate.query(sqlBuilderEmployees.build(), sqlBuilderEmployees.getParams(), new CompanyResourceMapper()));
		pagination.setRowCount(jdbcTemplate.queryForObject(sqlCount.buildCount("*"), sqlCount.getParams(), Integer.class));

		return pagination;
	}

	@Override
	public CompanyResourcePagination findAllContractorsByCompanyId(Long companyId, CompanyResourcePagination pagination) {

		SQLBuilder sqlBuilderContractors = new SQLBuilder();
		SQLBuilder sqlCount = new SQLBuilder();
		// @formatter:off
		/** Contractors query */
		sqlBuilderContractors
				.addColumn(" u.id, u.user_number, u.first_name, u.last_name, c.id AS companyId, c.name AS companyName ")
				.addColumn(" 0 AS work")
				.addColumn(" 0 AS payments")
				.addColumn("null AS roles, null AS lastLogin, lane.lane_type_id AS laneType ")
				.addTable(" user u ")
				.addJoin(" INNER   JOIN  company c ON c.id = u.company_id")
				.addJoin(" INNER   JOIN lane_association lane ON u.id = lane.user_id");

		sqlCount.addTable(" user u ")
				.addJoin(" INNER   JOIN  company c ON c.id = u.company_id")
				.addJoin(" INNER   JOIN lane_association lane ON u.id = lane.user_id");

		sqlBuilderContractors.addWhereClause(" u.email_confirmed = 'Y' ")
				.addWhereClause(" u.user_status_type_code IN ('pending', 'approved') ")
				.addWhereClause(" lane.company_id = :companyId ")
				.addWhereClause(" lane.lane_type_id IN (2, 3) ")
				.addWhereClause(" lane.deleted = 0 ")
				.addWhereClause(" lane.approval_status IN (1,5) ")
				.addWhereClause(" lane.verification_status = 1 ");
		// @formatter:on

		sqlBuilderContractors.addParam("companyId", companyId).addParam("midnightYTD", DateUtilities.getMidnightYTD());

		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(CompanyResourcePagination.FILTER_KEYS.KEYWORDS.toString()) != null) {
				String keyword = pagination.getFilters().get(CompanyResourcePagination.FILTER_KEYS.KEYWORDS.toString());

				sqlBuilderContractors.addParam("keyword", StringUtilities.processForLike(keyword)).addWhereClause(
						"(u.first_name like :keyword OR u.last_name like :keyword OR c.name like :keyword)");
			}
		}

		/** Apply same where clause to the count */
		sqlCount.getWhereClauses().addAll(sqlBuilderContractors.getWhereClauses());
		sqlCount.setParams(sqlBuilderContractors.getParams());

		/** Sorts */
		if (pagination.getSortColumn() != null) {
			sqlBuilderContractors.addOrderBy(
					sortFields.containsKey(pagination.getSortColumn()) ? sortFields.get(pagination.getSortColumn()) : sortFields
							.get(CompanyResourcePagination.SORTS.RESOURCE_LASTNAME.toString()), pagination.getSortDirection().toString());
		} else {
			sqlBuilderContractors.addOrderBy(" c.name ", " ASC");
		}

		/** pagination */
		sqlBuilderContractors.setStartRow(pagination.getStartRow());
		sqlBuilderContractors.setPageSize(pagination.getResultsLimit());

		pagination.setResults(jdbcTemplate.query(sqlBuilderContractors.build(), sqlBuilderContractors.getParams(), new CompanyResourceMapper()));
		pagination.setRowCount(jdbcTemplate.queryForObject(sqlCount.buildCount("DISTINCT u.id"), sqlCount.getParams(), Integer.class));

		return pagination;

	}

	private Long getTotalUserCountforCompany(Long companyId, List<String> statuses) {
		SQLBuilder userIdSql = new SQLBuilder()
				.addColumns("distinct u.id")
				.addTable("user u")
				.addJoin("JOIN user_acl_role uar ON u.id = uar.user_id AND uar.deleted = false")
				.addWhereClause("u.company_id", "=", "company_id", companyId)
				.addWhereClause("u.api_enabled = 0")
				.addWhereInClause("u.user_status_type_code", "statuses", statuses);

		return jdbcTemplate.queryForObject(userIdSql.buildCount(), userIdSql.getParams(), Long.class);
	}

	private List<Long> getUserIdsforCompany(Long companyId, List<String> statuses, String sortColumn,
			Pagination.SORT_DIRECTION sortDirection, Integer start, Integer limit) {
		SQLBuilder userIdSql = new SQLBuilder()
				.addColumns("distinct u.id as uid")
				.addTable("user u")
				.addJoin("JOIN user_acl_role uar ON u.id = uar.user_id AND uar.deleted = false")
				.addWhereClause("u.company_id", "=", "company_id", companyId)
				.addWhereClause("u.api_enabled = 0")
				.addWhereInClause("u.user_status_type_code", "statuses", statuses)
				.addGroupColumns("u.id");

		String sort = "u.createdOn";
		switch (CompanyUserPagination.SORTS.valueOf(sortColumn)) {
			case USER_NUMBER:
				sort = "u.user_number";
			break;
			case FIRST_NAME:
				sort = "u.first_name";
			break;
			case LAST_NAME:
				sort = "u.last_name";
			break;
			case EMAIL:
				sort = "u.email";
			break;
			case ROLES_STRING:
				sort = "COALESCE(GROUP_CONCAT(role.name SEPARATOR ', '), '')";

				userIdSql.addJoin("LEFT JOIN acl_role role ON role.id = uar.acl_role_id AND role.id NOT IN (:worker_acl_role, :shared_worker_acl_role) ");

				userIdSql.addParam("worker_acl_role", AclRole.ACL_WORKER);
				userIdSql.addParam("shared_worker_acl_role", AclRole.ACL_SHARED_WORKER);
			break;
			case LANE_ACCESS_STRING:
				sort = "COALESCE(GROUP_CONCAT(lane.name SEPARATOR ', '), '')";

				userIdSql.addJoin("LEFT JOIN acl_role lane ON lane.id = uar.acl_role_id AND lane.id IN (:worker_acl_role, :shared_worker_acl_role)");

				userIdSql.addParam("worker_acl_role", AclRole.ACL_WORKER);
				userIdSql.addParam("shared_worker_acl_role", AclRole.ACL_SHARED_WORKER);
			break;
			case LATEST_ACTIVITY:
				sort = "last_active_on";
				userIdSql.addColumn("(SELECT MAX(logged_on) FROM login_info WHERE user_id = u.id) AS last_active_on");
			break;
			default:
				sort = "u.created_on";
		}

		if (sortDirection.equals(Pagination.SORT_DIRECTION.ASC)) {
			userIdSql.addAscOrderBy(sort);
		} else {
			userIdSql.addDescOrderBy(sort);
		}

		userIdSql.setStartRow(start);
		userIdSql.setPageSize(limit);

		return jdbcTemplate.query(userIdSql.build(), userIdSql.getParams(), new RowMapper<Long>() {

			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("uid");
			}

		});
	}

	@SuppressWarnings("unchecked")
	private List<CompanyUser> getUsersforCompany(Long companyId, List<Long> userIds, List<String> statuses, String sortColumn,
			Pagination.SORT_DIRECTION sortDirection, Calendar fromDate, Calendar toDate) {

		String sort = "u.created_on";
		switch (CompanyUserPagination.SORTS.valueOf(sortColumn)) {
			case USER_NUMBER:
				sort = "u.user_number";
			break;
			case FIRST_NAME:
				sort = "u.first_name";
			break;
			case LAST_NAME:
				sort = "u.last_name";
			break;
			case EMAIL:
				sort = "u.email";
			break;
			case ROLES_STRING:
				sort = "roles";
			break;
			case LANE_ACCESS_STRING:
				sort = "lanes";
			break;
			case LATEST_ACTIVITY:
				sort = "last_active_on";
			break;
			default:
				sort = "u.created_on";
		}

		SQLBuilder sql = new SQLBuilder()
				.addColumns("u.id", "u.user_number", "u.first_name", "u.last_name", "u.email", "u.email_confirmed", "u.user_status_type_code")
				.addColumn("COALESCE(GROUP_CONCAT(role.name SEPARATOR ', '), '') AS roles")
				.addColumn("COALESCE(GROUP_CONCAT(lane.name SEPARATOR ', '), '') AS lanes")
				.addColumns("(SELECT MAX(logged_on) FROM login_info WHERE user_id = u.id) last_active_on", "NULL AS ip_address")
				.addColumn("(SELECT count(distinct work_id) FROM work_history_summary INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id " +
						" WHERE work_status_type_code = 'sent' AND time_dimension.date BETWEEN :fromDate AND :toDate AND buyer_user_id = u.id) as stats_sent_count")
				.addColumn("(SELECT count(distinct work_id) FROM work_history_summary INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id " +
						" WHERE work_status_type_code in ('paid', 'paymentPending') AND time_dimension.date BETWEEN :fromDate AND :toDate AND buyer_user_id = u.id) as stats_approved_count")
				.addColumn("(SELECT COALESCE(sum(work_price),0) FROM work_history_summary INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id " +
						" WHERE work_status_type_code = 'sent' AND time_dimension.date BETWEEN :fromDate AND :toDate AND buyer_user_id = u.id) as stats_sent_value")
				.addColumn("(SELECT COALESCE(sum(buyer_total_cost),0) FROM work_history_summary INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id " +
						" WHERE work_status_type_code in ('paid', 'paymentPending') AND time_dimension.date BETWEEN :fromDate AND :toDate AND buyer_user_id = u.id) as stats_approved_value")
				.addTable("user u")
				.addJoin("INNER JOIN user_acl_role uar ON u.id = uar.user_id AND uar.deleted = false")
				.addJoin("LEFT JOIN acl_role role ON role.id = uar.acl_role_id AND role.id NOT IN (:worker_acl_role, :shared_worker_acl_role) ")
				.addJoin("LEFT JOIN acl_role lane ON lane.id = uar.acl_role_id AND lane.id IN (:worker_acl_role, :shared_worker_acl_role)")
				.addWhereClause("u.company_id", "=", "company_id", companyId)
				.addWhereClause("u.api_enabled = 0")
				.addWhereInClause("u.user_status_type_code", "statuses", statuses)
				.addWhereInClause("u.id", "userids", userIds)
				.addParam("fromDate", fromDate)
				.addParam("toDate", toDate)
				.addGroupColumns("u.id");

		sql.addParam("worker_acl_role", AclRole.ACL_WORKER);
		sql.addParam("shared_worker_acl_role", AclRole.ACL_SHARED_WORKER);

		if (sortDirection.equals(Pagination.SORT_DIRECTION.ASC)) {
			sql.addAscOrderBy(sort);
		} else {
			sql.addDescOrderBy(sort);
		}

		RowMapper<CompanyUser> mapper = new RowMapper<CompanyUser>() {
			@Override
			public CompanyUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				CompanyUser user = new CompanyUser();

				user.setId(rs.getLong("id"));
				user.setUserNumber(rs.getString("user_number"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setEmail(rs.getString("email"));
				user.setEmailConfirmed(rs.getBoolean("email_confirmed"));
				user.setUserStatusType(rs.getString("user_status_type_code"));
				user.setRolesString(rs.getString("roles"));
				user.setLaneAccessString(rs.getString("lanes"));
				user.setLatestActivityOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("last_active_on")));
				user.setLatestActivityInetAddress(rs.getString("ip_address"));

				CompanyUserStats stats = new CompanyUserStats()
						.setSentCount(rs.getBigDecimal("stats_sent_count"))
						.setSentValue(rs.getBigDecimal("stats_sent_value"))
						.setApprovedCount(rs.getBigDecimal("stats_approved_count"))
						.setApprovedValue(rs.getBigDecimal("stats_approved_value"));

				user.setStats(stats);

				return user;
			}
		};

		return jdbcTemplate.query(sql.build(), sql.getParams(), mapper);
	}

	@Override
	public CompanyUserPagination findAllCompanyUsers(Long companyId, CompanyUserPagination pagination, Calendar fromDate, Calendar toDate) {
		Assert.notNull(companyId);
		Assert.notNull(fromDate);
		Assert.notNull(toDate);
		Assert.notNull(pagination, "Invalid pagination");

		List<String> statuses;
		if (pagination.hasFilter(CompanyUserPagination.FILTER_KEYS.IS_INACTIVE)) {
			String value = pagination.getFilter(CompanyUserPagination.FILTER_KEYS.IS_INACTIVE);
			boolean isInactive = StringUtils.isNumeric(value) ?
					BooleanUtils.toBoolean(StringUtilities.parseInteger(value)) :
					BooleanUtils.toBoolean(value);
			if (isInactive) {
				statuses = ImmutableList.of("deactivate");
			} else {
				statuses = ImmutableList.of("pending", "approved", "suspended");
			}
		} else {
			statuses = ImmutableList.of("pending", "approved", "suspended", "deactivate");
		}

		pagination.setRowCount(getTotalUserCountforCompany(companyId, statuses));

		List<Long> userIds = getUserIdsforCompany(companyId, statuses, pagination.getSortColumn(),
				pagination.getSortDirection(), pagination.getStartRow(), pagination.getResultsLimit());

		pagination.setResults(getUsersforCompany(companyId, userIds, statuses, pagination.getSortColumn(),
				pagination.getSortDirection(), fromDate, toDate));

		return pagination;
	}

	@Override
	public void applySorts(Pagination<User> pagination, Criteria query, Criteria count) {

		String sortColumn = "id";
		if (pagination.getSortColumn() != null) {

			if (pagination.getSortColumn().equals(UserPagination.SORTS.USER_NAME.name())) {
				sortColumn = "firstName";
			} else if (pagination.getSortColumn().equals(UserPagination.SORTS.COMPANY_NAME.name())) {
				sortColumn = "company.name";
			} else if (pagination.getSortColumn().equals(UserPagination.SORTS.CREATION_DATE.name())) {
				sortColumn = "createdOn";
			} else if (pagination.getSortColumn().equals(UserPagination.SORTS.MODIFIED_DATE.name())) {
				sortColumn = "modifiedOn";
			}

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				query.addOrder(Order.desc(sortColumn));
			} else {
				query.addOrder(Order.asc(sortColumn));
			}
		} else
			query.addOrder(Order.desc(sortColumn));
	}

	@Override
	public void applyFilters(Pagination<User> pagination, Criteria query, Criteria count) {
		if (pagination.getFilters() == null)
			return;

		if (pagination.hasFilter(UserPagination.FILTER_KEYS.CREATION_DATE_FROM)) {

			String fromDate = pagination.getFilter(UserPagination.FILTER_KEYS.CREATION_DATE_FROM);
			query.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(fromDate)));
			count.add(Restrictions.ge("createdOn", DateUtilities.getCalendarFromISO8601(fromDate)));
		}

		if (pagination.hasFilter(UserPagination.FILTER_KEYS.CREATION_DATE_TO)) {

			String toDate = pagination.getFilter(UserPagination.FILTER_KEYS.CREATION_DATE_TO);
			query.add(Restrictions.le("createdOn", DateUtilities.getCalendarFromISO8601(toDate)));
			count.add(Restrictions.le("createdOn", DateUtilities.getCalendarFromISO8601(toDate)));
		}

		if (pagination.hasFilter(UserPagination.FILTER_KEYS.FIRST_NAME)) {
			query.add(Restrictions.ilike("firstName", pagination.getFilter(UserPagination.FILTER_KEYS.FIRST_NAME)));
			count.add(Restrictions.ilike("firstName", pagination.getFilter(UserPagination.FILTER_KEYS.FIRST_NAME)));
		}

		if (pagination.hasFilter(UserPagination.FILTER_KEYS.KEYWORDS)) {
			String keywords = pagination.getFilter(UserPagination.FILTER_KEYS.KEYWORDS);

			query.add(Restrictions.or(
					Restrictions.ilike("firstName", keywords, MatchMode.ANYWHERE),
					Restrictions.or(Restrictions.ilike("lastName", keywords, MatchMode.ANYWHERE),
							Restrictions.ilike("company.name", keywords, MatchMode.ANYWHERE))

			));
			count.add(Restrictions.or(
					Restrictions.ilike("firstName", keywords, MatchMode.ANYWHERE),
					Restrictions.or(Restrictions.ilike("lastName", keywords, MatchMode.ANYWHERE),
							Restrictions.ilike("company.name", keywords, MatchMode.ANYWHERE))

			));
		}

		if (pagination.hasFilter(UserPagination.FILTER_KEYS.LAST_NAME)) {
			query.add(Restrictions.ilike("lastName", pagination.getFilter(UserPagination.FILTER_KEYS.LAST_NAME)));
			count.add(Restrictions.ilike("lastName", pagination.getFilter(UserPagination.FILTER_KEYS.LAST_NAME)));
		}

		if (pagination.hasFilter(UserPagination.FILTER_KEYS.COMPANY_NAME)) {
			query.add(Restrictions.ilike("company.name", pagination.getFilter(UserPagination.FILTER_KEYS.COMPANY_NAME)));
			count.add(Restrictions.ilike("company.name", pagination.getFilter(UserPagination.FILTER_KEYS.COMPANY_NAME)));
		}

	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {

		query.setFetchMode("profile", FetchMode.JOIN).setFetchMode("company", FetchMode.JOIN).createAlias("company", "company");
		count.setFetchMode("company", FetchMode.JOIN).createAlias("company", "company");

		for (Map.Entry<String, Object> e : params.entrySet()) {
			query.add(Restrictions.eq(e.getKey(), e.getValue()));
			count.add(Restrictions.eq(e.getKey(), e.getValue()));
		}

	}

	@Override
	public Map<String, Long> findActiveUserIdsByUserNumbers(Collection<String> userNumbers) {
		Assert.notNull(userNumbers);

		final Map<String, Long> userMap = Maps.newHashMap();

		String sql = "SELECT id, user_number" + " FROM user " + " WHERE user_number IN (:userNumbers)" + " AND user_status_type_code IN (:activeUserStatusTypes)";

		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("userNumbers", userNumbers);
		paramMap.put("activeUserStatusTypes", UserStatusType.ACTIVE_USER_STATUS_TYPES);

		jdbcTemplate.query(sql, paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				userMap.put(rs.getString("user_number"), rs.getLong("id"));
			}
		});

		return userMap;
	}

	public Integer getMaxUserId() {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("MAX(user.id) as userId")
				.addTable("user");
		try {
			return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Long.class).intValue();
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public User findInternalOwnerByWorkId(Long workId) {
		return (User) getFactory().getCurrentSession().createQuery("SELECT user " +
				"FROM work as work " +
				"JOIN work.buyer as user " +
				"WHERE work.id = :workId").setParameter("workId", workId).uniqueResult();
	}

	@Override
	public Map<String, String> getUserNamesByUserNumbers(Set<String> selectedResourcesUserNumbers) {
		Assert.notNull(selectedResourcesUserNumbers);

		final Map<String, String> userMap = Maps.newHashMap();

		String sql = "SELECT first_name, last_name, user_number" + " FROM user " + " WHERE user_number IN (:userNumbers)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userNumbers", selectedResourcesUserNumbers);

		jdbcTemplate.query(sql, params, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				userMap.put(rs.getString("user_number"), StringUtilities.fullName(firstName, lastName));
			}
		});
		return userMap;
	}

	@Override
	public User findByUuid(String uuid) {
		Assert.notNull(uuid);
		return (User) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	@Override
	public List<UserDTO> findUserDTOsByUuids(final List<String> uuids) {
		if (isEmpty(uuids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		final SQLBuilder builder = new SQLBuilder()
			.addColumns("id", "company_id", "first_name", "last_name", "uuid")
			.addTable("user")
			.addWhereClause("uuid IN (:uuids)")
			.addParam("uuids", uuids);

		return jdbcTemplate.query(builder.build(), builder.getParams(), USER_DTO_ROW_MAPPER);
	}

	@Override
	public Set<Long> findAllUserIdsByUuids(Collection<String> uuids) {
		return convertIdentifiers(uuids, "uuid", "id");
	}

	@Override
	public Set<String> findAllUserUuidsByIds(Collection<Long> ids) {
		return convertIdentifiers(ids, "id", "uuid");
	}

	@Override
	public List<UserIdentityDTO> findUserIdentitiesByUuids(Collection<String> uuids) {
		if (isEmpty(uuids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		SQLBuilder builder = new SQLBuilder()
			.addColumns("id", "user_number", "uuid")
			.addTable("user")
			.addWhereClause("uuid IN (:uuids)")
			.addParam("uuids", uuids);

		RowMapper<UserIdentityDTO> mapper = new RowMapper<UserIdentityDTO>() {
			public UserIdentityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new UserIdentityDTO(rs.getLong("id"), rs.getString("user_number"), rs.getString("uuid"));
			}
		};

		return jdbcTemplate.query(builder.build(), builder.getParams(), mapper);
	}

	@Override
	public List<UserIdentityDTO> findUserIdentitiesByIds(final Collection<Long> ids) {
		if (isEmpty(ids)) {
			return Lists.newArrayListWithExpectedSize(0);
		}

		final SQLBuilder builder = new SQLBuilder()
				.addColumns("id", "user_number", "uuid")
				.addTable("user")
				.addWhereClause("id IN (:ids)")
				.addParam("ids", ids);

		final RowMapper<UserIdentityDTO> mapper = new RowMapper<UserIdentityDTO>() {
			public UserIdentityDTO mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				return new UserIdentityDTO(rs.getLong("id"), rs.getString("user_number"), rs.getString("uuid"));
			}
		};

		return jdbcTemplate.query(builder.build(), builder.getParams(), mapper);
	}

	// Default row mapper for User object, feel free to add fields
	public class BasicUserRowMapper implements RowMapper {

		public BasicUserRowMapper() {}

		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("id"));
			user.setUserNumber(rs.getString("user_number"));
			user.setFirstName(rs.getString("first_name"));
			user.setLastName(rs.getString("last_name"));

			// couple of deprecated methods below, but ok, setting object contents, not db
			user.setEmail(rs.getString("email"));
			user.setChangedEmail(rs.getString("changed_email"));
			user.setEmailConfirmed(rs.getBoolean("email_confirmed"));
			user.setUserStatusType(new UserStatusType(rs.getString("user_status_type_code")));
			user.setApiEnabled(rs.getBoolean("api_enabled"));
			return user;
		}
	}
}
