package com.workmarket.dao.tool;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.domains.model.tool.UserToolAssociation;
import com.workmarket.domains.model.tool.UserToolAssociationPagination;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class UserToolAssociationDAOImpl extends AbstractDAO<UserToolAssociation> implements UserToolAssociationDAO {

	@Autowired @Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Autowired @Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;


	protected Class<UserToolAssociation> getEntityClass()
	{
		return UserToolAssociation.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ToolPagination findAllToolsByUser(Long userId, ToolPagination pagination)
	{
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		//Joins
		criteria.createAlias("tool", "tool").setFetchMode("tool", FetchMode.JOIN);
		criteria.createAlias("tool.industry", "industry").setFetchMode("i", FetchMode.JOIN);
		count.createAlias("tool", "tool").setFetchMode("tool", FetchMode.JOIN);

		// restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "user.id", userId, "deleted", Boolean.FALSE, "tool.deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "user.id", userId, "deleted", Boolean.FALSE, "tool.deleted", Boolean.FALSE);

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults((List<Tool>)CollectionUtilities.newListPropertyProjection(criteria.list(), "tool"));

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserToolAssociationPagination findAllAssociationsByUser(Long userId, UserToolAssociationPagination pagination)
	{
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.createAlias("tool", "t")
			.setFetchMode("t", FetchMode.JOIN)
			.createAlias("t.industry", "i")
			.setFetchMode("i", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.distinct(Projections.property("id")))
			.setProjection(Projections.rowCount())
			.createAlias("tool", "t");

		if (pagination.getSortColumn() == null)
		{
			criteria.addOrder(Order.asc("t.name"));
		} else
		{
			criteria.addOrder(Order.asc("t." + ToolPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		criteria.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("user.id", userId));

		criteria.add(Restrictions.eq("deleted", Boolean.FALSE));
		count.add(Restrictions.eq("deleted", Boolean.FALSE));
		criteria.add(Restrictions.eq("t.deleted", Boolean.FALSE));
		count.add(Restrictions.eq("t.deleted", Boolean.FALSE));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public void addToolToUser(Tool tool, User user) {
		UserToolAssociation association = findAssociationsByToolAndUser(tool.getId(), user.getId(), true);

		if (association == null)
		{
			association = new UserToolAssociation(user, tool);
			saveOrUpdate(association);
		} else
		{
			association.setDeleted(false);
		}
	}

	@Override
	public void removeToolFromUser(Tool tool, User user) {
		UserToolAssociation association = findAssociationsByToolAndUser(tool.getId(), user.getId(), true);

		association.setDeleted(true);
	}

	public UserToolAssociation findAssociationsByToolAndUser(Long toolId, Long userId)
	{
		return findAssociationsByToolAndUser(toolId, userId, false);
	}

	private UserToolAssociation findAssociationsByToolAndUser(Long toolId, Long userId, boolean includeDeleted) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("tool", "t")
				.add(Restrictions.eq("t.id", toolId))
				.add(Restrictions.eq("user.id", userId));

		if (!includeDeleted) {
			criteria.setFetchMode("t", FetchMode.JOIN)
					.add(Restrictions.eq("deleted", Boolean.FALSE))
					.add(Restrictions.eq("t.deleted", Boolean.FALSE));
		}

		return (UserToolAssociation)criteria.uniqueResult();
	}

	@Override
	public List<UserToolAssociation> findAssociationsByUser(Long userId) {
		return (List<UserToolAssociation>)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("tool", "t")
				.setFetchMode("t", FetchMode.JOIN)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("t.deleted", Boolean.FALSE))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.list();
	}

	@Override
	public ToolPagination findAllActiveToolsByUser(Long userId, ToolPagination pagination)
	{
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		//Joins
		criteria.createAlias("tool", "tool").setFetchMode("tool", FetchMode.JOIN);
		criteria.createAlias("tool.industry", "industry").setFetchMode("i", FetchMode.JOIN);
		count.createAlias("tool", "tool").setFetchMode("tool", FetchMode.JOIN);

		// restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "user.id", userId, "deleted", Boolean.FALSE, "tool.deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "user.id", userId, "deleted", Boolean.FALSE, "tool.deleted", Boolean.FALSE);

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults((List<Tool>)CollectionUtilities.newListPropertyProjection(criteria.list(), "tool"));

		return pagination;
	}

	public void applySorts(Pagination<Tool> pagination, Criteria query, Criteria count)
	{
		if (pagination.getSortColumn() != null)
		{
			String sort = "tool.name";

			if(UserToolAssociationPagination.SORTS.valueOf(pagination.getSortColumn()) != null)
			{
				sort = UserToolAssociationPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName();
			}

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC))
			{
				query.addOrder(Order.desc(sort));
				count.addOrder(Order.desc(sort));
		   	}
			else
			{
				query.addOrder(Order.asc(sort));
				count.addOrder(Order.asc(sort));
		   }
		}
	}

	public void mergeTools(Long fromToolId, Long toToolId) {
		// find users that have both fromToolId and toToolId and we'll just delete the entry for fromToolId
		String sql = "UPDATE user_tool_association AS from_uta " +
				"INNER JOIN user_tool_association AS to_uta ON from_uta.user_id = to_uta.user_id " +
				"SET from_uta.deleted = 1 " +
				"WHERE from_uta.tool_id = :fromToolId AND to_uta.tool_id = :toToolId;";

		jdbcTemplate.update(sql, CollectionUtilities.newObjectMap(
			"fromToolId", fromToolId,
			"toToolId", toToolId
		));

		// map the rest to toToolId
		sql = "UPDATE user_tool_association AS from_uta " +
				"SET from_uta.tool_id = :toToolId " +
				"WHERE from_uta.tool_id = :fromToolId AND from_uta.deleted = 0;";

		jdbcTemplate.update(sql, CollectionUtilities.newObjectMap(
			"fromToolId", fromToolId,
			"toToolId", toToolId
		));
	}
}
