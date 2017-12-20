package com.workmarket.dao.tag;


import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tag.CompanyAdminTag;
import com.workmarket.domains.model.tag.CompanyTag;
import com.workmarket.domains.model.tag.Tag;
import com.workmarket.domains.model.tag.TagPagination;

@Repository
public class TagDAOImpl extends AbstractDAO<Tag> implements TagDAO {

	protected Class<Tag> getEntityClass() {
		return Tag.class;
	}

	@Override
	public Tag findTagById(Long tagId) {
		Assert.notNull(tagId);
		return (Tag) getFactory().getCurrentSession().get(Tag.class, tagId);
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public TagPagination findAllTags(TagPagination pagination) {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		// filters
		for (TagPagination.FILTER_KEYS key : pagination.getResultFilters().keySet()) {
			Collection<Object> values = pagination.getResultFilters().get(key);
			for (Object value : values) {
				if (value instanceof Integer) {
					value = (long) ((Integer) value).intValue();
				}
				criteria.add(Restrictions.eq(key.getColumn(), value));
			}
		}

		//sorts
		for (TagPagination.SORTS key : pagination.getResultSorts().keySet()) {
			String direction = pagination.getResultSorts().get(key);

			if ("asc".equals(direction)) {
				criteria.addOrder(Order.asc(key.getColumn()));
			} else {
				criteria.addOrder(Order.desc(key.getColumn()));
			}
		}

		pagination.setResults(criteria.list());

		criteria.setProjection(Projections.rowCount());
		if (criteria.list().size() > 0)
			pagination.setRowCount(((Long) criteria.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public Tag findTagByName(String name) {
		Assert.hasText(name);
		return (Tag) getFactory().getCurrentSession().getNamedQuery("tag.findTagByName")
			.setParameter("name", name).setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public CompanyTag findCompanyTagByName(String tagName) {
		Assert.hasText(tagName);
		return (CompanyTag) getFactory().getCurrentSession().getNamedQuery("companyTag.findCompanyTagByName")
			.setParameter("name", tagName).setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public CompanyAdminTag findCompanyAdminTagByName(String tagName) {
		Assert.hasText(tagName);
		return (CompanyAdminTag) getFactory().getCurrentSession().getNamedQuery("companyAdminTag.findCompanyAdminTagByName")
			.setParameter("name", tagName).setMaxResults(1)
			.uniqueResult();
	}


}
