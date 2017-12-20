package com.workmarket.domains.work.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorRoutingStrategy;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
public class RoutingStrategyDAOImpl extends AbstractDAO<AbstractRoutingStrategy> implements RoutingStrategyDAO {
	
	protected Class<AbstractRoutingStrategy> getEntityClass() {
		return AbstractRoutingStrategy.class;
	}

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<Long> findAllScheduled() {

		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("deliveryStatus.code", DeliveryStatusType.SCHEDULED))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.le("createdOn", DateUtilities.addMinutes(Calendar.getInstance(), -1)))
				.setProjection(Projections.property("id")).list();
	}

	@Override
	public List<Long> findAllScheduledWithoutGroup() {

		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("deliveryStatus.code", DeliveryStatusType.SCHEDULED))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.isNull("routingStrategyGroup.id"))
				.add(Restrictions.le("createdOn", DateUtilities.addMinutes(Calendar.getInstance(), -1)))
				.setProjection(Projections.property("id")).list();
	}

	@Override
	public List<Long> findAllScheduledByGroup(long routingStrategyGroupId) {

		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("deliveryStatus.code", DeliveryStatusType.SCHEDULED))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("routingStrategyGroup.id", routingStrategyGroupId))
				.add(Restrictions.le("createdOn", DateUtilities.addMinutes(Calendar.getInstance(), -1)))
				.setProjection(Projections.property("id")).list();
	}

	@Override
	public List<Long> findAllGroupsRoutedByWork(long workId) {
		return getFactory().getCurrentSession().createCriteria(GroupRoutingStrategy.class)
				.add(Restrictions.eq("deliveryStatus.code", DeliveryStatusType.SENT))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("work.id", workId))
				.setProjection(Projections.property("userGroups")).list();
	}

	@Override
	public Map<Long, List<Long>> findAllGroupsRoutedByWork(List<Long> workIds) {
		if (isEmpty(workIds)) {
			return Collections.emptyMap();
		}

		Map<Long, List<Long>> routedGroupsMap = Maps.newHashMapWithExpectedSize(workIds.size());
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("work_id", "user_group_id")
				.addTable("group_routing_strategy_association")
				.addJoin("INNER JOIN routing_strategy on group_routing_strategy_association.routing_strategy_id = routing_strategy.id")
				.addWhereClause("routing_strategy.deleted = false")
				.addWhereClause("routing_strategy.delivery_status_type_code = :sent ")
				.addWhereClause("work_id IN (:workIds)")
				.addParam("sent", DeliveryStatusType.SENT)
				.addParam("workIds", workIds);

		List<Map<String, Object>> groups = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : groups) {
			Long workId = ((Integer) row.get("work_id")).longValue();
			Long groupId = ((Integer) row.get("user_group_id")).longValue();
			if (!routedGroupsMap.containsKey(workId)) {
				List<Long> list = Lists.newArrayList();
				routedGroupsMap.put(workId, list);
			}
			routedGroupsMap.get(workId).add(groupId);
		}
		return routedGroupsMap;
	}

	@Override
	public List<UserRoutingStrategy> findUserRoutingStrategiesByGroup(long routingSrategyGroupId) {
		List<UserRoutingStrategy> userRoutingStrategies = getFactory().getCurrentSession().createCriteria(getEntityClass(), "routingStrategy")
				.add(Restrictions.eq("routingStrategyGroup.id", routingSrategyGroupId))
				.add(Restrictions.eq("class", "user"))
				.add(Restrictions.eq("deliveryStatus.code", DeliveryStatusType.SCHEDULED))
				.add(Restrictions.eq("deleted", false))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return userRoutingStrategies;
	}

	@Override
	public List<GroupRoutingStrategy> findGroupRoutingStrategiesByGroup(long routingSrategyGroupId) {
		List<GroupRoutingStrategy> groupRoutingStrategies = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("routingStrategyGroup.id", routingSrategyGroupId))
			.add(Restrictions.eq("class", "group"))
			.add(Restrictions.eq("deliveryStatus.code", DeliveryStatusType.SCHEDULED))
			.add(Restrictions.eq("deleted", false))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return groupRoutingStrategies;
	}

	@Override
	public List<VendorRoutingStrategy> findVendorRoutingStrategiesByGroup(long routingSrategyGroupId) {
		List<VendorRoutingStrategy> vendorRoutingStrategies = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("routingStrategyGroup.id", routingSrategyGroupId))
			.add(Restrictions.eq("class", "vendor"))
			.add(Restrictions.eq("deliveryStatus.code", DeliveryStatusType.SCHEDULED))
			.add(Restrictions.eq("deleted", false))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return vendorRoutingStrategies;
	}

	@Override
	public List<AbstractRoutingStrategy> findAllRoutingStrategiesByWork(long workId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("deleted", false)).list();
	}
}