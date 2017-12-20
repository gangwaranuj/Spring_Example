package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.utility.CollectionUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class BaseWorkDAOImpl extends AbstractDAO<AbstractWork> implements BaseWorkDAO {

	@Override
	protected Class<AbstractWork> getEntityClass() {
		return AbstractWork.class;
	}

	@Override
	public AbstractWork findById(Long id) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("buyer", FetchMode.JOIN)
			.setFetchMode("address", FetchMode.JOIN)
			.setFetchMode("company", FetchMode.JOIN).add(Restrictions.eq("id", id));
		return (AbstractWork) criteria.uniqueResult();
	}

	@Override
	public AbstractWork findById(Long id, boolean loadEverything) {
		if (loadEverything) {
			Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
					.setFetchMode("buyer", FetchMode.JOIN)
					.setFetchMode("address", FetchMode.JOIN)
					.setFetchMode("company", FetchMode.JOIN)
					.setFetchMode("project", FetchMode.JOIN)
					.setFetchMode("priceHistory", FetchMode.JOIN)
					.setFetchMode("workSubStatusTypeAssociations", FetchMode.JOIN)
					.setFetchMode("workCustomFieldGroupAssociations", FetchMode.JOIN)
					.setFetchMode("assessmentsAssociations", FetchMode.JOIN)
					.add(Restrictions.eq("id", id));
			return (AbstractWork) criteria.uniqueResult();
		}
		return findById(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractWork findByWorkNumber(String workNumber) {
		Criteria criteria = getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.setFetchMode("buyer", FetchMode.JOIN)
				.setFetchMode("address", FetchMode.JOIN)
				.add(Restrictions.eq("workNumber", workNumber));

		return (AbstractWork)criteria.uniqueResult();
	}

	@Override
	public List<com.workmarket.domains.work.model.AbstractWork> findByWorkNumbers(List<String> workNumbers) {
		if(CollectionUtilities.isEmpty(workNumbers)) {
			return Collections.emptyList();
		}
		return getFactory().getCurrentSession().createQuery("from work where workNumber in (:workNumbers)")
				.setParameterList("workNumbers", workNumbers)
				.list();
	}

	@Override
	public Long findWorkId(String workNumber) {
		return (Long)getFactory().getCurrentSession().createCriteria(getEntityClass()).add(Restrictions.eq("workNumber", workNumber))
				.setProjection(Projections.id()).uniqueResult();
	}
}