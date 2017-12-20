package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by rahul on 2/5/14
 */
@Repository
public class DeliverableRequirementDAOImpl extends AbstractDAO<DeliverableRequirement> implements DeliverableRequirementDAO {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<DeliverableRequirement> getEntityClass() {
		return DeliverableRequirement.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DeliverableRequirement findDeletedDeliverableRequirementByGroupIdAndType(Long deliverableRequirementGroupId, String type) {
		return (DeliverableRequirement) getFactory().getCurrentSession().createQuery("FROM deliverableRequirement AS dr " +
				"WHERE dr.type.code = :deliverable_type AND dr.deliverableRequirementGroup.id = :deliverable_requirement_group_id AND dr.deleted = true")
				.setParameter("deliverable_type", type)
				.setParameter("deliverable_requirement_group_id", deliverableRequirementGroupId)
				.setMaxResults(1)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliverableRequirement> findAllDeliverableRequirementsByGroupId(Long deliverableRequirementGroupId) {
		return (List<DeliverableRequirement>) getFactory().getCurrentSession().createQuery("FROM deliverableRequirement AS dr " +
				"WHERE dr.deliverableRequirementGroup.id = :deliverable_requirement_group_id")
				.setParameter("deliverable_requirement_group_id", deliverableRequirementGroupId)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findAllDeliverableRequirementIdsByGroupId(Long deliverableRequirementGroupId) {

		SQLBuilder query = new SQLBuilder();
		query
			.addColumns("dr.id")
			.addTable("deliverable_requirement dr")
			.addWhereClause("dr.deliverable_requirement_group_id = :deliverableRequirementGroupId")
			.addWhereClause("dr.deleted = false")
			.addParam("deliverableRequirementGroupId", deliverableRequirementGroupId);

		return jdbcTemplate.queryForList(query.build(), query.getParams(), Long.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findDeliverableRequirementsWithMissingPositionOnAssets() {

		SQLBuilder query = new SQLBuilder();
		query
			.addColumns("deliverable_requirement_id")
			.setDistinct(true)
			.addTable("work_asset_association waa")
			.addWhereClause("deliverable_requirement_id IS NOT NULL")
			.addWhereClause("position IS NULL");

		return jdbcTemplate.queryForList(query.build(), query.getParams(), Long.class);
	}
}
