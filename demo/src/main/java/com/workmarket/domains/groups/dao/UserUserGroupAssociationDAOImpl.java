package com.workmarket.domains.groups.dao;

import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.groups.model.UserUserGroupAssociationPagination;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Repository
public class UserUserGroupAssociationDAOImpl extends PaginationAbstractDAO<UserUserGroupAssociation> implements UserUserGroupAssociationDAO {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<UserUserGroupAssociation> getEntityClass() {
		return UserUserGroupAssociation.class;
	}

	@Override
	public UserUserGroupAssociation findUserUserGroupAssociationById(long id) {
		return (UserUserGroupAssociation)getFactory().getCurrentSession().createCriteria(UserUserGroupAssociation.class)
				.createAlias("userGroup", "userGroup", CriteriaSpecification.INNER_JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("userGroup.creator", FetchMode.JOIN)
				.add(Restrictions.eq("id", id)).uniqueResult();
	}

	@Override
	@SuppressWarnings("JpaQueryApiInspection")
	public UserUserGroupAssociation findUserUserGroupAssociationByUserGroupIdAndUserId(Long userGroupId, Long userId) {
		List<UserUserGroupAssociation> list = findUserUserGroupAssociationByUserGroupIdAndUserId(userGroupId, Lists.newArrayList(userId));
		if (list != null) {
			return CollectionUtilities.first(list);
		}
		return null;
	}

	@Override
	public List<UserUserGroupAssociation> findUserUserGroupAssociationByUserGroupIdAndUserId(Long userGroupId, List<Long> userIds) {
		Assert.notNull(userGroupId);
		Assert.notNull(userIds);
		return getFactory().getCurrentSession().getNamedQuery("userUserGroupAssociation.findUserUserGroupAssociationByUserGroupIdAndUserId")
				.setParameter("userGroupId", userGroupId)
				.setParameterList("userIds", userIds).list();
	}

	@Override
	public UserUserGroupAssociationPagination findAllUserUserAssociationsByUserGroupIdAndVerificationStatusAndApprovalStatus(final long userGroupId, VerificationStatus verificationStatus, ApprovalStatus approvalStatus, UserUserGroupAssociationPagination pagination) {
		Assert.notNull(userGroupId);
		Assert.notNull(pagination);

		Map<String, Object> params = Maps.newHashMap();
		params.putAll(
				ImmutableMap.of("userGroup.id", userGroupId, "deleted", false)
		);

		if (approvalStatus != null)
			params.put("approvalStatus", approvalStatus);
		if (verificationStatus != null)
			params.put("verificationStatus", verificationStatus);

		return (UserUserGroupAssociationPagination) super.paginationQuery(pagination, params);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findAllUsersOfGroup(long groupId) {
		Assert.notNull(groupId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("user", "user", CriteriaSpecification.INNER_JOIN)
				.createAlias("user.profile", "profile", CriteriaSpecification.INNER_JOIN)
				.setFetchMode("user.company", FetchMode.JOIN)
				.setFetchMode("profile.address", FetchMode.JOIN)
				.setProjection(Projections.property("user"))
				.add(Restrictions.eq("userGroup.id", groupId))
				.add(Restrictions.eq("deleted", false));

		return criteria.list();
	}

	@Override
	@SuppressWarnings({"unchecked", "JpaQueryApiInspection"})
	public List<UserUserGroupAssociation> findAllUserUserGroupAssociationsByGroupIdAndUsers(Long userGroupId, List<Long> invitedUserIds){
		Assert.notNull(userGroupId);
		Assert.notEmpty(invitedUserIds);
		return getFactory().getCurrentSession().getNamedQuery("userUserGroupAssociation.findAllAssociationsByGroupIdAndUsers")
				.setLong("userGroupId", userGroupId)
				.setParameterList("userIds", invitedUserIds)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllUserIdsOfGroup(long groupId) {
		Assert.notNull(groupId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(User.class)
			.setProjection(Projections.property("id"))
			.createAlias("userGroupAssociations", "uga")
			.add(Restrictions.eq("uga.userGroup.id", groupId))
			.add(Restrictions.eq("uga.approvalStatus", ApprovalStatus.APPROVED))
			.add(Restrictions.eq("uga.deleted", false));

		return criteria.list();
	}

	@Override
	public UserPagination findAllUserOfGroup(long groupId, UserPagination pagination) {
		Assert.notNull(groupId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(User.class)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.createAlias("userGroupAssociations", "uga")
				.setFetchMode("avatarSmall", FetchMode.JOIN)
				.setFetchMode("profile", FetchMode.JOIN)
				.setFetchMode("profile.address", FetchMode.JOIN)
				.setFetchMode("company", FetchMode.JOIN);

		Criteria count = getFactory().getCurrentSession().createCriteria(User.class)
				.setProjection(Projections.rowCount())
				.createAlias("userGroupAssociations", "uga")
				.setFetchMode("company", FetchMode.JOIN);

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("lastName"));
		}

		criteria
				.add(Restrictions.eq("uga.userGroup.id", groupId))
				.add(Restrictions.eq("uga.approvalStatus", ApprovalStatus.APPROVED))
				.add(Restrictions.eq("uga.deleted", false));
		count
				.add(Restrictions.eq("uga.userGroup.id", groupId))
				.add(Restrictions.eq("uga.approvalStatus", ApprovalStatus.APPROVED))
				.add(Restrictions.eq("uga.deleted", false));

		//noinspection unchecked
		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserUserGroupAssociation> findAllActiveAssociations(final long userGroupId) {
		Assert.notNull(userGroupId);

		return getFactory().getCurrentSession().createQuery("select e from userUserGroupAssociation e where e.userGroup.id = :userGroupId and e.deleted = 0")
				.setParameter("userGroupId", userGroupId)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserUserGroupAssociation> findAllActiveAssociationsByUserIdAndCompanyId(Long userId, Long companyId) {
		return getFactory().getCurrentSession()
				.createQuery(
					" select e from userUserGroupAssociation e " +
					" where e.user.id = :userId " +
					" and e.deleted = 0 " +
					" and e.userGroup.company.id = :companyId ")
				.setParameter("userId", userId)
				.setParameter("companyId", companyId)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationIdsBetweenCompanies(Long companyId1, Long companyId2) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("userGroup", "group", Criteria.INNER_JOIN)
				.createAlias("user", "user", Criteria.INNER_JOIN)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("group.deleted", false))
				.add(Restrictions.eq("group.activeFlag", true))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.APPROVED))
				.add(Restrictions.or(
						Restrictions.and(Restrictions.eq("group.company.id", companyId1), Restrictions.eq("user.company.id", companyId2)),
						Restrictions.and(Restrictions.eq("group.company.id", companyId2), Restrictions.eq("user.company.id", companyId1))))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setProjection(Projections.property("id"));

		return criteria.list();
	}

	@Override
	public Integer removeAssociations(List<Long> ids) {
		if (ids.isEmpty()) {
			return 0;
		}

		Query query = getFactory().getCurrentSession()
				.createQuery(
						"update userUserGroupAssociation set deleted = 1, approvalStatus = 0, verificationStatus = 0 where id IN (:ids)"
				)
				.setParameterList("ids", ids);

		return query.executeUpdate();
	}

	@Override
	@SuppressWarnings({"unchecked", "JpaQueryApiInspection"})
	public List<UserUserGroupAssociation> findCompanyOwnedGroupAssociationsHavingUserAsMember(Long companyId, Long userId) {
		return	getFactory().getCurrentSession()
				.getNamedQuery("userUserGroupAssociation.findCompanyOwnedGroupsHavingUserAsMember")
				.setParameter("companyId", companyId)
				.setParameter("userId", userId)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserUserGroupAssociation> findAllAssociationsByGroupAndUserIds(List<Long> userIds, Long groupId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("userGroup.id", groupId))
				.add(Restrictions.in("user.id", userIds))
				.list();
	}

	@Override
	public void applySorts(Pagination<UserUserGroupAssociation> pagination, Criteria query, Criteria count) {
		String sort = "user.lastName";

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			query.addOrder(Order.desc(sort));
		} else {
			query.addOrder(Order.asc(sort));
		}
	}

	@Override
	public void applyFilters(Pagination<UserUserGroupAssociation> pagination, Criteria criteria, Criteria count) {
		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(UserUserGroupAssociationPagination.FILTER_KEYS.INVITED.name()) != null) {
				Boolean invited = Boolean.valueOf(pagination.getFilters().get(UserUserGroupAssociationPagination.FILTER_KEYS.INVITED.name()));
				criteria.add(Restrictions.gt("invitedFlag", invited));
				count.add(Restrictions.gt("invitedFlag", invited));
			}
		}
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {

		query
				.setFetchMode("userGroup", FetchMode.JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("user.avatarSmall", FetchMode.JOIN)
				.setFetchMode("user.profile", FetchMode.JOIN)
				.setFetchMode("user.profile.address", FetchMode.JOIN)
				.createAlias("user", "user");

		for (Map.Entry<String, Object> e : params.entrySet()) {
			query.add(Restrictions.eq(e.getKey(), e.getValue()));
			count.add(Restrictions.eq(e.getKey(), e.getValue()));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithLocationRequirements(long userId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN travel_distance_requirement tdr on tdr.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithCertification(long userId, long certificationId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("LEFT JOIN certification_requirement cr on cr.id = r.id")
				.addWhereClause("cr.certification_id = :certificationId");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "certificationId", certificationId, "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithLicense(long userId, long licenseId, ApprovalStatus approvalStatus) {
			SQLBuilder builder =
				findAllAssociationsBuilder()
					.addJoin("LEFT JOIN license_requirement lr on lr.id = r.id")
					.addWhereClause("lr.license_id = :licenseId");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "licenseId", licenseId, "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithIndustry(long userId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN industry_requirement ir on ir.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithInsurance(long userId, long insuranceId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("LEFT JOIN insurance_requirement ir on ir.id = r.id")
				.addWhereClause("ir.insurance_id = :insuranceId");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "insuranceId", insuranceId, "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithDrugTest(long userId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN drug_test_requirement dtr on dtr.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithBackgroundCheck(long userId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN background_check_requirement bcr on bcr.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "userId", userId),
			Long.class);
	}

	@Override
	public Long findUserGroupAssociationByUserIdAndGroupId(long userId, long groupId, ApprovalStatus approvalStatus) {
		return (Long)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("userGroup", "userGroup", Criteria.INNER_JOIN)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("userGroup.id", groupId))
				.add(Restrictions.eq("userGroup.deleted", false))
				.add(Restrictions.eq("userGroup.activeFlag", true))
				.add(Restrictions.eq("approvalStatus", approvalStatus))
				.add(Restrictions.eq("user.id", userId)).setMaxResults(1)
				.setProjection(Projections.id())
				.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithAssessment(long userId, long assessmentId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN test_requirement tr on tr.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "testId", assessmentId, "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithLaneRequirement(long userId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN resource_type_requirement rtr on rtr.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithWorkingHours(long userId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN availability_requirement ar on ar.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "userId", userId),
			Long.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findAllAssociationsWithRating(long userId, ApprovalStatus approvalStatus) {
		SQLBuilder builder =
			findAllAssociationsBuilder()
				.addJoin("JOIN rating_requirement rr on rr.id = r.id");
		return jdbcTemplate.queryForList(
			builder.build(),
			ImmutableMap.of("approvalStatus", approvalStatus.getCode(), "userId", userId),
			Long.class);
	}

	@Override
	public List<Long> findAllUserGroupAssociationsByUserIdAndApprovalStatus(long userId, ApprovalStatus approvalStatus) {
		return getFactory().getCurrentSession()
			.createQuery("select e.userGroup.id from userUserGroupAssociation e where e.user.id = :userId and e.deleted = 0 and e.approvalStatus = :approvalStatus")
			.setParameter("userId", userId)
			.setParameter("approvalStatus", approvalStatus)
			.list();
	}

	private SQLBuilder findAllAssociationsBuilder() {
		return new SQLBuilder()
			.addTable("user_user_group_association uuga")
			.addColumn("distinct(uuga.id)")
			.addJoin("LEFT JOIN user_group ug on uuga.user_group_id = ug.id")
			.addJoin("LEFT JOIN user_group_requirement_set_association ugrs on ugrs.user_group_id = ug.id")
			.addJoin("LEFT JOIN requirement_set rs on rs.id = ugrs.requirement_set_id")
			.addJoin("LEFT JOIN requirement r on r.requirement_set_id = rs.id")
			.addWhereClause("uuga.deleted = 0")
			.addWhereClause("ug.deleted = false")
			.addWhereClause("ug.active_flag = true")
			.addWhereClause("uuga.approval_status = :approvalStatus")
			.addWhereClause("uuga.user_id = :userId");
	}
}
