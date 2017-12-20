package com.workmarket.service.business;

import java.util.List;

import com.workmarket.domains.model.datetime.TimeZone;

public interface DateTimeService {
	List<TimeZone> findAllActiveTimeZones();
	TimeZone findTimeZonesById(Long timeZoneId);
	TimeZone findTimeZonesByTimeZoneId(String timeZoneId);
	TimeZone matchTimeZoneForUser(Long userId);
	TimeZone matchTimeZoneForPostalCode(String postalCode, String country, String state, String city);
}
