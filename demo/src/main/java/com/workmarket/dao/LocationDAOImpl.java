package com.workmarket.dao;

import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.crm.ClientLocationPagination;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class LocationDAOImpl extends AbstractDAO<Location> implements LocationDAO {

	protected Class<Location> getEntityClass() {
		return Location.class;
	}

	@Override
	public Location findLocationById(Long id) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("address", FetchMode.JOIN)
				.createAlias("address", "a")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.add(Restrictions.eq("id", id)).setMaxResults(1);

		return (Location) criteria.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Location> T findLocationById(Class<? extends Location> clazz, Long id) {
		Assert.isAssignable(Location.class, clazz);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz);
		criteria.setFetchMode("address", FetchMode.JOIN)
				.createAlias("address", "a")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.add(Restrictions.eq("id", id)).setMaxResults(1);

		return (T) criteria.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Location> T findLocationByIdAndCompany(Class<? extends Location> clazz, Long locationId, Long companyId) {
		Assert.isAssignable(Location.class, clazz);
		return (T) getFactory().getCurrentSession().createCriteria(clazz)
				.setFetchMode("address", FetchMode.JOIN)
				.createAlias("address", "a")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.add(Restrictions.eq("id", locationId))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public ClientLocation findPrimaryLocationByClient(Long clientCompanyId) {
		Assert.notNull(clientCompanyId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class);
		criteria.setFetchMode("address", FetchMode.JOIN)
				.createAlias("address", "a")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.add(Restrictions.eq("clientCompany.id", clientCompanyId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("primary", true));

		return (ClientLocation) criteria.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientLocation> findLocationByNumberAndCompany(String locationNumber, Long companyId) {
			return getFactory().getCurrentSession().createCriteria(ClientLocation.class)
					.add(Restrictions.eq("company.id", companyId))
					.add(Restrictions.eq("deleted", false))
					.add(Restrictions.eq("locationNumber", locationNumber))
					.list();
		}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientLocation> findLocationsByClientCompanyAndName(Long clientCompanyId, String name) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class)
				.add(Restrictions.eq("clientCompany.id", clientCompanyId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.ilike("name", name, MatchMode.EXACT));
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientLocation> findLocationsByCompanyAndName(Long companyId, String name) {
		return getFactory().getCurrentSession().createCriteria(ClientLocation.class)
				.add(Restrictions.eq("name", name))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("company.id", companyId))
				.list();
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<ClientLocation> findLocationsByNumberAndClientCompany(String number, Long clientCompanyId) {
		Assert.notNull(clientCompanyId);
		Assert.hasText(number);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class)
				.add(Restrictions.eq("clientCompany.id", clientCompanyId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.ilike("locationNumber", number, MatchMode.EXACT));
		return criteria.list();
	}

	@Override
	public ClientLocation findLocationByClientCompanyAndName(
		Long clientCompanyId, String locationName,
		String locationAddressLine1, String locationCity, String locationState,
		String locationPostalCode
	) {
		Criteria criteria = getCriteriaForNamedLocation(clientCompanyId, locationName, locationAddressLine1, locationCity, locationState, locationPostalCode);
		return (ClientLocation) criteria.uniqueResult();
	}

	@Override
	public ClientLocation findLocationByClientCompanyAndName(
		Long companyId, Long clientCompanyId, String locationName,
		String locationAddressLine1, String locationCity, String locationState,
		String locationPostalCode
	) {
		Assert.notNull(companyId);
		Criteria criteria = getCriteriaForNamedLocation(clientCompanyId, locationName, locationAddressLine1, locationCity, locationState, locationPostalCode);
		criteria.add(Restrictions.eq("company.id", companyId));
		return (ClientLocation) criteria.uniqueResult();
	}

	private Criteria getCriteriaForNamedLocation(Long clientCompanyId, String locationName, String locationAddressLine1, String locationCity, String locationState, String locationPostalCode) {
		Assert.notNull(clientCompanyId);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class)
			.setFetchMode("address", FetchMode.JOIN)
			.createAlias("address", "a")
			.setFetchMode("a.state", FetchMode.JOIN)
			.createAlias("a.state", "st")
			.add(Restrictions.eq("clientCompany.id", clientCompanyId))
			.add(Restrictions.eq("deleted", false))
			.setMaxResults(1);

		if (locationName != null) criteria.add(Restrictions.ilike("name", locationName, MatchMode.EXACT));
		if (locationPostalCode != null) criteria.add(Restrictions.ilike("a.postalCode", locationPostalCode, MatchMode.EXACT));
		if (locationState != null) criteria.add(Restrictions.ilike("st.shortName", locationState, MatchMode.EXACT));
		if (locationCity != null) criteria.add(Restrictions.ilike("a.city", locationCity, MatchMode.EXACT));
		if (locationAddressLine1 != null) criteria.add(Restrictions.ilike("a.address1", locationAddressLine1, MatchMode.EXACT));
		return criteria;
	}

	@Override
	public ClientLocation findLocationByClientCompanyAndLocationNumber(
		Long clientCompanyId, String locationNumber, String locationAddressLine1,
		String locationCity, String locationState, String locationPostalCode
	) {
		Criteria criteria = getCriteriaForNumberedLocation(clientCompanyId, locationNumber, locationAddressLine1, locationCity, locationState, locationPostalCode);
		return (ClientLocation) criteria.uniqueResult();
	}

	@Override
	public ClientLocation findLocationByClientCompanyAndLocationNumber(
		Long companyId, Long clientCompanyId, String locationNumber,
		String locationAddressLine1, String locationCity, String locationState,
		String locationPostalCode
	) {
		Assert.notNull(companyId);
		Criteria criteria = getCriteriaForNumberedLocation(clientCompanyId, locationNumber, locationAddressLine1, locationCity, locationState, locationPostalCode);
		criteria.add(Restrictions.eq("company.id", companyId));
		return (ClientLocation) criteria.uniqueResult();
	}

	private Criteria getCriteriaForNumberedLocation(long clientCompanyId, String locationNumber, String locationAddressLine1, String locationCity, String locationState, String locationPostalCode) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class)
			.setFetchMode("address", FetchMode.JOIN)
			.createAlias("address", "a")
			.setFetchMode("a.state", FetchMode.JOIN)
			.createAlias("a.state", "st")
			.add(Restrictions.eq("clientCompany.id", clientCompanyId))
			.add(Restrictions.eq("deleted", false))
			.setMaxResults(1);

		if (locationNumber != null) criteria.add(Restrictions.ilike("locationNumber", locationNumber, MatchMode.EXACT));
		if (locationPostalCode != null) criteria.add(Restrictions.ilike("a.postalCode", locationPostalCode, MatchMode.EXACT));
		if (locationState != null) criteria.add(Restrictions.ilike("st.shortName", locationState, MatchMode.EXACT));
		if (locationCity != null) criteria.add(Restrictions.ilike("a.city", locationCity, MatchMode.EXACT));
		if (locationAddressLine1 != null) criteria.add(Restrictions.ilike("a.address1", locationAddressLine1, MatchMode.EXACT));

		return criteria;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientLocation> findAllLocationsByClientCompany(Long companyId, Long clientCompanyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class)
				.setFetchMode("address", FetchMode.JOIN)
				.createAlias("address", "a")
				.createAlias("clientCompany", "cc")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.ne("name", ""))
				.addOrder(Order.asc("name"));

		if (clientCompanyId != null) {
			criteria.add(Restrictions.eq("cc.id", clientCompanyId));
		}

		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientLocation> findAllLocationsByCompany(Long companyId) {
		return getFactory().getCurrentSession().createCriteria(ClientLocation.class)
				.setFetchMode("address", FetchMode.JOIN)
				.setFetchMode("clientCompany", FetchMode.JOIN)
				.createAlias("address", "a")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.ne("name", ""))
				.addOrder(Order.asc("name"))
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ClientLocationPagination findAllLocationsByClientCompany(Long companyId, Long clientCompanyId, ClientLocationPagination pagination) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(ClientLocation.class);
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		criteria.setFetchMode("address", FetchMode.JOIN)
				.createAlias("address", "a")
				.createAlias("clientCompany", "cc")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.createAlias("phoneAssociations", "phoneAssociations", Criteria.LEFT_JOIN)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		criteria.add(Restrictions.eq("cc.id", clientCompanyId))
				.add(Restrictions.eq("cc.company.id", companyId))
				.add(Restrictions.eq("deleted", false));

		count.add(Restrictions.eq("clientCompany.id", clientCompanyId))
				.createAlias("clientCompany", "cc")
				.add(Restrictions.eq("cc.company.id", companyId))
				.add(Restrictions.eq("deleted", false));

		if (pagination.getSortColumn() == null) {
			criteria.addOrder(Order.asc("name"));
		}

		if (pagination.getFilters() != null) {
			if (pagination.hasFilter(ClientLocationPagination.FILTER_KEYS.LOCATION_NAME)) {
				String name = pagination.getFilter(ClientLocationPagination.FILTER_KEYS.LOCATION_NAME);
				criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
				count.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
			}
		}

		pagination.setResults(criteria.list());

		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ClientLocationPagination findAllLocations(Long companyId, ClientLocationPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientLocation.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(ClientLocation.class);
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		criteria.setFetchMode("address", FetchMode.JOIN)
				.createAlias("address", "a")
				.setFetchMode("a.dressCode", FetchMode.JOIN)
				.setFetchMode("a.locationType", FetchMode.JOIN)
				.createAlias("clientCompany", "cc", Criteria.LEFT_JOIN)
				.createAlias("phoneAssociations", "phoneAssociations", Criteria.LEFT_JOIN)
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.ne("name", ""))
				.add(Restrictions.eq("deleted", false))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		count.createAlias("clientCompany", "cc", Criteria.LEFT_JOIN)
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.ne("name", ""))
				.add(Restrictions.eq("deleted", false));

		String sortColumnName = "name";

		if (pagination.getSortColumn() == null) {
			sortColumnName = "name";
		} else if(pagination.getSortColumn().equals(ClientLocationPagination.SORTS.LOCATION_ID.toString())) {
			sortColumnName = "id";
		} else if (pagination.getSortColumn().equals(ClientLocationPagination.SORTS.LOCATION_TYPE.toString())) {
			sortColumnName = "a.locationType";
		} else if (pagination.getSortColumn().equals(ClientLocationPagination.SORTS.LOCATION_NAME.toString())) {
			sortColumnName = "locationNumber";
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			criteria.addOrder(Order.desc(sortColumnName));
		} else {
			criteria.addOrder(Order.asc(sortColumnName));
		}

		if (pagination.getFilters() != null) {
			if (pagination.hasFilter(ClientLocationPagination.FILTER_KEYS.LOCATION_NAME)) {
				String name = pagination.getFilter(ClientLocationPagination.FILTER_KEYS.LOCATION_NAME);
				criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
				count.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
			}

			if (pagination.hasFilter(ClientLocationPagination.FILTER_KEYS.CLIENT_ID)) {
				Long clientCompanyId = Long.parseLong(pagination.getFilter(ClientLocationPagination.FILTER_KEYS.CLIENT_ID));
				criteria.add(Restrictions.eq("cc.id", clientCompanyId));
				count.add(Restrictions.eq("cc.id", clientCompanyId));
			}
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}

}
