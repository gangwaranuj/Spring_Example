package com.workmarket.dao;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.Message;
import com.workmarket.domains.model.MessagePagination;
import com.workmarket.domains.model.Pagination;

@Repository
public class MessageDAOImpl extends DeletableAbstractDAO<Message> implements MessageDAO  {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Message> getEntityClass() {
		return Message.class;
	}

	@SuppressWarnings("unchecked")
	public MessagePagination findAllSentMessagesByUserGroup(Long groupId, MessagePagination pagination) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.setFetchMode("sender", FetchMode.JOIN)
			.createAlias("sender", "sender")
			.setFetchMode("sender.avatarSmall", FetchMode.JOIN)
			.createAlias("userGroups", "group");

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.rowCount())
			.createAlias("userGroups", "group");

		criteria.add(Restrictions.eq("group.id", groupId));
		count.add(Restrictions.eq("group.id", groupId));

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			criteria.addOrder(Order.desc("createdOn"));
		} else {
			criteria.addOrder(Order.asc("createdOn"));
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}

	@Override
	public Integer countAllSentMessagesByUserGroup(Long groupId) {
		StringBuilder sql = new StringBuilder()
			.append(" SELECT 	count(1) ")
			.append(" FROM 		message m ")
			.append(" INNER 	JOIN  user_group_message_association um  ")
			.append(" ON 		m.id = um.message_id  ")
			.append(" WHERE 	um.user_group_id = :groupId ")
			.append(" AND 		m.deleted = 0");

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("groupId", groupId);

		return jdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
	}

}