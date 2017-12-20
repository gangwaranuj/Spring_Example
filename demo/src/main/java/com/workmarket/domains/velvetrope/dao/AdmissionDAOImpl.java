package com.workmarket.domains.velvetrope.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.velvetrope.Venue;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
@Repository
public class AdmissionDAOImpl extends AbstractDAO<Admission> implements AdmissionDAO {

	@Override
	protected Class<?> getEntityClass() {
		return Admission.class;
	}

	@Override
	public List<Admission> findAllAdmissionsByKeyNameForVenue(String keyName, Venue... venues) {
		if (ArrayUtils.isEmpty(venues)) {
			return Collections.emptyList();
		}

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.in("venue", venues));
		criteria.add(Restrictions.eq("keyName", keyName));
		criteria.add(Restrictions.eq("deleted", false));

		return (List<Admission>) criteria.list();
	}

	@Override
	public List<Admission> findAllAdmissionsByKeyNameForVenueExcludingVenue(String keyName, Venue includedVenue, Venue excludedVenue) {
		// This finds all admissions for a given target venue that DO NOT also have the second given venue
		DetachedCriteria subselect = DetachedCriteria.forClass(Admission.class, "a")
			.add(Restrictions.eq("venue", excludedVenue))
			.add(Restrictions.eq("keyName", keyName))
			.add(Restrictions.eq("deleted", false))
			.setProjection(Projections.property("value"));

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("venue", includedVenue))
			.add(Restrictions.eq("keyName", keyName))
			.add(Restrictions.eq("deleted", false))
			.add(Subqueries.propertyNotIn("value", subselect));

		return (List<Admission>) criteria.list();
	}

	@Override
	public List<Admission> findAllAdmissionsByKeyNameExcludingVenueForVenues(String keyName, Venue excludedVenue, Venue... includedVenues) {
		// This finds all admissions for a given target venue that DO NOT also have the second given venue
		DetachedCriteria subselect = DetachedCriteria.forClass(Admission.class, "a")
			.add(Restrictions.eq("venue", excludedVenue))
			.add(Restrictions.eq("keyName", keyName))
			.add(Restrictions.eq("deleted", false))
			.setProjection(Projections.property("value"));

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.in("venue", includedVenues))
			.add(Restrictions.eq("keyName", keyName))
			.add(Restrictions.eq("deleted", false))
			.add(Subqueries.propertyNotIn("value", subselect));

		return (List<Admission>) criteria.list();
	}

	@Override
	public List<Admission> findAllAdmissionsByCompanyIdForVenue(Long companyId, String keyName, Venue... venues) {
		if (ArrayUtils.isEmpty(venues)) {
			return Collections.emptyList();
		}

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("value", String.valueOf(companyId)));
		criteria.add(Restrictions.eq("keyName", keyName));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.in("venue", venues));

		return (List<Admission>) criteria.list();
	}
}
