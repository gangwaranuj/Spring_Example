package com.workmarket.dao.request;

import com.google.common.collect.ImmutableList;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.request.AssessmentInvitation;
import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.domains.model.request.Request;
import com.workmarket.domains.model.request.RequestPagination;
import com.workmarket.domains.model.request.RequestStatusType;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.model.request.UserGroupInvitationPagination;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;


@SuppressWarnings("unchecked")
@Repository
public class RequestDAOImpl extends AbstractDAO<Request> implements RequestDAO {

	static final String INVITED_GROUPS_COUNT = " SELECT COUNT(DISTINCT g.id) \n" +
			" FROM user u \n" +
			" INNER JOIN request r ON u.id = r.invitee_user_id \n" +
			" INNER JOIN request_group_invitation rgi ON r.id = rgi.id \n" +
			" INNER JOIN user_group g on g.id = rgi.user_group_id \n" +
			" WHERE r.deleted = 0 \n" +
			" AND r.request_status_type_code = :sent \n" +
			" AND g.deleted = 0 \n" +
			" AND g.active_flag = 1 \n" +
			" AND u.id = :userId \n" +
			" AND NOT EXISTS ( \n" +
				" SELECT id \n" +
				" FROM user_user_group_association \n" +
				" WHERE approval_status IN (0,1,2) \n" +
				" AND deleted = false \n" +
				" AND verification_status IN (0,1,2) \n" +
				" AND user_id = u.id \n" +
				" AND user_group_id = rgi.user_group_id \n" +
			") \n";

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Request> getEntityClass() {
		return Request.class;
	}

	public List<Request> findByRequestor(Long userId) {
		return getFactory().getCurrentSession().getNamedQuery("request.byRequestor")
				.setLong("requestor_user_id", userId)
				.list();
	}

	public List<Request> findByInvitedUser(Long userId) {
		return getFactory().getCurrentSession().getNamedQuery("request.byInvitedUser")
				.setLong("invited_user_id", userId)
				.list();
	}

	public List<Request> findByInvitation(Long invitationId) {
		return getFactory().getCurrentSession().getNamedQuery("request.byInvitation")
				.setLong("invitation_id", invitationId)
				.list();
	}

	public List<AssessmentInvitation> findAssessmentInvitationsByAssessment(Long assessmentId) {
		return getFactory().getCurrentSession().getNamedQuery("request.assessmentInvitationByAssessment")
				.setLong("assessment_id", assessmentId)
				.list();
	}

	public RequestPagination findAssessmentInvitationsByAssessment(Long assessmentId, RequestPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.setFetchMode("invitedUser", FetchMode.JOIN)
				.setFetchMode("invitedUser.company", FetchMode.JOIN)
				.setFetchMode("invitedUser.profile", FetchMode.JOIN)
				.setFetchMode("invitedUser.profile.address", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.rowCount());

		criteria
				.add(Restrictions.eq("assessment.id", assessmentId))
				.add(Restrictions.eq("requestStatusType.code", "sent"))
				.add(Restrictions.eq("deleted", Boolean.FALSE));
		count
				.add(Restrictions.eq("assessment.id", assessmentId))
				.add(Restrictions.eq("requestStatusType.code", "sent"))
				.add(Restrictions.eq("deleted", Boolean.FALSE));

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("requestDate"));
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	public List<AssessmentInvitation> findAssessmentInvitationsByInvitedUserAndAsseessment(Long userId, Long assessmentId) {
		return getFactory().getCurrentSession().getNamedQuery("request.assessmentInvitationByInvitedUserAndAssessment")
				.setLong("invited_user_id", userId)
				.setLong("assessment_id", assessmentId)
				.list();
	}

	@Override
	public int countUserGroupInvitationsByInvitedUser(Long userId) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);
		params.addValue("sent", RequestStatusType.SENT);
		return jdbcTemplate.queryForObject(INVITED_GROUPS_COUNT, params, Integer.class);
	}

	@Override
	public void deleteUsersInvitationsToAllCompanyGroups(Long userId, Long companyId) {
		String sql =
			" UPDATE    request r \n" +
			" INNER     JOIN user u  ON u.id = r.requestor_user_id \n" +
			" INNER     JOIN request_group_invitation rgi ON r.id = rgi.id \n" +
			" SET       r.deleted = true \n" +
			" WHERE     r.deleted = false \n" +
			" AND       r.invitee_user_id = :userId \n" +
			" AND       u.company_id = :companyId \n";

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
		sqlParameterSource.addValue("userId", userId);
		sqlParameterSource.addValue("companyId", companyId);

		jdbcTemplate.update(sql, sqlParameterSource);
	}

	public List<UserGroupInvitation> findUserGroupInvitationsByUser(Long userId) {
		return getFactory().getCurrentSession().getNamedQuery("request.groupInvitationByInvitedUser")
				.setLong("invited_user_id", userId)
				.list();
	}

	public List<UserGroupInvitation> findUserGroupInvitationsByUserGroup(Long groupId) {
		return getFactory().getCurrentSession().getNamedQuery("request.groupInvitationByUserGroup")
				.setLong("user_group_id", groupId)
				.list();
	}

	public List<UserGroupInvitation> findUserGroupInvitationsByInvitedUserAndUserGroup(Long userId, Long groupId) {
		return getFactory().getCurrentSession().getNamedQuery("request.groupInvitationByInvitedUserAndUserGroup")
				.setLong("invited_user_id", userId)
				.setLong("user_group_id", groupId)
				.list();
	}


	@Override
	public UserGroupInvitation findLatestUserGroupInvitationByInvitedUserAndUserGroup(Long userId, Long groupId) {
		return (UserGroupInvitation) getFactory().getCurrentSession().createCriteria(UserGroupInvitation.class)
				.add(Restrictions.eq("invitedUser.id", userId))
				.add(Restrictions.eq("userGroup.id", groupId))
				.add(Restrictions.eq("requestStatusType.code", "sent"))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("id"))
				.setFetchMode("requestor", FetchMode.JOIN)
				.setMaxResults(1)
				.uniqueResult();
	}


	@Override
	public List<UserGroupInvitation> findUserGroupInvitationsByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId) {
		return getFactory().getCurrentSession().getNamedQuery("request.groupInvitationByInvitedUsersAndUserGroup")
				.setParameterList("invited_user_ids", userIds)
				.setLong("user_group_id", groupId)
				.list();
	}

	@Override
	public List<UserGroupInvitation> findUserGroupInvitationsAllStatusByInvitedUsersAndUserGroup(List<Long> userIds, Long groupId) {
		return getFactory().getCurrentSession().getNamedQuery("request.allGroupInvitationsByInvitedUsersAndUserGroup")
				.setParameterList("invited_user_ids", userIds)
				.setLong("user_group_id", groupId)
				.list();
	}

	@Override
	public UserGroupInvitationPagination findUserGroupInvitationsForInvitedUser(Long userId, UserGroupInvitationPagination pagination) {

		DetachedCriteria subquery = DetachedCriteria.forClass(UserUserGroupAssociation.class)
				.add(Restrictions.in("approvalStatus", ImmutableList.of(
						ApprovalStatus.PENDING,
						ApprovalStatus.APPROVED,
						ApprovalStatus.DECLINED)))
				.add(Restrictions.in("verificationStatus", ImmutableList.of(
						VerificationStatus.PENDING,
						VerificationStatus.VERIFIED,
						VerificationStatus.FAILED)))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("user.id", userId))
				.setProjection(Projections.distinct(Projections.property("userGroup.id")));

		Criteria criteria = getFactory().getCurrentSession().createCriteria(UserGroupInvitation.class)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.createAlias("userGroup", "ug")
				.createAlias("requestor", "r")
				.createAlias("requestor.company", "rc")
				.setFetchMode("invitedUser", FetchMode.JOIN)
				.setFetchMode("r", FetchMode.JOIN)
				.setFetchMode("rc", FetchMode.JOIN)
				.add(Restrictions.eq("invitedUser.id", userId))
				.add(Restrictions.eq("requestStatusType.code", "sent"))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("ug.deleted", Boolean.FALSE))
				.add(Restrictions.eq("ug.activeFlag", Boolean.TRUE))
				.add(Restrictions.in("invitationType", ImmutableList.of(
						UserGroupInvitationType.NEW,
						UserGroupInvitationType.RECOMMENDATION)))
				.add(Subqueries.propertyNotIn("userGroup.id", subquery));

		Criteria distinctCount = getFactory().getCurrentSession().createCriteria(UserGroupInvitation.class)
				.setProjection(Projections.rowCount())
				.setProjection(Projections.distinct(Projections.property("ug.id")))
				.createAlias("userGroup", "ug")
				.createAlias("requestor", "r")
				.createAlias("requestor.company", "rc")
				.setFetchMode("invitedUser", FetchMode.JOIN)
				.setFetchMode("r", FetchMode.JOIN)
				.setFetchMode("rc", FetchMode.JOIN)
				.add(Restrictions.eq("invitedUser.id", userId))
				.add(Restrictions.eq("requestStatusType.code", "sent"))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.eq("ug.deleted", Boolean.FALSE))
				.add(Restrictions.eq("ug.activeFlag", Boolean.TRUE))
				.add(Restrictions.in("invitationType", ImmutableList.of(
						UserGroupInvitationType.NEW,
						UserGroupInvitationType.RECOMMENDATION)))
				.add(Subqueries.propertyNotIn("userGroup.id", subquery));

		String sort = "requestDate";
		if (UserGroupInvitationPagination.SORTS.INVITED_ON.toString().equalsIgnoreCase(pagination.getSortColumn()))
			sort = "createdOn";
		else if (UserGroupInvitationPagination.SORTS.GROUP_NAME.toString().equalsIgnoreCase(pagination.getSortColumn()))
			sort = "ug.name";

		criteria.addOrder(pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC) ?
				Order.desc(sort) :
				Order.asc(sort));

		pagination.setResults(criteria.list());
		if (distinctCount.list().size() > 0)
			pagination.setRowCount(distinctCount.list().size());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public PasswordResetRequest findLatestSentPasswordResetRequestByInvitedUser(Long userId) {
		Assert.notNull(userId);
		return (PasswordResetRequest) getFactory().getCurrentSession().getNamedQuery("request.latestSentPasswordResetRequestByInvitedUser")
				.setLong("invited_user_id", userId)
				.setMaxResults(1)
				.uniqueResult();
	}

}
