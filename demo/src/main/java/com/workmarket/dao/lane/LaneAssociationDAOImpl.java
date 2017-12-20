package com.workmarket.dao.lane;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserLaneRelationship;
import com.workmarket.domains.model.UserLaneRelationshipPagination;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
@Repository
public class LaneAssociationDAOImpl extends AbstractDAO<LaneAssociation> implements LaneAssociationDAO {

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	private AuthenticationService authenticationService;

	@Override
	protected Class<LaneAssociation> getEntityClass() {
		return LaneAssociation.class;
	}

	@Override
	public LaneAssociation addUserToLane(User user, Company company, LaneType laneType) {
		Assert.notNull(user);
		Assert.notNull(company);

		//Look for a non-deleted association
		LaneAssociation activeAssociation = findActiveAssociationByUserIdAndCompanyId(user.getId(), company.getId());

		if (activeAssociation != null) {
			if (laneType.equals(activeAssociation.getLaneType())) {
				return activeAssociation;
			}
			throw new IllegalArgumentException("Adding user to lane " + laneType + " while he is already in lane " + activeAssociation.getLaneType()
				+ " is not supported userId: " + user.getId() + " companyId: " + company.getId());
		}

		if (laneType.equals(LaneType.LANE_3) && !authenticationService.isLane3Active(user)) {
			throw new IllegalArgumentException("Adding an unauthorized user [" + user.getId() + "] to lane 3 is not supported.");
		}

		LaneAssociation laneAssociationSpecificType = findAssociationByUserIdAndCompanyId(user.getId(), company.getId(), laneType);
		if (laneAssociationSpecificType == null) {
			laneAssociationSpecificType = new LaneAssociation(user, company, laneType);
		}

		laneAssociationSpecificType.setDeleted(false);
		saveOrUpdate(laneAssociationSpecificType);
		return laneAssociationSpecificType;
	}

	// TODO: Move this out of here
	@Override
	public UserLaneRelationshipPagination findAllUserLaneRelationships(Long userId, UserLaneRelationshipPagination pagination) {
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT  c.id as companyId, ")
				.append(" 	     c.effective_name AS companyName, ")
				.append(" 		 lane.lane_type_id AS laneType, ")
				.append(" 	 	 lane.approval_status AS approvalStatus, ")
				// get the last assignment date for this company
				.append(" 	(SELECT MAX(w.schedule_from) FROM work w")
				.append(" 	INNER 	JOIN work_resource wr")
				.append(" 	ON 		w.id = wr.work_id ")
				.append(" 	INNER	JOIN user u ")
				.append(" 	ON 		u.id = wr.user_id ")
				.append(" 	LEFT 	JOIN company co ON co.id = w.company_id ")
				.append(" 	WHERE 	co.id = c.id AND u.id = lane.user_id) AS lastAssignmentDate, ")
				// get the last assignment id for this company
				.append(" 	(SELECT MAX(w.id) FROM work w ")
				.append(" 	INNER 	JOIN work_resource wr")
				.append(" 	ON 		w.id = wr.work_id ")
				.append("   INNER 	JOIN user u ")
				.append("   ON 		u.id = wr.user_id")
				.append(" 	LEFT 	JOIN company co ON co.id = w.company_id")
				.append(" 	WHERE 	co.id = c.id AND u.id = lane.user_id) as lastAssignmentId,")
				// get total assignments with this company
				.append(" 	(SELECT COUNT(w.id) FROM work w")
				.append(" 	INNER 	JOIN work_resource wr")
				.append(" 	ON 		w.id = wr.work_id ")
				.append("   INNER 	JOIN user u ")
				.append("   ON 		u.id = wr.user_id")
				.append(" 	LEFT 	JOIN company co ON co.id = w.company_id")
				.append(" 	WHERE 	co.id = c.id AND u.id = lane.user_id) as totalAssignments")
				.append(" 	FROM 	company c")
				.append(" 	INNER   JOIN lane_association lane")
				.append(" 	ON 		c.id = lane.company_id ")
				.append("   INNER   join user u ")
				.append(" 	ON 		c.created_by = u.id ")
				.append(" 	WHERE   lane.user_id = :userId")
				.append(" 	AND     lane.deleted = 0 ")
				.append(" 	AND     lane.approval_status = 1 ")
				.append(" 	AND     lane.verification_status = 1 ")
				.append(" 	AND     lane.lane_type_id IN (2,3) ");
				

		String sort = " ORDER BY companyName ";

		if (pagination.getSortColumn() != null) {

			if (pagination.getSortColumn().equals(UserLaneRelationshipPagination.SORTS.TOTAL_ASSIGNMENTS)) {
				sort = " ORDER BY totalAssignments ";
			}
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			sort = sort + " DESC ";
		} else {
			sort = sort + " ASC ";
		}

		Map<String, Object> params = Maps.newHashMap();
		params.put("userId", userId);
		params.put("lane2", LaneType.LANE_2.ordinal());
		params.put("lane3", LaneType.LANE_3.ordinal());

		RowMapper<UserLaneRelationship> mapper = new RowMapper<UserLaneRelationship>() {
			@Override
			public UserLaneRelationship mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserLaneRelationship laneRelationship = new UserLaneRelationship();

				laneRelationship.setCompanyId(rs.getLong("companyId"));
				laneRelationship.setCompanyName(rs.getString("companyName"));
				laneRelationship.setLaneType(rs.getInt("laneType"));
				if (rs.getTimestamp("lastAssignmentDate") != null)
					laneRelationship.setLastAssignmentDate(DateUtilities.getCalendarFromDate(rs.getTimestamp("lastAssignmentDate")));
				laneRelationship.setLastAssignmentId(rs.getLong("lastAssignmentId"));
				laneRelationship.setTotalAssignments(rs.getInt("totalAssignments"));
				laneRelationship.setApprovalStatus(rs.getInt("approvalStatus"));

				return laneRelationship;
			}
		};

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass()).setProjection(Projections.rowCount())
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.or(Restrictions.eq("laneType", LaneType.LANE_2), Restrictions.eq("laneType", LaneType.LANE_3)))
				.add(Restrictions.eq("deleted", false)).add(Restrictions.eq("approvalStatus", ApprovalStatus.APPROVED))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		Integer limit = (pagination.getResultsLimit() == null ? Pagination.MAX_ROWS : pagination.getResultsLimit());

		sql.append(sort);
		sql.append(" LIMIT ").append(pagination.getStartRow()).append(", ").append(limit);

		// sql.append(sort);

		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		pagination.setResults(jdbcTemplate.query(sql.toString(), params, mapper));
		return pagination;
	}

	@Override
	public LaneAssociation findActiveAssociationByUserIdAndCompanyId(Long userId, Long companyId) {
		return (LaneAssociation) getFactory()
				.getCurrentSession()
				.createQuery(
						"select e from laneAssociation e " + " where e.company.id = :companyId and e.user.id = :userId and deleted = 0")
				.setParameter("companyId", companyId).setParameter("userId", userId).setMaxResults(1).uniqueResult();
	}

	@Override
	public LaneAssociation findAssociationByUserIdAndCompanyId(Long contractorUserId, Long companyId, LaneType type) {
		return (LaneAssociation) getFactory()
				.getCurrentSession()
				.createQuery(
						"select e from laneAssociation e where "
								+ " e.company.id = :companyId and e.user.id = :userId and laneType = :type")
				.setParameter("companyId", companyId).setParameter("userId", contractorUserId).setParameter("type", type).setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public List<LaneAssociation> findAllAssociationsWithApprovalStatus(ApprovalStatus approvalStatus) {
		Assert.notNull(approvalStatus);
		return getFactory().getCurrentSession().getNamedQuery("laneAssociation.findAllAssociationsWithApprovalStatus")
				.setParameter("approvalStatus", approvalStatus).list();
	}

	@Override
	public List<LaneAssociation> findAllAssociationsWhereUserIdIn(Long companyId, Set<Long> userIds) {
		Assert.notNull(companyId);
		Assert.notNull(userIds);

		if (userIds.size() == 0) {
			return Lists.newArrayList();
		}

		return getFactory().getCurrentSession().getNamedQuery("laneAssociation.findAllAssociationsWhereUserIdIn")
				.setParameter("companyId", companyId)
				.setParameterList("userIds", userIds).list();
	}

	@Override
	public List<Long> findAllCompaniesWhereUserIsResource(Long userId, LaneType laneType) {
		Assert.notNull(userId);
		Assert.notNull(laneType);
		return getFactory().getCurrentSession().createCriteria(getEntityClass()).add(Restrictions.eq("laneType", laneType))
				.add(Restrictions.eq("user.id", userId)).add(Restrictions.eq("deleted", false))
				.add(Restrictions.ne("approvalStatus", ApprovalStatus.DECLINED)).setProjection(Projections.property("company.id")).list();

	}

}