package com.workmarket.domains.work.dao;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.WorkVendorInvitation;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class WorkVendorInvitationDAOImpl extends DeletableAbstractDAO<WorkVendorInvitation> implements WorkVendorInvitationDAO {
	private static final Logger logger = LoggerFactory.getLogger(WorkVendorInvitationDAOImpl.class);

	@Resource(name = "readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<WorkVendorInvitation> getEntityClass() {
		return WorkVendorInvitation.class;
	}

	@Override
	public boolean hasInvitedAtLeastOneVendor(final Long workId) {
		SQLBuilder builder = new SQLBuilder()
			.addTable("work_to_company_association wva")
			.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
			.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId)
			.addLimitClause(0, 1, true);

		return jdbcTemplate.queryForObject(builder.buildCount("wva.id"), builder.getParams(), Integer.class) > 0;
	}

	@Override
	public List<Long> getDeclinedVendorIdsByWork(final Long workId) {
		SQLBuilder builder = new SQLBuilder()
				.addColumn("company_id")
				.addTable("work_to_company_association wva")
				.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
				.addWhereClause("wva.is_declined", SQLOperator.EQUALS, "is_declined", Boolean.TRUE)
				.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public List<String> getDeclinedVendorNumbersByWork(final Long workId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("c.company_number")
			.addTable("company c")
			.addJoin("INNER JOIN work_to_company_association wva ON c.id = wva.company_id")
			.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
			.addWhereClause("wva.is_declined", SQLOperator.EQUALS, "is_declined", Boolean.TRUE)
			.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<Long> getAssignToFirstToAcceptVendorIdsByWork(final Long workId) {
		SQLBuilder builder = new SQLBuilder()
				.addColumn("company_id")
				.addTable("work_to_company_association wva")
				.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
				.addWhereClause("wva.assign_to_first_resource", SQLOperator.EQUALS, "assign_to_first_to_accept", Boolean.TRUE)
				.addWhereClause("wva.is_declined", SQLOperator.EQUALS, "is_declined", Boolean.FALSE)
				.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public List<Long> getNotDeclinedVendorIdsByWork(final Long workId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("company_id")
			.addTable("work_to_company_association wva")
			.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
			.addWhereClause("wva.is_declined", SQLOperator.EQUALS, "is_declined", Boolean.FALSE)
			.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public List<String> getNotDeclinedVendorNumbersByWork(final Long workId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("c.company_number")
			.addTable("company c")
			.addJoin("INNER JOIN work_to_company_association wva ON c.id = wva.company_id")
			.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
			.addWhereClause("wva.is_declined", SQLOperator.EQUALS, "is_declined", Boolean.FALSE)
			.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<Long> getVendorIdsByWork(final Long workId) {
		SQLBuilder builder = new SQLBuilder()
				.addColumn("company_id")
				.addTable("work_to_company_association wva")
				.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
				.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public List<String> getVendorNumbersByWork(Long workId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("c.company_number")
			.addTable("company c")
			.addJoin("INNER JOIN work_to_company_association wva ON c.id = wva.company_id")
			.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
			.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<Long> getVendorInvitedByGroupIds(final Long workId, final Long companyId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("user_group_id")
			.addTable("work_vendor_invitation_to_group_association wviga")
			.addJoin("INNER JOIN work_to_company_association wva ON wviga.work_vendor_invitation_id = wva.id")
			.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
			.addWhereClause("wva.is_declined", SQLOperator.EQUALS, "is_declined", Boolean.FALSE)
			.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId)
			.addWhereClause("wva.company_id", SQLOperator.EQUALS, "company_id", companyId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	/**
	 * Given an assignment, get all vendors invited by groups, each vendor with a list of group ids.
	 *
	 * @param workId
	 * @return
	 */
	@Override
	public Map<Long, Set<Long>> getVendorInvitationGroupAssociationsByWorkId(final Long workId) {
		final SQLBuilder builder = new SQLBuilder()
			.addColumns("wviga.user_group_id AS user_group_id", "wva.company_id AS vendor_id")
			.addTable("work_vendor_invitation_to_group_association wviga")
			.addJoin("INNER JOIN work_to_company_association wva ON wviga.work_vendor_invitation_id = wva.id")
			.addWhereClause("wva.deleted", SQLOperator.EQUALS, "is_deleted", Boolean.FALSE)
			.addWhereClause("wva.is_declined", SQLOperator.EQUALS, "is_declined", Boolean.FALSE)
			.addWhereClause("wva.work_id", SQLOperator.EQUALS, "work_id", workId);

		final Map<Long, Set<Long>> vendorInvitationGroupAssociations = Maps.newHashMap();
		try {
			List<Map<String, Object>> rows =
				jdbcTemplate.queryForList(builder.build(), builder.getParams());
			for (Map<String, Object> row : rows) {
				final Long userGroupId = ((Integer) row.get("user_group_id")).longValue();
				final Long vendorId = ((Integer) row.get("vendor_id")).longValue();
				if (vendorInvitationGroupAssociations.containsKey(vendorId)) {
					vendorInvitationGroupAssociations.get(vendorId).add(userGroupId);
				} else {
					vendorInvitationGroupAssociations.put(vendorId, Sets.newHashSet(userGroupId));
				}
			}
		} catch (Exception e) {
			logger.error("failed to fetch vendor group association: ", e);
		}
		return vendorInvitationGroupAssociations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkVendorInvitation> getVendorInvitationsByWork(final Long workId) {
		return getFactory().getCurrentSession()
			.createQuery("FROM workVendorInvitation wva WHERE wva.deleted is false AND wva.isDeclined is false AND wva.workId = :workId")
			.setParameter("workId", workId).list();
	}
}
