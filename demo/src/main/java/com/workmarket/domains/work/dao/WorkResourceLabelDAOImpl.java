package com.workmarket.domains.work.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.model.WorkResourceLabel;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class WorkResourceLabelDAOImpl extends AbstractDAO<WorkResourceLabel> implements WorkResourceLabelDAO {

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<WorkResourceLabel> getEntityClass() {
		return WorkResourceLabel.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public WorkResourceLabel findByLabelCodeAndWorkResourceId(String workResourceLabelTypeCode, Long workResourceId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("workResourceLabel.findByWorkResourceLabelAndResourceId")
				.setParameter("workResourceLabelTypeCode", workResourceLabelTypeCode)
				.setParameter("workResourceId", workResourceId);
		return (WorkResourceLabel)query.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkResourceLabel> findByWorkResource(Long workResourceId) {
		return getFactory().getCurrentSession().getNamedQuery("workResourceLabel.findByWorkResourceId")
				.setLong("workResourceId", workResourceId)
				.list();
	}

	@Override
	public Integer countConfirmedWorkResourceLabelByUserId(List<Long> userIds, WorkResourceAggregateFilter filter) {
		Query query;

		// This isn't beautiful but Rocio desired that these remain named queries vs. criteria queries

		if (filter.isScopedToCompany()) {
			if (filter.isSetLessThan24Hours()) {
				query = getFactory().getCurrentSession().getNamedQuery("workResourceLabel.countConfirmedWorkResourceLabelByUserIdAndCompanyAndLessThan24HrsFlag");
				query.setParameter("lessThan24HoursFromAppointmentTime", filter.isLessThan24Hours());
			} else {
				query = getFactory().getCurrentSession().getNamedQuery("workResourceLabel.countConfirmedWorkResourceLabelByUserIdAndCompany");
			}
			query.setParameter("companyId", filter.getCompanyId());
		} else {
			if (filter.isSetLessThan24Hours()) {
				query = getFactory().getCurrentSession().getNamedQuery("workResourceLabel.countConfirmedWorkResourceLabelByUserIdAndLessThan24HrsFlag");
				query.setParameter("lessThan24HoursFromAppointmentTime", filter.isLessThan24Hours());
			} else {
				query = getFactory().getCurrentSession().getNamedQuery("workResourceLabel.countConfirmedWorkResourceLabelByUserId");
			}
		}

		query.setParameterList("userIds", userIds)
			.setParameter("workResourceLabelTypeCode", filter.getResourceLabelTypeCode())
			.setParameter("createdOn", filter.getFromDate());

		return ((Long)query.uniqueResult()).intValue();
	}

	@Override
	public Map<Long, List<WorkResourceLabel>> findVisibleForWork(Long workId) {
		List<WorkResourceLabel> labels = getFactory().getCurrentSession().getNamedQuery("workResourceLabel.findForWork")
				.setLong("workId", workId)
				.list();
		Map<Long,List<WorkResourceLabel>> lookup = Maps.newHashMap();
		for (WorkResourceLabel label : labels) {
			List<WorkResourceLabel> labelList = (List<WorkResourceLabel>) MapUtils.getObject(lookup, label.getWorkResourceUserId(), Lists.<WorkResourceLabel>newArrayList());
			labelList.add(label);
			lookup.put(label.getWorkResourceUserId(), labelList);
		}
		return lookup;
	}

	@Override
	public Map<String, Integer> countAllConfirmedWorkResourceLabelsByUserId(WorkResourceAggregateFilter filter, List<Long> userIds) {
		Map<String, Integer> workResourceLabelMap = Maps.newHashMap();
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("COALESCE(COUNT(work_resource_label.id),0) as count", "work_resource_label_type_code")
				.addTable("work_resource_label")
				.addWhereInClause("work_resource_label.work_resource_user_id ", "userIds", userIds);

		if (filter.isSetFromDate()) {
			builder.addWhereClause("work_resource_label.created_on BETWEEN :fromDate AND :toDate")
					.addParam("fromDate", filter.getFromDate())
					.addParam("toDate", DateUtilities.formatTodayForSQL());
		}

		if (filter.isSetLessThan24Hours()) {
			builder.addWhereClause("work_resource_label.less_than_24_hours_from_appointment = :isLessThan24Hours")
					.addParam("isLessThan24Hours", filter.isLessThan24Hours());
		}

		if (filter.isScopedToCompany()) {
			builder.addWhereClause("work_resource_label.work_company_id = :companyId")
					.addParam("companyId", filter.getCompanyId());
		}

		builder.addWhereClause("work_resource_label.confirmed = true")
				.addWhereClause("work_resource_label.ignored = false")
				.addParam("userId", userIds)
				.addGroupColumns("work_resource_label_type_code");

		try {
			List<Map<String, Object>> labels = jdbcTemplate.queryForList(builder.build(), builder.getParams());
			for (Map<String, Object> row : labels) {
				workResourceLabelMap.put((String)row.get("work_resource_label_type_code"), ((Long)row.get("count")).intValue());
			}
		}
		catch (EmptyResultDataAccessException e){
			return workResourceLabelMap;
		}
		return workResourceLabelMap;
	}

	@Override
	public Map<Long, List<WorkResourceLabel>> findConfirmedForUserByCompanyInWork(Long userId, Long viewingUserId, Long viewingCompanyId, Collection<Long> workIds) {

		if (CollectionUtils.isEmpty(workIds)) return Collections.emptyMap();

		Criteria query = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("workId", workIds))
				.add(Restrictions.eq("workResourceUserId", userId))
				.add(Restrictions.eq("confirmed", true))
				.add(Restrictions.eq("ignored", false))
				.add(Restrictions.eq("workResourceLabelType.visible", true));

		if (!userId.equals(viewingUserId)) {
			query.add(Restrictions.eq("workCompanyId", viewingCompanyId));
		}

		HibernateUtilities.addJoins(query, Criteria.INNER_JOIN, "workResourceLabelType");

		List<WorkResourceLabel> labels = query.list();

		Map<Long,List<WorkResourceLabel>> lookup = Maps.newHashMap();

		for (WorkResourceLabel l : labels) {
			Long key = l.getWorkId();
			if (!lookup.containsKey(key))
				lookup.put(key, Lists.<WorkResourceLabel>newArrayList());
			lookup.get(key).add(l);
		}

		return lookup;
	}
}
