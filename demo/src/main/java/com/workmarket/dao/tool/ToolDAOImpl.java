package com.workmarket.dao.tool;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.SqlUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class ToolDAOImpl extends AbstractDAO<Tool> implements ToolDAO
{

	protected Class<Tool> getEntityClass()
	{
		return Tool.class;
	}

	@Override
	public Tool findToolById(Long toolId)
	{
		Assert.notNull(toolId);
		return (Tool) getFactory().getCurrentSession().get(Tool.class, toolId);
	}

	@Override
	public Tool findToolByNameAndIndustryId(String name, Long industryId)
	{
		Assert.hasText(name);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("industry", FetchMode.JOIN)
			.add(Restrictions.eq("name", name))
			.add(Restrictions.eq("industry.id", industryId))
			.add(Restrictions.eq("deleted", Boolean.FALSE));

		return (Tool) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ToolPagination findAllTools(ToolPagination pagination)
	{
		Assert.notNull(pagination, "Invalid pagination");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		//Joins
		criteria.createAlias("industry", "industry").setFetchMode("industry", FetchMode.JOIN);
		count.createAlias("industry", "industry");

		// restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "deleted", Boolean.FALSE);

		if (!pagination.getFilters().isEmpty()) {
			for (String column : pagination.getFilters().keySet()) {

				String columnName = ToolPagination.FILTER_KEYS.valueOf(column).getColumnName();
				String columnValue = pagination.getFilters().get(column);

				if (ToolPagination.FILTER_KEYS.NAME.toString().equals(column)) {
					criteria.add(Restrictions.ilike(columnName, SqlUtilities.prepareLikeString(columnValue)));
					count.add(Restrictions.ilike(columnName, SqlUtilities.prepareLikeString(columnValue)));
				} else if (ToolPagination.FILTER_KEYS.POPULARITY.toString().equals(column)) {
					criteria.add(Restrictions.gt(columnName, Long.valueOf(columnValue)));
					count.add(Restrictions.gt(columnName, Long.valueOf(columnValue)));
				}

			}
		}

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ToolPagination findAllToolsByIndustry(Integer industryId, ToolPagination pagination)
	{
		Assert.notNull(industryId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		//Joins
		criteria.createAlias("industry", "industry").setFetchMode("industry", FetchMode.JOIN);

		// restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "deleted", Boolean.FALSE, "industry.id", industryId.longValue());
		HibernateUtilities.addRestrictionsEq(count, "deleted", Boolean.FALSE, "industry.id", industryId.longValue());

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@SuppressWarnings("unchecked")
	public List<SuggestionDTO> suggest(String prefix, String property)
	{

		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.ilike(property, prefix, MatchMode.START))
			.add(Restrictions.eq("deleted", Boolean.FALSE))
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
			.setMaxResults(10)
			.setProjection(Projections.projectionList()
				.add(Projections.property("id"), "id")
				.add(Projections.property(property), "value")
			)
			.setResultTransformer(Transformers.aliasToBean(SuggestionDTO.class))
			.list();
	}

	public void applySorts(Pagination<Tool> pagination, Criteria query, Criteria count)
	{
		if (pagination.getSortColumn() != null)
		{
			String sort = "name";

			if(ToolPagination.SORTS.valueOf(pagination.getSortColumn()) != null)
			{
				sort = ToolPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName();
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
}
