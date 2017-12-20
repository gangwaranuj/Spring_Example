package com.workmarket.dao.specialty;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
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
public class SpecialtyDAOImpl extends AbstractDAO<Specialty> implements SpecialtyDAO {

	protected Class<Specialty> getEntityClass() {
		return Specialty.class;
	}

	@Override
	public Specialty findSpecialtyById(Long specialtyId) {
		Assert.notNull(specialtyId);
		return (Specialty)getFactory().getCurrentSession().get(Specialty.class, specialtyId);

	}

	@Override
	public Specialty findSpecialtyByNameAndIndustryId(String name, Long industryId) {
		Assert.hasText(name);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("industry", FetchMode.JOIN)
			.add(Restrictions.eq("name", name))
			.add(Restrictions.eq("industry.id", industryId));

		return (Specialty)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SpecialtyPagination findAllSpecialties(SpecialtyPagination pagination)
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

				if (SpecialtyPagination.FILTER_KEYS.NAME.toString().equals(column)) {
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

	public void applySorts(Pagination<Specialty> pagination, Criteria query, Criteria count)
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

	@SuppressWarnings("unchecked")
	@Override
	public SpecialtyPagination findAllSpecialtiesByIndustry(Integer industryId, SpecialtyPagination pagination) {
		Assert.notNull(industryId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFirstResult(pagination.getStartRow())
			.setMaxResults(pagination.getResultsLimit())
			.createAlias("industry", "i")
			.setFetchMode("i", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.rowCount());

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("name"));
		} else {
			criteria.addOrder(Order.asc(SpecialtyPagination.SORTS.valueOf(pagination.getSortColumn()).getColumnName()));
		}

		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("industry.id", industryId.longValue()));
		count.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("industry.id", industryId.longValue()));

		pagination.setResults(criteria.list());
		if(count.list().size()> 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
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

	@Override
	public List<Specialty> findSpecialtiesByIds(Long[] specialtyIds) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.in("id", specialtyIds));

		return criteria.list();
	}
}
