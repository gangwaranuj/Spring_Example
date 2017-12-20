package com.workmarket.dao.datetime;

import java.util.List;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.datetime.TimeZone;

public interface TimeZoneDAO extends PaginatableDAOInterface<TimeZone> {

	List<TimeZone> findAllActiveTimeZones();

	TimeZone findTimeZonesById(Long timeZoneId);

	TimeZone findTimeZonesByTimeZoneId(String timeZoneId);
}
