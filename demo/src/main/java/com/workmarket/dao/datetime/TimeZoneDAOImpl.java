package com.workmarket.dao.datetime;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.datetime.TimeZone;

@SuppressWarnings("unchecked")
@Repository
public class TimeZoneDAOImpl extends PaginationAbstractDAO<TimeZone> implements TimeZoneDAO {
	@Override
	protected Class<TimeZone> getEntityClass() {
		return TimeZone.class;
	}

	@Override
	public void applySorts(Pagination<TimeZone> timeZonePagination, Criteria query, Criteria count) {}
	@Override
	public void applyFilters(Pagination<TimeZone> timeZonePagination, Criteria query, Criteria count) {}
	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {}

	@Override
	public List<TimeZone> findAllActiveTimeZones() {
		return getFactory().getCurrentSession().getNamedQuery("TimeZone.findAllActiveTimeZones").list();
	}

	@Override
	public TimeZone findTimeZonesById(Long timeZoneId) {
		return (TimeZone) getFactory().getCurrentSession().get(TimeZone.class, timeZoneId);
	}

	@Override
	public TimeZone findTimeZonesByTimeZoneId(String timeZoneId) {
		return (TimeZone) getFactory().getCurrentSession().getNamedQuery("TimeZone.findTimeZonesByTimeZoneId")
			.setParameter("timeZoneId", timeZoneId).uniqueResult();
	}
}
