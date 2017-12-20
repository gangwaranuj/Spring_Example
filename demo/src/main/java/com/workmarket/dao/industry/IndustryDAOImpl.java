package com.workmarket.dao.industry;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.IndustryPagination;

@Repository
public class IndustryDAOImpl extends AbstractDAO<Industry> implements IndustryDAO {

	@Override
	protected Class<Industry> getEntityClass() {
		return Industry.class;
	}

	@Override
	public Industry get(Long industryId) {
		return (Industry) getFactory().getCurrentSession().get(getEntityClass(), industryId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Industry> findAllIndustries() {
		return getFactory().getCurrentSession().createQuery("from industry where deleted=0 order by order, name").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IndustryPagination findAllIndustries(IndustryPagination pagination) {
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		criteria.addOrder(Order.asc("order"))
			.addOrder(Order.asc("name"));

		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		// Removing NONE from the list
		criteria.add(Restrictions.gt("id", 1L));
		count.add(Restrictions.gt("id", 1L));

		pagination.setResults(criteria.list());

		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;
	}

	@Override
	public Industry findIndustryById(Long industryId) {
		return (Industry) getFactory().getCurrentSession().createQuery("select e from industry e where e.id = :industryId")
			.setParameter("industryId", industryId)
			.uniqueResult();
	}

	@Override
	public Map<Long, String> findAllIndustryNamesToHydrateSearchData(Set<Long> industryIdsInResponse) {
		if (industryIdsInResponse == null || industryIdsInResponse.size() == 0) {
			return emptyMap();
		}

		Query q = getFactory().getCurrentSession().createQuery("select i.id, i.name from industry i where i.deleted = false and i.id in (:industryIds)");
		q.setParameterList("industryIds", industryIdsInResponse);
		@SuppressWarnings("unchecked")
		List<Object> results = q.list();
		Map<Long, String> returnVal = newHashMapWithExpectedSize(results.size());
		for (Object result : results) {
			Object[] row = (Object[]) result;
			returnVal.put((Long) row[0], (String) row[1]);
		}
		return returnVal;
	}

	@Override
	public Industry findIndustryByName(String name) {
		return (Industry) getFactory().getCurrentSession().createQuery("select e from industry e where e.name = :industryName and e.deleted = 0")
			.setParameter("industryName", name)
			.uniqueResult();
	}
}
